package com.android.systemui.util.sensors;

import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ThresholdSensor;

public final /* synthetic */ class ProximitySensor$ProximityCheck$$ExternalSyntheticLambda0 implements ThresholdSensor.Listener {
    public final /* synthetic */ ProximitySensor.ProximityCheck f$0;

    public /* synthetic */ ProximitySensor$ProximityCheck$$ExternalSyntheticLambda0(ProximitySensor.ProximityCheck proximityCheck) {
        this.f$0 = proximityCheck;
    }

    public final void onThresholdCrossed(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        this.f$0.onProximityEvent(thresholdSensorEvent);
    }
}
