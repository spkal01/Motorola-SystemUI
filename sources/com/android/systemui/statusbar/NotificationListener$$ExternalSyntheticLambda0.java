package com.android.systemui.statusbar;

import android.service.notification.NotificationListenerService;

public final /* synthetic */ class NotificationListener$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ NotificationListener f$0;
    public final /* synthetic */ NotificationListenerService.RankingMap f$1;

    public /* synthetic */ NotificationListener$$ExternalSyntheticLambda0(NotificationListener notificationListener, NotificationListenerService.RankingMap rankingMap) {
        this.f$0 = notificationListener;
        this.f$1 = rankingMap;
    }

    public final void run() {
        this.f$0.lambda$onNotificationRankingUpdate$3(this.f$1);
    }
}
