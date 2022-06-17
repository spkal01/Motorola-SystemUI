package com.android.systemui.statusbar.notification.stack;

import android.app.AlarmManager;

public final /* synthetic */ class CliEducationView$$ExternalSyntheticLambda1 implements AlarmManager.OnAlarmListener {
    public final /* synthetic */ CliEducationView f$0;

    public /* synthetic */ CliEducationView$$ExternalSyntheticLambda1(CliEducationView cliEducationView) {
        this.f$0 = cliEducationView;
    }

    public final void onAlarm() {
        this.f$0.resetEducationSwipe();
    }
}
