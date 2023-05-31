package dst.ass3.event.impl;

import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

public class EventSourceFunction implements IEventSourceFunction {

    @Override
    public void open(Configuration parameters) throws Exception {

    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return null;
    }

    @Override
    public IterationRuntimeContext getIterationRuntimeContext() {
        return null;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void run(SourceContext<ITripEventInfo> ctx) throws Exception {

    }

    @Override
    public void cancel() {

    }
}
