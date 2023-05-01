package dst.ass2.aop;

import java.io.File;

public class PluginExecutor implements IPluginExecutor{


    @Override
    public void monitor(File dir) {
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
}
