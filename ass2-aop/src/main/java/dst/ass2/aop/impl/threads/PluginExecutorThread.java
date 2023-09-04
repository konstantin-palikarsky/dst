package dst.ass2.aop.impl.threads;

import dst.ass2.aop.IPluginExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginExecutorThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(PluginExecutorThread.class);
    private final IPluginExecutable pluginFile;

    public PluginExecutorThread(IPluginExecutable pluginFile) {
        this.pluginFile = pluginFile;
    }


    @Override
    public void run() {
        LOG.info("Executing plugin: {} ", pluginFile.toString());

        pluginFile.execute();
    }

}
