package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternFlatTimeoutFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    private SinkFunction<LifecycleEvent> lifecycleEventStreamSink;
    private SinkFunction<MatchingDuration> matchingDurationStreamSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutStreamSink;

    private Time matchingTimeout;


    @Override
    public void initialize(StreamExecutionEnvironment env) {
        DataStreamSource<ITripEventInfo> input = env.addSource(new EventSourceFunction());

        var lifecycleEventStream
                = input
                .filter(event -> event.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());


        var matchingPattern =
                Pattern.<LifecycleEvent>begin("create")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.CREATED);
                            }
                        }).followedBy("match")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.MATCHED);
                            }
                        }).within(matchingTimeout);

        final var timeoutTag = new OutputTag<MatchingTimeoutWarning>("timeout-side-output") {
        };

        var mainStream = CEP.pattern(lifecycleEventStream.keyBy(LifecycleEvent::getTripId), matchingPattern)
                .inProcessingTime()
                .flatSelect(
                        timeoutTag,
                        new PatternFlatTimeoutFunction<LifecycleEvent, MatchingTimeoutWarning>() {
                            @Override
                            public void timeout(Map<String, List<LifecycleEvent>> pattern, long timeoutTimestamp, Collector<MatchingTimeoutWarning> out) {
                                System.err.println("Timing out");
                                LifecycleEvent start = pattern.get("create").get(0);
                                out.collect(new MatchingTimeoutWarning(start.getTripId(), start.getRegion()));
                            }
                        },

                        new PatternFlatSelectFunction<LifecycleEvent, MatchingDuration>() {
                            @Override
                            public void flatSelect(Map<String, List<LifecycleEvent>> pattern, Collector<MatchingDuration> out) {
                                LifecycleEvent start = pattern.get("create").get(0);
                                LifecycleEvent end = pattern.get("match").get(0);
                                out.collect(new MatchingDuration(end.getTripId(), end.getRegion(), end.getTimestamp() - start.getTimestamp()));
                            }
                        }
                );

        mainStream.addSink(matchingDurationStreamSink);
        mainStream.getSideOutput(timeoutTag).addSink(matchingTimeoutStreamSink);
        lifecycleEventStream.addSink(lifecycleEventStreamSink);
    }

    @Override
    public void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink) {
        lifecycleEventStreamSink = sink;
    }

    @Override
    public void setMatchingDurationTimeout(Time time) {
        this.matchingTimeout = time;
    }

    @Override
    public void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink) {
        matchingDurationStreamSink = sink;
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
    }

    @Override
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {
        matchingTimeoutStreamSink = sink;
    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {
    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
    }

}
