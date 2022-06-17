package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.statusbar.notification.ConversationNotificationManager$onNotificationPanelExpandStateChanged$expanded$1 */
/* compiled from: ConversationNotifications.kt */
final class C1518x7388b338 extends Lambda implements Function1<Map.Entry<? extends String, ? extends ConversationNotificationManager.ConversationState>, Pair<? extends String, ? extends NotificationEntry>> {
    final /* synthetic */ ConversationNotificationManager this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C1518x7388b338(ConversationNotificationManager conversationNotificationManager) {
        super(1);
        this.this$0 = conversationNotificationManager;
    }

    @Nullable
    public final Pair<String, NotificationEntry> invoke(@NotNull Map.Entry<String, ConversationNotificationManager.ConversationState> entry) {
        Intrinsics.checkNotNullParameter(entry, "$dstr$key$_u24__u24");
        String key = entry.getKey();
        NotificationEntry activeNotificationUnfiltered = this.this$0.notificationEntryManager.getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered == null) {
            return null;
        }
        ExpandableNotificationRow row = activeNotificationUnfiltered.getRow();
        if (Intrinsics.areEqual((Object) row == null ? null : Boolean.valueOf(row.isExpanded()), (Object) Boolean.TRUE)) {
            return TuplesKt.m104to(key, activeNotificationUnfiltered);
        }
        return null;
    }
}
