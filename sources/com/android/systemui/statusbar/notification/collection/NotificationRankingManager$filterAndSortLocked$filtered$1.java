package com.android.systemui.statusbar.notification.collection;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationRankingManager.kt */
/* synthetic */ class NotificationRankingManager$filterAndSortLocked$filtered$1 extends FunctionReferenceImpl implements Function1<NotificationEntry, Boolean> {
    NotificationRankingManager$filterAndSortLocked$filtered$1(NotificationRankingManager notificationRankingManager) {
        super(1, notificationRankingManager, NotificationRankingManager.class, "filter", "filter(Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;)Z", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((NotificationEntry) obj));
    }

    public final boolean invoke(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "p0");
        return ((NotificationRankingManager) this.receiver).filter(notificationEntry);
    }
}
