package com.android.systemui.util.sensors;

import com.android.systemui.util.sensors.ThresholdSensor;

public final /* synthetic */ class ProximitySensor$$ExternalSyntheticLambda0 implements ThresholdSensor.Listener {
    public final /* synthetic */ ProximitySensor f$0;

    public /* synthetic */ ProximitySensor$$ExternalSyntheticLambda0(ProximitySensor proximitySensor) {
        this.f$0 = proximitySensor;
    }

    public final void onThresholdCrossed(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        this.f$0.onPrimarySensorEvent(thresholdSensorEvent);
    }
}
