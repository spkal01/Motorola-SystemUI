package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;

public final /* synthetic */ class NotificationRowBinderImpl$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ ExpandableNotificationRow f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ NotificationRowContentBinder.InflationCallback f$3;

    public /* synthetic */ NotificationRowBinderImpl$$ExternalSyntheticLambda0(ExpandableNotificationRow expandableNotificationRow, boolean z, boolean z2, NotificationRowContentBinder.InflationCallback inflationCallback) {
        this.f$0 = expandableNotificationRow;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = inflationCallback;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        NotificationRowBinderImpl.lambda$inflateContentViews$1(this.f$0, this.f$1, this.f$2, this.f$3, notificationEntry);
    }
}
