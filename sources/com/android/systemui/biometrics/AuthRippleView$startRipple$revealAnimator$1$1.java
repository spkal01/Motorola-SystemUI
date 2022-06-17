package com.android.systemui.biometrics;

import android.animation.ValueAnimator;
import com.android.systemui.statusbar.LightRevealScrim;
import java.util.Objects;

/* compiled from: AuthRippleView.kt */
final class AuthRippleView$startRipple$revealAnimator$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ LightRevealScrim $lightReveal;

    AuthRippleView$startRipple$revealAnimator$1$1(LightRevealScrim lightRevealScrim) {
        this.$lightReveal = lightRevealScrim;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        LightRevealScrim lightRevealScrim = this.$lightReveal;
        if (lightRevealScrim != null) {
            Object animatedValue = valueAnimator.getAnimatedValue();
            Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
            lightRevealScrim.setRevealAmount(((Float) animatedValue).floatValue());
        }
    }
}
