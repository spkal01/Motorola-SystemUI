package com.android.systemui.statusbar;

import android.app.PendingIntent;
import android.view.View;
import android.widget.RemoteViews;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class NotificationRemoteInputManager$1$$ExternalSyntheticLambda0 implements NotificationRemoteInputManager.ClickHandler {
    public final /* synthetic */ NotificationRemoteInputManager.C14711 f$0;
    public final /* synthetic */ RemoteViews.RemoteResponse f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ PendingIntent f$3;
    public final /* synthetic */ NotificationEntry f$4;

    public /* synthetic */ NotificationRemoteInputManager$1$$ExternalSyntheticLambda0(NotificationRemoteInputManager.C14711 r1, RemoteViews.RemoteResponse remoteResponse, View view, PendingIntent pendingIntent, NotificationEntry notificationEntry) {
        this.f$0 = r1;
        this.f$1 = remoteResponse;
        this.f$2 = view;
        this.f$3 = pendingIntent;
        this.f$4 = notificationEntry;
    }

    public final boolean handleClick() {
        return this.f$0.lambda$onInteraction$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
