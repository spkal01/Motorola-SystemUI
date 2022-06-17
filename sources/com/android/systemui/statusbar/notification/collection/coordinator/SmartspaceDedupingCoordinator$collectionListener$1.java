package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartspaceDedupingCoordinator.kt */
public final class SmartspaceDedupingCoordinator$collectionListener$1 implements NotifCollectionListener {
    final /* synthetic */ SmartspaceDedupingCoordinator this$0;

    SmartspaceDedupingCoordinator$collectionListener$1(SmartspaceDedupingCoordinator smartspaceDedupingCoordinator) {
        this.this$0 = smartspaceDedupingCoordinator;
    }

    public void onEntryAdded(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        TrackedSmartspaceTarget trackedSmartspaceTarget = (TrackedSmartspaceTarget) this.this$0.trackedSmartspaceTargets.get(notificationEntry.getKey());
        if (trackedSmartspaceTarget != null) {
            boolean unused = this.this$0.updateFilterStatus(trackedSmartspaceTarget);
        }
    }

    public void onEntryUpdated(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        TrackedSmartspaceTarget trackedSmartspaceTarget = (TrackedSmartspaceTarget) this.this$0.trackedSmartspaceTargets.get(notificationEntry.getKey());
        if (trackedSmartspaceTarget != null) {
            boolean unused = this.this$0.updateFilterStatus(trackedSmartspaceTarget);
        }
    }

    public void onEntryRemoved(@NotNull NotificationEntry notificationEntry, int i) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        TrackedSmartspaceTarget trackedSmartspaceTarget = (TrackedSmartspaceTarget) this.this$0.trackedSmartspaceTargets.get(notificationEntry.getKey());
        if (trackedSmartspaceTarget != null) {
            this.this$0.cancelExceptionTimeout(trackedSmartspaceTarget);
        }
    }
}
