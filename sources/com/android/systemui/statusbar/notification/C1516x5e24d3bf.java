package com.android.systemui.statusbar.notification;

import android.app.Notification;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;
import java.util.function.BiFunction;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.notification.ConversationNotificationManager$onNotificationPanelExpandStateChanged$1 */
/* compiled from: ConversationNotifications.kt */
final class C1516x5e24d3bf implements BiFunction<String, ConversationNotificationManager.ConversationState, ConversationNotificationManager.ConversationState> {
    final /* synthetic */ Map<String, NotificationEntry> $expanded;

    C1516x5e24d3bf(Map<String, NotificationEntry> map) {
        this.$expanded = map;
    }

    @NotNull
    public final ConversationNotificationManager.ConversationState apply(@NotNull String str, @NotNull ConversationNotificationManager.ConversationState conversationState) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(conversationState, "state");
        return this.$expanded.containsKey(str) ? ConversationNotificationManager.ConversationState.copy$default(conversationState, 0, (Notification) null, 2, (Object) null) : conversationState;
    }
}
