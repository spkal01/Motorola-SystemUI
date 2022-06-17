package com.android.systemui.statusbar.notification.stack;

public final /* synthetic */ class CliNotificationStackClient$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CliNotificationStackClient f$0;

    public /* synthetic */ CliNotificationStackClient$$ExternalSyntheticLambda2(CliNotificationStackClient cliNotificationStackClient) {
        this.f$0 = cliNotificationStackClient;
    }

    public final void run() {
        this.f$0.internalConnectToCurrentUser();
    }
}
