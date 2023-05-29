package dst.ass3.messaging.impl;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkloadMonitor implements IWorkloadMonitor {
    private final HashMap<Region, QueueInfo> regionQueueMap;

    public WorkloadMonitor(Client client) {
        var regionQueueMap = new HashMap<Region, QueueInfo>();

        regionQueueMap.put(Region.AT_LINZ, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_LINZ));
        regionQueueMap.put(Region.AT_VIENNA, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_VIENNA));
        regionQueueMap.put(Region.DE_BERLIN, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_DE_BERLIN));

        this.regionQueueMap = regionQueueMap;
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        var regionRequestsMap = new HashMap<Region, Long>();

        regionQueueMap.keySet().forEach(
                x -> regionRequestsMap.put(x, regionQueueMap.get(x).getMessagesReady())
        );

        return regionRequestsMap;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        var regionWorkersMap = new HashMap<Region, Long>();

        regionQueueMap.keySet().forEach(
                x -> regionWorkersMap.put(x, regionQueueMap.get(x).getConsumerCount())
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

}
