package com.android.systemui.statusbar;

import android.app.PendingIntent;
import android.view.View;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class NotificationRemoteInputManager$$ExternalSyntheticLambda0 implements NotificationRemoteInputManager.BouncerChecker {
    public final /* synthetic */ NotificationRemoteInputManager f$0;
    public final /* synthetic */ NotificationRemoteInputManager.AuthBypassPredicate f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ PendingIntent f$3;
    public final /* synthetic */ ExpandableNotificationRow f$4;

    public /* synthetic */ NotificationRemoteInputManager$$ExternalSyntheticLambda0(NotificationRemoteInputManager notificationRemoteInputManager, NotificationRemoteInputManager.AuthBypassPredicate authBypassPredicate, View view, PendingIntent pendingIntent, ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = notificationRemoteInputManager;
        this.f$1 = authBypassPredicate;
        this.f$2 = view;
        this.f$3 = pendingIntent;
        this.f$4 = expandableNotificationRow;
    }

    public final boolean showBouncerIfNecessary() {
        return this.f$0.lambda$activateRemoteInput$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
