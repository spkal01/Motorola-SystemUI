package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationInteractionTracker.kt */
public final class NotificationInteractionTracker implements NotifCollectionListener, NotificationInteractionListener {
    @NotNull
    private final NotificationClickNotifier clicker;
    @NotNull
    private final NotificationEntryManager entryManager;
    @NotNull
    private final Map<String, Boolean> interactions = new LinkedHashMap();

    public NotificationInteractionTracker(@NotNull NotificationClickNotifier notificationClickNotifier, @NotNull NotificationEntryManager notificationEntryManager) {
        Intrinsics.checkNotNullParameter(notificationClickNotifier, "clicker");
        Intrinsics.checkNotNullParameter(notificationEntryManager, "entryManager");
        this.clicker = notificationClickNotifier;
        this.entryManager = notificationEntryManager;
        notificationClickNotifier.addNotificationInteractionListener(this);
        notificationEntryManager.addCollectionListener(this);
    }

    public final boolean hasUserInteractedWith(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        Boolean bool = this.interactions.get(str);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void onEntryAdded(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Map<String, Boolean> map = this.interactions;
        String key = notificationEntry.getKey();
        Intrinsics.checkNotNullExpressionValue(key, "entry.key");
        map.put(key, Boolean.FALSE);
    }

    public void onEntryCleanUp(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        this.interactions.remove(notificationEntry.getKey());
    }

    public void onNotificationInteraction(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        this.interactions.put(str, Boolean.TRUE);
    }
}
