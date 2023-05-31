package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticityController implements IElasticityController {
    private static final double SCALE_OUT_THRESH = 0.1;
    private static final double SCALE_DOWN_THRESH = 0.05;

    private final IContainerService containerService;
    private final IWorkloadMonitor workloadMonitor;

    private final Map<Region, Double> regionWaitTimes;

    public ElasticityController(IContainerService containerService,
                                IWorkloadMonitor workloadMonitor) {
        this.containerService = containerService;
        this.workloadMonitor = workloadMonitor;

        this.regionWaitTimes = new HashMap<>();
        regionWaitTimes.put(Region.AT_LINZ, 30000d);
        regionWaitTimes.put(Region.AT_VIENNA, 30000d);
        regionWaitTimes.put(Region.DE_BERLIN, 120000d);
    }

    @Override
    public void adjustWorkers() throws ContainerException {
        var workersMap = workloadMonitor.getWorkerCount();
        var requestCountMap = workloadMonitor.getRequestCount();
        var requestTimeMap = workloadMonitor.getAverageProcessingTime();

        for (Region region : Region.values()) {
            var requestsForRegion = requestCountMap.get(region);
            var requestTimeForRegion = requestTimeMap.get(region);
            var workerCountForRegion = workersMap.get(region);

            double expectedWaitTime = computeWaitTimeForRegion(requestsForRegion,
                    requestTimeForRegion, workerCountForRegion);


            var shouldScaleOut = expectedWaitTime > (regionWaitTimes.get(region) * (1 + SCALE_OUT_THRESH));
            var shouldScaleDown = expectedWaitTime < (regionWaitTimes.get(region) * (1 - SCALE_DOWN_THRESH));

            if (shouldScaleOut) {
                var numberOfAdditionalWorkers = nonOptimalWorkerCount(region,
                        expectedWaitTime, workerCountForRegion);
                for (int i = 0; i < numberOfAdditionalWorkers; i++) {
                    containerService.startWorker(region);
                }
            } else if (shouldScaleDown) {

                var numberOfUnneededWorkers = nonOptimalWorkerCount(
                        region,
                        expectedWaitTime,
                        workerCountForRegion
                );

                var nContainerIdsForRegion =
                        getNContainerIdsForRegion(numberOfUnneededWorkers, region);

                for (String containerId : nContainerIdsForRegion) {
                    containerService.stopContainer(containerId);
                }
            }
        }
    }

    private List<String> getNContainerIdsForRegion(long count, Region region) throws ContainerException {

        return containerService.listContainers().stream()
                .filter(x -> x.getWorkerRegion().equals(region))
                .map(ContainerInfo::getContainerId)
                .limit(count)
                .collect(Collectors.toList());
    }

    private long nonOptimalWorkerCount(Region region, Double expectedWaitTime, Long workerCount) {
        var maxWaitTime = regionWaitTimes.get(region);

        long optimalWorkerCount = Math.round((expectedWaitTime * (double) workerCount) / maxWaitTime);

        return Math.abs(optimalWorkerCount - workerCount);
    }


    private Double computeWaitTimeForRegion(Long requestCount, Double requestRuntime, Long workerCount) {
        return ((double) requestCount / (double) workerCount) * requestRuntime;
    }
}
