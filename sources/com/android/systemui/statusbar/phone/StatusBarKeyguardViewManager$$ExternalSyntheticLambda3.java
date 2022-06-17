package com.android.systemui.statusbar.phone;

import android.hardware.biometrics.BiometricSourceType;

public final /* synthetic */ class StatusBarKeyguardViewManager$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ StatusBarKeyguardViewManager f$0;
    public final /* synthetic */ BiometricSourceType f$1;

    public /* synthetic */ StatusBarKeyguardViewManager$$ExternalSyntheticLambda3(StatusBarKeyguardViewManager statusBarKeyguardViewManager, BiometricSourceType biometricSourceType) {
        this.f$0 = statusBarKeyguardViewManager;
        this.f$1 = biometricSourceType;
    }

    public final void run() {
        this.f$0.lambda$wakeAndUnlockDejank$4(this.f$1);
    }
}
