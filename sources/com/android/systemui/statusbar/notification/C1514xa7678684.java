package com.android.systemui.statusbar.notification;

import com.android.internal.widget.ConversationLayout;

/* renamed from: com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$4$1 */
/* compiled from: ConversationNotifications.kt */
final class C1514xa7678684 implements Runnable {
    final /* synthetic */ boolean $important;
    final /* synthetic */ ConversationLayout $layout;

    C1514xa7678684(ConversationLayout conversationLayout, boolean z) {
        this.$layout = conversationLayout;
        this.$important = z;
    }

    public final void run() {
        this.$layout.setIsImportantConversation(this.$important, true);
    }
}
