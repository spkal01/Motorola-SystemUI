package com.android.systemui.biometrics;

import android.animation.ValueAnimator;
import com.android.systemui.statusbar.LightRevealScrim;
import com.android.systemui.statusbar.charging.RippleShader;
import java.util.Objects;

/* compiled from: AuthRippleView.kt */
final class AuthRippleView$startRipple$rippleAnimator$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ LightRevealScrim $lightReveal;
    final /* synthetic */ AuthRippleView this$0;

    AuthRippleView$startRipple$rippleAnimator$1$1(AuthRippleView authRippleView, LightRevealScrim lightRevealScrim) {
        this.this$0 = authRippleView;
        this.$lightReveal = lightRevealScrim;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        long currentPlayTime = valueAnimator.getCurrentPlayTime();
        RippleShader access$getRippleShader$p = this.this$0.rippleShader;
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        access$getRippleShader$p.setProgress(((Float) animatedValue).floatValue());
        this.this$0.rippleShader.setTime((float) currentPlayTime);
        LightRevealScrim lightRevealScrim = this.$lightReveal;
        if (lightRevealScrim != null) {
            Object animatedValue2 = valueAnimator.getAnimatedValue();
            Objects.requireNonNull(animatedValue2, "null cannot be cast to non-null type kotlin.Float");
            lightRevealScrim.setRevealAmount(((Float) animatedValue2).floatValue());
        }
        this.this$0.invalidate();
    }
}
