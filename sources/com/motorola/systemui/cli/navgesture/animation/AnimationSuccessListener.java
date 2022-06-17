package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public abstract class AnimationSuccessListener extends AnimatorListenerAdapter {
    protected boolean mCancelled = false;

    public abstract void onAnimationSuccess(Animator animator);

    public void onAnimationCancel(Animator animator) {
        this.mCancelled = true;
    }

    public void onAnimationEnd(Animator animator) {
        if (!this.mCancelled) {
            onAnimationSuccess(animator);
        }
    }
}
