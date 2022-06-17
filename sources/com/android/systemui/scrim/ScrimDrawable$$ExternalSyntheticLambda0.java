package com.android.systemui.scrim;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScrimDrawable$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScrimDrawable f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ScrimDrawable$$ExternalSyntheticLambda0(ScrimDrawable scrimDrawable, int i, int i2) {
        this.f$0 = scrimDrawable;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setColor$0(this.f$1, this.f$2, valueAnimator);
    }
}
