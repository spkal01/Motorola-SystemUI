package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import android.app.PendingIntent;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class DesktopNotificationActivityStarter$$ExternalSyntheticLambda0 implements ActivityStarter.OnDismissAction {
    public final /* synthetic */ DesktopNotificationActivityStarter f$0;
    public final /* synthetic */ NotificationEntry f$1;
    public final /* synthetic */ ExpandableNotificationRow f$2;
    public final /* synthetic */ RemoteInputController f$3;
    public final /* synthetic */ PendingIntent f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ DesktopNotificationActivityStarter$$ExternalSyntheticLambda0(DesktopNotificationActivityStarter desktopNotificationActivityStarter, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, RemoteInputController remoteInputController, PendingIntent pendingIntent, boolean z, boolean z2) {
        this.f$0 = desktopNotificationActivityStarter;
        this.f$1 = notificationEntry;
        this.f$2 = expandableNotificationRow;
        this.f$3 = remoteInputController;
        this.f$4 = pendingIntent;
        this.f$5 = z;
        this.f$6 = z2;
    }

    public final boolean onDismiss() {
        return this.f$0.lambda$onNotificationClicked$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
