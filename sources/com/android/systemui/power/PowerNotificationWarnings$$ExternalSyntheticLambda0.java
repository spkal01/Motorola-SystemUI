package com.android.systemui.power;

import android.content.DialogInterface;

public final /* synthetic */ class PowerNotificationWarnings$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PowerNotificationWarnings f$0;

    public /* synthetic */ PowerNotificationWarnings$$ExternalSyntheticLambda0(PowerNotificationWarnings powerNotificationWarnings) {
        this.f$0 = powerNotificationWarnings;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showUsbHighTemperatureAlarmInternal$5(dialogInterface, i);
    }
}
