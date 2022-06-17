package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;

public final /* synthetic */ class LightBarTransitionsController$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ LightBarTransitionsController f$0;

    public /* synthetic */ LightBarTransitionsController$$ExternalSyntheticLambda0(LightBarTransitionsController lightBarTransitionsController) {
        this.f$0 = lightBarTransitionsController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateIconTint$0(valueAnimator);
    }
}
