package com.motorola.systemui.cli.navgesture.animation.remote;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ RecentsAnimationCallbacks f$0;
    public final /* synthetic */ RecentsAnimationTargetSetController f$1;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda3(RecentsAnimationCallbacks recentsAnimationCallbacks, RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.f$0 = recentsAnimationCallbacks;
        this.f$1 = recentsAnimationTargetSetController;
    }

    public final void run() {
        this.f$0.lambda$onAnimationFinished$3(this.f$1);
    }
}
