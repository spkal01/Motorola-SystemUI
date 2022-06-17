package com.android.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.android.internal.jank.InteractionJankMonitor;

public abstract class KeyguardInputView extends LinearLayout {
    private Runnable mOnFinishImeAnimationRunnable;

    /* access modifiers changed from: package-private */
    public void animateForIme(float f, boolean z) {
    }

    /* access modifiers changed from: package-private */
    public boolean disallowInterceptTouch(MotionEvent motionEvent) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public abstract CharSequence getTitle();

    /* access modifiers changed from: package-private */
    public void startAppearAnimation() {
    }

    /* access modifiers changed from: package-private */
    public boolean startDisappearAnimation(Runnable runnable) {
        return false;
    }

    public KeyguardInputView(Context context) {
        super(context);
    }

    public KeyguardInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public KeyguardInputView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public AnimatorListenerAdapter getAnimationListener(final int i) {
        return new AnimatorListenerAdapter() {
            private boolean mIsCancel;

            public void onAnimationCancel(Animator animator) {
                this.mIsCancel = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mIsCancel) {
                    InteractionJankMonitor.getInstance().cancel(i);
                } else {
                    InteractionJankMonitor.getInstance().end(i);
                }
            }

            public void onAnimationStart(Animator animator) {
                InteractionJankMonitor.getInstance().begin(KeyguardInputView.this, i);
            }
        };
    }

    public void setOnFinishImeAnimationRunnable(Runnable runnable) {
        this.mOnFinishImeAnimationRunnable = runnable;
    }

    public void runOnFinishImeAnimationRunnable() {
        Runnable runnable = this.mOnFinishImeAnimationRunnable;
        if (runnable != null) {
            runnable.run();
            this.mOnFinishImeAnimationRunnable = null;
        }
    }
}
