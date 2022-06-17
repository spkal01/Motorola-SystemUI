package com.android.systemui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class SwipeHelper$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SwipeHelper f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ SwipeHelper$$ExternalSyntheticLambda0(SwipeHelper swipeHelper, View view, boolean z) {
        this.f$0 = swipeHelper;
        this.f$1 = view;
        this.f$2 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$snapChild$0(this.f$1, this.f$2, valueAnimator);
    }
}
