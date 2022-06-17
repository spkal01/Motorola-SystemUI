package com.android.systemui.statusbar.phone;

import android.content.Intent;
import com.android.systemui.animation.ActivityLaunchAnimator;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ Intent f$1;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$2;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda31(StatusBar statusBar, Intent intent, ActivityLaunchAnimator.Controller controller) {
        this.f$0 = statusBar;
        this.f$1 = intent;
        this.f$2 = controller;
    }

    public final void run() {
        this.f$0.lambda$postStartActivityDismissingKeyguard$29(this.f$1, this.f$2);
    }
}
