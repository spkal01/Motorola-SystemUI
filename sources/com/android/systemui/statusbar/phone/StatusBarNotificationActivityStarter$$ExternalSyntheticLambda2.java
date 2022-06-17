package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class StatusBarNotificationActivityStarter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ StatusBarNotificationActivityStarter f$0;
    public final /* synthetic */ NotificationEntry f$1;

    public /* synthetic */ StatusBarNotificationActivityStarter$$ExternalSyntheticLambda2(StatusBarNotificationActivityStarter statusBarNotificationActivityStarter, NotificationEntry notificationEntry) {
        this.f$0 = statusBarNotificationActivityStarter;
        this.f$1 = notificationEntry;
    }

    public final void run() {
        this.f$0.lambda$expandBubbleStackOnMainThread$3(this.f$1);
    }
}
