package com.android.systemui.media;

import android.animation.ValueAnimator;
import java.util.Objects;

/* compiled from: LightSourceDrawable.kt */
final class LightSourceDrawable$illuminate$1$2$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ LightSourceDrawable this$0;

    LightSourceDrawable$illuminate$1$2$1(LightSourceDrawable lightSourceDrawable) {
        this.this$0 = lightSourceDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        RippleData access$getRippleData$p = this.this$0.rippleData;
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        access$getRippleData$p.setProgress(((Float) animatedValue).floatValue());
        this.this$0.invalidateSelf();
    }
}
