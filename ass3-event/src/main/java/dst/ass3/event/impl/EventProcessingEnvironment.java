package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.events.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    @Override
    public void initialize(StreamExecutionEnvironment env) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setMatchingDurationTimeout(Time time) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
        throw new RuntimeException("Unimplemented");
    }

}
