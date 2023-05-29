package dst.ass3.messaging.impl;

import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IQueueManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueManager implements IQueueManager {
    private final ConnectionFactory connectionFactory;

    public QueueManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void setUp() {

        try (var conn = connectionFactory.newConnection();
             var channel = conn.createChannel()) {

            channel.exchangeDeclare(Constants.TOPIC_EXCHANGE, "direct", true);

            for (String qname : Constants.WORK_QUEUES) {
                channel.queueDeclare(qname, true, false, false, null);
            }

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Error communicating with RabbitMQ in QueueManager");
        }
    }

    @Override
    public void tearDown() {

        try (var conn = connectionFactory.newConnection();
             var channel = conn.createChannel()) {

            channel.exchangeDelete(Constants.TOPIC_EXCHANGE);

            for (String qname : Constants.WORK_QUEUES) {
                channel.queueDelete(qname);
            }

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Error communicating with RabbitMQ in QueueManager");
        }
    }

    @Override
    public void close() throws IOException {
    }
}
