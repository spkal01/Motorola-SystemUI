package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class NotificationContentInflater$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ NotificationContentInflater f$0;
    public final /* synthetic */ ExpandableNotificationRow f$1;
    public final /* synthetic */ NotificationEntry f$2;

    public /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda5(NotificationContentInflater notificationContentInflater, ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry) {
        this.f$0 = notificationContentInflater;
        this.f$1 = expandableNotificationRow;
        this.f$2 = notificationEntry;
    }

    public final void run() {
        this.f$0.lambda$freeNotificationView$1(this.f$1, this.f$2);
    }
}
