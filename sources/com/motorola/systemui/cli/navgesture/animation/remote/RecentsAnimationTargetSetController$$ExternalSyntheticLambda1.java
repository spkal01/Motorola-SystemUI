package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.RecentsAnimationControllerCompat;

public final /* synthetic */ class RecentsAnimationTargetSetController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RecentsAnimationControllerCompat f$0;

    public /* synthetic */ RecentsAnimationTargetSetController$$ExternalSyntheticLambda1(RecentsAnimationControllerCompat recentsAnimationControllerCompat) {
        this.f$0 = recentsAnimationControllerCompat;
    }

    public final void run() {
        this.f$0.cleanupScreenshot();
    }
}
