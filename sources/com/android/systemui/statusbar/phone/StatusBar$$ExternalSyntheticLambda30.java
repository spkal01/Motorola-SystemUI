package com.android.systemui.statusbar.phone;

import android.content.Intent;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda30 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ Intent f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ ActivityStarter.Callback f$6;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda30(StatusBar statusBar, Intent intent, int i, ActivityLaunchAnimator.Controller controller, boolean z, boolean z2, ActivityStarter.Callback callback) {
        this.f$0 = statusBar;
        this.f$1 = intent;
        this.f$2 = i;
        this.f$3 = controller;
        this.f$4 = z;
        this.f$5 = z2;
        this.f$6 = callback;
    }

    public final void run() {
        this.f$0.lambda$startActivityDismissingKeyguard$21(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
