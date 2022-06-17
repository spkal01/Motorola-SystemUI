package com.android.systemui.screenshot;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView f$0;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda0(ScreenshotView screenshotView) {
        this.f$0 = screenshotView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$startLongScreenshotTransition$18(valueAnimator);
    }
}
