package com.android.systemui.statusbar;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public final /* synthetic */ class NotificationListener$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ NotificationListener f$0;
    public final /* synthetic */ StatusBarNotification[] f$1;
    public final /* synthetic */ NotificationListenerService.RankingMap f$2;

    public /* synthetic */ NotificationListener$$ExternalSyntheticLambda4(NotificationListener notificationListener, StatusBarNotification[] statusBarNotificationArr, NotificationListenerService.RankingMap rankingMap) {
        this.f$0 = notificationListener;
        this.f$1 = statusBarNotificationArr;
        this.f$2 = rankingMap;
    }

    public final void run() {
        this.f$0.lambda$onListenerConnected$0(this.f$1, this.f$2);
    }
}
