package com.android.systemui.statusbar.notification;

import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public interface NotificationActivityStarter {
    boolean isCollapsingToShowActivityOverLockscreen() {
        return false;
    }

    void onNotificationClicked(StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow);

    void startHistoryIntent(View view, boolean z);

    void startNotificationGutsIntent(Intent intent, int i, ExpandableNotificationRow expandableNotificationRow);
}
