package com.android.systemui.statusbar;

import android.animation.ValueAnimator;

public final /* synthetic */ class ViewTransformationHelper$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ViewTransformationHelper f$0;
    public final /* synthetic */ TransformableView f$1;

    public /* synthetic */ ViewTransformationHelper$$ExternalSyntheticLambda1(ViewTransformationHelper viewTransformationHelper, TransformableView transformableView) {
        this.f$0 = viewTransformationHelper;
        this.f$1 = transformableView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$transformFrom$1(this.f$1, valueAnimator);
    }
}
