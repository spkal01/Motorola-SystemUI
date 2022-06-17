package com.android.systemui.statusbar.notification.collection.coordinator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class KeyguardCoordinator implements Coordinator {
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private boolean mHideSilentNotificationsOnLockscreen;
    private final HighPriorityProvider mHighPriorityProvider;
    private final KeyguardStateController.Callback mKeyguardCallback = new KeyguardStateController.Callback() {
        public void onUnlockedChanged() {
            KeyguardCoordinator.this.invalidateListFromFilter("onUnlockedChanged");
        }

        public void onKeyguardShowingChanged() {
            KeyguardCoordinator.this.invalidateListFromFilter("onKeyguardShowingChanged");
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onStrongAuthStateChanged(int i) {
            KeyguardCoordinator.this.invalidateListFromFilter("onStrongAuthStateChanged");
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainHandler;
    private final NotifFilter mNotifFilter = new NotifFilter("KeyguardCoordinator") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            int i;
            StatusBarNotification sbn = notificationEntry.getSbn();
            if (!KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                return false;
            }
            if (!KeyguardCoordinator.this.mLockscreenUserManager.shouldShowLockscreenNotifications()) {
                return true;
            }
            int currentUserId = KeyguardCoordinator.this.mLockscreenUserManager.getCurrentUserId();
            if (sbn.getUser().getIdentifier() == -1) {
                i = currentUserId;
            } else {
                i = sbn.getUser().getIdentifier();
            }
            if (KeyguardCoordinator.this.mKeyguardUpdateMonitor.isUserInLockdown(currentUserId) || KeyguardCoordinator.this.mKeyguardUpdateMonitor.isUserInLockdown(i) || ((KeyguardCoordinator.this.mLockscreenUserManager.isLockscreenPublicMode(currentUserId) || KeyguardCoordinator.this.mLockscreenUserManager.isLockscreenPublicMode(i)) && (notificationEntry.getRanking().getLockscreenVisibilityOverride() == -1 || !KeyguardCoordinator.this.mLockscreenUserManager.userAllowsNotificationsInPublic(currentUserId) || !KeyguardCoordinator.this.mLockscreenUserManager.userAllowsNotificationsInPublic(i)))) {
                return true;
            }
            if (notificationEntry.getParent() != null) {
                if (KeyguardCoordinator.this.priorityExceedsLockscreenShowingThreshold(notificationEntry.getParent())) {
                    return false;
                }
            }
            return !KeyguardCoordinator.this.priorityExceedsLockscreenShowingThreshold(notificationEntry);
        }
    };
    private final StatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() {
        public void onStateChanged(int i) {
            KeyguardCoordinator.this.invalidateListFromFilter("onStatusBarStateChanged");
        }
    };

    public KeyguardCoordinator(Context context, Handler handler, KeyguardStateController keyguardStateController, NotificationLockscreenUserManager notificationLockscreenUserManager, BroadcastDispatcher broadcastDispatcher, StatusBarStateController statusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, HighPriorityProvider highPriorityProvider) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mKeyguardStateController = keyguardStateController;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mStatusBarStateController = statusBarStateController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mHighPriorityProvider = highPriorityProvider;
    }

    public void attach(NotifPipeline notifPipeline) {
        readShowSilentNotificationSetting();
        setupInvalidateNotifListCallbacks();
        notifPipeline.addFinalizeFilter(this.mNotifFilter);
    }

    /* access modifiers changed from: private */
    public boolean priorityExceedsLockscreenShowingThreshold(ListEntry listEntry) {
        if (listEntry == null) {
            return false;
        }
        if (this.mHideSilentNotificationsOnLockscreen) {
            return this.mHighPriorityProvider.isHighPriority(listEntry);
        }
        if (listEntry.getRepresentativeEntry() == null || listEntry.getRepresentativeEntry().getRanking().isAmbient()) {
            return false;
        }
        return true;
    }

    private void setupInvalidateNotifListCallbacks() {
        this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        C15532 r0 = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z, Uri uri) {
                if (uri.equals(Settings.Secure.getUriFor("lock_screen_show_silent_notifications"))) {
                    KeyguardCoordinator.this.readShowSilentNotificationSetting();
                }
                if (KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                    KeyguardCoordinator keyguardCoordinator = KeyguardCoordinator.this;
                    keyguardCoordinator.invalidateListFromFilter("Settings " + uri + " changed");
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, r0, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_allow_private_notifications"), true, r0, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, r0);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_silent_notifications"), false, r0, -1);
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mBroadcastDispatcher.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                    KeyguardCoordinator.this.invalidateListFromFilter(intent.getAction());
                }
            }
        }, new IntentFilter("android.intent.action.USER_SWITCHED"));
    }

    /* access modifiers changed from: private */
    public void invalidateListFromFilter(String str) {
        this.mNotifFilter.invalidateList();
    }

    /* access modifiers changed from: private */
    public void readShowSilentNotificationSetting() {
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) != 0) {
            z = false;
        }
        this.mHideSilentNotificationsOnLockscreen = z;
    }
}
