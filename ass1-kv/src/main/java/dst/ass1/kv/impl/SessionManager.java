package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Objects;
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
        var redisClient = redisPool.getResource();

        if (!redisClient.exists(sessionId)) {
            throw new SessionNotFoundException();
        }

        redisClient.hset(sessionId, key, value);
    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        var redisClient = redisPool.getResource();

        if (!redisClient.exists(sessionId)) {
            throw new SessionNotFoundException();
        }

        return redisClient.hget(sessionId, key);
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        return Long.parseLong(getSessionVariable(sessionId, USERID_KEY));
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        var redisClient = redisPool.getResource();

        if (!redisClient.exists(sessionId)) {
            throw new SessionNotFoundException();
        }

        return (int) redisClient.ttl(sessionId);
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {

        var redisClient = redisPool.getResource();

        redisClient.watch(userId.toString());
        var potentialKey = redisClient.get(userId.toString());

        try (Transaction t = redisClient.multi()) {
            var sessionToken =
                    Objects.requireNonNullElseGet(
                            potentialKey,
                            () -> createSession(t, userId.toString(), timeToLive)
                    );
            if (t.exec() == null) {
                throw new SessionCreationFailedException();
            }
            return sessionToken;
        }
    }

    private String createSession(Transaction t, String userId, int timeToLive) {
        var sessionToken = userId + ":" + UUID.randomUUID();

        t.set(userId, sessionToken);
        t.expire(userId, timeToLive);

        t.hset(sessionToken, USERID_KEY, userId);
        t.expire(sessionToken, timeToLive);
        return sessionToken;
    }

    @Override
    public void close() {
        redisPool.close();
    }
}
