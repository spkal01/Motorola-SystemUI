package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RecentsAnimationCallbacks f$0;
    public final /* synthetic */ RemoteAnimationTargetCompat f$1;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda1(RecentsAnimationCallbacks recentsAnimationCallbacks, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.f$0 = recentsAnimationCallbacks;
        this.f$1 = remoteAnimationTargetCompat;
    }

    public final void run() {
        this.f$0.lambda$onTaskAppeared$2(this.f$1);
    }
}
