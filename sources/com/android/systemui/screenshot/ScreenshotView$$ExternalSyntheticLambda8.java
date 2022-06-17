package com.android.systemui.screenshot;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda8 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda8(ScreenshotView screenshotView, float f, float f2) {
        this.f$0 = screenshotView;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createScreenshotTranslateDismissAnimation$21(this.f$1, this.f$2, valueAnimator);
    }
}
