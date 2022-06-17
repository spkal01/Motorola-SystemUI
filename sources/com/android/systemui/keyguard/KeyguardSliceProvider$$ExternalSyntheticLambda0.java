package com.android.systemui.keyguard;

import android.app.AlarmManager;

public final /* synthetic */ class KeyguardSliceProvider$$ExternalSyntheticLambda0 implements AlarmManager.OnAlarmListener {
    public final /* synthetic */ KeyguardSliceProvider f$0;

    public /* synthetic */ KeyguardSliceProvider$$ExternalSyntheticLambda0(KeyguardSliceProvider keyguardSliceProvider) {
        this.f$0 = keyguardSliceProvider;
    }

    public final void onAlarm() {
        this.f$0.updateNextAlarm();
    }
}
