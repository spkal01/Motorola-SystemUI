package com.android.systemui.statusbar.notification.collection;

import android.service.notification.StatusBarNotification;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationRankingManager.kt */
final class NotificationRankingManager$rankingComparator$1 implements Comparator<NotificationEntry> {
    final /* synthetic */ NotificationRankingManager this$0;

    NotificationRankingManager$rankingComparator$1(NotificationRankingManager notificationRankingManager) {
        this.this$0 = notificationRankingManager;
    }

    public final int compare(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        NotificationEntry notificationEntry3 = notificationEntry;
        NotificationEntry notificationEntry4 = notificationEntry2;
        StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkNotNullExpressionValue(sbn, "a.sbn");
        StatusBarNotification sbn2 = notificationEntry2.getSbn();
        Intrinsics.checkNotNullExpressionValue(sbn2, "b.sbn");
        int rank = notificationEntry.getRanking().getRank();
        int rank2 = notificationEntry2.getRanking().getRank();
        Intrinsics.checkNotNullExpressionValue(notificationEntry3, "a");
        boolean access$isColorizedForegroundService = NotificationRankingManagerKt.isColorizedForegroundService(notificationEntry);
        Intrinsics.checkNotNullExpressionValue(notificationEntry4, "b");
        boolean access$isColorizedForegroundService2 = NotificationRankingManagerKt.isColorizedForegroundService(notificationEntry2);
        boolean access$isImportantCall = NotificationRankingManagerKt.isImportantCall(notificationEntry);
        boolean access$isImportantCall2 = NotificationRankingManagerKt.isImportantCall(notificationEntry2);
        int access$getPeopleNotificationType = this.this$0.getPeopleNotificationType(notificationEntry3);
        int access$getPeopleNotificationType2 = this.this$0.getPeopleNotificationType(notificationEntry4);
        boolean access$isImportantMedia = this.this$0.isImportantMedia(notificationEntry3);
        boolean access$isImportantMedia2 = this.this$0.isImportantMedia(notificationEntry4);
        boolean access$isSystemMax = NotificationRankingManagerKt.isSystemMax(notificationEntry);
        StatusBarNotification statusBarNotification = sbn;
        boolean access$isSystemMax2 = NotificationRankingManagerKt.isSystemMax(notificationEntry2);
        StatusBarNotification statusBarNotification2 = sbn2;
        boolean isRowHeadsUp = notificationEntry.isRowHeadsUp();
        int i = rank;
        boolean isRowHeadsUp2 = notificationEntry2.isRowHeadsUp();
        int i2 = rank2;
        boolean access$isHighPriority = this.this$0.isHighPriority(notificationEntry3);
        boolean access$isHighPriority2 = this.this$0.isHighPriority(notificationEntry4);
        if (isRowHeadsUp != isRowHeadsUp2) {
            if (!isRowHeadsUp) {
                return 1;
            }
        } else if (isRowHeadsUp) {
            return this.this$0.headsUpManager.compare(notificationEntry3, notificationEntry4);
        } else {
            if (access$isColorizedForegroundService != access$isColorizedForegroundService2) {
                if (!access$isColorizedForegroundService) {
                    return 1;
                }
            } else if (access$isImportantCall != access$isImportantCall2) {
                if (!access$isImportantCall) {
                    return 1;
                }
            } else if (this.this$0.getUsePeopleFiltering() && access$getPeopleNotificationType != access$getPeopleNotificationType2) {
                return this.this$0.peopleNotificationIdentifier.compareTo(access$getPeopleNotificationType, access$getPeopleNotificationType2);
            } else {
                if (access$isImportantMedia != access$isImportantMedia2) {
                    if (!access$isImportantMedia) {
                        return 1;
                    }
                } else if (access$isSystemMax == access$isSystemMax2) {
                    boolean z = access$isHighPriority;
                    if (z != access$isHighPriority2) {
                        return Intrinsics.compare(z ? 1 : 0, access$isHighPriority2 ? 1 : 0) * -1;
                    }
                    int i3 = i;
                    int i4 = i2;
                    if (i3 != i4) {
                        return i3 - i4;
                    }
                    return Intrinsics.compare(statusBarNotification2.getNotification().when, statusBarNotification.getNotification().when);
                } else if (!access$isSystemMax) {
                    return 1;
                }
            }
        }
        return -1;
    }
}
