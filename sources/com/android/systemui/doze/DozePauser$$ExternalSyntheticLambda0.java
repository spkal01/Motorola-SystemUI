package com.android.systemui.doze;

import android.app.AlarmManager;

public final /* synthetic */ class DozePauser$$ExternalSyntheticLambda0 implements AlarmManager.OnAlarmListener {
    public final /* synthetic */ DozePauser f$0;

    public /* synthetic */ DozePauser$$ExternalSyntheticLambda0(DozePauser dozePauser) {
        this.f$0 = dozePauser;
    }

    public final void onAlarm() {
        this.f$0.onTimeout();
    }
}
