package com.android.systemui.statusbar.notification.row;

import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.systemui.statusbar.notification.row.NotificationInfo;

public final /* synthetic */ class NotificationGutsManager$$ExternalSyntheticLambda4 implements NotificationInfo.OnAppSettingsClickListener {
    public final /* synthetic */ NotificationGutsManager f$0;
    public final /* synthetic */ NotificationGuts f$1;
    public final /* synthetic */ StatusBarNotification f$2;
    public final /* synthetic */ ExpandableNotificationRow f$3;

    public /* synthetic */ NotificationGutsManager$$ExternalSyntheticLambda4(NotificationGutsManager notificationGutsManager, NotificationGuts notificationGuts, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = notificationGutsManager;
        this.f$1 = notificationGuts;
        this.f$2 = statusBarNotification;
        this.f$3 = expandableNotificationRow;
    }

    public final void onClick(View view, Intent intent) {
        this.f$0.lambda$initializeNotificationInfo$2(this.f$1, this.f$2, this.f$3, view, intent);
    }
}
