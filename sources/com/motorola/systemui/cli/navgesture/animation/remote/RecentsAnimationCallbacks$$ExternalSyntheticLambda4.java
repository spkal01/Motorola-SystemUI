package com.motorola.systemui.cli.navgesture.animation.remote;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ RecentsAnimationTargetSetController f$0;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda4(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.f$0 = recentsAnimationTargetSetController;
    }

    public final void run() {
        this.f$0.finishAnimationToApp();
    }
}
