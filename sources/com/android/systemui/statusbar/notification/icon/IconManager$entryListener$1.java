package com.android.systemui.statusbar.notification.icon;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: IconManager.kt */
public final class IconManager$entryListener$1 implements NotifCollectionListener {
    final /* synthetic */ IconManager this$0;

    IconManager$entryListener$1(IconManager iconManager) {
        this.this$0 = iconManager;
    }

    public void onEntryInit(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        notificationEntry.addOnSensitivityChangedListener(this.this$0.sensitivityListener);
    }

    public void onEntryCleanUp(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        notificationEntry.removeOnSensitivityChangedListener(this.this$0.sensitivityListener);
    }

    public void onRankingApplied() {
        for (NotificationEntry next : this.this$0.notifCollection.getAllNotifs()) {
            IconManager iconManager = this.this$0;
            Intrinsics.checkNotNullExpressionValue(next, "entry");
            boolean access$isImportantConversation = iconManager.isImportantConversation(next);
            if (next.getIcons().getAreIconsAvailable() && access$isImportantConversation != next.getIcons().isImportantConversation()) {
                this.this$0.updateIconsSafe(next);
            }
            next.getIcons().setImportantConversation(access$isImportantConversation);
        }
    }
}
