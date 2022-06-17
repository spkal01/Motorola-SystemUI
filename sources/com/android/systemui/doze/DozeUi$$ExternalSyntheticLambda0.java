package com.android.systemui.doze;

import android.app.AlarmManager;

public final /* synthetic */ class DozeUi$$ExternalSyntheticLambda0 implements AlarmManager.OnAlarmListener {
    public final /* synthetic */ DozeUi f$0;

    public /* synthetic */ DozeUi$$ExternalSyntheticLambda0(DozeUi dozeUi) {
        this.f$0 = dozeUi;
    }

    public final void onAlarm() {
        this.f$0.onTimeTick();
    }
}
