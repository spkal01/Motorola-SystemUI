package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class NotificationClicker$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ExpandableNotificationRow f$0;

    public /* synthetic */ NotificationClicker$$ExternalSyntheticLambda0(ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = expandableNotificationRow;
    }

    public final void run() {
        this.f$0.setJustClicked(false);
    }
}
