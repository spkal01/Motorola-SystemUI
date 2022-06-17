package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;

public final /* synthetic */ class ExpandableNotificationRow$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ ExpandableNotificationRow f$0;
    public final /* synthetic */ NotificationMenuRowPlugin.MenuItem f$1;

    public /* synthetic */ ExpandableNotificationRow$$ExternalSyntheticLambda2(ExpandableNotificationRow expandableNotificationRow, NotificationMenuRowPlugin.MenuItem menuItem) {
        this.f$0 = expandableNotificationRow;
        this.f$1 = menuItem;
    }

    public final void onClick(View view) {
        this.f$0.lambda$getSnoozeClickListener$1(this.f$1, view);
    }
}
