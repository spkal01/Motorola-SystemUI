package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController$7$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1743x383f1792 implements Runnable {
    public final /* synthetic */ NotificationStackScrollLayoutController.C17267 f$0;
    public final /* synthetic */ ExpandableNotificationRow f$1;

    public /* synthetic */ C1743x383f1792(NotificationStackScrollLayoutController.C17267 r1, ExpandableNotificationRow expandableNotificationRow) {
        this.f$0 = r1;
        this.f$1 = expandableNotificationRow;
    }

    public final void run() {
        this.f$0.lambda$onHeadsUpUnPinned$0(this.f$1);
    }
}
