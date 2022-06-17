package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;

public final /* synthetic */ class HeadsUpController$1$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ HeadsUpController f$0;

    public /* synthetic */ HeadsUpController$1$$ExternalSyntheticLambda0(HeadsUpController headsUpController) {
        this.f$0 = headsUpController;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        this.f$0.showAlertingView(notificationEntry);
    }
}
