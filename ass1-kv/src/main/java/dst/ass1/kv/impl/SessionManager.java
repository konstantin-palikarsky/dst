package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.Jedis;

public class SessionManager implements ISessionManager {
    Jedis redisClient;

    public SessionManager(String host, int port){
        redisClient = new Jedis(host,port);
    }

    @Override
    public String createSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        return null;
    }

    @Override
    public void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException {

    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        return null;
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        return null;
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        return 0;
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        return null;
    }

    @Override
    public void close() {

    }
}
