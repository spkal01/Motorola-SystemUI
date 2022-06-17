package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.service.notification.NotificationListenerService;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotifEvent.kt */
public final class RankingUpdatedEvent extends NotifEvent {
    @NotNull
    private final NotificationListenerService.RankingMap rankingMap;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof RankingUpdatedEvent) && Intrinsics.areEqual((Object) this.rankingMap, (Object) ((RankingUpdatedEvent) obj).rankingMap);
    }

    public int hashCode() {
        return this.rankingMap.hashCode();
    }

    @NotNull
    public String toString() {
        return "RankingUpdatedEvent(rankingMap=" + this.rankingMap + ')';
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public RankingUpdatedEvent(@NotNull NotificationListenerService.RankingMap rankingMap2) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(rankingMap2, "rankingMap");
        this.rankingMap = rankingMap2;
    }

    public void dispatchToListener(@NotNull NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkNotNullParameter(notifCollectionListener, "listener");
        notifCollectionListener.onRankingUpdate(this.rankingMap);
    }
}
