package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController$NotificationListContainerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1744xbe435293 implements Consumer {
    public final /* synthetic */ NotificationStackScrollLayoutController.NotificationListContainerImpl f$0;
    public final /* synthetic */ ExpandableNotificationRow f$1;

    public /* synthetic */ C1744xbe435293(NotificationStackScrollLayoutController.NotificationListContainerImpl notificationListContainerImpl, ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = notificationListContainerImpl;
        this.f$1 = expandableNotificationRow;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$bindRow$0(this.f$1, (Boolean) obj);
    }
}
