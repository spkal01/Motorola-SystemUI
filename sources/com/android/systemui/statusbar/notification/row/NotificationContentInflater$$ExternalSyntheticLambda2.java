package com.android.systemui.statusbar.notification.row;

import android.view.View;

public final /* synthetic */ class NotificationContentInflater$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ExpandableNotificationRow f$0;

    public /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda2(ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = expandableNotificationRow;
    }

    public final void run() {
        this.f$0.getCliRow().getPublicLayout().setContractedChild((View) null);
    }
}
