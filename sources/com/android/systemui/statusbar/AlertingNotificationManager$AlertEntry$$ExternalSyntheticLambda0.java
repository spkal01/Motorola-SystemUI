package com.android.systemui.statusbar;

import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class AlertingNotificationManager$AlertEntry$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AlertingNotificationManager.AlertEntry f$0;
    public final /* synthetic */ NotificationEntry f$1;

    public /* synthetic */ AlertingNotificationManager$AlertEntry$$ExternalSyntheticLambda0(AlertingNotificationManager.AlertEntry alertEntry, NotificationEntry notificationEntry) {
        this.f$0 = alertEntry;
        this.f$1 = notificationEntry;
    }

    public final void run() {
        this.f$0.lambda$setEntry$0(this.f$1);
    }
}
