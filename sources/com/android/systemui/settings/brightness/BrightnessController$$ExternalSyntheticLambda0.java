package com.android.systemui.settings.brightness;

import android.animation.ValueAnimator;

public final /* synthetic */ class BrightnessController$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BrightnessController f$0;

    public /* synthetic */ BrightnessController$$ExternalSyntheticLambda0(BrightnessController brightnessController) {
        this.f$0 = brightnessController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateSliderTo$0(valueAnimator);
    }
}
