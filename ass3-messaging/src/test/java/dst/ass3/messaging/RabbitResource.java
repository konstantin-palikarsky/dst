package dst.ass3.messaging;

import com.rabbitmq.http.client.Client;
import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.configuration.IllegalConfigurationException;
import org.apache.qpid.server.model.SystemConfig;
import org.junit.rules.ExternalResource;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RabbitResource extends ExternalResource {

    private RabbitAdmin admin;
    private EmbeddedInMemoryQpidBroker broker;
    private Client manager;
    private CachingConnectionFactory connectionFactory;
    private RabbitTemplate client;

    @Override
    protected void before() throws Throwable {
        this.broker = new EmbeddedInMemoryQpidBroker();
        this.broker.start();

        manager = new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);

        connectionFactory = new CachingConnectionFactory(Constants.RMQ_HOST);
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);

        client = new RabbitTemplate(connectionFactory);
        admin = new RabbitAdmin(connectionFactory);
    }

    @Override
    protected void after() {
        connectionFactory.destroy();
        this.broker.shutdown();
    }

    public Client getManager() {
        return manager;
    }

    public RabbitTemplate getClient() {
        return client;
    }

    public RabbitAdmin getAdmin() {
        return admin;
    }

    public CachingConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    private static class EmbeddedInMemoryQpidBroker {


        private static final String DEFAULT_INITIAL_CONFIGURATION_LOCATION = "broker-config.json";

        private final SystemLauncher systemLauncher;

        public EmbeddedInMemoryQpidBroker() {
            this.systemLauncher = new SystemLauncher();
        }

        public void start() throws Exception {
            this.systemLauncher.startup(createSystemConfig());
        }

        public void shutdown() {
            this.systemLauncher.shutdown();
        }

        private Map<String, Object> createSystemConfig() throws IllegalConfigurationException {
            Map<String, Object> attributes = new HashMap<>();
            URL initialConfigUrl = EmbeddedInMemoryQpidBroker.class.getClassLoader().getResource(DEFAULT_INITIAL_CONFIGURATION_LOCATION);

            if (initialConfigUrl == null) {
                throw new IllegalConfigurationException("Configuration location '" + DEFAULT_INITIAL_CONFIGURATION_LOCATION + "' not found");
            }
            attributes.put(SystemConfig.TYPE, "Memory");
            attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, initialConfigUrl.toExternalForm());
            attributes.put(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT, true);
            return attributes;
        }
    }
}
