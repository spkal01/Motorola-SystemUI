package com.android.systemui.statusbar.notification.collection.legacy;

import android.service.notification.NotificationListenerService;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class LegacyNotificationRankerStub implements LegacyNotificationRanker {
    private final Comparator<NotificationEntry> mEntryComparator = Comparator.comparingLong(LegacyNotificationRankerStub$$ExternalSyntheticLambda0.INSTANCE);
    private NotificationListenerService.RankingMap mRankingMap = new NotificationListenerService.RankingMap(new NotificationListenerService.Ranking[0]);

    public boolean isNotificationForCurrentProfiles(NotificationEntry notificationEntry) {
        return true;
    }

    public List<NotificationEntry> updateRanking(NotificationListenerService.RankingMap rankingMap, Collection<NotificationEntry> collection, String str) {
        if (rankingMap != null) {
            this.mRankingMap = rankingMap;
        }
        ArrayList arrayList = new ArrayList(collection);
        arrayList.sort(this.mEntryComparator);
        return arrayList;
    }

    public NotificationListenerService.RankingMap getRankingMap() {
        return this.mRankingMap;
    }
}
