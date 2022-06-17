package com.android.systemui.animation;

import com.android.systemui.animation.ActivityLaunchAnimator;

/* compiled from: ActivityLaunchAnimator.kt */
final class ActivityLaunchAnimator$callOnIntentStartedOnMainThread$1 implements Runnable {
    final /* synthetic */ ActivityLaunchAnimator.Controller $this_callOnIntentStartedOnMainThread;
    final /* synthetic */ boolean $willAnimate;

    ActivityLaunchAnimator$callOnIntentStartedOnMainThread$1(ActivityLaunchAnimator.Controller controller, boolean z) {
        this.$this_callOnIntentStartedOnMainThread = controller;
        this.$willAnimate = z;
    }

    public final void run() {
        this.$this_callOnIntentStartedOnMainThread.onIntentStarted(this.$willAnimate);
    }
}
