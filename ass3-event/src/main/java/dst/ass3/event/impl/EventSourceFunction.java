package dst.ass3.event.impl;

import dst.ass3.event.Constants;
import dst.ass3.event.EventSubscriber;
import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

import java.net.InetSocketAddress;

public class EventSourceFunction implements IEventSourceFunction {
    private EventSubscriber eventSubscriber;
    private RuntimeContext runtimeContext;
    private boolean running;

    @Override
    public void open(Configuration parameters) {
        eventSubscriber = EventSubscriber.subscribe(new InetSocketAddress(Constants.EVENT_PUBLISHER_PORT));
    }

    @Override
    public void close() {
        if (eventSubscriber == null){
            return;
        }
        cancel();
        eventSubscriber.close();
        eventSubscriber = null;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public IterationRuntimeContext getIterationRuntimeContext() {
        return null;
    }

    @Override
    public void run(SourceContext<ITripEventInfo> ctx) {
        running = true;
        while (running) {
            ITripEventInfo event = eventSubscriber.receive();
            if (event == null) {
                running = false;
                break;
            }
            ctx.collect(event);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
