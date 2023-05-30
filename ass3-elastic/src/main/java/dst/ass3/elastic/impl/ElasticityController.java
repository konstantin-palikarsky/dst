package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.messaging.IWorkloadMonitor;

public class ElasticityController implements IElasticityController {
    private final IContainerService containerService;
    private final IWorkloadMonitor workloadMonitor;

    public ElasticityController(IContainerService containerService,
                                IWorkloadMonitor workloadMonitor) {
        this.containerService = containerService;
        this.workloadMonitor = workloadMonitor;
    }

    @Override
    public void adjustWorkers() throws ContainerException {
        throw new RuntimeException("Unimplemented");
    }
}
