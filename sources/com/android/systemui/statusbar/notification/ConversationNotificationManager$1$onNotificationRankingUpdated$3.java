package com.android.systemui.statusbar.notification;

import com.android.internal.widget.ConversationLayout;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationNotifications.kt */
final class ConversationNotificationManager$1$onNotificationRankingUpdated$3 extends Lambda implements Function1<ConversationLayout, Boolean> {
    final /* synthetic */ boolean $important;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ConversationNotificationManager$1$onNotificationRankingUpdated$3(boolean z) {
        super(1);
        this.$important = z;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((ConversationLayout) obj));
    }

    public final boolean invoke(@NotNull ConversationLayout conversationLayout) {
        Intrinsics.checkNotNullParameter(conversationLayout, "it");
        return conversationLayout.isImportantConversation() == this.$important;
    }
}
