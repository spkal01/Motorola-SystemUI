package com.android.systemui.util.sensors;

import com.android.systemui.util.sensors.ThresholdSensor;
import java.util.function.Consumer;

public final /* synthetic */ class ProximitySensor$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ ThresholdSensor.ThresholdSensorEvent f$0;

    public /* synthetic */ ProximitySensor$$ExternalSyntheticLambda1(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        this.f$0 = thresholdSensorEvent;
    }

    public final void accept(Object obj) {
        ((ThresholdSensor.Listener) obj).onThresholdCrossed(this.f$0);
    }
}
