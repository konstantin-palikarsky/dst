package dst.ass3.messaging.impl;

import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.*;

public class MessagingFactory implements IMessagingFactory {

    @Override
    public IQueueManager createQueueManager() {
        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);

        return new QueueManager(connectionFactory);
    }

    @Override
    public IRequestGateway createRequestGateway() {
        return new RequestGateway();
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        return new WorkloadMonitor();
    }

    @Override
    public void close() {
        // implement if needed
    }
}
