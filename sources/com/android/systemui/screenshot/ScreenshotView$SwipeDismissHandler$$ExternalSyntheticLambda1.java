package com.android.systemui.screenshot;

import android.animation.ValueAnimator;
import com.android.systemui.screenshot.ScreenshotView;

public final /* synthetic */ class ScreenshotView$SwipeDismissHandler$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenshotView.SwipeDismissHandler f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ ScreenshotView$SwipeDismissHandler$$ExternalSyntheticLambda1(ScreenshotView.SwipeDismissHandler swipeDismissHandler, float f, float f2) {
        this.f$0 = swipeDismissHandler;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createSwipeReturnAnimation$1(this.f$1, this.f$2, valueAnimator);
    }
}
