package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import java.util.function.Consumer;

public final /* synthetic */ class NotificationPanelViewController$$ExternalSyntheticLambda12 implements Consumer {
    public final /* synthetic */ NotificationStackScrollLayoutController f$0;

    public /* synthetic */ NotificationPanelViewController$$ExternalSyntheticLambda12(NotificationStackScrollLayoutController notificationStackScrollLayoutController) {
        this.f$0 = notificationStackScrollLayoutController;
    }

    public final void accept(Object obj) {
        this.f$0.setTrackingHeadsUp((ExpandableNotificationRow) obj);
    }
}
