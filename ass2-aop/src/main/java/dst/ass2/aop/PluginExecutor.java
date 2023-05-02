package dst.ass2.aop;

import java.io.File;
import java.nio.file.*;
import java.util.Objects;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class PluginExecutor implements IPluginExecutor {
    private WatchService watchService;

    public PluginExecutor(WatchService watchService) {
        this.watchService = watchService;
    }

    @Override
    public void monitor(File dir) {
        checkDirForPlugins(dir);


        try {
            Path dirPath = FileSystems.getDefault().getPath(dir.getAbsolutePath());

            dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, ENTRY_MODIFY);

            WatchKey watchKey;

            System.err.println("Before loop");
            while ((watchKey = watchService.take()) != null) {
                System.err.println("In loop");
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    System.err.println("In event loop");
                }
                watchKey.reset();
            }
            System.err.println("Out of loop");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void stopMonitoring(File dir) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void start() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void stop() {
        throw new RuntimeException("Unimplemented");
    }

    private void checkDirForPlugins(File dir) {
        //early termination hack
        if (!dir.isDirectory() ||
                Objects.requireNonNull(dir.listFiles()).length==0) {
            System.err.println("This directory is empty or not a directory");
            return;
        }


        for (File file : Objects.requireNonNull(dir.listFiles())) {
            checkIfPluginExecutable(file);
        }

    }

    private void checkIfPluginExecutable(File file) {
        System.err.println("Checking if " + file.getName() + " is plugin executable");
    }
}
