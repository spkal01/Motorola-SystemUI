package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifStabilityManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VisualStabilityCoordinator implements Coordinator {
    protected static final long ALLOW_SECTION_CHANGE_TIMEOUT = 500;
    private final DelayableExecutor mDelayableExecutor;
    /* access modifiers changed from: private */
    public Map<String, Runnable> mEntriesThatCanChangeSection = new HashMap();
    /* access modifiers changed from: private */
    public final Set<String> mEntriesWithSuppressedSectionChange = new HashSet();
    /* access modifiers changed from: private */
    public final HeadsUpManager mHeadsUpManager;
    /* access modifiers changed from: private */
    public boolean mIsSuppressingGroupChange = false;
    private final NotifStabilityManager mNotifStabilityManager = new NotifStabilityManager("VisualStabilityCoordinator") {
        public void onBeginRun() {
            boolean unused = VisualStabilityCoordinator.this.mIsSuppressingGroupChange = false;
            VisualStabilityCoordinator.this.mEntriesWithSuppressedSectionChange.clear();
        }

        public boolean isGroupChangeAllowed(NotificationEntry notificationEntry) {
            boolean z = VisualStabilityCoordinator.this.mReorderingAllowed || VisualStabilityCoordinator.this.mHeadsUpManager.isAlerting(notificationEntry.getKey());
            VisualStabilityCoordinator.access$076(VisualStabilityCoordinator.this, z ^ true ? 1 : 0);
            return z;
        }

        public boolean isSectionChangeAllowed(NotificationEntry notificationEntry) {
            boolean z = VisualStabilityCoordinator.this.mReorderingAllowed || VisualStabilityCoordinator.this.mHeadsUpManager.isAlerting(notificationEntry.getKey()) || VisualStabilityCoordinator.this.mEntriesThatCanChangeSection.containsKey(notificationEntry.getKey());
            if (z) {
                VisualStabilityCoordinator.this.mEntriesWithSuppressedSectionChange.add(notificationEntry.getKey());
            }
            return z;
        }
    };
    /* access modifiers changed from: private */
    public boolean mPanelExpanded;
    /* access modifiers changed from: private */
    public boolean mPulsing;
    /* access modifiers changed from: private */
    public boolean mReorderingAllowed;
    /* access modifiers changed from: private */
    public boolean mScreenOn;
    private final StatusBarStateController mStatusBarStateController;
    final StatusBarStateController.StateListener mStatusBarStateControllerListener = new StatusBarStateController.StateListener() {
        public void onPulsingChanged(boolean z) {
            boolean unused = VisualStabilityCoordinator.this.mPulsing = z;
            VisualStabilityCoordinator.this.updateAllowedStates();
        }

        public void onExpandedChanged(boolean z) {
            boolean unused = VisualStabilityCoordinator.this.mPanelExpanded = z;
            VisualStabilityCoordinator.this.updateAllowedStates();
        }
    };
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    final WakefulnessLifecycle.Observer mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
        public void onFinishedGoingToSleep() {
            boolean unused = VisualStabilityCoordinator.this.mScreenOn = false;
            VisualStabilityCoordinator.this.updateAllowedStates();
        }

        public void onStartedWakingUp() {
            boolean unused = VisualStabilityCoordinator.this.mScreenOn = true;
            VisualStabilityCoordinator.this.updateAllowedStates();
        }
    };

    /* JADX WARNING: type inference failed for: r2v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$076(VisualStabilityCoordinator visualStabilityCoordinator, int i) {
        ? r2 = (byte) (i | visualStabilityCoordinator.mIsSuppressingGroupChange);
        visualStabilityCoordinator.mIsSuppressingGroupChange = r2;
        return r2;
    }

    public VisualStabilityCoordinator(HeadsUpManager headsUpManager, WakefulnessLifecycle wakefulnessLifecycle, StatusBarStateController statusBarStateController, DelayableExecutor delayableExecutor) {
        this.mHeadsUpManager = headsUpManager;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mStatusBarStateController = statusBarStateController;
        this.mDelayableExecutor = delayableExecutor;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
        boolean z = true;
        if (!(this.mWakefulnessLifecycle.getWakefulness() == 2 || this.mWakefulnessLifecycle.getWakefulness() == 1)) {
            z = false;
        }
        this.mScreenOn = z;
        this.mStatusBarStateController.addCallback(this.mStatusBarStateControllerListener);
        this.mPulsing = this.mStatusBarStateController.isPulsing();
        notifPipeline.setVisualStabilityManager(this.mNotifStabilityManager);
    }

    /* access modifiers changed from: private */
    public void updateAllowedStates() {
        boolean isReorderingAllowed = isReorderingAllowed();
        this.mReorderingAllowed = isReorderingAllowed;
        if (!isReorderingAllowed) {
            return;
        }
        if (this.mIsSuppressingGroupChange || isSuppressingSectionChange()) {
            this.mNotifStabilityManager.invalidateList();
        }
    }

    private boolean isSuppressingSectionChange() {
        return !this.mEntriesWithSuppressedSectionChange.isEmpty();
    }

    private boolean isReorderingAllowed() {
        return (!this.mScreenOn || !this.mPanelExpanded) && !this.mPulsing;
    }

    public void temporarilyAllowSectionChanges(NotificationEntry notificationEntry, long j) {
        String key = notificationEntry.getKey();
        boolean isSectionChangeAllowed = this.mNotifStabilityManager.isSectionChangeAllowed(notificationEntry);
        if (this.mEntriesThatCanChangeSection.containsKey(key)) {
            this.mEntriesThatCanChangeSection.get(key).run();
        }
        this.mEntriesThatCanChangeSection.put(key, this.mDelayableExecutor.executeAtTime(new VisualStabilityCoordinator$$ExternalSyntheticLambda0(this, key), j + ALLOW_SECTION_CHANGE_TIMEOUT));
        if (!isSectionChangeAllowed) {
            this.mNotifStabilityManager.invalidateList();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$temporarilyAllowSectionChanges$0(String str) {
        this.mEntriesThatCanChangeSection.remove(str);
    }
}
