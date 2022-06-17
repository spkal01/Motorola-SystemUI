package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationNotifications.kt */
public final class AnimatedImageNotificationManager$bind$3 implements NotificationEntryListener {
    final /* synthetic */ AnimatedImageNotificationManager this$0;

    AnimatedImageNotificationManager$bind$3(AnimatedImageNotificationManager animatedImageNotificationManager) {
        this.this$0 = animatedImageNotificationManager;
    }

    public void onEntryInflated(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        ExpandableNotificationRow row = notificationEntry.getRow();
        if (row != null) {
            AnimatedImageNotificationManager animatedImageNotificationManager = this.this$0;
            animatedImageNotificationManager.updateAnimatedImageDrawables(row, animatedImageNotificationManager.isStatusBarExpanded || row.isHeadsUp());
        }
    }

    public void onEntryReinflated(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        onEntryInflated(notificationEntry);
    }
}
