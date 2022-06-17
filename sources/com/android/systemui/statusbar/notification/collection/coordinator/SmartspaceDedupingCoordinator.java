package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.lockscreen.LockscreenSmartspaceController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartspaceDedupingCoordinator.kt */
public final class SmartspaceDedupingCoordinator implements Coordinator {
    @NotNull
    private final SystemClock clock;
    @NotNull
    private final SmartspaceDedupingCoordinator$collectionListener$1 collectionListener = new SmartspaceDedupingCoordinator$collectionListener$1(this);
    @NotNull
    private final DelayableExecutor executor;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartspaceDedupingCoordinator$filter$1 filter = new SmartspaceDedupingCoordinator$filter$1(this);
    /* access modifiers changed from: private */
    public boolean isOnLockscreen;
    @NotNull
    private final NotifPipeline notifPipeline;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationEntryManager notificationEntryManager;
    @NotNull
    private final NotificationLockscreenUserManager notificationLockscreenUserManager;
    @NotNull
    private final LockscreenSmartspaceController smartspaceController;
    @NotNull
    private final SysuiStatusBarStateController statusBarStateController;
    @NotNull
    private final SmartspaceDedupingCoordinator$statusBarStateListener$1 statusBarStateListener = new SmartspaceDedupingCoordinator$statusBarStateListener$1(this);
    /* access modifiers changed from: private */
    @NotNull
    public Map<String, TrackedSmartspaceTarget> trackedSmartspaceTargets = new LinkedHashMap();

    public SmartspaceDedupingCoordinator(@NotNull SysuiStatusBarStateController sysuiStatusBarStateController, @NotNull LockscreenSmartspaceController lockscreenSmartspaceController, @NotNull NotificationEntryManager notificationEntryManager2, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager2, @NotNull NotifPipeline notifPipeline2, @NotNull DelayableExecutor delayableExecutor, @NotNull SystemClock systemClock) {
        Intrinsics.checkNotNullParameter(sysuiStatusBarStateController, "statusBarStateController");
        Intrinsics.checkNotNullParameter(lockscreenSmartspaceController, "smartspaceController");
        Intrinsics.checkNotNullParameter(notificationEntryManager2, "notificationEntryManager");
        Intrinsics.checkNotNullParameter(notificationLockscreenUserManager2, "notificationLockscreenUserManager");
        Intrinsics.checkNotNullParameter(notifPipeline2, "notifPipeline");
        Intrinsics.checkNotNullParameter(delayableExecutor, "executor");
        Intrinsics.checkNotNullParameter(systemClock, "clock");
        this.statusBarStateController = sysuiStatusBarStateController;
        this.smartspaceController = lockscreenSmartspaceController;
        this.notificationEntryManager = notificationEntryManager2;
        this.notificationLockscreenUserManager = notificationLockscreenUserManager2;
        this.notifPipeline = notifPipeline2;
        this.executor = delayableExecutor;
        this.clock = systemClock;
    }

    public void attach(@NotNull NotifPipeline notifPipeline2) {
        Intrinsics.checkNotNullParameter(notifPipeline2, "pipeline");
        notifPipeline2.addPreGroupFilter(this.filter);
        notifPipeline2.addCollectionListener(this.collectionListener);
        this.statusBarStateController.addCallback(this.statusBarStateListener);
        this.smartspaceController.addListener(new SmartspaceDedupingCoordinator$attach$1(this));
        this.notificationLockscreenUserManager.addKeyguardNotificationSuppressor(new SmartspaceDedupingCoordinator$attach$2(this));
        recordStatusBarState(this.statusBarStateController.getState());
    }

