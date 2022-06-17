package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class DesktopNotificationActivityStarter$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DesktopNotificationActivityStarter f$0;
    public final /* synthetic */ NotificationEntry f$1;
    public final /* synthetic */ NotificationEntry f$2;

    public /* synthetic */ DesktopNotificationActivityStarter$$ExternalSyntheticLambda1(DesktopNotificationActivityStarter desktopNotificationActivityStarter, NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        this.f$0 = desktopNotificationActivityStarter;
        this.f$1 = notificationEntry;
        this.f$2 = notificationEntry2;
    }

    public final void run() {
        this.f$0.lambda$handleNotificationClickAfterPanelCollapsed$2(this.f$1, this.f$2);
    }
}
