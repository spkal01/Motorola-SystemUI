package com.android.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* compiled from: TextAnimator.kt */
public final class TextAnimator$setTextStyle$listener$1 extends AnimatorListenerAdapter {
    final /* synthetic */ Runnable $onAnimationEnd;
    final /* synthetic */ TextAnimator this$0;

    TextAnimator$setTextStyle$listener$1(Runnable runnable, TextAnimator textAnimator) {
        this.$onAnimationEnd = runnable;
        this.this$0 = textAnimator;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.$onAnimationEnd.run();
        this.this$0.mo9920x2f3d2b23().removeListener(this);
    }

    public void onAnimationCancel(@Nullable Animator animator) {
        this.this$0.mo9920x2f3d2b23().removeListener(this);
    }
}
