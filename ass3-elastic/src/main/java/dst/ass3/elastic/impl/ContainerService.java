package dst.ass3.elastic.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.ContainerNotFoundException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.messaging.Region;

import java.util.List;
import java.util.stream.Collectors;

public class ContainerService implements IContainerService {
    private final DockerClient dockerClient;

    public ContainerService() {


        this.dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
    }

    @Override
    public List<ContainerInfo> listContainers() throws ContainerException {
        var containers = dockerClient.
                listContainersCmd()
                .withShowAll(false)
                .exec();


        return containers.stream()
                .map(x -> containerToInfo(x.getId())).collect(Collectors.toList());
    }

    @Override
    public void stopContainer(String containerId) throws ContainerException {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotFoundException e) {
            throw new ContainerNotFoundException(e);
        }
    }


    @Override
    public ContainerInfo startWorker(Region region) throws ContainerException {
        var container = dockerClient.createContainerCmd("dst/ass3-worker")
                .withCmd(region.toString().toLowerCase())
                .withHostConfig(new HostConfig()
                        .withNetworkMode("dst")
                        .withAutoRemove(true))
                .exec();


        dockerClient.startContainerCmd(container.getId()).exec();


        return containerToInfo(container.getId());
    }
    private ContainerInfo containerToInfo(String containerId) {
        var inspectedContainer = dockerClient.inspectContainerCmd(containerId).exec();

        var info = new ContainerInfo();
        info.setContainerId(containerId);
        info.setImage("dst/ass3-worker");
        info.setRunning(Boolean.TRUE.equals(inspectedContainer.getState().getRunning()));

        var executeCommand = inspectedContainer.getConfig().getCmd();

        if (executeCommand == null || executeCommand.length == 0) {
            return info;
        }

        info.setWorkerRegion(parseRegion(executeCommand[executeCommand.length - 1]));

        return info;
    }

    private Region parseRegion(String s) {

        switch (s) {
            case "at_linz":
                return Region.AT_LINZ;
            case "at_vienna":
                return Region.AT_VIENNA;
            case "de_berlin":
                return Region.DE_BERLIN;
            default:
                return null;
        }
    }
}
