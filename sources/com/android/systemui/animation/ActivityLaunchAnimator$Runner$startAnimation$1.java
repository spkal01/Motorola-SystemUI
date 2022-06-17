package com.android.systemui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.ViewGroupOverlay;
import com.android.systemui.animation.ActivityLaunchAnimator;
import org.jetbrains.annotations.Nullable;

/* compiled from: ActivityLaunchAnimator.kt */
public final class ActivityLaunchAnimator$Runner$startAnimation$1 extends AnimatorListenerAdapter {
    final /* synthetic */ IRemoteAnimationFinishedCallback $iCallback;
    final /* synthetic */ boolean $isExpandingFullyAbove;
    final /* synthetic */ ViewGroupOverlay $launchContainerOverlay;
    final /* synthetic */ GradientDrawable $windowBackgroundLayer;
    final /* synthetic */ ActivityLaunchAnimator this$0;
    final /* synthetic */ ActivityLaunchAnimator.Runner this$1;

    ActivityLaunchAnimator$Runner$startAnimation$1(ActivityLaunchAnimator activityLaunchAnimator, ActivityLaunchAnimator.Runner runner, boolean z, ViewGroupOverlay viewGroupOverlay, GradientDrawable gradientDrawable, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
        this.this$0 = activityLaunchAnimator;
        this.this$1 = runner;
        this.$isExpandingFullyAbove = z;
        this.$launchContainerOverlay = viewGroupOverlay;
        this.$windowBackgroundLayer = gradientDrawable;
        this.$iCallback = iRemoteAnimationFinishedCallback;
    }

    public void onAnimationStart(@Nullable Animator animator, boolean z) {
        Log.d("ActivityLaunchAnimator", "Animation started");
        this.this$0.callback.setBlursDisabledForAppLaunch(true);
        this.this$1.controller.onLaunchAnimationStart(this.$isExpandingFullyAbove);
        this.$launchContainerOverlay.add(this.$windowBackgroundLayer);
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        Log.d("ActivityLaunchAnimator", "Animation ended");
        this.this$0.callback.setBlursDisabledForAppLaunch(false);
        IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback = this.$iCallback;
        if (iRemoteAnimationFinishedCallback != null) {
            this.this$1.invoke(iRemoteAnimationFinishedCallback);
        }
        this.this$1.controller.onLaunchAnimationEnd(this.$isExpandingFullyAbove);
        this.$launchContainerOverlay.remove(this.$windowBackgroundLayer);
    }
}
