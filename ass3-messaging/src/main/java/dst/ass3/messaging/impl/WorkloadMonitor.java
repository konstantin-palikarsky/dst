package dst.ass3.messaging.impl;

import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.Map;

public class WorkloadMonitor implements IWorkloadMonitor {

    @Override
    public Map<Region, Long> getRequestCount() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void close() throws IOException {
        throw new RuntimeException("Unimplemented");
    }

}
