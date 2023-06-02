package dst.ass3.event.impl;

import dst.ass3.event.model.events.LifecycleEvent;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

import java.io.Serializable;

public class PunctuatedWatermark implements WatermarkGenerator<LifecycleEvent>, Serializable {

    @Override
    public void onEvent(LifecycleEvent event, long eventTimestamp, WatermarkOutput output) {
            output.emitWatermark(new Watermark(event.getTimestamp()));

    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
    }
}