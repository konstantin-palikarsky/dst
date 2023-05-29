package dst.ass3.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.TripRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RequestGateway implements IRequestGateway {
    private final ConnectionFactory connectionFactory;

    public RequestGateway(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void submitRequest(TripRequest request) {
        String targetQueue;
        switch (request.getRegion()) {
            case AT_LINZ:
                targetQueue = Constants.QUEUE_AT_LINZ;
                break;
            case AT_VIENNA:
                targetQueue = Constants.QUEUE_AT_VIENNA;
                break;
            case DE_BERLIN:
                targetQueue = Constants.QUEUE_DE_BERLIN;
                break;
            default:
                throw new RuntimeException("Non-existent request region");
        }

        var jacksonMapper = new ObjectMapper();

        try (var conn = connectionFactory.newConnection();
             var channel = conn.createChannel()) {

            var bytesJson = jacksonMapper.writeValueAsBytes(request);

            channel.basicPublish("", targetQueue,
                    false, false,
                    MessageProperties.BASIC, bytesJson);


        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Error communicating with RabbitMQ in RequestGateway");
        }


    }

    @Override
    public void close() throws IOException {
    }
}
