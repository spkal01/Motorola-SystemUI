package com.android.systemui.power;

import android.content.DialogInterface;

public final /* synthetic */ class PowerNotificationWarnings$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PowerNotificationWarnings f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PowerNotificationWarnings$$ExternalSyntheticLambda5(PowerNotificationWarnings powerNotificationWarnings, int i, int i2) {
        this.f$0 = powerNotificationWarnings;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showStartSaverConfirmation$13(this.f$1, this.f$2, dialogInterface, i);
    }
}
