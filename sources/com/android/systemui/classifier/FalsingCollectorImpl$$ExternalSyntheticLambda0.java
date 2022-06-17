package com.android.systemui.classifier;

import com.android.systemui.util.sensors.ThresholdSensor;

public final /* synthetic */ class FalsingCollectorImpl$$ExternalSyntheticLambda0 implements ThresholdSensor.Listener {
    public final /* synthetic */ FalsingCollectorImpl f$0;

    public /* synthetic */ FalsingCollectorImpl$$ExternalSyntheticLambda0(FalsingCollectorImpl falsingCollectorImpl) {
        this.f$0 = falsingCollectorImpl;
    }

    public final void onThresholdCrossed(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        this.f$0.onProximityEvent(thresholdSensorEvent);
    }
}
