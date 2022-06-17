package com.android.systemui.statusbar.phone;

import android.app.AlarmManager;

public final /* synthetic */ class ScrimController$$ExternalSyntheticLambda1 implements AlarmManager.OnAlarmListener {
    public final /* synthetic */ ScrimController f$0;

    public /* synthetic */ ScrimController$$ExternalSyntheticLambda1(ScrimController scrimController) {
        this.f$0 = scrimController;
    }

    public final void onAlarm() {
        this.f$0.onHideWallpaperTimeout();
    }
}
