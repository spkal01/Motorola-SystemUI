package com.android.systemui.biometrics;

import android.animation.ValueAnimator;

public final /* synthetic */ class AuthPanelController$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AuthPanelController f$0;

    public /* synthetic */ AuthPanelController$$ExternalSyntheticLambda2(AuthPanelController authPanelController) {
        this.f$0 = authPanelController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateForContentDimensions$2(valueAnimator);
    }
}
