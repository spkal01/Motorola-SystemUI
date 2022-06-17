package com.android.systemui.power;

import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class PowerNotificationWarnings$$ExternalSyntheticLambda12 implements ActivityStarter.Callback {
    public final /* synthetic */ PowerNotificationWarnings f$0;

    public /* synthetic */ PowerNotificationWarnings$$ExternalSyntheticLambda12(PowerNotificationWarnings powerNotificationWarnings) {
        this.f$0 = powerNotificationWarnings;
    }

    public final void onActivityStarted(int i) {
        this.f$0.lambda$showUsbHighTemperatureAlarmInternal$4(i);
    }
}
