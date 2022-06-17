package com.android.p011wm.shell.bubbles;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.bubbles.BadgedImageView$$ExternalSyntheticLambda0 */
public final /* synthetic */ class BadgedImageView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BadgedImageView f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ BadgedImageView$$ExternalSyntheticLambda0(BadgedImageView badgedImageView, boolean z) {
        this.f$0 = badgedImageView;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateDotScale$1(this.f$1, valueAnimator);
    }
}
