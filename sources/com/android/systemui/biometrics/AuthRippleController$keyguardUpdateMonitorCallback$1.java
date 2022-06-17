package com.android.systemui.biometrics;

import android.hardware.biometrics.BiometricSourceType;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import org.jetbrains.annotations.Nullable;

/* compiled from: AuthRippleController.kt */
public final class AuthRippleController$keyguardUpdateMonitorCallback$1 extends KeyguardUpdateMonitorCallback {
    final /* synthetic */ AuthRippleController this$0;

    AuthRippleController$keyguardUpdateMonitorCallback$1(AuthRippleController authRippleController) {
        this.this$0 = authRippleController;
    }

    public void onBiometricAuthenticated(int i, @Nullable BiometricSourceType biometricSourceType, boolean z) {
        this.this$0.showRipple(biometricSourceType);
    }
}
