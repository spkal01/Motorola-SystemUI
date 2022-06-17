package com.android.systemui.statusbar.notification.collection.legacy;

import android.service.notification.NotificationListenerService;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: LegacyNotificationRanker.kt */
public interface LegacyNotificationRanker {
    @Nullable
    NotificationListenerService.RankingMap getRankingMap();

    boolean isNotificationForCurrentProfiles(@NotNull NotificationEntry notificationEntry);

    @NotNull
    List<NotificationEntry> updateRanking(@Nullable NotificationListenerService.RankingMap rankingMap, @NotNull Collection<NotificationEntry> collection, @NotNull String str);
}
