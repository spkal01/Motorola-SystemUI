package com.android.systemui.statusbar.notification.collection.coordinator;

import android.app.NotificationChannel;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationCoordinator.kt */
public final class ConversationCoordinator$notificationPromoter$1 extends NotifPromoter {
    ConversationCoordinator$notificationPromoter$1() {
        super("ConversationCoordinator");
    }

    public boolean shouldPromoteToTopLevel(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        NotificationChannel channel = notificationEntry.getChannel();
        return Intrinsics.areEqual((Object) channel == null ? null : Boolean.valueOf(channel.isImportantConversation()), (Object) Boolean.TRUE);
    }
}
