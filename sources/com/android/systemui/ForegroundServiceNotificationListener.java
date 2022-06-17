package com.android.systemui;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.util.time.SystemClock;

public class ForegroundServiceNotificationListener {
    private final Context mContext;
    private final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public final ForegroundServiceController mForegroundServiceController;

    public ForegroundServiceNotificationListener(Context context, ForegroundServiceController foregroundServiceController, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline, SystemClock systemClock) {
        this.mContext = context;
        this.mForegroundServiceController = foregroundServiceController;
        this.mEntryManager = notificationEntryManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPendingEntryAdded(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
        });
        notifPipeline.addCollectionListener(new NotifCollectionListener() {
            public void onEntryAdded(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryUpdated(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
        });
    }

    /* access modifiers changed from: private */
    public void addNotification(NotificationEntry notificationEntry, int i) {
        updateNotification(notificationEntry, i);
    }

    /* access modifiers changed from: private */
    public void removeNotification(final StatusBarNotification statusBarNotification) {
        this.mForegroundServiceController.updateUserState(statusBarNotification.getUserId(), new ForegroundServiceController.UserStateUpdateCallback() {
            public boolean updateUserState(ForegroundServicesUserState foregroundServicesUserState) {
                if (!ForegroundServiceNotificationListener.this.mForegroundServiceController.isDisclosureNotification(statusBarNotification)) {
                    return foregroundServicesUserState.removeNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
                }
                foregroundServicesUserState.setRunningServices((String[]) null, 0);
                return true;
            }
        }, false);
    }

    /* access modifiers changed from: private */
    public void updateNotification(NotificationEntry notificationEntry, int i) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        this.mForegroundServiceController.updateUserState(sbn.getUserId(), new ForegroundServiceNotificationListener$$ExternalSyntheticLambda0(this, sbn, i), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateNotification$0(StatusBarNotification statusBarNotification, int i, ForegroundServicesUserState foregroundServicesUserState) {
        if (this.mForegroundServiceController.isDisclosureNotification(statusBarNotification)) {
            Bundle bundle = statusBarNotification.getNotification().extras;
            if (bundle != null) {
                foregroundServicesUserState.setRunningServices(bundle.getStringArray("android.foregroundApps"), statusBarNotification.getNotification().when);
            }
        } else {
            foregroundServicesUserState.removeNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
            if ((statusBarNotification.getNotification().flags & 64) != 0 && i > 1) {
                foregroundServicesUserState.addImportantNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
            }
            if (Notification.Builder.recoverBuilder(this.mContext, statusBarNotification.getNotification()).usesStandardHeader()) {
                foregroundServicesUserState.addStandardLayoutNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
            }
        }
        return true;
    }
}
