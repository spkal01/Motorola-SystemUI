package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;

public final /* synthetic */ class StatusBarRemoteInputCallback$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ StatusBarRemoteInputCallback f$0;
    public final /* synthetic */ NotificationStackScrollLayout f$1;
    public final /* synthetic */ ExpandableNotificationRow f$2;

    public /* synthetic */ StatusBarRemoteInputCallback$$ExternalSyntheticLambda4(StatusBarRemoteInputCallback statusBarRemoteInputCallback, NotificationStackScrollLayout notificationStackScrollLayout, ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = statusBarRemoteInputCallback;
        this.f$1 = notificationStackScrollLayout;
        this.f$2 = expandableNotificationRow;
    }

    public final void run() {
        this.f$0.lambda$onWorkChallengeChanged$1(this.f$1, this.f$2);
    }
}
