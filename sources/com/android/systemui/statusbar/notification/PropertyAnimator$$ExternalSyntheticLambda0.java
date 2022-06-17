package com.android.systemui.statusbar.notification;

import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;

public final /* synthetic */ class PropertyAnimator$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Property f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ PropertyAnimator$$ExternalSyntheticLambda0(Property property, View view) {
        this.f$0 = property;
        this.f$1 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.set(this.f$1, (Float) valueAnimator.getAnimatedValue());
    }
}
