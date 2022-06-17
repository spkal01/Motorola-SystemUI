package com.android.keyguard;

import android.hardware.fingerprint.FingerprintManager;

public final /* synthetic */ class KeyguardUpdateMonitor$$ExternalSyntheticLambda1 implements FingerprintManager.FingerprintDetectionCallback {
    public final /* synthetic */ KeyguardUpdateMonitor f$0;

    public /* synthetic */ KeyguardUpdateMonitor$$ExternalSyntheticLambda1(KeyguardUpdateMonitor keyguardUpdateMonitor) {
        this.f$0 = keyguardUpdateMonitor;
    }

    public final void onFingerprintDetected(int i, int i2, boolean z) {
        this.f$0.lambda$new$3(i, i2, z);
    }
}
