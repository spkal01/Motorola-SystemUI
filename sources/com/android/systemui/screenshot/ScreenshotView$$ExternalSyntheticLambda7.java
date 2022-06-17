package com.android.systemui.screenshot;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda7 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView f$0;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda7(ScreenshotView screenshotView) {
        this.f$0 = screenshotView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$startLongScreenshotTransition$16(valueAnimator);
    }
}
