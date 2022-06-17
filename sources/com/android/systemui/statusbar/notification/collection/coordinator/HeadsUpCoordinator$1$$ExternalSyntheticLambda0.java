package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;

public final /* synthetic */ class HeadsUpCoordinator$1$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ HeadsUpCoordinator f$0;

    public /* synthetic */ HeadsUpCoordinator$1$$ExternalSyntheticLambda0(HeadsUpCoordinator headsUpCoordinator) {
        this.f$0 = headsUpCoordinator;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        this.f$0.onHeadsUpViewBound(notificationEntry);
    }
}
