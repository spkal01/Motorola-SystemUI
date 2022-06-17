package com.android.systemui.statusbar.notification.collection.coordinator;

/* compiled from: SmartspaceDedupingCoordinator.kt */
final class SmartspaceDedupingCoordinator$updateAlertException$1 implements Runnable {
    final /* synthetic */ TrackedSmartspaceTarget $target;
    final /* synthetic */ SmartspaceDedupingCoordinator this$0;

    SmartspaceDedupingCoordinator$updateAlertException$1(TrackedSmartspaceTarget trackedSmartspaceTarget, SmartspaceDedupingCoordinator smartspaceDedupingCoordinator) {
        this.$target = trackedSmartspaceTarget;
        this.this$0 = smartspaceDedupingCoordinator;
    }

    public final void run() {
        this.$target.setCancelTimeoutRunnable((Runnable) null);
        this.$target.setShouldFilter(true);
        this.this$0.filter.invalidateList();
        this.this$0.notificationEntryManager.updateNotifications("deduping timeout expired");
    }
}
