package com.android.systemui.doze;

import com.android.systemui.statusbar.phone.KeyguardBouncerDelegate;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda1 implements KeyguardBouncerDelegate.Callback {
    public final /* synthetic */ DozeTriggers f$0;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda1(DozeTriggers dozeTriggers) {
        this.f$0 = dozeTriggers;
    }

    public final void onSensorPulse(int i, float f, float f2, float[] fArr) {
        this.f$0.onSensor(i, f, f2, fArr);
    }
}
