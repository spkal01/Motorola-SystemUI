package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;

public final /* synthetic */ class LowPriorityInflationHelper$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ ExpandableNotificationRow f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ LowPriorityInflationHelper$$ExternalSyntheticLambda0(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.f$0 = expandableNotificationRow;
        this.f$1 = z;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        this.f$0.setIsLowPriority(this.f$1);
    }
}
