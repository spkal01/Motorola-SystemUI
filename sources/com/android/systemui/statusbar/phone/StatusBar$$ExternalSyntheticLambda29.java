package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import com.android.systemui.animation.ActivityLaunchAnimator;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ PendingIntent f$1;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$2;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda29(StatusBar statusBar, PendingIntent pendingIntent, ActivityLaunchAnimator.Controller controller) {
        this.f$0 = statusBar;
        this.f$1 = pendingIntent;
        this.f$2 = controller;
    }

    public final void run() {
        this.f$0.lambda$postStartActivityDismissingKeyguard$28(this.f$1, this.f$2);
    }
}
