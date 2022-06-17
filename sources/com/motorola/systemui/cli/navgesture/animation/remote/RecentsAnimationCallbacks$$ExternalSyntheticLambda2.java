package com.motorola.systemui.cli.navgesture.animation.remote;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ RecentsAnimationCallbacks f$0;
    public final /* synthetic */ RecentsAnimationTargetSet f$1;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda2(RecentsAnimationCallbacks recentsAnimationCallbacks, RecentsAnimationTargetSet recentsAnimationTargetSet) {
        this.f$0 = recentsAnimationCallbacks;
        this.f$1 = recentsAnimationTargetSet;
    }

    public final void run() {
        this.f$0.lambda$onAnimationStart$0(this.f$1);
    }
}
