package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.IContainerService;
import dst.ass3.messaging.Region;

import java.util.List;

public class ContainerService implements IContainerService {

    @Override
    public List<ContainerInfo> listContainers() throws ContainerException {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void stopContainer(String containerId) throws ContainerException {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public ContainerInfo startWorker(Region region) throws ContainerException {
        throw new RuntimeException("Unimplemented");
    }
}
