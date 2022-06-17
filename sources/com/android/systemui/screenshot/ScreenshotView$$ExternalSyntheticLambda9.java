package com.android.systemui.screenshot;

import android.animation.ValueAnimator;
import android.graphics.PointF;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda9 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ PointF f$4;
    public final /* synthetic */ PointF f$5;
    public final /* synthetic */ float f$6;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda9(ScreenshotView screenshotView, float f, float f2, float f3, PointF pointF, PointF pointF2, float f4) {
        this.f$0 = screenshotView;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
        this.f$4 = pointF;
        this.f$5 = pointF2;
        this.f$6 = f4;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createScreenshotDropInAnimation$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
    }
}
