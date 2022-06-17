package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import com.motorola.systemui.cli.navgesture.Interpolators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AnimatorPlaybackController implements ValueAnimator.AnimatorUpdateListener {
    protected final AnimatorSet mAnim;
    private final ValueAnimator mAnimationPlayer;
    protected float mCurrentFraction;
    private final long mDuration;
    /* access modifiers changed from: private */
    public Runnable mEndAction;
    private OnAnimationEndDispatcher mEndListener;
    protected Runnable mOnCancelRunnable;
    private boolean mSkipToEnd = false;
    protected boolean mTargetCancelled = false;

    public abstract void setPlayFraction(float f);

    public static AnimatorPlaybackController wrap(AnimatorSet animatorSet, long j) {
        return wrap(animatorSet, j, (Runnable) null);
    }

    public static AnimatorPlaybackController wrap(AnimatorSet animatorSet, long j, Runnable runnable) {
        return new AnimatorPlaybackControllerVL(animatorSet, j, runnable);
    }

    protected AnimatorPlaybackController(AnimatorSet animatorSet, long j, Runnable runnable) {
        this.mAnim = animatorSet;
        this.mDuration = j;
        this.mOnCancelRunnable = runnable;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mAnimationPlayer = ofFloat;
        ofFloat.setInterpolator(Interpolators.LINEAR);
        OnAnimationEndDispatcher onAnimationEndDispatcher = new OnAnimationEndDispatcher();
        this.mEndListener = onAnimationEndDispatcher;
        ofFloat.addListener(onAnimationEndDispatcher);
        ofFloat.addUpdateListener(this);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                AnimatorPlaybackController animatorPlaybackController = AnimatorPlaybackController.this;
                animatorPlaybackController.mTargetCancelled = true;
                Runnable runnable = animatorPlaybackController.mOnCancelRunnable;
                if (runnable != null) {
                    runnable.run();
                    AnimatorPlaybackController.this.mOnCancelRunnable = null;
                }
            }

            public void onAnimationEnd(Animator animator) {
                AnimatorPlaybackController animatorPlaybackController = AnimatorPlaybackController.this;
                animatorPlaybackController.mTargetCancelled = false;
                animatorPlaybackController.mOnCancelRunnable = null;
            }

            public void onAnimationStart(Animator animator) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
            }
        });
    }

    public AnimatorSet getTarget() {
        return this.mAnim;
    }

    public TimeInterpolator getInterpolator() {
        return this.mAnim.getInterpolator() != null ? this.mAnim.getInterpolator() : Interpolators.LINEAR;
    }

    public ValueAnimator getAnimationPlayer() {
        return this.mAnimationPlayer;
    }

    public float getInterpolatedProgress() {
        return getInterpolator().getInterpolation(this.mCurrentFraction);
    }

    public void setEndAction(Runnable runnable) {
        this.mEndAction = runnable;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public long clampDuration(float f) {
        long j = this.mDuration;
        float f2 = ((float) j) * f;
        if (f2 <= 0.0f) {
            return 0;
        }
        return Math.min((long) f2, j);
    }

    public void dispatchOnStart() {
        dispatchOnStartRecursively(this.mAnim);
    }

    private void dispatchOnStartRecursively(Animator animator) {
        for (T onAnimationStart : nonNullList(animator.getListeners())) {
            onAnimationStart.onAnimationStart(animator);
        }
        if (animator instanceof AnimatorSet) {
            for (T dispatchOnStartRecursively : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                dispatchOnStartRecursively(dispatchOnStartRecursively);
            }
        }
    }

    public void dispatchOnCancel() {
        dispatchOnCancelRecursively(this.mAnim);
    }

    private void dispatchOnCancelRecursively(Animator animator) {
        for (T onAnimationCancel : nonNullList(animator.getListeners())) {
            onAnimationCancel.onAnimationCancel(animator);
        }
        if (animator instanceof AnimatorSet) {
            for (T dispatchOnCancelRecursively : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                dispatchOnCancelRecursively(dispatchOnCancelRecursively);
            }
        }
    }

    public void dispatchSetInterpolator(TimeInterpolator timeInterpolator) {
        dispatchSetInterpolatorRecursively(this.mAnim, timeInterpolator);
    }

    private void dispatchSetInterpolatorRecursively(Animator animator, TimeInterpolator timeInterpolator) {
        animator.setInterpolator(timeInterpolator);
        if (animator instanceof AnimatorSet) {
            for (T dispatchSetInterpolatorRecursively : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                dispatchSetInterpolatorRecursively(dispatchSetInterpolatorRecursively, timeInterpolator);
            }
        }
    }

    public static class AnimatorPlaybackControllerVL extends AnimatorPlaybackController {
        private final ValueAnimator[] mChildAnimations;

        private AnimatorPlaybackControllerVL(AnimatorSet animatorSet, long j, Runnable runnable) {
            super(animatorSet, j, runnable);
            ArrayList arrayList = new ArrayList();
            getAnimationsRecur(this.mAnim, arrayList);
            this.mChildAnimations = (ValueAnimator[]) arrayList.toArray(new ValueAnimator[arrayList.size()]);
        }

        private void getAnimationsRecur(AnimatorSet animatorSet, ArrayList<ValueAnimator> arrayList) {
            long duration = animatorSet.getDuration();
            TimeInterpolator interpolator = animatorSet.getInterpolator();
            Iterator<Animator> it = animatorSet.getChildAnimations().iterator();
            while (it.hasNext()) {
                Animator next = it.next();
                if (duration > 0) {
                    next.setDuration(duration);
                }
                if (interpolator != null) {
                    next.setInterpolator(interpolator);
                }
                if (next instanceof ValueAnimator) {
                    arrayList.add((ValueAnimator) next);
                } else if (next instanceof AnimatorSet) {
                    getAnimationsRecur((AnimatorSet) next, arrayList);
                } else {
                    throw new RuntimeException("Unknown animation type " + next);
                }
            }
        }

        public void setPlayFraction(float f) {
            this.mCurrentFraction = f;
            if (!this.mTargetCancelled) {
                long clampDuration = clampDuration(f);
                for (ValueAnimator valueAnimator : this.mChildAnimations) {
                    valueAnimator.setCurrentPlayTime(Math.min(clampDuration, valueAnimator.getDuration()));
                }
            }
        }
    }

    private class OnAnimationEndDispatcher extends AnimationSuccessListener {
        boolean mDispatched;

        private OnAnimationEndDispatcher() {
            this.mDispatched = false;
        }

        public void onAnimationStart(Animator animator) {
            this.mCancelled = false;
            this.mDispatched = false;
        }

        public void onAnimationSuccess(Animator animator) {
            if (!this.mDispatched) {
                dispatchOnEndRecursively(AnimatorPlaybackController.this.mAnim);
                if (AnimatorPlaybackController.this.mEndAction != null) {
                    AnimatorPlaybackController.this.mEndAction.run();
                }
                this.mDispatched = true;
            }
        }

        private void dispatchOnEndRecursively(Animator animator) {
            for (Animator.AnimatorListener onAnimationEnd : AnimatorPlaybackController.nonNullList(animator.getListeners())) {
                onAnimationEnd.onAnimationEnd(animator);
            }
            if (animator instanceof AnimatorSet) {
                for (Animator dispatchOnEndRecursively : AnimatorPlaybackController.nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                    dispatchOnEndRecursively(dispatchOnEndRecursively);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static <T> List<T> nonNullList(ArrayList<T> arrayList) {
        return arrayList == null ? Collections.emptyList() : arrayList;
    }
}
