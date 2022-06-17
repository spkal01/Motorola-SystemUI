package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class ExpandableNotificationRowController$$ExternalSyntheticLambda2 implements ExpandableNotificationRow.LongPressListener {
    public final /* synthetic */ ExpandableNotificationRowController f$0;

    public /* synthetic */ ExpandableNotificationRowController$$ExternalSyntheticLambda2(ExpandableNotificationRowController expandableNotificationRowController) {
        this.f$0 = expandableNotificationRowController;
    }

    public final boolean onLongPress(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
        return this.f$0.lambda$init$0(view, i, i2, menuItem);
    }
}
