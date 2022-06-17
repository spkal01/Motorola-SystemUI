package com.android.systemui.statusbar.notification.stack;

import java.util.function.BiConsumer;

/* renamed from: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController$$ExternalSyntheticLambda8 */
public final /* synthetic */ class C1691x60684b implements BiConsumer {
    public final /* synthetic */ NotificationRoundnessManager f$0;

    public /* synthetic */ C1691x60684b(NotificationRoundnessManager notificationRoundnessManager) {
        this.f$0 = notificationRoundnessManager;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.setExpanded(((Float) obj).floatValue(), ((Float) obj2).floatValue());
    }
}
