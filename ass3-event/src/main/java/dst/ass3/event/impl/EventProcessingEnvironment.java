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
import org.apache.flink.streaming.api.datastream.KeyedStream;
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
    private SinkFunction<TripFailedWarning> tripFailedWarningStreamSink;
    private Time matchingTimeout;


    @Override
    public void initialize(StreamExecutionEnvironment env) {
        DataStreamSource<ITripEventInfo> input = env.addSource(new EventSourceFunction());

        var lifecycleEventStream
                = input
                .filter(event -> event.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps())
                .keyBy(LifecycleEvent::getTripId);

        lifecycleEventStream.addSink(lifecycleEventStreamSink);

        var tripMatchedPattern =
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

        registerAndSinkTripFailedWarningStream(lifecycleEventStream);

        /*This hack is unfortunately necessary as time-progress is being modeled via sleep which
         * makes processingTime difficult to use */
        registerAndSinkDurationStream(lifecycleEventStream, tripMatchedPattern);
        registerAndSinkDurationWarningStream(lifecycleEventStream, tripMatchedPattern);

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
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {
        matchingTimeoutStreamSink = sink;
    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {
        this.tripFailedWarningStreamSink = sink;
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
    }


    private void registerAndSinkTripFailedWarningStream(KeyedStream<LifecycleEvent, Long> lifecycleEventStream) {
        var tripMatchFailedPattern =
                Pattern.<LifecycleEvent>begin("queued")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.QUEUED);
                            }
                        }).followedBy("matched")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.MATCHED);
                            }
                        }).times(3);


        var tripFailedStream =
                CEP.pattern(lifecycleEventStream, tripMatchFailedPattern)
                        .inEventTime()
                        .flatSelect(
                                new PatternFlatSelectFunction<LifecycleEvent, TripFailedWarning>() {
                                    @Override
                                    public void flatSelect(Map<String, List<LifecycleEvent>> pattern,
                                                           Collector<TripFailedWarning> out) throws Exception {
                                        LifecycleEvent start = pattern.get("queued").get(0);
                                        out.collect(new TripFailedWarning(start.getTripId(), start.getRegion()));
                                    }
                                }
                        );

        tripFailedStream.addSink(tripFailedWarningStreamSink);
    }

    private void registerAndSinkDurationStream(KeyedStream<LifecycleEvent, Long> lifecycleEventStream,
                                               Pattern<LifecycleEvent, LifecycleEvent> matchingPattern) {
        var durationStream =
                CEP.pattern(lifecycleEventStream, matchingPattern)
                        .inProcessingTime()
                        .flatSelect(
                                new PatternFlatSelectFunction<LifecycleEvent, MatchingDuration>() {
                                    @Override
                                    public void flatSelect(Map<String, List<LifecycleEvent>> pattern, Collector<MatchingDuration> out) {
                                        LifecycleEvent start = pattern.get("create").get(0);
                                        LifecycleEvent end = pattern.get("match").get(0);
                                        out.collect(new MatchingDuration(end.getTripId(), end.getRegion(), end.getTimestamp() - start.getTimestamp()));
                                    }
                                }
                        );
        durationStream.addSink(matchingDurationStreamSink);

    }

    private void registerAndSinkDurationWarningStream(KeyedStream<LifecycleEvent, Long> lifecycleEventStream,
                                                      Pattern<LifecycleEvent, LifecycleEvent> matchingPattern) {
        final var timeoutTag = new OutputTag<MatchingTimeoutWarning>("timeout-side-output") {
        };

        var errorStream =
                CEP.pattern(lifecycleEventStream, matchingPattern)
                        .inEventTime()
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
                                    public void flatSelect(
                                            Map<String, List<LifecycleEvent>> pattern,
                                            Collector<MatchingDuration> out) {
                                    }
                                });

        errorStream.getSideOutput(timeoutTag).addSink(matchingTimeoutStreamSink);
    }
}
