package com.motorola.systemui.desktop.overwrites.statusbar.notification.interruption;

import android.os.Build;
import android.os.PowerManager;
import android.os.RemoteException;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptSuppressor;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;
import java.util.List;

public class DesktopNotificationInterruptStateProviderImpl implements NotificationInterruptStateProvider {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final IDreamManager mDreamManager;
    private final HeadsUpManager mHeadsUpManager;
    private final NotificationFilter mNotificationFilter;
    private final PowerManager mPowerManager;
    private final List<NotificationInterruptSuppressor> mSuppressors = new ArrayList();

    public boolean shouldBubbleUp(NotificationEntry notificationEntry) {
        return false;
    }

    public boolean shouldLaunchFullScreenIntentWhenAdded(NotificationEntry notificationEntry) {
        return false;
    }

    public DesktopNotificationInterruptStateProviderImpl(PowerManager powerManager, IDreamManager iDreamManager, HeadsUpManager headsUpManager, NotificationFilter notificationFilter) {
        this.mPowerManager = powerManager;
        this.mDreamManager = iDreamManager;
        this.mHeadsUpManager = headsUpManager;
        this.mNotificationFilter = notificationFilter;
    }

    public boolean shouldHeadsUp(NotificationEntry notificationEntry) {
        return shouldHeadsUpWhenAwake(notificationEntry);
    }

    public void addSuppressor(NotificationInterruptSuppressor notificationInterruptSuppressor) {
        this.mSuppressors.add(notificationInterruptSuppressor);
    }

    private boolean shouldHeadsUpWhenAwake(NotificationEntry notificationEntry) {
        boolean z;
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (!canAlertCommon(notificationEntry) || !canAlertAwakeCommon(notificationEntry)) {
            return false;
        }
        if (isSnoozedPackage(sbn)) {
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: snoozed package: " + sbn.getKey());
            }
            return false;
        } else if (notificationEntry.shouldSuppressPeek()) {
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No heads up: suppressed by DND: " + sbn.getKey());
            }
            return false;
        } else if (notificationEntry.getImportance() < 4) {
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No heads up: unimportant notification: " + sbn.getKey());
            }
            return false;
        } else {
            try {
                z = this.mDreamManager.isDreaming();
            } catch (RemoteException e) {
                Log.e("DesktopNotificationInterruptStateProviderImpl", "Failed to query dream manager.", e);
                z = false;
            }
            if (!(this.mPowerManager.isScreenOn() && !z)) {
                if (DEBUG) {
                    Log.d("DesktopNotificationInterruptStateProviderImpl", "No heads up: not in use: " + sbn.getKey());
                }
                return false;
            }
            for (int i = 0; i < this.mSuppressors.size(); i++) {
                if (this.mSuppressors.get(i).suppressAwakeHeadsUp(notificationEntry)) {
                    if (DEBUG) {
                        Log.d("DesktopNotificationInterruptStateProviderImpl", "No heads up: aborted by suppressor: " + this.mSuppressors.get(i).getName() + " sbnKey=" + sbn.getKey());
                    }
                    return false;
                }
            }
            return true;
        }
    }

    private boolean canAlertCommon(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (this.mNotificationFilter.shouldFilterOut(notificationEntry)) {
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: filtered notification: " + sbn.getKey());
            }
            return false;
        } else if (!sbn.isGroup() || !sbn.getNotification().suppressAlertingDueToGrouping()) {
            for (int i = 0; i < this.mSuppressors.size(); i++) {
                if (this.mSuppressors.get(i).suppressInterruptions(notificationEntry)) {
                    if (DEBUG) {
                        Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: aborted by suppressor: " + this.mSuppressors.get(i).getName() + " sbnKey=" + sbn.getKey());
                    }
                    return false;
                }
            }
            if (!notificationEntry.hasJustLaunchedFullScreenIntent()) {
                return true;
            }
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: recent fullscreen: " + sbn.getKey());
            }
            return false;
        } else {
            if (DEBUG) {
                Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: suppressed due to group alert behavior");
            }
            return false;
        }
    }

    private boolean canAlertAwakeCommon(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        for (int i = 0; i < this.mSuppressors.size(); i++) {
            if (this.mSuppressors.get(i).suppressAwakeInterruptions(notificationEntry)) {
                if (DEBUG) {
                    Log.d("DesktopNotificationInterruptStateProviderImpl", "No alerting: aborted by suppressor: " + this.mSuppressors.get(i).getName() + " sbnKey=" + sbn.getKey());
                }
                return false;
            }
        }
        return true;
    }

    private boolean isSnoozedPackage(StatusBarNotification statusBarNotification) {
        return this.mHeadsUpManager.isSnoozed(statusBarNotification.getPackageName());
    }
}
