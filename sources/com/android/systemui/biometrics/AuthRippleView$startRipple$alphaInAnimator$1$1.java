package com.android.systemui.biometrics;

import android.animation.ValueAnimator;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.statusbar.charging.RippleShader;
import java.util.Objects;

/* compiled from: AuthRippleView.kt */
final class AuthRippleView$startRipple$alphaInAnimator$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ AuthRippleView this$0;

    AuthRippleView$startRipple$alphaInAnimator$1$1(AuthRippleView authRippleView) {
        this.this$0 = authRippleView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        RippleShader access$getRippleShader$p = this.this$0.rippleShader;
        int color = this.this$0.rippleShader.getColor();
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Int");
        access$getRippleShader$p.setColor(ColorUtils.setAlphaComponent(color, ((Integer) animatedValue).intValue()));
        this.this$0.invalidate();
    }
}
