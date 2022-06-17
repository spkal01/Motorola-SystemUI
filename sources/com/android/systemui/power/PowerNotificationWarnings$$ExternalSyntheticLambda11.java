package com.android.systemui.power;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;

public final /* synthetic */ class PowerNotificationWarnings$$ExternalSyntheticLambda11 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ PowerNotificationWarnings f$0;
    public final /* synthetic */ Ringtone f$1;
    public final /* synthetic */ AudioManager f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ PowerNotificationWarnings$$ExternalSyntheticLambda11(PowerNotificationWarnings powerNotificationWarnings, Ringtone ringtone, AudioManager audioManager, int i) {
        this.f$0 = powerNotificationWarnings;
        this.f$1 = ringtone;
        this.f$2 = audioManager;
        this.f$3 = i;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$showMotoUsbHighTemperatureAlarmInternal$9(this.f$1, this.f$2, this.f$3, dialogInterface);
    }
}
