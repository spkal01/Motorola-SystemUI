package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import com.android.systemui.animation.ActivityLaunchAnimator;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$1;
    public final /* synthetic */ PendingIntent f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda33(StatusBar statusBar, ActivityLaunchAnimator.Controller controller, PendingIntent pendingIntent, boolean z, boolean z2, Runnable runnable) {
        this.f$0 = statusBar;
        this.f$1 = controller;
        this.f$2 = pendingIntent;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = runnable;
    }

    public final void run() {
        this.f$0.lambda$startPendingIntentDismissingKeyguard$37(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
