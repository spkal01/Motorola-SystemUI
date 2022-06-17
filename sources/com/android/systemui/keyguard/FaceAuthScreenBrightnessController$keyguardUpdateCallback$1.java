package com.android.systemui.keyguard;

import android.hardware.biometrics.BiometricSourceType;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import org.jetbrains.annotations.Nullable;

/* compiled from: FaceAuthScreenBrightnessController.kt */
public final class FaceAuthScreenBrightnessController$keyguardUpdateCallback$1 extends KeyguardUpdateMonitorCallback {
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    FaceAuthScreenBrightnessController$keyguardUpdateCallback$1(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController) {
        this.this$0 = faceAuthScreenBrightnessController;
    }

    public void onBiometricRunningStateChanged(boolean z, @Nullable BiometricSourceType biometricSourceType) {
        if (biometricSourceType == BiometricSourceType.FACE) {
            FaceAuthScreenBrightnessController faceAuthScreenBrightnessController = this.this$0;
            if (!faceAuthScreenBrightnessController.enabled) {
                z = false;
            }
            faceAuthScreenBrightnessController.setOverridingBrightness(z);
        }
    }
}
