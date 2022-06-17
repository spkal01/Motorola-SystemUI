package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.interruption.HeadsUpController;
import com.android.systemui.statusbar.notification.interruption.HeadsUpViewBinder;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.Objects;

public class HeadsUpCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public NotificationEntry mCurrentHun;
    /* access modifiers changed from: private */
    public NotifLifetimeExtender.OnEndLifetimeExtensionCallback mEndLifetimeExtension;
    /* access modifiers changed from: private */
    public final HeadsUpManager mHeadsUpManager;
    /* access modifiers changed from: private */
    public final HeadsUpViewBinder mHeadsUpViewBinder;
    /* access modifiers changed from: private */
    public final NodeController mIncomingHeaderController;
    private final NotifLifetimeExtender mLifetimeExtender = new NotifLifetimeExtender() {
        public String getName() {
            return "HeadsUpCoordinator";
        }

        public void setCallback(NotifLifetimeExtender.OnEndLifetimeExtensionCallback onEndLifetimeExtensionCallback) {
            NotifLifetimeExtender.OnEndLifetimeExtensionCallback unused = HeadsUpCoordinator.this.mEndLifetimeExtension = onEndLifetimeExtensionCallback;
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry, int i) {
            boolean access$600 = HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
            if (access$600) {
                NotificationEntry unused = HeadsUpCoordinator.this.mNotifExtendingLifetime = notificationEntry;
            }
            return access$600;
        }

        public void cancelLifetimeExtension(NotificationEntry notificationEntry) {
            if (Objects.equals(HeadsUpCoordinator.this.mNotifExtendingLifetime, notificationEntry)) {
                NotificationEntry unused = HeadsUpCoordinator.this.mNotifExtendingLifetime = null;
            }
        }
    };
    private final NotifCollectionListener mNotifCollectionListener = new NotifCollectionListener() {
        public void onEntryAdded(NotificationEntry notificationEntry) {
            if (HeadsUpCoordinator.this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                HeadsUpCoordinator.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, new HeadsUpCoordinator$1$$ExternalSyntheticLambda1(HeadsUpCoordinator.this));
            }
        }

        public void onEntryUpdated(NotificationEntry notificationEntry) {
            boolean alertAgain = HeadsUpController.alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
            boolean shouldHeadsUp = HeadsUpCoordinator.this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry);
            if (HeadsUpCoordinator.this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                if (shouldHeadsUp) {
                    HeadsUpCoordinator.this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), alertAgain);
                } else if (!HeadsUpCoordinator.this.mHeadsUpManager.isEntryAutoHeadsUpped(notificationEntry.getKey())) {
                    HeadsUpCoordinator.this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), false);
                }
            } else if (shouldHeadsUp && alertAgain) {
                HeadsUpCoordinator.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, new HeadsUpCoordinator$1$$ExternalSyntheticLambda0(HeadsUpCoordinator.this));
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
            String key = notificationEntry.getKey();
            if (HeadsUpCoordinator.this.mHeadsUpManager.isAlerting(key)) {
                HeadsUpCoordinator.this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), HeadsUpCoordinator.this.mRemoteInputManager.getController().isSpinning(key) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY);
            }
        }

        public void onEntryCleanUp(NotificationEntry notificationEntry) {
            HeadsUpCoordinator.this.mHeadsUpViewBinder.abortBindCallback(notificationEntry);
        }
    };
    /* access modifiers changed from: private */
    public NotificationEntry mNotifExtendingLifetime;
    private final NotifPromoter mNotifPromoter = new NotifPromoter("HeadsUpCoordinator") {
        public boolean shouldPromoteToTopLevel(NotificationEntry notificationEntry) {
            return HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
        }
    };
    private final NotifSectioner mNotifSectioner = new NotifSectioner("HeadsUp") {
        public boolean isInSection(ListEntry listEntry) {
            return HeadsUpCoordinator.this.isCurrentlyShowingHun(listEntry);
        }

        public NodeController getHeaderNodeController() {
            return HeadsUpCoordinator.this.mIncomingHeaderController;
        }
    };
    /* access modifiers changed from: private */
    public final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final OnHeadsUpChangedListener mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
        public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
            NotificationEntry topEntry = HeadsUpCoordinator.this.mHeadsUpManager.getTopEntry();
            if (!Objects.equals(HeadsUpCoordinator.this.mCurrentHun, topEntry)) {
                NotificationEntry unused = HeadsUpCoordinator.this.mCurrentHun = topEntry;
                HeadsUpCoordinator.this.endNotifLifetimeExtension();
            }
            if (!z) {
                HeadsUpCoordinator.this.mHeadsUpViewBinder.unbindHeadsUpView(notificationEntry);
            }
        }
    };
    /* access modifiers changed from: private */
    public final NotificationRemoteInputManager mRemoteInputManager;

    public HeadsUpCoordinator(HeadsUpManager headsUpManager, HeadsUpViewBinder headsUpViewBinder, NotificationInterruptStateProvider notificationInterruptStateProvider, NotificationRemoteInputManager notificationRemoteInputManager, NodeController nodeController) {
        this.mHeadsUpManager = headsUpManager;
        this.mHeadsUpViewBinder = headsUpViewBinder;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mIncomingHeaderController = nodeController;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mHeadsUpManager.addListener(this.mOnHeadsUpChangedListener);
        notifPipeline.addCollectionListener(this.mNotifCollectionListener);
        notifPipeline.addPromoter(this.mNotifPromoter);
        notifPipeline.addNotificationLifetimeExtender(this.mLifetimeExtender);
    }

    public NotifSectioner getSectioner() {
        return this.mNotifSectioner;
    }

    /* access modifiers changed from: private */
    public void onHeadsUpViewBound(NotificationEntry notificationEntry) {
        this.mHeadsUpManager.showNotification(notificationEntry);
    }

    /* access modifiers changed from: private */
    public boolean isCurrentlyShowingHun(ListEntry listEntry) {
        return this.mCurrentHun == listEntry.getRepresentativeEntry();
    }

    /* access modifiers changed from: private */
    public void endNotifLifetimeExtension() {
        NotificationEntry notificationEntry = this.mNotifExtendingLifetime;
        if (notificationEntry != null) {
            this.mEndLifetimeExtension.onEndLifetimeExtension(this.mLifetimeExtender, notificationEntry);
            this.mNotifExtendingLifetime = null;
        }
    }
}
