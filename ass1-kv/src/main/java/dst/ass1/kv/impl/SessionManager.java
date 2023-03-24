package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.UUID;

public class SessionManager implements ISessionManager {
    private static final String USERID_KEY = "userId";
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

        var sessionToken = UUID.randomUUID().toString();
        var hashVariables = new HashMap<String, String>();
        hashVariables.put(USERID_KEY, userId.toString());

        var redisClient = redisPool.getResource();
        redisClient.watch(sessionToken);

        try (Transaction t = redisClient.multi()) {
            redisClient.hset(sessionToken, hashVariables);
            redisClient.expire(sessionToken, timeToLive);
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
        return null;
    }

    @Override
    public void close() {
        redisPool.close();
    }
}
