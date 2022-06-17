package com.android.systemui.screenshot;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView f$0;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda2(ScreenshotView screenshotView) {
        this.f$0 = screenshotView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createScreenshotTranslateDismissAnimation$20(valueAnimator);
    }
}
