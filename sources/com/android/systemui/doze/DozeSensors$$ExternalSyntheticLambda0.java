package com.android.systemui.doze;

import com.android.systemui.util.sensors.ThresholdSensor;

public final /* synthetic */ class DozeSensors$$ExternalSyntheticLambda0 implements ThresholdSensor.Listener {
    public final /* synthetic */ DozeSensors f$0;

    public /* synthetic */ DozeSensors$$ExternalSyntheticLambda0(DozeSensors dozeSensors) {
        this.f$0 = dozeSensors;
    }

    public final void onThresholdCrossed(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        this.f$0.lambda$new$0(thresholdSensorEvent);
    }
}
