package com.android.systemui.doze;

import com.android.systemui.doze.DozeSensors;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda0 implements DozeSensors.Callback {
    public final /* synthetic */ DozeTriggers f$0;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda0(DozeTriggers dozeTriggers) {
        this.f$0 = dozeTriggers;
    }

    public final void onSensorPulse(int i, float f, float f2, float[] fArr) {
        this.f$0.onSensor(i, f, f2, fArr);
    }
}
