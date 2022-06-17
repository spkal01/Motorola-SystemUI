package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;

public final /* synthetic */ class NotificationGroupAlertTransferHelper$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ NotificationGroupAlertTransferHelper f$0;
    public final /* synthetic */ NotificationEntry f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ NotificationGroupAlertTransferHelper$$ExternalSyntheticLambda0(NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper, NotificationEntry notificationEntry, int i) {
        this.f$0 = notificationGroupAlertTransferHelper;
        this.f$1 = notificationEntry;
        this.f$2 = i;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        this.f$0.lambda$alertNotificationWhenPossible$0(this.f$1, this.f$2, notificationEntry);
    }
}
