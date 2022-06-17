package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$activeConversationEntries$1 */
/* compiled from: ConversationNotifications.kt */
final class C1515xe0754237 extends Lambda implements Function1<String, NotificationEntry> {
    final /* synthetic */ ConversationNotificationManager this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C1515xe0754237(ConversationNotificationManager conversationNotificationManager) {
        super(1);
        this.this$0 = conversationNotificationManager;
    }

    @Nullable
    public final NotificationEntry invoke(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "it");
        return this.this$0.notificationEntryManager.getActiveNotificationUnfiltered(str);
    }
}
