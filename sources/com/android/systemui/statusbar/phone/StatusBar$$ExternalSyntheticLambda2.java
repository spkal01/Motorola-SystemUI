package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.view.RemoteAnimationAdapter;
import com.android.systemui.animation.ActivityLaunchAnimator;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda2 implements ActivityLaunchAnimator.PendingIntentStarter {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ PendingIntent f$1;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda2(StatusBar statusBar, PendingIntent pendingIntent) {
        this.f$0 = statusBar;
        this.f$1 = pendingIntent;
    }

    public final int startPendingIntent(RemoteAnimationAdapter remoteAnimationAdapter) {
        return this.f$0.lambda$startPendingIntentDismissingKeyguard$36(this.f$1, remoteAnimationAdapter);
    }
}
