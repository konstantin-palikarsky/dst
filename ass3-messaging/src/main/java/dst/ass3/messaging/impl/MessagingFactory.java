package dst.ass3.messaging.impl;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import dst.ass3.messaging.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MessagingFactory implements IMessagingFactory {

    @Override
    public IQueueManager createQueueManager() {
        return new QueueManager(getConfiguredFactory());
    }

    @Override
    public IRequestGateway createRequestGateway() {
        return new RequestGateway(getConfiguredFactory());
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        Client client;
        try {
            client = new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Could not instantiate RabbitMQ client for workload monitoring!");
        }

        return new WorkloadMonitor(client);
    }

    @Override
    public void close() {
        // implement if needed
    }

    private ConnectionFactory getConfiguredFactory() {
        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);
        return connectionFactory;
    }
}
