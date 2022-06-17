package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class ExpandableNotificationRowController$$ExternalSyntheticLambda0 implements ExpandableNotificationRow.CoordinateOnClickListener {
    public final /* synthetic */ NotificationGutsManager f$0;

    public /* synthetic */ ExpandableNotificationRowController$$ExternalSyntheticLambda0(NotificationGutsManager notificationGutsManager) {
        this.f$0 = notificationGutsManager;
    }

    public final boolean onClick(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
        return this.f$0.openGuts(view, i, i2, menuItem);
    }
}
