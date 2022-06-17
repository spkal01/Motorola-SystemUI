package com.motorola.systemui.cli.navgesture.animation.remote;

public final /* synthetic */ class TaskAnimationManager$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RecentsAnimationTargetSetController f$0;

    public /* synthetic */ TaskAnimationManager$$ExternalSyntheticLambda1(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.f$0 = recentsAnimationTargetSetController;
    }

    public final void run() {
        this.f$0.finishAnimationToHome();
    }
}
