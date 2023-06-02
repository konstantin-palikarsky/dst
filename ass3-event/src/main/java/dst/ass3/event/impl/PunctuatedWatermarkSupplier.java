package dst.ass3.event.impl;

import dst.ass3.event.model.events.LifecycleEvent;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;

import java.io.Serializable;

public class PunctuatedWatermarkSupplier implements WatermarkGeneratorSupplier<LifecycleEvent>, Serializable {
    @Override
    public WatermarkGenerator<LifecycleEvent> createWatermarkGenerator(Context context) {
        return new PunctuatedWatermark();
    }
}
