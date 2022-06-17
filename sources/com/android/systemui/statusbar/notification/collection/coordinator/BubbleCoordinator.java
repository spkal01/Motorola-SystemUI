package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.wmshell.BubblesManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BubbleCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public final Optional<BubblesManager> mBubblesManagerOptional;
    /* access modifiers changed from: private */
    public final Optional<Bubbles> mBubblesOptional;
    /* access modifiers changed from: private */
    public final NotifDismissInterceptor mDismissInterceptor = new NotifDismissInterceptor() {
        public String getName() {
            return "BubbleCoordinator";
        }

        public void setCallback(NotifDismissInterceptor.OnEndDismissInterception onEndDismissInterception) {
            NotifDismissInterceptor.OnEndDismissInterception unused = BubbleCoordinator.this.mOnEndDismissInterception = onEndDismissInterception;
        }

        public boolean shouldInterceptDismissal(NotificationEntry notificationEntry) {
            if (!BubbleCoordinator.this.mBubblesManagerOptional.isPresent() || !((BubblesManager) BubbleCoordinator.this.mBubblesManagerOptional.get()).handleDismissalInterception(notificationEntry)) {
                BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
                return false;
            }
            BubbleCoordinator.this.mInterceptedDismissalEntries.add(notificationEntry.getKey());
            return true;
        }

        public void cancelDismissInterception(NotificationEntry notificationEntry) {
            BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
        }
    };
    /* access modifiers changed from: private */
    public final Set<String> mInterceptedDismissalEntries = new HashSet();
    private final BubblesManager.NotifCallback mNotifCallback = new BubblesManager.NotifCallback() {
        public void maybeCancelSummary(NotificationEntry notificationEntry) {
        }

        public void removeNotification(NotificationEntry notificationEntry, DismissedByUserStats dismissedByUserStats, int i) {
            if (BubbleCoordinator.this.isInterceptingDismissal(notificationEntry)) {
                BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
                BubbleCoordinator.this.mOnEndDismissInterception.onEndDismissInterception(BubbleCoordinator.this.mDismissInterceptor, notificationEntry, dismissedByUserStats);
            } else if (BubbleCoordinator.this.mNotifPipeline.getAllNotifs().contains(notificationEntry)) {
                BubbleCoordinator.this.mNotifCollection.dismissNotification(notificationEntry, dismissedByUserStats);
            }
        }

        public void invalidateNotifications(String str) {
            BubbleCoordinator.this.mNotifFilter.invalidateList();
        }
    };
    /* access modifiers changed from: private */
    public final NotifCollection mNotifCollection;
    /* access modifiers changed from: private */
    public final NotifFilter mNotifFilter = new NotifFilter("BubbleCoordinator") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            return BubbleCoordinator.this.mBubblesOptional.isPresent() && ((Bubbles) BubbleCoordinator.this.mBubblesOptional.get()).isBubbleNotificationSuppressedFromShade(notificationEntry.getKey(), notificationEntry.getSbn().getGroupKey());
        }
    };
    /* access modifiers changed from: private */
    public NotifPipeline mNotifPipeline;
    /* access modifiers changed from: private */
    public NotifDismissInterceptor.OnEndDismissInterception mOnEndDismissInterception;

    public BubbleCoordinator(Optional<BubblesManager> optional, Optional<Bubbles> optional2, NotifCollection notifCollection) {
        this.mBubblesManagerOptional = optional;
        this.mBubblesOptional = optional2;
        this.mNotifCollection = notifCollection;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mNotifPipeline = notifPipeline;
        notifPipeline.addNotificationDismissInterceptor(this.mDismissInterceptor);
        this.mNotifPipeline.addFinalizeFilter(this.mNotifFilter);
        if (this.mBubblesManagerOptional.isPresent()) {
            this.mBubblesManagerOptional.get().addNotifCallback(this.mNotifCallback);
        }
    }

    /* access modifiers changed from: private */
    public boolean isInterceptingDismissal(NotificationEntry notificationEntry) {
        return this.mInterceptedDismissalEntries.contains(notificationEntry.getKey());
    }
}
