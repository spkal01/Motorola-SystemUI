package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.BindStage;

public final /* synthetic */ class NotifBindPipeline$$ExternalSyntheticLambda2 implements BindStage.StageCallback {
    public final /* synthetic */ NotifBindPipeline f$0;

    public /* synthetic */ NotifBindPipeline$$ExternalSyntheticLambda2(NotifBindPipeline notifBindPipeline) {
        this.f$0 = notifBindPipeline;
    }

    public final void onStageFinished(NotificationEntry notificationEntry) {
        this.f$0.lambda$startPipeline$1(notificationEntry);
    }
}
