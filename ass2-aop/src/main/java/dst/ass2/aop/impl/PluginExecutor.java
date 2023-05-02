package dst.ass2.aop.impl;

import dst.ass2.aop.IPluginExecutor;
import dst.ass2.aop.impl.threads.DirectoryMonitorThread;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginExecutor implements IPluginExecutor {
    private final Map<File, DirectoryMonitorThread> dirToMonitorMap = new ConcurrentHashMap<>();
    private final ExecutorService pluginRunnerPool = Executors.newFixedThreadPool(100);


    private boolean executorIsRunning = false;

    public PluginExecutor() {
    }

    @Override
    public void monitor(File dir) {
        if (!dir.isDirectory()) {
            throw new RuntimeException("Trying to monitor a file, not a directory: " + dir.getName());
        }
        var monitorThreadForFile = new DirectoryMonitorThread(dir, pluginRunnerPool);
        dirToMonitorMap.put(dir, monitorThreadForFile);
    }

    @Override
    public void stopMonitoring(File dir) {
        var monitorThread = dirToMonitorMap.remove(dir);

        if (monitorThread == null) {
            System.err.println("This monitor is already detached");
            return;
        }

        if (executorIsRunning) {
            monitorThread.interrupt();
        }
    }

    @Override
    public void start() {
        for (DirectoryMonitorThread thread : dirToMonitorMap.values()) {
            thread.start();
        }

        executorIsRunning = true;
    }

    @Override
    public void stop() {
        pluginRunnerPool.shutdown();

        for (DirectoryMonitorThread thread : dirToMonitorMap.values()) {
            thread.interrupt();
        }

        executorIsRunning = false;
    }

}
