package com.android.systemui.statusbar;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public final /* synthetic */ class NotificationListener$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ NotificationListener f$0;
    public final /* synthetic */ StatusBarNotification f$1;
    public final /* synthetic */ NotificationListenerService.RankingMap f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ NotificationListener$$ExternalSyntheticLambda2(NotificationListener notificationListener, StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
        this.f$0 = notificationListener;
        this.f$1 = statusBarNotification;
        this.f$2 = rankingMap;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$onNotificationRemoved$2(this.f$1, this.f$2, this.f$3);
    }
}
