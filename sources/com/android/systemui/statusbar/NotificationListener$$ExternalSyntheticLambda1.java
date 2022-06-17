package com.android.systemui.statusbar;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public final /* synthetic */ class NotificationListener$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ NotificationListener f$0;
    public final /* synthetic */ StatusBarNotification f$1;
    public final /* synthetic */ NotificationListenerService.RankingMap f$2;

    public /* synthetic */ NotificationListener$$ExternalSyntheticLambda1(NotificationListener notificationListener, StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        this.f$0 = notificationListener;
        this.f$1 = statusBarNotification;
        this.f$2 = rankingMap;
    }

    public final void run() {
        this.f$0.lambda$onNotificationPosted$1(this.f$1, this.f$2);
    }
}
