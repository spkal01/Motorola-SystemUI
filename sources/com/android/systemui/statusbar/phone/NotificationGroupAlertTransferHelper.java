package com.android.systemui.statusbar.phone;

import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationGroupAlertTransferHelper implements OnHeadsUpChangedListener, StatusBarStateController.StateListener {
    private NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public final ArrayMap<String, GroupAlertEntry> mGroupAlertEntries = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final NotificationGroupManagerLegacy mGroupManager = ((NotificationGroupManagerLegacy) Dependency.get(NotificationGroupManagerLegacy.class));
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsDozing;
    private final NotificationEntryListener mNotificationEntryListener = new NotificationEntryListener() {
        public void onPendingEntryAdded(NotificationEntry notificationEntry) {
            GroupAlertEntry groupAlertEntry = (GroupAlertEntry) NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get(NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(notificationEntry.getSbn()));
            if (groupAlertEntry != null && groupAlertEntry.mGroup.alertOverride == null) {
                NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
            NotificationGroupAlertTransferHelper.this.mPendingAlerts.remove(notificationEntry.getKey());
        }
    };
    private final NotificationGroupManagerLegacy.OnGroupChangeListener mOnGroupChangeListener = new NotificationGroupManagerLegacy.OnGroupChangeListener() {
        public void onGroupCreated(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, String str) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.put(str, new GroupAlertEntry(notificationGroup));
        }

        public void onGroupRemoved(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, String str) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.remove(str);
        }

        public void onGroupSuppressionChanged(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, boolean z) {
            NotificationGroupAlertTransferHelper.this.onGroupChanged(notificationGroup, notificationGroup.alertOverride);
        }

        public void onGroupAlertOverrideChanged(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
            NotificationGroupAlertTransferHelper.this.onGroupChanged(notificationGroup, notificationEntry);
        }
    };
    /* access modifiers changed from: private */
    public final ArrayMap<String, PendingAlertInfo> mPendingAlerts = new ArrayMap<>();
    private final RowContentBindStage mRowContentBindStage;

    public void onStateChanged(int i) {
    }

    public NotificationGroupAlertTransferHelper(RowContentBindStage rowContentBindStage) {
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        this.mRowContentBindStage = rowContentBindStage;
    }

    public void bind(NotificationEntryManager notificationEntryManager, NotificationGroupManagerLegacy notificationGroupManagerLegacy) {
        if (this.mEntryManager == null) {
            this.mEntryManager = notificationEntryManager;
            notificationEntryManager.addNotificationEntryListener(this.mNotificationEntryListener);
            notificationGroupManagerLegacy.registerGroupChangeListener(this.mOnGroupChangeListener);
            return;
        }
        throw new IllegalStateException("Already bound.");
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void onDozingChanged(boolean z) {
        if (this.mIsDozing != z) {
            for (GroupAlertEntry next : this.mGroupAlertEntries.values()) {
                next.mLastAlertTransferTime = 0;
                next.mAlertSummaryOnNextAddition = false;
            }
        }
        this.mIsDozing = z;
    }

    /* access modifiers changed from: private */
    public void onGroupChanged(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, NotificationEntry notificationEntry) {
        NotificationEntry notificationEntry2 = notificationGroup.summary;
        if (notificationEntry2 != null) {
            if (notificationGroup.suppressed || notificationGroup.alertOverride != null) {
                checkForForwardAlertTransfer(notificationEntry2, notificationEntry);
                return;
            }
            GroupAlertEntry groupAlertEntry = this.mGroupAlertEntries.get(this.mGroupManager.getGroupKey(notificationEntry2.getSbn()));
            if (groupAlertEntry.mAlertSummaryOnNextAddition) {
                if (!this.mHeadsUpManager.isAlerting(notificationGroup.summary.getKey())) {
                    alertNotificationWhenPossible(notificationGroup.summary);
                }
                groupAlertEntry.mAlertSummaryOnNextAddition = false;
                return;
            }
            checkShouldTransferBack(groupAlertEntry);
        }
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        if (z && notificationEntry.getSbn().getNotification().isGroupSummary()) {
            checkForForwardAlertTransfer(notificationEntry, (NotificationEntry) null);
        }
    }

    private void checkForForwardAlertTransfer(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        NotificationGroupManagerLegacy.NotificationGroup groupForSummary = this.mGroupManager.getGroupForSummary(notificationEntry.getSbn());
        if (groupForSummary != null && groupForSummary.alertOverride != null) {
            handleOverriddenSummaryAlerted(notificationEntry);
        } else if (this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn())) {
            handleSuppressedSummaryAlerted(notificationEntry, notificationEntry2);
        }
    }

    private int getPendingChildrenNotAlerting(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        int i = 0;
        if (notificationEntryManager == null) {
            return 0;
        }
        for (NotificationEntry next : notificationEntryManager.getPendingNotificationsIterator()) {
            if (isPendingNotificationInGroup(next, notificationGroup) && onlySummaryAlerts(next)) {
                i++;
            }
        }
        return i;
    }

    private boolean pendingInflationsWillAddChildren(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        if (notificationEntryManager == null) {
            return false;
        }
        for (NotificationEntry isPendingNotificationInGroup : notificationEntryManager.getPendingNotificationsIterator()) {
            if (isPendingNotificationInGroup(isPendingNotificationInGroup, notificationGroup)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPendingNotificationInGroup(NotificationEntry notificationEntry, NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
        return this.mGroupManager.isGroupChild(notificationEntry.getSbn()) && Objects.equals(this.mGroupManager.getGroupKey(notificationEntry.getSbn()), this.mGroupManager.getGroupKey(notificationGroup.summary.getSbn())) && !notificationGroup.children.containsKey(notificationEntry.getKey());
    }

    private void handleSuppressedSummaryAlerted(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        ArrayList<NotificationEntry> logicalChildren;
        GroupAlertEntry groupAlertEntry = this.mGroupAlertEntries.get(this.mGroupManager.getGroupKey(notificationEntry.getSbn()));
        if (this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn()) && groupAlertEntry != null) {
            boolean isAlerting = this.mHeadsUpManager.isAlerting(notificationEntry.getKey());
            boolean z = notificationEntry2 != null && this.mHeadsUpManager.isAlerting(notificationEntry2.getKey());
            if ((isAlerting || z) && !pendingInflationsWillAddChildren(groupAlertEntry.mGroup) && (logicalChildren = this.mGroupManager.getLogicalChildren(notificationEntry.getSbn())) != null && !logicalChildren.isEmpty()) {
                NotificationEntry next = logicalChildren.iterator().next();
                if (isAlerting) {
                    tryTransferAlertState(notificationEntry, notificationEntry, next, groupAlertEntry);
                } else if (canStillTransferBack(groupAlertEntry)) {
                    tryTransferAlertState(notificationEntry, notificationEntry2, next, groupAlertEntry);
                }
            }
        }
    }

    private void handleOverriddenSummaryAlerted(NotificationEntry notificationEntry) {
        ArrayList<NotificationEntry> logicalChildren;
        GroupAlertEntry groupAlertEntry = this.mGroupAlertEntries.get(this.mGroupManager.getGroupKey(notificationEntry.getSbn()));
        NotificationGroupManagerLegacy.NotificationGroup groupForSummary = this.mGroupManager.getGroupForSummary(notificationEntry.getSbn());
        if (groupForSummary != null && groupForSummary.alertOverride != null && groupAlertEntry != null) {
            if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                tryTransferAlertState(notificationEntry, notificationEntry, groupForSummary.alertOverride, groupAlertEntry);
            } else if (canStillTransferBack(groupAlertEntry) && (logicalChildren = this.mGroupManager.getLogicalChildren(notificationEntry.getSbn())) != null) {
                logicalChildren.remove(groupForSummary.alertOverride);
                if (releaseChildAlerts(logicalChildren)) {
                    tryTransferAlertState(notificationEntry, (NotificationEntry) null, groupForSummary.alertOverride, groupAlertEntry);
                }
            }
        }
    }

    private void tryTransferAlertState(NotificationEntry notificationEntry, NotificationEntry notificationEntry2, NotificationEntry notificationEntry3, GroupAlertEntry groupAlertEntry) {
        if (notificationEntry3 != null && !notificationEntry3.getRow().keepInParent() && !notificationEntry3.isRowRemoved() && !notificationEntry3.isRowDismissed()) {
            if (!this.mHeadsUpManager.isAlerting(notificationEntry3.getKey()) && onlySummaryAlerts(notificationEntry)) {
                groupAlertEntry.mLastAlertTransferTime = SystemClock.elapsedRealtime();
            }
            transferAlertState(notificationEntry2, notificationEntry3);
        }
    }

    private void transferAlertState(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        if (notificationEntry != null) {
            this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), true);
        }
        alertNotificationWhenPossible(notificationEntry2);
    }

    /* access modifiers changed from: private */
    public void checkShouldTransferBack(GroupAlertEntry groupAlertEntry) {
        if (canStillTransferBack(groupAlertEntry)) {
            NotificationEntry notificationEntry = groupAlertEntry.mGroup.summary;
            if (onlySummaryAlerts(notificationEntry)) {
                ArrayList<NotificationEntry> logicalChildren = this.mGroupManager.getLogicalChildren(notificationEntry.getSbn());
                int size = logicalChildren.size();
                if (getPendingChildrenNotAlerting(groupAlertEntry.mGroup) + size > 1 && releaseChildAlerts(logicalChildren) && !this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                    if (size > 1) {
                        alertNotificationWhenPossible(notificationEntry);
                    } else {
                        groupAlertEntry.mAlertSummaryOnNextAddition = true;
                    }
                    groupAlertEntry.mLastAlertTransferTime = 0;
                }
            }
        }
    }

    private boolean canStillTransferBack(GroupAlertEntry groupAlertEntry) {
        return SystemClock.elapsedRealtime() - groupAlertEntry.mLastAlertTransferTime < 300;
    }

    private boolean releaseChildAlerts(List<NotificationEntry> list) {
        boolean z = false;
        for (int i = 0; i < list.size(); i++) {
            NotificationEntry notificationEntry = list.get(i);
            if (onlySummaryAlerts(notificationEntry) && this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), true);
                z = true;
            }
            if (this.mPendingAlerts.containsKey(notificationEntry.getKey())) {
                this.mPendingAlerts.get(notificationEntry.getKey()).mAbortOnInflation = true;
                z = true;
            }
        }
        return z;
    }

    private void alertNotificationWhenPossible(NotificationEntry notificationEntry) {
        int contentFlag = this.mHeadsUpManager.getContentFlag();
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mRowContentBindStage.getStageParams(notificationEntry);
        if ((rowContentBindParams.getContentViews() & contentFlag) == 0) {
            this.mPendingAlerts.put(notificationEntry.getKey(), new PendingAlertInfo(notificationEntry));
            rowContentBindParams.requireContentViews(contentFlag);
            this.mRowContentBindStage.requestRebind(notificationEntry, new NotificationGroupAlertTransferHelper$$ExternalSyntheticLambda0(this, notificationEntry, contentFlag));
        } else if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
            this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), true);
        } else {
            this.mHeadsUpManager.showNotification(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$alertNotificationWhenPossible$0(NotificationEntry notificationEntry, int i, NotificationEntry notificationEntry2) {
        PendingAlertInfo remove = this.mPendingAlerts.remove(notificationEntry.getKey());
        if (remove == null) {
            return;
        }
        if (remove.isStillValid()) {
            alertNotificationWhenPossible(notificationEntry);
            return;
        }
        ((RowContentBindParams) this.mRowContentBindStage.getStageParams(notificationEntry)).markContentViewsFreeable(i);
        this.mRowContentBindStage.requestRebind(notificationEntry, (NotifBindPipeline.BindCallback) null);
    }

    private boolean onlySummaryAlerts(NotificationEntry notificationEntry) {
        return notificationEntry != null && notificationEntry.getSbn().getNotification().getGroupAlertBehavior() == 1;
    }

    private class PendingAlertInfo {
        boolean mAbortOnInflation;
        final NotificationEntry mEntry;
        final StatusBarNotification mOriginalNotification;

        PendingAlertInfo(NotificationEntry notificationEntry) {
            this.mOriginalNotification = notificationEntry.getSbn();
            this.mEntry = notificationEntry;
        }

        /* access modifiers changed from: private */
        public boolean isStillValid() {
            if (!this.mAbortOnInflation && this.mEntry.getSbn().getGroupKey() == this.mOriginalNotification.getGroupKey() && this.mEntry.getSbn().getNotification().isGroupSummary() == this.mOriginalNotification.getNotification().isGroupSummary()) {
                return true;
            }
            return false;
        }
    }

    private static class GroupAlertEntry {
        boolean mAlertSummaryOnNextAddition;
        final NotificationGroupManagerLegacy.NotificationGroup mGroup;
        long mLastAlertTransferTime;

        GroupAlertEntry(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
            this.mGroup = notificationGroup;
        }
    }
}
