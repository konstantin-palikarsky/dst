package dst.ass3.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.http.client.Client;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;
import dst.ass3.messaging.WorkerResponse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class WorkloadMonitor implements IWorkloadMonitor {
    private final Map<Region, List<Long>> regionProcessingTimes = new HashMap<>();
    private final Client client;
    private final Channel channel;
    private final Connection connection;

    public WorkloadMonitor(Client client, ConnectionFactory connectionFactory) {
        this.client = client;

        try {
            this.connection = connectionFactory.newConnection();
            this.channel = connection.createChannel();

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Error instantiating RabbitMQ in WorkloadMonitor");
        }

        setUpMonitorQueue();
    }

    private void setUpMonitorQueue() {

        try {
            var queueName = channel.queueDeclare("WORKLOAD_MONITOR:" + UUID.randomUUID()
                    , false, false, true, null).getQueue();

            for (Region region : Region.values()) {
                channel.queueBind(queueName, Constants.TOPIC_EXCHANGE, regionToRoute(region));
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                var jacksonMapper = new ObjectMapper();

                var workerResponse = jacksonMapper.readValue(delivery.getBody(), WorkerResponse.class);
                var region = routeToRegion(delivery.getEnvelope().getRoutingKey());

                if (region == null) {
                    System.err.println("Message from unknown region!");
                    return;
                }

                var timesList = regionProcessingTimes.computeIfAbsent(region, k -> new ArrayList<>());

                if (timesList.size() >= 10) {
                    timesList.remove(0);
                }

                timesList.add(workerResponse.getProcessingTime());
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {/*Empty*/});

        } catch (IOException e) {
            throw new RuntimeException("Error communicating with RabbitMQ in WorkloadMonitor");
        }
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        Map<Region, Double> averageTimes = new HashMap<>();

        this.regionProcessingTimes.keySet().forEach(
                x -> averageTimes.put(x, regionProcessingTimes.get(x).stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0))
        );

        return averageTimes;
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        var regionRequestsMap = new HashMap<Region, Long>();

        client.getQueues().forEach(
                x -> {
                    var region = queueNameToRegion(x.getName());
                    if (region == null) {
                        return;
                    }

                    regionRequestsMap.put(region, x.getMessagesReady());
                }
        );

        return regionRequestsMap;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        var regionWorkersMap = new HashMap<Region, Long>();

        client.getQueues().forEach(
                x -> {
                    var region = queueNameToRegion(x.getName());
                    if (region == null) {
                        return;
                    }
                    regionWorkersMap.put(region, x.getConsumerCount());
                }
        );

        return regionWorkersMap;
    }

    @Override
    public void close() throws IOException {
        if (connection.isOpen()) {
            this.connection.close();
        }

        if (this.channel.isOpen()) {
            try {
                this.channel.close();
            } catch (Exception ignored) {
                System.err.println("Timed out while closing WorkloadMonitor channel");
            }
        }

    }

    private Region queueNameToRegion(String queueName) {
        switch (queueName) {
            case Constants.QUEUE_AT_LINZ:
                return Region.AT_LINZ;
            case Constants.QUEUE_AT_VIENNA:
                return Region.AT_VIENNA;
            case Constants.QUEUE_DE_BERLIN:
                return Region.DE_BERLIN;
            default:
                System.err.println("Different request region: " + queueName);
                return null;
        }
    }

    private Region routeToRegion(String routingKey) {
        switch (routingKey) {
            case Constants.ROUTING_KEY_AT_LINZ:
                return Region.AT_LINZ;
            case Constants.ROUTING_KEY_AT_VIENNA:
                return Region.AT_VIENNA;
            case Constants.ROUTING_KEY_DE_BERLIN:
                return Region.DE_BERLIN;
            default:
                return null;
        }
    }

    private String regionToRoute(Region region) {
        switch (region) {
            case AT_LINZ:
                return Constants.ROUTING_KEY_AT_LINZ;
            case AT_VIENNA:
                return Constants.ROUTING_KEY_AT_VIENNA;
            case DE_BERLIN:
                return Constants.ROUTING_KEY_DE_BERLIN;
            default:
                return null;
        }
    }

}
