package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.collection.render.NodeController;

public class RankingCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public final NodeController mAlertingHeaderController;
    private final NotifSectioner mAlertingNotifSectioner = new NotifSectioner("Alerting") {
        public boolean isInSection(ListEntry listEntry) {
            return RankingCoordinator.this.mHighPriorityProvider.isHighPriority(listEntry);
        }

        public NodeController getHeaderNodeController() {
            return RankingCoordinator.this.mAlertingHeaderController;
        }
    };
    /* access modifiers changed from: private */
    public final NotifFilter mDndVisualEffectsFilter = new NotifFilter("DndSuppressingVisualEffects") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            if (RankingCoordinator.this.mStatusBarStateController.isDozing() && notificationEntry.shouldSuppressAmbient()) {
                return true;
            }
            if (RankingCoordinator.this.mStatusBarStateController.isDozing() || !notificationEntry.shouldSuppressNotificationList()) {
                return false;
            }
            return true;
        }
    };
    /* access modifiers changed from: private */
    public final HighPriorityProvider mHighPriorityProvider;
    /* access modifiers changed from: private */
    public final NodeController mSilentHeaderController;
    private final NotifSectioner mSilentNotifSectioner = new NotifSectioner("Silent") {
        public boolean isInSection(ListEntry listEntry) {
            return !RankingCoordinator.this.mHighPriorityProvider.isHighPriority(listEntry);
        }

        public NodeController getHeaderNodeController() {
            return RankingCoordinator.this.mSilentHeaderController;
        }
    };
    private final StatusBarStateController.StateListener mStatusBarStateCallback = new StatusBarStateController.StateListener() {
        public void onDozingChanged(boolean z) {
            RankingCoordinator.this.mDndVisualEffectsFilter.invalidateList();
        }
    };
    /* access modifiers changed from: private */
    public final StatusBarStateController mStatusBarStateController;
    private final NotifFilter mSuspendedFilter = new NotifFilter("IsSuspendedFilter") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            return notificationEntry.getRanking().isSuspended();
        }
    };

    public RankingCoordinator(StatusBarStateController statusBarStateController, HighPriorityProvider highPriorityProvider, NodeController nodeController, NodeController nodeController2) {
        this.mStatusBarStateController = statusBarStateController;
        this.mHighPriorityProvider = highPriorityProvider;
        this.mAlertingHeaderController = nodeController;
        this.mSilentHeaderController = nodeController2;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mStatusBarStateController.addCallback(this.mStatusBarStateCallback);
        notifPipeline.addPreGroupFilter(this.mSuspendedFilter);
        notifPipeline.addPreGroupFilter(this.mDndVisualEffectsFilter);
    }

    public NotifSectioner getAlertingSectioner() {
        return this.mAlertingNotifSectioner;
    }

    public NotifSectioner getSilentSectioner() {
        return this.mSilentNotifSectioner;
    }
}
