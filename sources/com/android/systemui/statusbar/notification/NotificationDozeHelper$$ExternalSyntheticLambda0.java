package com.android.systemui.statusbar.notification;

import android.animation.ValueAnimator;
import java.util.function.Consumer;

public final /* synthetic */ class NotificationDozeHelper$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ NotificationDozeHelper$$ExternalSyntheticLambda0(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.accept((Float) valueAnimator.getAnimatedValue());
    }
}
