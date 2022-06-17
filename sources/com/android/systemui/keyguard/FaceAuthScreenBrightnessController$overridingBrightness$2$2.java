package com.android.systemui.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* compiled from: FaceAuthScreenBrightnessController.kt */
public final class FaceAuthScreenBrightnessController$overridingBrightness$2$2 extends AnimatorListenerAdapter {
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    FaceAuthScreenBrightnessController$overridingBrightness$2$2(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController) {
        this.this$0 = faceAuthScreenBrightnessController;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.brightnessAnimator = null;
    }
}
