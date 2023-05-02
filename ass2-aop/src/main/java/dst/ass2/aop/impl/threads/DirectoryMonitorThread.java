package dst.ass2.aop.impl.threads;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class DirectoryMonitorThread extends Thread{
    private final File dir;
    private final ExecutorService pluginRunnerPool;

    public DirectoryMonitorThread(File dir, ExecutorService pluginRunnerPool) {
        this.dir = dir;
        this.pluginRunnerPool = pluginRunnerPool;
    }

    @Override
    public void run() {
        System.err.println("Attempted to monitor "+dir.getName());
    }
}
