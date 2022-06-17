package com.android.systemui.keyguard;

import android.animation.ValueAnimator;
import android.util.MathUtils;
import android.view.View;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: FaceAuthScreenBrightnessController.kt */
final class FaceAuthScreenBrightnessController$overridingBrightness$2$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ float $targetBrightness;
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    FaceAuthScreenBrightnessController$overridingBrightness$2$1(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController, float f) {
        this.this$0 = faceAuthScreenBrightnessController;
        this.$targetBrightness = f;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float floatValue = ((Float) animatedValue).floatValue();
        float constrainedMap = MathUtils.constrainedMap(this.this$0.userDefinedBrightness, this.$targetBrightness, 0.0f, 0.5f, floatValue);
        float constrainedMap2 = MathUtils.constrainedMap(0.0f, this.this$0.maxScrimOpacity, 0.5f, 1.0f, floatValue);
        this.this$0.notificationShadeWindowController.setFaceAuthDisplayBrightness(constrainedMap);
        View access$getWhiteOverlay$p = this.this$0.whiteOverlay;
        if (access$getWhiteOverlay$p != null) {
            access$getWhiteOverlay$p.setAlpha(constrainedMap2);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
            throw null;
        }
    }
}
