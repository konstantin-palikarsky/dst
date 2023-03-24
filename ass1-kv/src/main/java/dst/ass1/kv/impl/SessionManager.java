package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.HashMap;
import java.util.UUID;

import static redis.clients.jedis.params.ScanParams.SCAN_POINTER_START;

public class SessionManager implements ISessionManager {
    private static final String USERID_KEY = "userId";
    private static final String USER_SET_KEY = "users";

    JedisPool redisPool;

    public SessionManager(String host, int port) {
        redisPool = new JedisPool(host, port);
    }

    /**
     * We create the sessions using Redis hashes, we need to add new fields and values to our session
     * freely for which sets are not a reasonable usage
     */
    @Override
    public String createSession(Long userId, int timeToLive) throws SessionCreationFailedException {

        var sessionToken = "";

        var redisClient = redisPool.getResource();

        try (Transaction t = redisClient.multi()) {
            sessionToken = createSession(t, userId.toString(), timeToLive);
            if (t.exec() == null) {
                throw new SessionCreationFailedException();
            }
        }

        return sessionToken;
    }

    @Override
    public void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException {
        var hashVariables = new HashMap<String, String>();
        hashVariables.put(key, value);

        var redisClient = redisPool.getResource();

        var session = redisClient.hgetAll(sessionId);
        if (session.keySet().isEmpty()) {
            throw new SessionNotFoundException();
        }

        redisClient.hset(sessionId, hashVariables);
    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        var session = redisPool.getResource().hgetAll(sessionId);

        if (session.keySet().isEmpty()) {
            throw new SessionNotFoundException();
        } else {
            return session.get(key);
        }
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        return Long.parseLong(getSessionVariable(sessionId, USERID_KEY));
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        var ttl = redisPool.getResource().ttl(sessionId);

        if (ttl == -2) {
            throw new SessionNotFoundException();
        }

        return (int) ttl;
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {

        var redisClient = redisPool.getResource();
        var sessionToken = "";

        redisClient.watch(USER_SET_KEY, userId.toString());


        boolean exists = false;
        ScanParams scanParams = new ScanParams().match(userId +":*");
        String cur = SCAN_POINTER_START;
        do {
            ScanResult<String> scanResult = redisClient.scan(cur, scanParams, "hash");

            if (!scanResult.getResult().isEmpty()) {
                exists = true;
                sessionToken = scanResult.getResult().get(0);
            }

            cur = scanResult.getCursor();
        } while (!cur.equals(SCAN_POINTER_START) && !exists);


        try (Transaction t = redisClient.multi()) {

            if (!exists) {

                sessionToken = createSession(t, userId.toString(), timeToLive);

                if (t.exec() == null) {
                    throw new SessionCreationFailedException();
                }

            }
        }

        return sessionToken;
    }

    private String createSession(Transaction t, String userId, int timeToLive) {
        var sessionToken = userId + ":" + UUID.randomUUID();

        var hashVariables = new HashMap<String, String>();
        hashVariables.put(USERID_KEY, userId.toString());

        t.hset(sessionToken, hashVariables);
        t.expire(sessionToken, timeToLive);
        return sessionToken;
    }

    @Override
    public void close() {
        redisPool.close();
    }
}
