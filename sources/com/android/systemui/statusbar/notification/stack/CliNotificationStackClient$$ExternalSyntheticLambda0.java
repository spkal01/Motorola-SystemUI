package com.android.systemui.statusbar.notification.stack;

import android.os.IBinder;

public final /* synthetic */ class CliNotificationStackClient$$ExternalSyntheticLambda0 implements IBinder.DeathRecipient {
    public final /* synthetic */ CliNotificationStackClient f$0;

    public /* synthetic */ CliNotificationStackClient$$ExternalSyntheticLambda0(CliNotificationStackClient cliNotificationStackClient) {
        this.f$0 = cliNotificationStackClient;
    }

    public final void binderDied() {
        this.f$0.cleanupAfterDeath();
    }
}