    /* access modifiers changed from: private */
    public final boolean isDupedWithSmartspaceContent(NotificationEntry notificationEntry) {
        TrackedSmartspaceTarget trackedSmartspaceTarget = this.trackedSmartspaceTargets.get(notificationEntry.getKey());
        if (trackedSmartspaceTarget == null) {
            return false;
        }
        return trackedSmartspaceTarget.getShouldFilter();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onNewSmartspaceTargets(java.util.List<? extends android.os.Parcelable> r6) {
        /*
            r5 = this;
            java.util.LinkedHashMap r0 = new java.util.LinkedHashMap
            r0.<init>()
            java.util.Map<java.lang.String, com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget> r1 = r5.trackedSmartspaceTargets
            java.util.Iterator r6 = r6.iterator()
            boolean r2 = r6.hasNext()
            if (r2 == 0) goto L_0x003f
            java.lang.Object r6 = r6.next()
            android.os.Parcelable r6 = (android.os.Parcelable) r6
            boolean r2 = r6 instanceof android.app.smartspace.SmartspaceTarget
            if (r2 == 0) goto L_0x001e
            android.app.smartspace.SmartspaceTarget r6 = (android.app.smartspace.SmartspaceTarget) r6
            goto L_0x001f
        L_0x001e:
            r6 = 0
        L_0x001f:
            if (r6 != 0) goto L_0x0022
            goto L_0x003f
        L_0x0022:
            java.lang.String r6 = r6.getSourceNotificationKey()
            if (r6 != 0) goto L_0x0029
            goto L_0x003f
        L_0x0029:
            java.lang.Object r2 = r1.get(r6)
            if (r2 == 0) goto L_0x0030
            goto L_0x0035
        L_0x0030:
            com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget r2 = new com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget
            r2.<init>(r6)
        L_0x0035:
            com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget r2 = (com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget) r2
            r0.put(r6, r2)
            boolean r6 = r5.updateFilterStatus(r2)
            goto L_0x0040
        L_0x003f:
            r6 = 0
        L_0x0040:
            java.util.Set r2 = r1.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0048:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x006f
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            boolean r4 = r0.containsKey(r3)
            if (r4 != 0) goto L_0x0048
            java.lang.Object r6 = r1.get(r3)
            com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget r6 = (com.android.systemui.statusbar.notification.collection.coordinator.TrackedSmartspaceTarget) r6
            if (r6 != 0) goto L_0x0063
            goto L_0x006d
        L_0x0063:
            java.lang.Runnable r6 = r6.getCancelTimeoutRunnable()
            if (r6 != 0) goto L_0x006a
            goto L_0x006d
        L_0x006a:
            r6.run()
        L_0x006d:
            r6 = 1
            goto L_0x0048
        L_0x006f:
            if (r6 == 0) goto L_0x007d
            com.android.systemui.statusbar.notification.collection.coordinator.SmartspaceDedupingCoordinator$filter$1 r6 = r5.filter
            r6.invalidateList()
            com.android.systemui.statusbar.notification.NotificationEntryManager r6 = r5.notificationEntryManager
            java.lang.String r1 = "Smartspace targets changed"
            r6.updateNotifications(r1)
        L_0x007d:
            r5.trackedSmartspaceTargets = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.coordinator.SmartspaceDedupingCoordinator.onNewSmartspaceTargets(java.util.List):void");
    }

    /* access modifiers changed from: private */
    public final boolean updateFilterStatus(TrackedSmartspaceTarget trackedSmartspaceTarget) {
        boolean shouldFilter = trackedSmartspaceTarget.getShouldFilter();
        NotificationEntry entry = this.notifPipeline.getEntry(trackedSmartspaceTarget.getKey());
        if (entry != null) {
            updateAlertException(trackedSmartspaceTarget, entry);
            trackedSmartspaceTarget.setShouldFilter(!hasRecentlyAlerted(entry));
        }
        if (trackedSmartspaceTarget.getShouldFilter() == shouldFilter || !this.isOnLockscreen) {
            return false;
        }
        return true;
    }

    private final void updateAlertException(TrackedSmartspaceTarget trackedSmartspaceTarget, NotificationEntry notificationEntry) {
        long currentTimeMillis = this.clock.currentTimeMillis();
        long lastAudiblyAlertedMillis = notificationEntry.getRanking().getLastAudiblyAlertedMillis() + SmartspaceDedupingCoordinatorKt.ALERT_WINDOW;
        if (lastAudiblyAlertedMillis != trackedSmartspaceTarget.getAlertExceptionExpires() && lastAudiblyAlertedMillis > currentTimeMillis) {
            Runnable cancelTimeoutRunnable = trackedSmartspaceTarget.getCancelTimeoutRunnable();
            if (cancelTimeoutRunnable != null) {
                cancelTimeoutRunnable.run();
            }
            trackedSmartspaceTarget.setAlertExceptionExpires(lastAudiblyAlertedMillis);
            trackedSmartspaceTarget.setCancelTimeoutRunnable(this.executor.executeDelayed(new SmartspaceDedupingCoordinator$updateAlertException$1(trackedSmartspaceTarget, this), lastAudiblyAlertedMillis - currentTimeMillis));
        }
    }

    /* access modifiers changed from: private */
    public final void cancelExceptionTimeout(TrackedSmartspaceTarget trackedSmartspaceTarget) {
        Runnable cancelTimeoutRunnable = trackedSmartspaceTarget.getCancelTimeoutRunnable();
        if (cancelTimeoutRunnable != null) {
            cancelTimeoutRunnable.run();
        }
        trackedSmartspaceTarget.setCancelTimeoutRunnable((Runnable) null);
        trackedSmartspaceTarget.setAlertExceptionExpires(0);
    }

    /* access modifiers changed from: private */
    public final void recordStatusBarState(int i) {
        boolean z = this.isOnLockscreen;
        boolean z2 = true;
        if (i != 1) {
            z2 = false;
        }
        this.isOnLockscreen = z2;
        if (z2 != z) {
            this.filter.invalidateList();
        }
    }

    private final boolean hasRecentlyAlerted(NotificationEntry notificationEntry) {
        return this.clock.currentTimeMillis() - notificationEntry.getRanking().getLastAudiblyAlertedMillis() <= SmartspaceDedupingCoordinatorKt.ALERT_WINDOW;
    }
}
