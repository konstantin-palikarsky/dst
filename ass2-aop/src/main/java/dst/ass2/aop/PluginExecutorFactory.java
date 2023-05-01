package dst.ass2.aop;

public class PluginExecutorFactory {

    public static IPluginExecutor createPluginExecutor() {
         return new PluginExecutor();
    }

}
