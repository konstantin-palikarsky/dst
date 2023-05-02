package dst.ass2.aop;

import java.io.IOException;
import java.nio.file.FileSystems;

public class PluginExecutorFactory {

    public static IPluginExecutor createPluginExecutor() {
        try {
            return new PluginExecutor(FileSystems.getDefault().newWatchService());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't instantiate new Watch Service for plugin executor");
        }
    }

}
