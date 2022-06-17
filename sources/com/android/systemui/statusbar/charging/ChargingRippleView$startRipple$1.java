package com.android.systemui.statusbar.charging;

import android.animation.ValueAnimator;
import java.util.Objects;

/* compiled from: ChargingRippleView.kt */
final class ChargingRippleView$startRipple$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ChargingRippleView this$0;

    ChargingRippleView$startRipple$1(ChargingRippleView chargingRippleView) {
        this.this$0 = chargingRippleView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        long currentPlayTime = valueAnimator.getCurrentPlayTime();
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float floatValue = ((Float) animatedValue).floatValue();
        this.this$0.rippleShader.setProgress(floatValue);
        this.this$0.rippleShader.setDistortionStrength(((float) 1) - floatValue);
        this.this$0.rippleShader.setTime((float) currentPlayTime);
        this.this$0.invalidate();
    }
}
