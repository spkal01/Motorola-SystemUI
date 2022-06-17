package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class NotificationLockscreenUserManagerImpl implements Dumpable, NotificationLockscreenUserManager, StatusBarStateController.StateListener {
    protected final BroadcastReceiver mAllUsersReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(intent.getAction()) && NotificationLockscreenUserManagerImpl.this.isCurrentProfile(getSendingUserId())) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED");
            }
        }
    };
    private boolean mAllowLockscreenRemoteInput;
    protected final BroadcastReceiver mBaseBroadcastReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r11, android.content.Intent r12) {
            /*
                r10 = this;
                java.lang.Class<com.android.systemui.statusbar.notification.stack.CliNotificationStackClient> r11 = com.android.systemui.statusbar.notification.stack.CliNotificationStackClient.class
                java.lang.String r0 = r12.getAction()
                r0.hashCode()
                int r1 = r0.hashCode()
                r2 = 1
                r3 = 0
                r4 = -1
                switch(r1) {
                    case -1238404651: goto L_0x004c;
                    case -864107122: goto L_0x0041;
                    case -598152660: goto L_0x0036;
                    case 833559602: goto L_0x002b;
                    case 959232034: goto L_0x0020;
                    case 1121780209: goto L_0x0015;
                    default: goto L_0x0013;
                }
            L_0x0013:
                r0 = r4
                goto L_0x0056
            L_0x0015:
                java.lang.String r1 = "android.intent.action.USER_ADDED"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x001e
                goto L_0x0013
            L_0x001e:
                r0 = 5
                goto L_0x0056
            L_0x0020:
                java.lang.String r1 = "android.intent.action.USER_SWITCHED"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x0029
                goto L_0x0013
            L_0x0029:
                r0 = 4
                goto L_0x0056
            L_0x002b:
                java.lang.String r1 = "android.intent.action.USER_UNLOCKED"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x0034
                goto L_0x0013
            L_0x0034:
                r0 = 3
                goto L_0x0056
            L_0x0036:
                java.lang.String r1 = "com.android.systemui.statusbar.work_challenge_unlocked_notification_action"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x003f
                goto L_0x0013
            L_0x003f:
                r0 = 2
                goto L_0x0056
            L_0x0041:
                java.lang.String r1 = "android.intent.action.MANAGED_PROFILE_AVAILABLE"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x004a
                goto L_0x0013
            L_0x004a:
                r0 = r2
                goto L_0x0056
            L_0x004c:
                java.lang.String r1 = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"
                boolean r0 = r0.equals(r1)
                if (r0 != 0) goto L_0x0055
                goto L_0x0013
            L_0x0055:
                r0 = r3
            L_0x0056:
                switch(r0) {
                    case 0: goto L_0x0142;
                    case 1: goto L_0x0142;
                    case 2: goto L_0x00f4;
                    case 3: goto L_0x00be;
                    case 4: goto L_0x005b;
                    case 5: goto L_0x0142;
                    default: goto L_0x0059;
                }
            L_0x0059:
                goto L_0x0147
            L_0x005b:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                java.lang.String r0 = "android.intent.extra.user_handle"
                int r12 = r12.getIntExtra(r0, r4)
                r11.mCurrentUserId = r12
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r11.updateCurrentProfilesCache()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                java.lang.String r12 = "userId "
                r11.append(r12)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r12 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                int r12 = r12.mCurrentUserId
                r11.append(r12)
                java.lang.String r12 = " is in the house"
                r11.append(r12)
                java.lang.String r11 = r11.toString()
                java.lang.String r12 = "LockscreenUserManager"
                android.util.Log.v(r12, r11)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r11.updateLockscreenNotificationSetting()
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r11.updatePublicMode()
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r11 = r11.getEntryManager()
                java.lang.String r12 = "user switched"
                r11.reapplyFilterAndSort(r12)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r11 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                java.util.List r11 = r11.mListeners
                java.util.Iterator r11 = r11.iterator()
            L_0x00aa:
                boolean r12 = r11.hasNext()
                if (r12 == 0) goto L_0x0147
                java.lang.Object r12 = r11.next()
                com.android.systemui.statusbar.NotificationLockscreenUserManager$UserChangedListener r12 = (com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener) r12
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r0 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                int r0 = r0.mCurrentUserId
                r12.onUserChanged(r0)
                goto L_0x00aa
            L_0x00be:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r12 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                android.content.Context r12 = r12.mContext
                boolean r12 = com.android.systemui.moto.DesktopFeature.isDesktopDisplayContext(r12)
                if (r12 != 0) goto L_0x00d3
                java.lang.Class<com.android.systemui.recents.OverviewProxyService> r12 = com.android.systemui.recents.OverviewProxyService.class
                java.lang.Object r12 = com.android.systemui.Dependency.get(r12)
                com.android.systemui.recents.OverviewProxyService r12 = (com.android.systemui.recents.OverviewProxyService) r12
                r12.startConnectionToCurrentUser()
            L_0x00d3:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r10 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                android.content.Context r10 = r10.mContext
                com.android.systemui.moto.MotoFeature r10 = com.android.systemui.moto.MotoFeature.getInstance(r10)
                boolean r10 = r10.isSupportCli()
                if (r10 == 0) goto L_0x0147
                java.lang.Object r10 = com.android.systemui.Dependency.get(r11)
                com.android.systemui.statusbar.notification.stack.CliNotificationStackClient r10 = (com.android.systemui.statusbar.notification.stack.CliNotificationStackClient) r10
                r10.updateEnabledState()
                java.lang.Object r10 = com.android.systemui.Dependency.get(r11)
                com.android.systemui.statusbar.notification.stack.CliNotificationStackClient r10 = (com.android.systemui.statusbar.notification.stack.CliNotificationStackClient) r10
                r10.startConnectionToCurrentUser()
                goto L_0x0147
            L_0x00f4:
                java.lang.String r11 = "android.intent.extra.INTENT"
                android.os.Parcelable r11 = r12.getParcelableExtra(r11)
                r5 = r11
                android.content.IntentSender r5 = (android.content.IntentSender) r5
                java.lang.String r11 = "android.intent.extra.INDEX"
                java.lang.String r11 = r12.getStringExtra(r11)
                if (r5 == 0) goto L_0x0110
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r12 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this     // Catch:{ SendIntentException -> 0x0110 }
                android.content.Context r4 = r12.mContext     // Catch:{ SendIntentException -> 0x0110 }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r4.startIntentSender(r5, r6, r7, r8, r9)     // Catch:{ SendIntentException -> 0x0110 }
            L_0x0110:
                if (r11 == 0) goto L_0x0147
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r12 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r12 = r12.getEntryManager()
                com.android.systemui.statusbar.notification.collection.NotificationEntry r12 = r12.getActiveNotificationUnfiltered(r11)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r0 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r0 = r0.getEntryManager()
                int r0 = r0.getActiveNotificationsCount()
                if (r12 == 0) goto L_0x0130
                android.service.notification.NotificationListenerService$Ranking r1 = r12.getRanking()
                int r3 = r1.getRank()
            L_0x0130:
                com.android.internal.statusbar.NotificationVisibility$NotificationLocation r12 = com.android.systemui.statusbar.notification.logging.NotificationLogger.getNotificationLocation(r12)
                com.android.internal.statusbar.NotificationVisibility r12 = com.android.internal.statusbar.NotificationVisibility.obtain(r11, r3, r0, r2, r12)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r10 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.NotificationClickNotifier r10 = r10.mClickNotifier
                r10.onNotificationClick(r11, r12)
                goto L_0x0147
            L_0x0142:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r10 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r10.updateCurrentProfilesCache()
            L_0x0147:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.C14602.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    /* access modifiers changed from: private */
    public final NotificationClickNotifier mClickNotifier;
    protected final Context mContext;
    protected final SparseArray<UserInfo> mCurrentManagedProfiles = new SparseArray<>();
    protected final SparseArray<UserInfo> mCurrentProfiles = new SparseArray<>();
    protected int mCurrentUserId = 0;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    private NotificationEntryManager mEntryManager;
    protected KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private List<NotificationLockscreenUserManager.KeyguardNotificationSuppressor> mKeyguardSuppressors = new ArrayList();
    /* access modifiers changed from: private */
    public final List<NotificationLockscreenUserManager.UserChangedListener> mListeners = new ArrayList();
    private final Object mLock = new Object();
    private LockPatternUtils mLockPatternUtils;
    private final SparseBooleanArray mLockscreenPublicMode = new SparseBooleanArray();
    protected ContentObserver mLockscreenSettingsObserver;
    private final Handler mMainHandler;
    protected NotificationPresenter mPresenter;
    protected ContentObserver mSettingsObserver;
    private boolean mShowLockscreenNotifications;
    private int mState = 0;
    private final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final SparseBooleanArray mUsersAllowingNotifications = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final SparseBooleanArray mUsersAllowingPrivateNotifications = new SparseBooleanArray();
    private final SparseBooleanArray mUsersWithSeperateWorkChallenge = new SparseBooleanArray();

    /* access modifiers changed from: private */
    public NotificationEntryManager getEntryManager() {
        if (this.mEntryManager == null) {
            this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        }
        return this.mEntryManager;
    }

    public NotificationLockscreenUserManagerImpl(Context context, BroadcastDispatcher broadcastDispatcher, DevicePolicyManager devicePolicyManager, UserManager userManager, NotificationClickNotifier notificationClickNotifier, KeyguardManager keyguardManager, StatusBarStateController statusBarStateController, Handler handler, DeviceProvisionedController deviceProvisionedController, KeyguardStateController keyguardStateController) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mUserManager = userManager;
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mClickNotifier = notificationClickNotifier;
        statusBarStateController.addCallback(this);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mKeyguardManager = keyguardManager;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mKeyguardStateController = keyguardStateController;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter) {
        this.mPresenter = notificationPresenter;
        this.mLockscreenSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_SHOW_NOTIFICATIONS, or LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS change");
            }
        };
        this.mSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z) {
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                if (NotificationLockscreenUserManagerImpl.this.mDeviceProvisionedController.isDeviceProvisioned()) {
                    NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_ALLOW_REMOTE_INPUT or ZEN_MODE change");
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_allow_private_notifications"), true, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
        this.mBroadcastDispatcher.registerReceiver(this.mAllUsersReceiver, new IntentFilter("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED"), (Executor) null, UserHandle.ALL);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        this.mBroadcastDispatcher.registerReceiver(this.mBaseBroadcastReceiver, intentFilter, (Executor) null, UserHandle.ALL);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        this.mContext.registerReceiver(this.mBaseBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", (Handler) null);
        updateCurrentProfilesCache();
        this.mSettingsObserver.onChange(false);
    }

    public boolean shouldShowLockscreenNotifications() {
        return this.mShowLockscreenNotifications;
    }

    public boolean shouldAllowLockscreenRemoteInput() {
        return this.mAllowLockscreenRemoteInput;
    }

    public boolean isCurrentProfile(int i) {
        boolean z;
        synchronized (this.mLock) {
            if (i != -1) {
                try {
                    if (this.mCurrentProfiles.get(i) == null) {
                        z = false;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            z = true;
        }
        return z;
    }

    private boolean shouldTemporarilyHideNotifications(int i) {
        if (i == -1) {
            i = this.mCurrentUserId;
        }
        return ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isUserInLockdown(i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r0 = r1.mCurrentUserId;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldHideNotifications(int r2) {
        /*
            r1 = this;
            boolean r0 = r1.isLockscreenPublicMode(r2)
            if (r0 == 0) goto L_0x000c
            boolean r0 = r1.userAllowsNotificationsInPublic(r2)
            if (r0 == 0) goto L_0x001c
        L_0x000c:
            int r0 = r1.mCurrentUserId
            if (r2 == r0) goto L_0x0016
            boolean r0 = r1.shouldHideNotifications((int) r0)
            if (r0 != 0) goto L_0x001c
        L_0x0016:
            boolean r1 = r1.shouldTemporarilyHideNotifications(r2)
            if (r1 == 0) goto L_0x001e
        L_0x001c:
            r1 = 1
            goto L_0x001f
        L_0x001e:
            r1 = 0
        L_0x001f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.shouldHideNotifications(int):boolean");
    }

    public boolean shouldHideNotifications(String str) {
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        NotificationEntry activeNotificationUnfiltered = getEntryManager().getActiveNotificationUnfiltered(str);
        if (!isLockscreenPublicMode(this.mCurrentUserId) || activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getLockscreenVisibilityOverride() != -1) {
            return false;
        }
        return true;
    }

    public boolean shouldShowOnKeyguard(NotificationEntry notificationEntry) {
        boolean z;
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return false;
        }
        for (int i = 0; i < this.mKeyguardSuppressors.size(); i++) {
            if (this.mKeyguardSuppressors.get(i).shouldSuppressOnKeyguard(notificationEntry)) {
                return false;
            }
        }
        if (hideSilentNotificationsOnLockscreen()) {
            z = notificationEntry.getBucket() == 1 || (notificationEntry.getBucket() != 6 && notificationEntry.getImportance() >= 3);
        } else {
            z = !notificationEntry.getRanking().isAmbient();
        }
        if (!this.mShowLockscreenNotifications || !z) {
            return false;
        }
        return true;
    }

    private boolean hideSilentNotificationsOnLockscreen() {
        return ((Boolean) DejankUtils.whitelistIpcs(new NotificationLockscreenUserManagerImpl$$ExternalSyntheticLambda1(this))).booleanValue();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hideSilentNotificationsOnLockscreen$0() {
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) != 0) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private void setShowLockscreenNotifications(boolean z) {
        this.mShowLockscreenNotifications = z;
    }

    private void setLockscreenAllowRemoteInput(boolean z) {
        this.mAllowLockscreenRemoteInput = z;
    }

    /* access modifiers changed from: protected */
    public void updateLockscreenNotificationSetting() {
        boolean z = true;
        boolean z2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, this.mCurrentUserId) != 0;
        boolean z3 = (this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, this.mCurrentUserId) & 4) == 0;
        if (!z2 || !z3) {
            z = false;
        }
        setShowLockscreenNotifications(z);
        setLockscreenAllowRemoteInput(false);
    }

    public boolean userAllowsPrivateNotificationsInPublic(int i) {
        boolean z = true;
        if (i == -1) {
            return true;
        }
        if (this.mUsersAllowingPrivateNotifications.indexOfKey(i) >= 0) {
            return this.mUsersAllowingPrivateNotifications.get(i);
        }
        boolean z2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0, i) != 0;
        boolean adminAllowsKeyguardFeature = adminAllowsKeyguardFeature(i, 8);
        if (!z2 || !adminAllowsKeyguardFeature) {
            z = false;
        }
        this.mUsersAllowingPrivateNotifications.append(i, z);
        return z;
    }

    private boolean adminAllowsKeyguardFeature(int i, int i2) {
        if (i == -1 || (this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, i) & i2) == 0) {
            return true;
        }
        return false;
    }

    public void setLockscreenPublicMode(boolean z, int i) {
        this.mLockscreenPublicMode.put(i, z);
    }

    public boolean isLockscreenPublicMode(int i) {
        if (i == -1) {
            return this.mLockscreenPublicMode.get(this.mCurrentUserId, false);
        }
        return this.mLockscreenPublicMode.get(i, false);
    }

    public boolean needsSeparateWorkChallenge(int i) {
        return this.mUsersWithSeperateWorkChallenge.get(i, false);
    }

    public boolean userAllowsNotificationsInPublic(int i) {
        boolean z = true;
        if (isCurrentProfile(i) && i != this.mCurrentUserId) {
            return true;
        }
        if (this.mUsersAllowingNotifications.indexOfKey(i) >= 0) {
            return this.mUsersAllowingNotifications.get(i);
        }
        boolean z2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0, i) != 0;
        boolean adminAllowsKeyguardFeature = adminAllowsKeyguardFeature(i, 4);
        boolean privateNotificationsAllowed = this.mKeyguardManager.getPrivateNotificationsAllowed();
        if (!z2 || !adminAllowsKeyguardFeature || !privateNotificationsAllowed) {
            z = false;
        }
        this.mUsersAllowingNotifications.append(i, z);
        return z;
    }

    public boolean needsRedaction(NotificationEntry notificationEntry) {
        int userId = notificationEntry.getSbn().getUserId();
        boolean z = (!this.mCurrentManagedProfiles.contains(userId) && (userAllowsPrivateNotificationsInPublic(this.mCurrentUserId) ^ true)) || (userAllowsPrivateNotificationsInPublic(userId) ^ true);
        boolean z2 = notificationEntry.getSbn().getNotification().visibility == 0;
        if (packageHasVisibilityOverride(notificationEntry.getSbn().getKey())) {
            return true;
        }
        if (!z2 || !z) {
            return false;
        }
        return true;
    }

    private boolean packageHasVisibilityOverride(String str) {
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        NotificationEntry activeNotificationUnfiltered = getEntryManager().getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getLockscreenVisibilityOverride() != 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void updateCurrentProfilesCache() {
        synchronized (this.mLock) {
            this.mCurrentProfiles.clear();
            this.mCurrentManagedProfiles.clear();
            UserManager userManager = this.mUserManager;
            if (userManager != null) {
                for (UserInfo userInfo : userManager.getProfiles(this.mCurrentUserId)) {
                    this.mCurrentProfiles.put(userInfo.id, userInfo);
                    if ("android.os.usertype.profile.MANAGED".equals(userInfo.userType)) {
                        this.mCurrentManagedProfiles.put(userInfo.id, userInfo);
                    }
                }
            }
        }
        this.mMainHandler.post(new NotificationLockscreenUserManagerImpl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCurrentProfilesCache$1() {
        for (NotificationLockscreenUserManager.UserChangedListener onCurrentProfilesChanged : this.mListeners) {
            onCurrentProfilesChanged.onCurrentProfilesChanged(this.mCurrentProfiles);
        }
    }

    public boolean isAnyProfilePublicMode() {
        synchronized (this.mLock) {
            for (int size = this.mCurrentProfiles.size() - 1; size >= 0; size--) {
                if (isLockscreenPublicMode(this.mCurrentProfiles.valueAt(size).id)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int getCurrentUserId() {
        return this.mCurrentUserId;
    }

    public SparseArray<UserInfo> getCurrentProfiles() {
        return this.mCurrentProfiles;
    }

    public void onStateChanged(int i) {
        this.mState = i;
        updatePublicMode();
    }

    public void updatePublicMode() {
        boolean z = this.mState != 0 || this.mKeyguardStateController.isShowing();
        boolean z2 = z && this.mKeyguardStateController.isMethodSecure();
        SparseArray<UserInfo> currentProfiles = getCurrentProfiles();
        this.mUsersWithSeperateWorkChallenge.clear();
        for (int size = currentProfiles.size() - 1; size >= 0; size--) {
            int i = currentProfiles.valueAt(size).id;
            boolean booleanValue = ((Boolean) DejankUtils.whitelistIpcs(new NotificationLockscreenUserManagerImpl$$ExternalSyntheticLambda2(this, i))).booleanValue();
            setLockscreenPublicMode((z2 || i == getCurrentUserId() || !booleanValue || !this.mLockPatternUtils.isSecure(i)) ? z2 : z || this.mKeyguardManager.isDeviceLocked(i), i);
            this.mUsersWithSeperateWorkChallenge.put(i, booleanValue);
        }
        getEntryManager().updateNotifications("NotificationLockscreenUserManager.updatePublicMode");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updatePublicMode$2(int i) {
        return Boolean.valueOf(this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i));
    }

    public void addUserChangedListener(NotificationLockscreenUserManager.UserChangedListener userChangedListener) {
        this.mListeners.add(userChangedListener);
    }

    public void addKeyguardNotificationSuppressor(NotificationLockscreenUserManager.KeyguardNotificationSuppressor keyguardNotificationSuppressor) {
        this.mKeyguardSuppressors.add(keyguardNotificationSuppressor);
    }

    public void removeUserChangedListener(NotificationLockscreenUserManager.UserChangedListener userChangedListener) {
        this.mListeners.remove(userChangedListener);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationLockscreenUserManager state:");
        printWriter.print("  mCurrentUserId=");
        printWriter.println(this.mCurrentUserId);
        printWriter.print("  mShowLockscreenNotifications=");
        printWriter.println(this.mShowLockscreenNotifications);
        printWriter.print("  mAllowLockscreenRemoteInput=");
        printWriter.println(this.mAllowLockscreenRemoteInput);
        printWriter.print("  mCurrentProfiles=");
        synchronized (this.mLock) {
            for (int size = this.mCurrentProfiles.size() - 1; size >= 0; size += -1) {
                printWriter.print("" + this.mCurrentProfiles.valueAt(size).id + " ");
            }
        }
        printWriter.print("  mCurrentManagedProfiles=");
        synchronized (this.mLock) {
            for (int size2 = this.mCurrentManagedProfiles.size() - 1; size2 >= 0; size2 += -1) {
                printWriter.print("" + this.mCurrentManagedProfiles.valueAt(size2).id + " ");
            }
        }
        printWriter.println();
    }
}
