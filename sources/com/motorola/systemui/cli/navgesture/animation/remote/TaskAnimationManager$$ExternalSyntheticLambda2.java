package com.motorola.systemui.cli.navgesture.animation.remote;

import android.content.Intent;

public final /* synthetic */ class TaskAnimationManager$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TaskAnimationManager f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ TaskAnimationManager$$ExternalSyntheticLambda2(TaskAnimationManager taskAnimationManager, Intent intent) {
        this.f$0 = taskAnimationManager;
        this.f$1 = intent;
    }

    public final void run() {
        this.f$0.lambda$startRecentsAnimation$1(this.f$1);
    }
}
