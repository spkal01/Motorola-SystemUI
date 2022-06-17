package com.android.systemui.keyguard;

import android.animation.ValueAnimator;
import android.view.View;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: FaceAuthScreenBrightnessController.kt */
final class FaceAuthScreenBrightnessController$overridingBrightness$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    FaceAuthScreenBrightnessController$overridingBrightness$1$1(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController) {
        this.this$0 = faceAuthScreenBrightnessController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        View access$getWhiteOverlay$p = this.this$0.whiteOverlay;
        if (access$getWhiteOverlay$p != null) {
            Object animatedValue = valueAnimator.getAnimatedValue();
            Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
            access$getWhiteOverlay$p.setAlpha(((Float) animatedValue).floatValue());
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
        throw null;
    }
}
