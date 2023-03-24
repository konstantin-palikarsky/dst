package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SessionManagerFactory implements ISessionManagerFactory {
    private static final String HOST_KEY = "redis.host";
    private static final String PORT_KEY = "redis.port";
    private static final String PROPERTIES_PATH = "redis.properties";

    @Override
    public ISessionManager createSessionManager(Properties properties) {

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_PATH)) {

            Properties redisProperties = new Properties();
            redisProperties.load(input);

            return new SessionManager(redisProperties.getProperty(HOST_KEY),
                    Integer.parseInt(redisProperties.getProperty(PORT_KEY)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
