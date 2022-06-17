package com.android.systemui.statusbar.notification.row;

import androidx.core.p002os.CancellationSignal;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.BindRequester;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;

public final /* synthetic */ class NotifBindPipeline$$ExternalSyntheticLambda1 implements BindRequester.BindRequestListener {
    public final /* synthetic */ NotifBindPipeline f$0;

    public /* synthetic */ NotifBindPipeline$$ExternalSyntheticLambda1(NotifBindPipeline notifBindPipeline) {
        this.f$0 = notifBindPipeline;
    }

    public final void onBindRequest(NotificationEntry notificationEntry, CancellationSignal cancellationSignal, NotifBindPipeline.BindCallback bindCallback) {
        this.f$0.onBindRequested(notificationEntry, cancellationSignal, bindCallback);
    }
}
