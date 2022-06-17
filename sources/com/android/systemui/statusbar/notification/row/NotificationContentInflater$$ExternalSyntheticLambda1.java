package com.android.systemui.statusbar.notification.row;

import android.view.View;

public final /* synthetic */ class NotificationContentInflater$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ExpandableNotificationRow f$0;

    public /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda1(ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = expandableNotificationRow;
    }

    public final void run() {
        this.f$0.getCliRow().getPrivateLayout().setContractedChild((View) null);
    }
}
