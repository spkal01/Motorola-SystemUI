package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

/* renamed from: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C1686x606846 implements GroupExpansionManager.OnGroupExpansionChangeListener {
    public final /* synthetic */ DesktopNotificationStackScrollLayoutController f$0;

    public /* synthetic */ C1686x606846(DesktopNotificationStackScrollLayoutController desktopNotificationStackScrollLayoutController) {
        this.f$0 = desktopNotificationStackScrollLayoutController;
    }

    public final void onGroupExpansionChange(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.f$0.lambda$new$2(expandableNotificationRow, z);
    }
}
