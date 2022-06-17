package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public final /* synthetic */ class HeadsUpController$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ HeadsUpManager f$0;

    public /* synthetic */ HeadsUpController$$ExternalSyntheticLambda0(HeadsUpManager headsUpManager) {
        this.f$0 = headsUpManager;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        this.f$0.showNotification(notificationEntry);
    }
}
