package com.android.systemui.statusbar.notification.stack;

import android.util.ArrayMap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class CliNotificationAnimationUtils {
    private AnimationSet mBackgroundAnimation;
    private AnimationSet mCardAnimation;
    private AnimationSet mCarouselAnimation;
    private ArrayMap<View, Animation> mCurrentAnimation = new ArrayMap<>();
    private long mDuration = 150;
    private AnimationSet mKeyguardAnimation;
    private AnimationSet mStackAnimation;

    public void reset() {
        this.mCurrentAnimation.clear();
        this.mCardAnimation = new AnimationSet(true);
        this.mKeyguardAnimation = new AnimationSet(true);
        this.mBackgroundAnimation = new AnimationSet(true);
        this.mCarouselAnimation = new AnimationSet(true);
        this.mStackAnimation = new AnimationSet(true);
        this.mDuration = 150;
    }

    public void setTwice(boolean z) {
        if (z) {
            this.mDuration = 75;
        }
    }

    public Animation createCarouselAnimation(View view, boolean z) {
        AlphaAnimation alphaAnimation;
        TranslateAnimation translateAnimation;
        if (z) {
            alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) view.getHeight(), 0.0f);
        } else {
            alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) view.getHeight());
        }
        this.mCarouselAnimation.addAnimation(alphaAnimation);
        this.mCarouselAnimation.addAnimation(translateAnimation);
        this.mCurrentAnimation.put(view, this.mCarouselAnimation);
        this.mDuration = 150;
        return this.mCarouselAnimation;
    }

    public Animation CreateStackAnimation(View view, boolean z, float f, int i) {
        TranslateAnimation translateAnimation;
        if (z) {
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, f, 0.0f);
        } else {
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, f, (float) i);
        }
        if (z) {
            this.mDuration = (long) ((f / ((float) i)) * 500.0f);
        } else {
            float f2 = (float) i;
            this.mDuration = (long) (((f2 - f) / f2) * 500.0f);
        }
        if (this.mDuration == 0) {
            this.mDuration = 500;
        }
        this.mStackAnimation.addAnimation(translateAnimation);
        this.mCurrentAnimation.put(view, this.mStackAnimation);
        return this.mStackAnimation;
    }

    public Animation createCardAnimation(View view, float f, boolean z) {
        AlphaAnimation alphaAnimation;
        TranslateAnimation translateAnimation;
        if (z) {
            alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, f, 0.0f);
        } else {
            alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, f);
        }
        this.mCardAnimation.addAnimation(alphaAnimation);
        this.mCardAnimation.addAnimation(translateAnimation);
        this.mCurrentAnimation.put(view, this.mCardAnimation);
        return this.mCardAnimation;
    }

    public Animation createKeyguardAnimation(View view, boolean z) {
        AlphaAnimation alphaAnimation;
        ScaleAnimation scaleAnimation;
        if (z) {
            alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.5f);
        } else {
            alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        }
        this.mKeyguardAnimation.addAnimation(alphaAnimation);
        this.mKeyguardAnimation.addAnimation(scaleAnimation);
        this.mCurrentAnimation.put(view, this.mKeyguardAnimation);
        return this.mKeyguardAnimation;
    }

    public Animation createBackgroundAnimation(View view, boolean z, float f) {
        AlphaAnimation alphaAnimation;
        if (z) {
            alphaAnimation = new AlphaAnimation(f, 1.0f);
        } else {
            alphaAnimation = new AlphaAnimation(1.0f, f);
        }
        this.mBackgroundAnimation.addAnimation(alphaAnimation);
        this.mCurrentAnimation.put(view, alphaAnimation);
        return this.mBackgroundAnimation;
    }

    public void startAnimation() {
        for (View next : this.mCurrentAnimation.keySet()) {
            Animation animation = this.mCurrentAnimation.get(next);
            animation.setDuration(this.mDuration);
            next.startAnimation(animation);
        }
    }

    public void cancelAnimation() {
        for (View view : this.mCurrentAnimation.keySet()) {
            this.mCurrentAnimation.get(view).cancel();
        }
    }
}
