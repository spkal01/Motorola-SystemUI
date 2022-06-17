package com.android.systemui.biometrics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* compiled from: AuthRippleView.kt */
public final class AuthRippleView$startRipple$animatorSet$1$1 extends AnimatorListenerAdapter {
    final /* synthetic */ Runnable $onAnimationEnd;
    final /* synthetic */ AuthRippleView this$0;

    AuthRippleView$startRipple$animatorSet$1$1(AuthRippleView authRippleView, Runnable runnable) {
        this.this$0 = authRippleView;
        this.$onAnimationEnd = runnable;
    }

    public void onAnimationStart(@Nullable Animator animator) {
        this.this$0.rippleInProgress = true;
        this.this$0.setVisibility(0);
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        Runnable runnable = this.$onAnimationEnd;
        if (runnable != null) {
            runnable.run();
        }
        this.this$0.rippleInProgress = false;
        this.this$0.setVisibility(8);
    }
}
