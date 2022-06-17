package com.android.systemui.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

/* compiled from: FaceAuthScreenBrightnessController.kt */
public final class FaceAuthScreenBrightnessController$overridingBrightness$1$2 extends AnimatorListenerAdapter {
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    FaceAuthScreenBrightnessController$overridingBrightness$1$2(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController) {
        this.this$0 = faceAuthScreenBrightnessController;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        View access$getWhiteOverlay$p = this.this$0.whiteOverlay;
        if (access$getWhiteOverlay$p != null) {
            access$getWhiteOverlay$p.setVisibility(4);
            this.this$0.brightnessAnimator = null;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
        throw null;
    }
}
