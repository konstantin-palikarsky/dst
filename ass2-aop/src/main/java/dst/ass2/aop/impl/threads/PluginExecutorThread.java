package dst.ass2.aop.impl.threads;

import java.io.File;
import java.util.Objects;

public class PluginExecutorThread extends Thread {

    @Override
    public void run() {
        System.err.println("Attempted to execute a plugin");
    }

    private void checkDirForPlugins(File dir) {
        //early termination hack
        if (!dir.isDirectory() ||
                Objects.requireNonNull(dir.listFiles()).length == 0) {
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
