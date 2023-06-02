package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternFlatTimeoutFunction;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    private SinkFunction<LifecycleEvent> lifecycleEventStreamSink;
    private SinkFunction<MatchingDuration> matchingDurationStreamSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutStreamSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningStreamSink;
    private SinkFunction<Alert> alertStreamSink;
    private SinkFunction<AverageMatchingDuration> averageDurationStreamSink;
    private Time matchingTimeout;


    @Override
    public void initialize(StreamExecutionEnvironment env) {
        DataStreamSource<ITripEventInfo> input = env.addSource(new EventSourceFunction());

        var lifecycleEventStream
                = input
                .filter(event -> event.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(WatermarkStrategy.
                        forGenerator(new PunctuatedWatermarkSupplier()
                        ).withTimestampAssigner((TimestampAssignerSupplier<LifecycleEvent>) context -> (SerializableTimestampAssigner<LifecycleEvent>) (lifecycleEvent, recordTimestamp) -> lifecycleEvent.getTimestamp()))
                .keyBy(LifecycleEvent::getTripId);

        lifecycleEventStream.addSink(lifecycleEventStreamSink);


        var tripFailedWarningStream = registerAndSinkTripFailedWarningStream(lifecycleEventStream);
        var matchDurationStream = registerAndSinkDurationStream(lifecycleEventStream);

        matchDurationStream.keyBy(event -> event.getRegion().name())
                .window(GlobalWindows.create())
                .trigger(PurgingTrigger.of(CountTrigger.of(5)))
                .process(new ProcessWindowFunction<MatchingDuration, AverageMatchingDuration, String, GlobalWindow>() {
                    @Override
                    public void process(String key,
                                        ProcessWindowFunction<MatchingDuration, AverageMatchingDuration, String,
                                                GlobalWindow>.Context context, Iterable<MatchingDuration> elements,
                                        Collector<AverageMatchingDuration> out) {
                        Region region = Region.valueOf(key);

                        double sum = 0;
                        int count = 0;

                        for (MatchingDuration event : elements) {
                            sum += event.getDuration();
                            count++;
                        }

                        if (count >= 5) {
                            out.collect(new AverageMatchingDuration(region, sum / count));
                        }


                    }
                }).addSink(averageDurationStreamSink);


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
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
        this.alertStreamSink = sink;
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
        this.averageDurationStreamSink = sink;
    }


    private DataStream<TripFailedWarning> registerAndSinkTripFailedWarningStream(KeyedStream<LifecycleEvent, Long> lifecycleEventStream) {
        var tripMatchFailedPattern =
                Pattern.<LifecycleEvent>begin("matched")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.MATCHED);
                            }
                        }).followedBy("queued")
                        .where(new IterativeCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event, Context<LifecycleEvent> ctx) {
                                return event.getState().equals(TripState.QUEUED);
                            }
                        }).times(3);


        var tripFailedStream =
                CEP.pattern(lifecycleEventStream, tripMatchFailedPattern)
                        .inEventTime()
                        .select(
                                new PatternSelectFunction<LifecycleEvent, TripFailedWarning>() {
                                    @Override
                                    public TripFailedWarning select(Map<String, List<LifecycleEvent>> pattern) throws Exception {

                                        LifecycleEvent start = pattern.get("queued").get(0);
                                        return new TripFailedWarning(start.getTripId(), start.getRegion());
                                    }
                                }
                        );

        tripFailedStream.addSink(tripFailedWarningStreamSink);

        return tripFailedStream;
    }

    private DataStream<MatchingDuration> registerAndSinkDurationStream(
            KeyedStream<LifecycleEvent, Long> lifecycleEventStream) {

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

        var durationStream =
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
                                    public void flatSelect(Map<String, List<LifecycleEvent>> pattern, Collector<MatchingDuration> out) {
                                        LifecycleEvent start = pattern.get("create").get(0);
                                        LifecycleEvent end = pattern.get("match").get(0);
                                        out.collect(new MatchingDuration(end.getTripId(), end.getRegion(), end.getTimestamp() - start.getTimestamp()));
                                    }
                                }
                        );

        durationStream.addSink(matchingDurationStreamSink);
        durationStream.getSideOutput(timeoutTag).addSink(matchingTimeoutStreamSink);

        return durationStream;
    }
}
