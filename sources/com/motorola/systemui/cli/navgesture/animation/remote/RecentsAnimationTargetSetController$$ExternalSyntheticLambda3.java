package com.motorola.systemui.cli.navgesture.animation.remote;

public final /* synthetic */ class RecentsAnimationTargetSetController$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ RecentsAnimationTargetSetController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ RecentsAnimationTargetSetController$$ExternalSyntheticLambda3(RecentsAnimationTargetSetController recentsAnimationTargetSetController, boolean z, boolean z2, Runnable runnable) {
        this.f$0 = recentsAnimationTargetSetController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$finishController$1(this.f$1, this.f$2, this.f$3);
    }
}
