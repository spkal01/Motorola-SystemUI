package com.android.systemui.statusbar;

public final /* synthetic */ class NotificationRemoteInputManager$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ NotificationRemoteInputManager f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ NotificationRemoteInputManager$$ExternalSyntheticLambda3(NotificationRemoteInputManager notificationRemoteInputManager, String str) {
        this.f$0 = notificationRemoteInputManager;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$releaseNotificationIfKeptForRemoteInputHistory$3(this.f$1);
    }
}
