package com.android.systemui.biometrics;

import android.animation.ValueAnimator;

public final /* synthetic */ class UdfpsEnrollDrawable$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ UdfpsEnrollDrawable f$0;

    public /* synthetic */ UdfpsEnrollDrawable$$ExternalSyntheticLambda2(UdfpsEnrollDrawable udfpsEnrollDrawable) {
        this.f$0 = udfpsEnrollDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onEnrollmentProgress$1(valueAnimator);
    }
}
