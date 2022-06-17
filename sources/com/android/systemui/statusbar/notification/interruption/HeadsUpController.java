package com.android.systemui.statusbar.notification.interruption;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.motorola.systemui.desktop.util.DesktopDisplayContext;
import java.util.Objects;

public class HeadsUpController {
    private NotifCollectionListener mCollectionListener = new NotifCollectionListener() {
        public void onEntryAdded(NotificationEntry notificationEntry) {
            if (HeadsUpController.this.mInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                HeadsUpController.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, new HeadsUpController$1$$ExternalSyntheticLambda0(HeadsUpController.this));
            }
        }

        public void onEntryUpdated(NotificationEntry notificationEntry) {
            HeadsUpController.this.updateHunState(notificationEntry);
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
            HeadsUpController.this.stopAlerting(notificationEntry);
        }

        public void onEntryCleanUp(NotificationEntry notificationEntry) {
            HeadsUpController.this.mHeadsUpViewBinder.abortBindCallback(notificationEntry);
        }
    };
    private final Context mContext;
    private final HeadsUpManager mHeadsUpManager;
    /* access modifiers changed from: private */
    public final HeadsUpViewBinder mHeadsUpViewBinder;
    /* access modifiers changed from: private */
    public final NotificationInterruptStateProvider mInterruptStateProvider;
    private final NotificationListener mNotificationListener;
    private OnHeadsUpChangedListener mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
        public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
            if (!z && !notificationEntry.getRow().isRemoved()) {
                HeadsUpController.this.mHeadsUpViewBinder.unbindHeadsUpView(notificationEntry);
            }
        }
    };
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final StatusBarStateController mStatusBarStateController;
    private final VisualStabilityManager mVisualStabilityManager;

    HeadsUpController(Context context, HeadsUpViewBinder headsUpViewBinder, NotificationInterruptStateProvider notificationInterruptStateProvider, HeadsUpManager headsUpManager, NotificationRemoteInputManager notificationRemoteInputManager, StatusBarStateController statusBarStateController, VisualStabilityManager visualStabilityManager, NotificationListener notificationListener) {
        this.mHeadsUpViewBinder = headsUpViewBinder;
        this.mHeadsUpManager = headsUpManager;
        this.mInterruptStateProvider = notificationInterruptStateProvider;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mStatusBarStateController = statusBarStateController;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mNotificationListener = notificationListener;
        this.mContext = context;
    }

    public void attach(NotificationEntryManager notificationEntryManager, HeadsUpManager headsUpManager) {
        if (!(this.mContext instanceof DesktopDisplayContext)) {
            notificationEntryManager.addCollectionListener(this.mCollectionListener);
            headsUpManager.addListener(this.mOnHeadsUpChangedListener);
        }
    }

    /* access modifiers changed from: private */
    public void showAlertingView(NotificationEntry notificationEntry) {
        this.mHeadsUpManager.showNotification(notificationEntry);
        if (!this.mStatusBarStateController.isDozing()) {
            setNotificationShown(notificationEntry.getSbn());
        }
        notifyHeadsUpShowing(notificationEntry);
    }

    /* access modifiers changed from: private */
    public void updateHunState(NotificationEntry notificationEntry) {
        boolean alertAgain = alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
        boolean shouldHeadsUp = this.mInterruptStateProvider.shouldHeadsUp(notificationEntry);
        if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
            if (shouldHeadsUp) {
                this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), alertAgain);
            } else if (!this.mHeadsUpManager.isEntryAutoHeadsUpped(notificationEntry.getKey())) {
                this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), false);
            }
        } else if (shouldHeadsUp && alertAgain) {
            HeadsUpViewBinder headsUpViewBinder = this.mHeadsUpViewBinder;
            HeadsUpManager headsUpManager = this.mHeadsUpManager;
            Objects.requireNonNull(headsUpManager);
            headsUpViewBinder.bindHeadsUpView(notificationEntry, new HeadsUpController$$ExternalSyntheticLambda0(headsUpManager));
        }
    }

    private void setNotificationShown(StatusBarNotification statusBarNotification) {
        try {
            this.mNotificationListener.setNotificationsShown(new String[]{statusBarNotification.getKey()});
        } catch (RuntimeException e) {
            Log.d("HeadsUpBindController", "failed setNotificationsShown: ", e);
        }
    }

    /* access modifiers changed from: private */
    public void stopAlerting(NotificationEntry notificationEntry) {
        String key = notificationEntry.getKey();
        if (this.mHeadsUpManager.isAlerting(key)) {
            this.mHeadsUpManager.removeNotification(key, (this.mRemoteInputManager.getController().isSpinning(key) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY) || !this.mVisualStabilityManager.isReorderingAllowed());
        }
    }

    public static boolean alertAgain(NotificationEntry notificationEntry, Notification notification) {
        return notificationEntry == null || !notificationEntry.hasInterrupted() || (notification.flags & 8) == 0;
    }

    private void notifyHeadsUpShowing(NotificationEntry notificationEntry) {
        if (!Build.IS_USER) {
            Log.d("HeadsUpBindController", "notifyHeadsUpShowing: " + notificationEntry.getKey() + " ;" + notificationEntry.getSbn().getNotification().category);
        }
        Intent intent = new Intent("com.com.android.systemui.ACTION_HEADS_UP_SHOWING");
        intent.setPackage(this.mContext.getPackageName());
        intent.putExtra("key", notificationEntry.getKey());
        intent.putExtra("category", notificationEntry.getSbn().getNotification().category);
        intent.putExtra("package", notificationEntry.getSbn().getPackageName());
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }
}
