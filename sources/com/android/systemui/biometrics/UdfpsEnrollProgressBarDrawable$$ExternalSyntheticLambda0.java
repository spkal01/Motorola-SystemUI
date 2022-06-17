package com.android.systemui.biometrics;

import android.animation.ValueAnimator;

public final /* synthetic */ class UdfpsEnrollProgressBarDrawable$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ UdfpsEnrollProgressBarDrawable f$0;

    public /* synthetic */ UdfpsEnrollProgressBarDrawable$$ExternalSyntheticLambda0(UdfpsEnrollProgressBarDrawable udfpsEnrollProgressBarDrawable) {
        this.f$0 = udfpsEnrollProgressBarDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setEnrollmentProgress$0(valueAnimator);
    }
}
