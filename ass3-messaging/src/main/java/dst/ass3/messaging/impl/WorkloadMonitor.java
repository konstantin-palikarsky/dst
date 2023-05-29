package dst.ass3.messaging.impl;

import com.rabbitmq.http.client.Client;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkloadMonitor implements IWorkloadMonitor {
    private final Client client;

    public WorkloadMonitor(Client client) {
        this.client = client;
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        var regionRequestsMap = new HashMap<Region, Long>();

        client.getQueues().forEach(
                x -> regionRequestsMap.put(nameToRegion(x.getName()), x.getMessagesReady())
        );

        return regionRequestsMap;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        var regionWorkersMap = new HashMap<Region, Long>();

        client.getQueues().forEach(
                x -> regionWorkersMap.put(nameToRegion(x.getName()), x.getConsumerCount())
        );

        return regionWorkersMap;
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        return null;
    }

    @Override
    public void close() throws IOException {
    }

    private Region nameToRegion(String name) {
        switch (name) {
            case Constants.QUEUE_AT_LINZ:
                return Region.AT_LINZ;
            case Constants.QUEUE_AT_VIENNA:
                return Region.AT_VIENNA;
            case Constants.QUEUE_DE_BERLIN:
                return Region.DE_BERLIN;
            default:
                throw new RuntimeException("Non-existent request region");
        }
    }

}
