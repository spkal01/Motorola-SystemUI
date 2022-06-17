package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class StatusBarNotificationActivityStarter$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ StatusBarNotificationActivityStarter f$0;
    public final /* synthetic */ NotificationEntry f$1;
    public final /* synthetic */ NotificationEntry f$2;

    public /* synthetic */ StatusBarNotificationActivityStarter$$ExternalSyntheticLambda4(StatusBarNotificationActivityStarter statusBarNotificationActivityStarter, NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        this.f$0 = statusBarNotificationActivityStarter;
        this.f$1 = notificationEntry;
        this.f$2 = notificationEntry2;
    }

    public final void run() {
        this.f$0.lambda$handleNotificationClickAfterPanelCollapsed$2(this.f$1, this.f$2);
    }
}
