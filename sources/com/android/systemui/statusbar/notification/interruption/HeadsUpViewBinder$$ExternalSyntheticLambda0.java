package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;

public final /* synthetic */ class HeadsUpViewBinder$$ExternalSyntheticLambda0 implements NotifBindPipeline.BindCallback {
    public final /* synthetic */ RowContentBindParams f$0;
    public final /* synthetic */ NotifBindPipeline.BindCallback f$1;

    public /* synthetic */ HeadsUpViewBinder$$ExternalSyntheticLambda0(RowContentBindParams rowContentBindParams, NotifBindPipeline.BindCallback bindCallback) {
        this.f$0 = rowContentBindParams;
        this.f$1 = bindCallback;
    }

    public final void onBindFinished(NotificationEntry notificationEntry) {
        HeadsUpViewBinder.lambda$bindHeadsUpView$0(this.f$0, this.f$1, notificationEntry);
    }
}
