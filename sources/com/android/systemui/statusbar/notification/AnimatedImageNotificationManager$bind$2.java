package com.android.systemui.statusbar.notification;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ConversationNotifications.kt */
public final class AnimatedImageNotificationManager$bind$2 implements StatusBarStateController.StateListener {
    final /* synthetic */ AnimatedImageNotificationManager this$0;

    AnimatedImageNotificationManager$bind$2(AnimatedImageNotificationManager animatedImageNotificationManager) {
        this.this$0 = animatedImageNotificationManager;
    }

    public void onExpandedChanged(boolean z) {
        this.this$0.isStatusBarExpanded = z;
        List<NotificationEntry> activeNotificationsForCurrentUser = this.this$0.notificationEntryManager.getActiveNotificationsForCurrentUser();
        Intrinsics.checkNotNullExpressionValue(activeNotificationsForCurrentUser, "notificationEntryManager.activeNotificationsForCurrentUser");
        AnimatedImageNotificationManager animatedImageNotificationManager = this.this$0;
        for (NotificationEntry row : activeNotificationsForCurrentUser) {
            ExpandableNotificationRow row2 = row.getRow();
            if (row2 != null) {
                animatedImageNotificationManager.updateAnimatedImageDrawables(row2, z || row2.isHeadsUp());
            }
        }
    }
}
