package com.android.systemui.animation;

import android.view.IRemoteAnimationFinishedCallback;
import android.view.RemoteAnimationTarget;
import com.android.systemui.animation.ActivityLaunchAnimator;

/* compiled from: ActivityLaunchAnimator.kt */
final class ActivityLaunchAnimator$Runner$onAnimationStart$1 implements Runnable {
    final /* synthetic */ RemoteAnimationTarget[] $apps;
    final /* synthetic */ IRemoteAnimationFinishedCallback $iCallback;
    final /* synthetic */ RemoteAnimationTarget[] $nonApps;
    final /* synthetic */ ActivityLaunchAnimator.Runner this$0;

    ActivityLaunchAnimator$Runner$onAnimationStart$1(ActivityLaunchAnimator.Runner runner, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
        this.this$0 = runner;
        this.$apps = remoteAnimationTargetArr;
        this.$nonApps = remoteAnimationTargetArr2;
        this.$iCallback = iRemoteAnimationFinishedCallback;
    }

    public final void run() {
        this.this$0.startAnimation(this.$apps, this.$nonApps, this.$iCallback);
    }
}
