package com.android.systemui.statusbar.phone;

public final /* synthetic */ class BiometricUnlockController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BiometricUnlockController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ BiometricUnlockController$$ExternalSyntheticLambda1(BiometricUnlockController biometricUnlockController, boolean z, boolean z2) {
        this.f$0 = biometricUnlockController;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void run() {
        this.f$0.lambda$startWakeAndUnlock$0(this.f$1, this.f$2);
    }
}
