package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.BiometricUnlockController;

public final /* synthetic */ class BiometricUnlockController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BiometricUnlockController f$0;
    public final /* synthetic */ BiometricUnlockController.PendingAuthenticated f$1;

    public /* synthetic */ BiometricUnlockController$$ExternalSyntheticLambda0(BiometricUnlockController biometricUnlockController, BiometricUnlockController.PendingAuthenticated pendingAuthenticated) {
        this.f$0 = biometricUnlockController;
        this.f$1 = pendingAuthenticated;
    }

    public final void run() {
        this.f$0.lambda$onFinishedGoingToSleep$1(this.f$1);
    }
}
