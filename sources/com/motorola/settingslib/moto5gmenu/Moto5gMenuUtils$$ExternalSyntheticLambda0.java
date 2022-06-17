package com.motorola.settingslib.moto5gmenu;

import android.telephony.TelephonyManager;

public final /* synthetic */ class Moto5gMenuUtils$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ TelephonyManager f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ Moto5gMenuUtils$$ExternalSyntheticLambda0(TelephonyManager telephonyManager, int i, int i2) {
        this.f$0 = telephonyManager;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        Moto5gMenuUtils.lambda$setAllowedNetworkTypesForReason$0(this.f$0, this.f$1, this.f$2);
    }
}
