package com.android.systemui.statusbar.notification.row;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.NotificationGuts;

public final /* synthetic */ class NotificationGutsManager$$ExternalSyntheticLambda2 implements NotificationGuts.OnGutsClosedListener {
    public final /* synthetic */ NotificationGutsManager f$0;
    public final /* synthetic */ ExpandableNotificationRow f$1;
    public final /* synthetic */ StatusBarNotification f$2;

    public /* synthetic */ NotificationGutsManager$$ExternalSyntheticLambda2(NotificationGutsManager notificationGutsManager, ExpandableNotificationRow expandableNotificationRow, StatusBarNotification statusBarNotification) {
        this.f$0 = notificationGutsManager;
        this.f$1 = expandableNotificationRow;
        this.f$2 = statusBarNotification;
    }

    public final void onGutsClosed(NotificationGuts notificationGuts) {
        this.f$0.lambda$bindGuts$0(this.f$1, this.f$2, notificationGuts);
    }
}
