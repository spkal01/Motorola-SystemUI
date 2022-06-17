package com.android.keyguard;

public final /* synthetic */ class KeyguardUpdateMonitor$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ KeyguardUpdateMonitor f$0;

    public /* synthetic */ KeyguardUpdateMonitor$$ExternalSyntheticLambda7(KeyguardUpdateMonitor keyguardUpdateMonitor) {
        this.f$0 = keyguardUpdateMonitor;
    }

    public final void run() {
        this.f$0.updateBiometricListeningState();
    }
}
