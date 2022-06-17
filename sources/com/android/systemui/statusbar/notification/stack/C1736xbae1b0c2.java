package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C1736xbae1b0c2 implements GroupExpansionManager.OnGroupExpansionChangeListener {
    public final /* synthetic */ NotificationStackScrollLayoutController f$0;

    public /* synthetic */ C1736xbae1b0c2(NotificationStackScrollLayoutController notificationStackScrollLayoutController) {
        this.f$0 = notificationStackScrollLayoutController;
    }

    public final void onGroupExpansionChange(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.f$0.lambda$new$2(expandableNotificationRow, z);
    }
}
