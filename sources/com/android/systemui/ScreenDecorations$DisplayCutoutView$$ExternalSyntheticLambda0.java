package com.android.systemui;

import android.animation.ValueAnimator;
import com.android.systemui.ScreenDecorations;

public final /* synthetic */ class ScreenDecorations$DisplayCutoutView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScreenDecorations.DisplayCutoutView f$0;

    public /* synthetic */ ScreenDecorations$DisplayCutoutView$$ExternalSyntheticLambda0(ScreenDecorations.DisplayCutoutView displayCutoutView) {
        this.f$0 = displayCutoutView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setShowProtection$1(valueAnimator);
    }
}
