package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.statusbar.notification.ConversationNotificationManager$onNotificationPanelExpandStateChanged$2 */
/* compiled from: ConversationNotifications.kt */
final class C1517x5e24d3c0 extends Lambda implements Function1<NotificationEntry, ExpandableNotificationRow> {
    public static final C1517x5e24d3c0 INSTANCE = new C1517x5e24d3c0();

    C1517x5e24d3c0() {
        super(1);
    }

    @Nullable
    public final ExpandableNotificationRow invoke(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "it");
        return notificationEntry.getRow();
    }
}
