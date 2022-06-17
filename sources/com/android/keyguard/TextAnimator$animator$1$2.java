package com.android.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* compiled from: TextAnimator.kt */
public final class TextAnimator$animator$1$2 extends AnimatorListenerAdapter {
    final /* synthetic */ TextAnimator this$0;

    TextAnimator$animator$1$2(TextAnimator textAnimator) {
        this.this$0 = textAnimator;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.mo9921x92092a50().rebase();
    }

    public void onAnimationCancel(@Nullable Animator animator) {
        this.this$0.mo9921x92092a50().rebase();
    }
}
