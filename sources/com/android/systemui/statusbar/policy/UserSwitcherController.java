package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyCallback;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManagerGlobal;
import android.widget.BaseAdapter;
import androidx.appcompat.R$styleable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.GuestResumeSessionReceiver;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.SystemUISecondaryUserService;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.QSUserSwitcherEvent;
import com.android.systemui.p006qs.tiles.UserDetailView;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.user.CreateUserActivity;
import com.android.systemui.util.settings.SecureSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Provider;

public class UserSwitcherController implements Dumpable {
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    /* access modifiers changed from: private */
    public final IActivityTaskManager mActivityTaskManager;
    private final ArrayList<WeakReference<BaseUserAdapter>> mAdapters = new ArrayList<>();
    @VisibleForTesting
    Dialog mAddUserDialog;
    /* access modifiers changed from: private */
    public boolean mAddUsersFromLockScreen;
    /* access modifiers changed from: private */
    public final Executor mBgExecutor;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final KeyguardStateController.Callback mCallback;
    protected final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    @VisibleForTesting
    AlertDialog mExitGuestDialog;
    /* access modifiers changed from: private */
    public FalsingManager mFalsingManager;
    private SparseBooleanArray mForcePictureLoadForUserId = new SparseBooleanArray(2);
    /* access modifiers changed from: private */
    public final DeviceProvisionedController.DeviceProvisionedListener mGuaranteeGuestPresentAfterProvisioned;
    private final AtomicBoolean mGuestCreationScheduled;
    /* access modifiers changed from: private */
    public final AtomicBoolean mGuestIsResetting;
    @VisibleForTesting
    final GuestResumeSessionReceiver mGuestResumeSessionReceiver;
    /* access modifiers changed from: private */
    public final boolean mGuestUserAutoCreated;
    protected final Handler mHandler;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public int mLastNonGuestUser = 0;
    @VisibleForTesting
    boolean mPauseRefreshUsers;
    private final TelephonyCallback.CallStateListener mPhoneStateListener = new TelephonyCallback.CallStateListener() {
        private int mCallState;

        public void onCallStateChanged(int i) {
            if (this.mCallState != i) {
                this.mCallState = i;
                UserSwitcherController.this.refreshUsers(-10000);
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            int i = -10000;
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                AlertDialog alertDialog = UserSwitcherController.this.mExitGuestDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    UserSwitcherController.this.mExitGuestDialog.cancel();
                    UserSwitcherController.this.mExitGuestDialog = null;
                }
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
                UserInfo userInfo = UserSwitcherController.this.mUserManager.getUserInfo(intExtra);
                int size = UserSwitcherController.this.mUsers.size();
                int i2 = 0;
                while (i2 < size) {
                    UserRecord userRecord = (UserRecord) UserSwitcherController.this.mUsers.get(i2);
                    UserInfo userInfo2 = userRecord.info;
                    if (userInfo2 != null) {
                        boolean z2 = userInfo2.id == intExtra;
                        if (userRecord.isCurrent != z2) {
                            UserSwitcherController.this.mUsers.set(i2, userRecord.copyWithIsCurrent(z2));
                        }
                        if (z2 && !userRecord.isGuest) {
                            int unused = UserSwitcherController.this.mLastNonGuestUser = userRecord.info.id;
                        }
                        if ((userInfo == null || !userInfo.isAdmin()) && userRecord.isRestricted) {
                            UserSwitcherController.this.mUsers.remove(i2);
                            i2--;
                        }
                    }
                    i2++;
                }
                UserSwitcherController.this.notifyAdapters();
                if (UserSwitcherController.this.mSecondaryUser != -10000) {
                    context.stopServiceAsUser(UserSwitcherController.this.mSecondaryUserServiceIntent, UserHandle.of(UserSwitcherController.this.mSecondaryUser));
                    int unused2 = UserSwitcherController.this.mSecondaryUser = -10000;
                }
                if (!(userInfo == null || userInfo.id == 0)) {
                    context.startServiceAsUser(UserSwitcherController.this.mSecondaryUserServiceIntent, UserHandle.of(userInfo.id));
                    int unused3 = UserSwitcherController.this.mSecondaryUser = userInfo.id;
                }
                if (UserSwitcherController.this.mGuestUserAutoCreated) {
                    UserSwitcherController.this.guaranteeGuestPresent();
                }
            } else {
                if ("android.intent.action.USER_INFO_CHANGED".equals(intent.getAction())) {
                    i = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                } else if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction()) && intent.getIntExtra("android.intent.extra.user_handle", -10000) != 0) {
                    return;
                }
                z = false;
            }
            UserSwitcherController.this.refreshUsers(i);
            if (z) {
                UserSwitcherController.this.mUnpauseRefreshUsers.run();
            }
        }
    };
    private boolean mResumeUserOnGuestLogout = true;
    /* access modifiers changed from: private */
    public int mSecondaryUser = -10000;
    /* access modifiers changed from: private */
    public Intent mSecondaryUserServiceIntent;
    private final ContentObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public boolean mSimpleUserSwitcher;
    private final TelephonyListenerManager mTelephonyListenerManager;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;
    /* access modifiers changed from: private */
    public final Runnable mUnpauseRefreshUsers = new Runnable() {
        public void run() {
            UserSwitcherController.this.mHandler.removeCallbacks(this);
            UserSwitcherController userSwitcherController = UserSwitcherController.this;
            userSwitcherController.mPauseRefreshUsers = false;
            userSwitcherController.refreshUsers(-10000);
        }
    };
    public final DetailAdapter mUserDetailAdapter;
    protected final UserManager mUserManager;
    protected final UserTracker mUserTracker;
    /* access modifiers changed from: private */
    public ArrayList<UserRecord> mUsers = new ArrayList<>();

    public UserSwitcherController(Context context, UserManager userManager, UserTracker userTracker, KeyguardStateController keyguardStateController, DeviceProvisionedController deviceProvisionedController, DevicePolicyManager devicePolicyManager, Handler handler, ActivityStarter activityStarter, BroadcastDispatcher broadcastDispatcher, UiEventLogger uiEventLogger, FalsingManager falsingManager, TelephonyListenerManager telephonyListenerManager, IActivityTaskManager iActivityTaskManager, UserDetailAdapter userDetailAdapter, SecureSettings secureSettings, Executor executor) {
        UserTracker userTracker2 = userTracker;
        KeyguardStateController keyguardStateController2 = keyguardStateController;
        Handler handler2 = handler;
        BroadcastDispatcher broadcastDispatcher2 = broadcastDispatcher;
        UiEventLogger uiEventLogger2 = uiEventLogger;
        C20766 r10 = new KeyguardStateController.Callback() {
            public void onKeyguardShowingChanged() {
                if (!UserSwitcherController.this.mKeyguardStateController.isShowing()) {
                    UserSwitcherController userSwitcherController = UserSwitcherController.this;
                    userSwitcherController.mHandler.post(new UserSwitcherController$6$$ExternalSyntheticLambda0(userSwitcherController));
                    return;
                }
                UserSwitcherController.this.notifyAdapters();
            }
        };
        this.mCallback = r10;
        this.mGuaranteeGuestPresentAfterProvisioned = new DeviceProvisionedController.DeviceProvisionedListener() {
            public void onDeviceProvisionedChanged() {
                if (UserSwitcherController.this.isDeviceAllowedToAddGuest()) {
                    UserSwitcherController.this.mBgExecutor.execute(new UserSwitcherController$7$$ExternalSyntheticLambda0(this));
                    UserSwitcherController.this.guaranteeGuestPresent();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onDeviceProvisionedChanged$0() {
                UserSwitcherController.this.mDeviceProvisionedController.removeCallback(UserSwitcherController.this.mGuaranteeGuestPresentAfterProvisioned);
            }
        };
        this.mContext = context;
        this.mUserTracker = userTracker2;
        this.mBroadcastDispatcher = broadcastDispatcher2;
        this.mTelephonyListenerManager = telephonyListenerManager;
        this.mActivityTaskManager = iActivityTaskManager;
        this.mUiEventLogger = uiEventLogger2;
        this.mFalsingManager = falsingManager;
        GuestResumeSessionReceiver guestResumeSessionReceiver = new GuestResumeSessionReceiver(this, userTracker2, uiEventLogger2, secureSettings);
        this.mGuestResumeSessionReceiver = guestResumeSessionReceiver;
        this.mUserDetailAdapter = userDetailAdapter;
        this.mBgExecutor = executor;
        if (!UserManager.isGuestUserEphemeral()) {
            guestResumeSessionReceiver.register(broadcastDispatcher2);
        }
        this.mGuestUserAutoCreated = context.getResources().getBoolean(17891622);
        this.mGuestIsResetting = new AtomicBoolean();
        this.mGuestCreationScheduled = new AtomicBoolean();
        this.mKeyguardStateController = keyguardStateController2;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mHandler = handler2;
        this.mActivityStarter = activityStarter;
        this.mUserManager = userManager;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        broadcastDispatcher2.registerReceiver(this.mReceiver, intentFilter, (Executor) null, UserHandle.SYSTEM);
        this.mSimpleUserSwitcher = shouldUseSimpleUserSwitcher();
        this.mSecondaryUserServiceIntent = new Intent(context, SystemUISecondaryUserService.class);
        IntentFilter intentFilter2 = new IntentFilter();
        context.registerReceiverAsUser(this.mReceiver, UserHandle.SYSTEM, intentFilter2, "com.android.systemui.permission.SELF", (Handler) null);
        C20711 r2 = new ContentObserver(handler2) {
            public void onChange(boolean z) {
                UserSwitcherController userSwitcherController = UserSwitcherController.this;
                boolean unused = userSwitcherController.mSimpleUserSwitcher = userSwitcherController.shouldUseSimpleUserSwitcher();
                UserSwitcherController userSwitcherController2 = UserSwitcherController.this;
                boolean z2 = false;
                if (Settings.Global.getInt(userSwitcherController2.mContext.getContentResolver(), "add_users_when_locked", 0) != 0) {
                    z2 = true;
                }
                boolean unused2 = userSwitcherController2.mAddUsersFromLockScreen = z2;
                UserSwitcherController.this.refreshUsers(-10000);
            }
        };
        this.mSettingsObserver = r2;
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("lockscreenSimpleUserSwitcher"), true, r2);
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("add_users_when_locked"), true, r2);
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("allow_user_switching_when_system_user_locked"), true, r2);
        r2.onChange(false);
        keyguardStateController2.addCallback(r10);
        listenForCallState();
        refreshUsers(-10000);
    }

    /* access modifiers changed from: private */
    public void refreshUsers(int i) {
        UserInfo userInfo;
        if (i != -10000) {
            this.mForcePictureLoadForUserId.put(i, true);
        }
        if (!this.mPauseRefreshUsers) {
            boolean z = this.mForcePictureLoadForUserId.get(-1);
            SparseArray sparseArray = new SparseArray(this.mUsers.size());
            int size = this.mUsers.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserRecord userRecord = this.mUsers.get(i2);
                if (!(userRecord == null || userRecord.picture == null || (userInfo = userRecord.info) == null || z || this.mForcePictureLoadForUserId.get(userInfo.id))) {
                    sparseArray.put(userRecord.info.id, userRecord.picture);
                }
            }
            this.mForcePictureLoadForUserId.clear();
            final boolean z2 = this.mAddUsersFromLockScreen;
            new AsyncTask<SparseArray<Bitmap>, Void, ArrayList<UserRecord>>() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: java.lang.Object} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: android.content.pm.UserInfo} */
                /* access modifiers changed from: protected */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public java.util.ArrayList<com.android.systemui.statusbar.policy.UserSwitcherController.UserRecord> doInBackground(android.util.SparseArray<android.graphics.Bitmap>... r21) {
                    /*
                        r20 = this;
                        r0 = r20
                        r1 = 0
                        r2 = r21[r1]
                        com.android.systemui.statusbar.policy.UserSwitcherController r3 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.content.Context r3 = r3.mContext
                        com.android.systemui.moto.MotoFeature r3 = com.android.systemui.moto.MotoFeature.getInstance(r3)
                        boolean r3 = r3.isSupportPrivacySpace()
                        if (r3 == 0) goto L_0x001a
                        com.android.systemui.statusbar.policy.UserSwitcherController r3 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r3 = r3.mUserManager
                        r3.filterPrivacySpace()
                    L_0x001a:
                        com.android.systemui.statusbar.policy.UserSwitcherController r3 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r3 = r3.mUserManager
                        java.util.List r3 = r3.getAliveUsers()
                        r4 = 0
                        if (r3 != 0) goto L_0x0026
                        return r4
                    L_0x0026:
                        java.util.ArrayList r5 = new java.util.ArrayList
                        int r6 = r3.size()
                        r5.<init>(r6)
                        com.android.systemui.statusbar.policy.UserSwitcherController r6 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        com.android.systemui.settings.UserTracker r6 = r6.mUserTracker
                        int r6 = r6.getUserId()
                        com.android.systemui.statusbar.policy.UserSwitcherController r7 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r8 = r7.mUserManager
                        com.android.systemui.settings.UserTracker r7 = r7.mUserTracker
                        int r7 = r7.getUserId()
                        android.os.UserHandle r7 = android.os.UserHandle.of(r7)
                        int r7 = r8.getUserSwitchability(r7)
                        r8 = 1
                        if (r7 != 0) goto L_0x004e
                        r7 = r8
                        goto L_0x004f
                    L_0x004e:
                        r7 = r1
                    L_0x004f:
                        java.util.Iterator r3 = r3.iterator()
                        r9 = r4
                    L_0x0054:
                        boolean r10 = r3.hasNext()
                        if (r10 == 0) goto L_0x00d9
                        java.lang.Object r10 = r3.next()
                        r12 = r10
                        android.content.pm.UserInfo r12 = (android.content.pm.UserInfo) r12
                        int r10 = r12.id
                        if (r6 != r10) goto L_0x0067
                        r15 = r8
                        goto L_0x0068
                    L_0x0067:
                        r15 = r1
                    L_0x0068:
                        if (r15 == 0) goto L_0x006d
                        r19 = r12
                        goto L_0x006f
                    L_0x006d:
                        r19 = r9
                    L_0x006f:
                        if (r7 != 0) goto L_0x0077
                        if (r15 == 0) goto L_0x0074
                        goto L_0x0077
                    L_0x0074:
                        r18 = r1
                        goto L_0x0079
                    L_0x0077:
                        r18 = r8
                    L_0x0079:
                        boolean r9 = r12.isEnabled()
                        if (r9 == 0) goto L_0x00d5
                        boolean r9 = r12.isGuest()
                        if (r9 == 0) goto L_0x0098
                        com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord r4 = new com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord
                        r11 = 0
                        r13 = 1
                        r14 = 0
                        r16 = 0
                        r9 = r4
                        r10 = r12
                        r12 = r13
                        r13 = r15
                        r15 = r16
                        r16 = r7
                        r9.<init>(r10, r11, r12, r13, r14, r15, r16)
                        goto L_0x00d5
                    L_0x0098:
                        boolean r9 = r12.supportsSwitchToByUser()
                        if (r9 == 0) goto L_0x00d5
                        int r9 = r12.id
                        java.lang.Object r9 = r2.get(r9)
                        android.graphics.Bitmap r9 = (android.graphics.Bitmap) r9
                        if (r9 != 0) goto L_0x00c6
                        com.android.systemui.statusbar.policy.UserSwitcherController r9 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r9 = r9.mUserManager
                        int r10 = r12.id
                        android.graphics.Bitmap r9 = r9.getUserIcon(r10)
                        if (r9 == 0) goto L_0x00c6
                        com.android.systemui.statusbar.policy.UserSwitcherController r10 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.content.Context r10 = r10.mContext
                        android.content.res.Resources r10 = r10.getResources()
                        int r11 = com.android.systemui.R$dimen.max_avatar_size
                        int r10 = r10.getDimensionPixelSize(r11)
                        android.graphics.Bitmap r9 = android.graphics.Bitmap.createScaledBitmap(r9, r10, r10, r8)
                    L_0x00c6:
                        r13 = r9
                        com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord r9 = new com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord
                        r14 = 0
                        r16 = 0
                        r17 = 0
                        r11 = r9
                        r11.<init>(r12, r13, r14, r15, r16, r17, r18)
                        r5.add(r9)
                    L_0x00d5:
                        r9 = r19
                        goto L_0x0054
                    L_0x00d9:
                        int r2 = r5.size()
                        if (r2 > r8) goto L_0x00e1
                        if (r4 == 0) goto L_0x00ea
                    L_0x00e1:
                        com.android.systemui.statusbar.policy.UserSwitcherController r2 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.content.Context r2 = r2.mContext
                        java.lang.String r3 = "HasSeenMultiUser"
                        com.android.systemui.Prefs.putBoolean(r2, r3, r8)
                    L_0x00ea:
                        com.android.systemui.statusbar.policy.UserSwitcherController r2 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r2 = r2.mUserManager
                        android.os.UserHandle r3 = android.os.UserHandle.SYSTEM
                        java.lang.String r6 = "no_add_user"
                        boolean r2 = r2.hasBaseUserRestriction(r6, r3)
                        r2 = r2 ^ r8
                        if (r9 == 0) goto L_0x0107
                        boolean r3 = r9.isAdmin()
                        if (r3 != 0) goto L_0x0103
                        int r3 = r9.id
                        if (r3 != 0) goto L_0x0107
                    L_0x0103:
                        if (r2 == 0) goto L_0x0107
                        r3 = r8
                        goto L_0x0108
                    L_0x0107:
                        r3 = r1
                    L_0x0108:
                        if (r2 == 0) goto L_0x0110
                        boolean r2 = r9
                        if (r2 == 0) goto L_0x0110
                        r2 = r8
                        goto L_0x0111
                    L_0x0110:
                        r2 = r1
                    L_0x0111:
                        if (r3 != 0) goto L_0x0115
                        if (r2 == 0) goto L_0x0119
                    L_0x0115:
                        if (r4 != 0) goto L_0x0119
                        r6 = r8
                        goto L_0x011a
                    L_0x0119:
                        r6 = r1
                    L_0x011a:
                        if (r3 != 0) goto L_0x011e
                        if (r2 == 0) goto L_0x012a
                    L_0x011e:
                        com.android.systemui.statusbar.policy.UserSwitcherController r2 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        android.os.UserManager r2 = r2.mUserManager
                        boolean r2 = r2.canAddMoreUsers()
                        if (r2 == 0) goto L_0x012a
                        r2 = r8
                        goto L_0x012b
                    L_0x012a:
                        r2 = r1
                    L_0x012b:
                        boolean r3 = r9
                        r3 = r3 ^ r8
                        if (r4 != 0) goto L_0x0179
                        com.android.systemui.statusbar.policy.UserSwitcherController r4 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        boolean r4 = r4.mGuestUserAutoCreated
                        if (r4 == 0) goto L_0x0160
                        com.android.systemui.statusbar.policy.UserSwitcherController r4 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        java.util.concurrent.atomic.AtomicBoolean r4 = r4.mGuestIsResetting
                        boolean r4 = r4.get()
                        if (r4 != 0) goto L_0x0149
                        if (r7 == 0) goto L_0x0149
                        r16 = r8
                        goto L_0x014b
                    L_0x0149:
                        r16 = r1
                    L_0x014b:
                        com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord r1 = new com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord
                        r10 = 0
                        r11 = 0
                        r12 = 1
                        r13 = 0
                        r14 = 0
                        r15 = 0
                        r9 = r1
                        r9.<init>(r10, r11, r12, r13, r14, r15, r16)
                        com.android.systemui.statusbar.policy.UserSwitcherController r4 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        r4.checkIfAddUserDisallowedByAdminOnly(r1)
                        r5.add(r1)
                        goto L_0x017c
                    L_0x0160:
                        if (r6 == 0) goto L_0x017c
                        com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord r1 = new com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord
                        r10 = 0
                        r11 = 0
                        r12 = 1
                        r13 = 0
                        r14 = 0
                        r9 = r1
                        r15 = r3
                        r16 = r7
                        r9.<init>(r10, r11, r12, r13, r14, r15, r16)
                        com.android.systemui.statusbar.policy.UserSwitcherController r4 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        r4.checkIfAddUserDisallowedByAdminOnly(r1)
                        r5.add(r1)
                        goto L_0x017c
                    L_0x0179:
                        r5.add(r4)
                    L_0x017c:
                        if (r2 == 0) goto L_0x0194
                        com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord r1 = new com.android.systemui.statusbar.policy.UserSwitcherController$UserRecord
                        r10 = 0
                        r11 = 0
                        r12 = 0
                        r13 = 0
                        r14 = 1
                        r9 = r1
                        r15 = r3
                        r16 = r7
                        r9.<init>(r10, r11, r12, r13, r14, r15, r16)
                        com.android.systemui.statusbar.policy.UserSwitcherController r0 = com.android.systemui.statusbar.policy.UserSwitcherController.this
                        r0.checkIfAddUserDisallowedByAdminOnly(r1)
                        r5.add(r1)
                    L_0x0194:
                        return r5
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.UserSwitcherController.C20722.doInBackground(android.util.SparseArray[]):java.util.ArrayList");
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(ArrayList<UserRecord> arrayList) {
                    if (arrayList != null) {
                        ArrayList unused = UserSwitcherController.this.mUsers = arrayList;
                        UserSwitcherController.this.notifyAdapters();
                    }
                }
            }.execute(new SparseArray[]{sparseArray});
        }
    }

    private void pauseRefreshUsers() {
        if (!this.mPauseRefreshUsers) {
            this.mHandler.postDelayed(this.mUnpauseRefreshUsers, 3000);
            this.mPauseRefreshUsers = true;
        }
    }

    /* access modifiers changed from: private */
    public void notifyAdapters() {
        for (int size = this.mAdapters.size() - 1; size >= 0; size--) {
            BaseUserAdapter baseUserAdapter = (BaseUserAdapter) this.mAdapters.get(size).get();
            if (baseUserAdapter != null) {
                baseUserAdapter.notifyDataSetChanged();
            } else {
                this.mAdapters.remove(size);
            }
        }
    }

    public boolean isSimpleUserSwitcher() {
        return this.mSimpleUserSwitcher;
    }

    public boolean useFullscreenUserSwitcher() {
        int intValue = ((Integer) DejankUtils.whitelistIpcs(new UserSwitcherController$$ExternalSyntheticLambda1(this))).intValue();
        if (intValue != -1) {
            return intValue != 0;
        }
        return this.mContext.getResources().getBoolean(R$bool.config_enableFullscreenUserSwitcher);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Integer lambda$useFullscreenUserSwitcher$0() {
        return Integer.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "enable_fullscreen_user_switcher", -1));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void onUserListItemClicked(UserRecord userRecord) {
        int i;
        UserInfo userInfo;
        if (userRecord.isGuest && userRecord.info == null) {
            i = createGuest();
            if (i != -10000) {
                this.mUiEventLogger.log(QSUserSwitcherEvent.QS_USER_GUEST_ADD);
            } else {
                return;
            }
        } else if (userRecord.isAddUser) {
            showAddUserDialog();
            return;
        } else {
            i = userRecord.info.id;
        }
        int userId = this.mUserTracker.getUserId();
        if (userId == i) {
            if (userRecord.isGuest) {
                showExitGuestDialog(i);
            }
        } else if (!UserManager.isGuestUserEphemeral() || (userInfo = this.mUserManager.getUserInfo(userId)) == null || !userInfo.isGuest()) {
            switchToUserId(i);
        } else {
            showExitGuestDialog(userId, userRecord.resolveId());
        }
    }

    public void switchToUserId(int i) {
        try {
            pauseRefreshUsers();
            ActivityManager.getService().switchUser(i);
        } catch (RemoteException e) {
            Log.e("UserSwitcherController", "Couldn't switch user.", e);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r2.mUserManager.getUserInfo((r0 = r2.mLastNonGuestUser));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showExitGuestDialog(int r3) {
        /*
            r2 = this;
            boolean r0 = r2.mResumeUserOnGuestLogout
            if (r0 == 0) goto L_0x001f
            int r0 = r2.mLastNonGuestUser
            if (r0 == 0) goto L_0x001f
            android.os.UserManager r1 = r2.mUserManager
            android.content.pm.UserInfo r0 = r1.getUserInfo(r0)
            if (r0 == 0) goto L_0x001f
            boolean r1 = r0.isEnabled()
            if (r1 == 0) goto L_0x001f
            boolean r1 = r0.supportsSwitchToByUser()
            if (r1 == 0) goto L_0x001f
            int r0 = r0.id
            goto L_0x0020
        L_0x001f:
            r0 = 0
        L_0x0020:
            r2.showExitGuestDialog(r3, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.UserSwitcherController.showExitGuestDialog(int):void");
    }

    /* access modifiers changed from: protected */
    public void showExitGuestDialog(int i, int i2) {
        AlertDialog alertDialog = this.mExitGuestDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mExitGuestDialog.cancel();
        }
        ExitGuestDialog exitGuestDialog = new ExitGuestDialog(this.mContext, i, i2);
        this.mExitGuestDialog = exitGuestDialog;
        exitGuestDialog.show();
    }

    public void showAddUserDialog() {
        Dialog dialog = this.mAddUserDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mAddUserDialog.cancel();
        }
        AddUserDialog addUserDialog = new AddUserDialog(this.mContext);
        this.mAddUserDialog = addUserDialog;
        addUserDialog.show();
    }

    private void listenForCallState() {
        this.mTelephonyListenerManager.addCallStateListener(this.mPhoneStateListener);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("UserSwitcherController state:");
        printWriter.println("  mLastNonGuestUser=" + this.mLastNonGuestUser);
        printWriter.print("  mUsers.size=");
        printWriter.println(this.mUsers.size());
        for (int i = 0; i < this.mUsers.size(); i++) {
            printWriter.print("    ");
            printWriter.println(this.mUsers.get(i).toString());
        }
        printWriter.println("mSimpleUserSwitcher=" + this.mSimpleUserSwitcher);
        printWriter.println("mGuestUserAutoCreated=" + this.mGuestUserAutoCreated);
    }

    public String getCurrentUserName() {
        UserRecord userRecord;
        UserInfo userInfo;
        if (this.mUsers.isEmpty() || (userRecord = this.mUsers.get(0)) == null || (userInfo = userRecord.info) == null) {
            return null;
        }
        if (userRecord.isGuest) {
            return this.mContext.getString(R$string.guest_nickname);
        }
        return userInfo.name;
    }

    public void onDensityOrFontScaleChanged() {
        refreshUsers(-1);
    }

    @VisibleForTesting
    public void addAdapter(WeakReference<BaseUserAdapter> weakReference) {
        this.mAdapters.add(weakReference);
    }

    @VisibleForTesting
    public ArrayList<UserRecord> getUsers() {
        return this.mUsers;
    }

    public void removeGuestUser(int i, int i2) {
        UserInfo userInfo = this.mUserTracker.getUserInfo();
        if (userInfo.id != i) {
            Log.w("UserSwitcherController", "User requesting to start a new session (" + i + ") is not current user (" + userInfo.id + ")");
        } else if (!userInfo.isGuest()) {
            Log.w("UserSwitcherController", "User requesting to start a new session (" + i + ") is not a guest");
        } else if (!this.mUserManager.markGuestForDeletion(userInfo.id)) {
            Log.w("UserSwitcherController", "Couldn't mark the guest for deletion for user " + i);
        } else if (i2 == -10000) {
            try {
                int createGuest = createGuest();
                if (createGuest == -10000) {
                    Log.e("UserSwitcherController", "Could not create new guest, switching back to system user");
                    switchToUserId(0);
                    this.mUserManager.removeUser(userInfo.id);
                    WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
                    return;
                }
                switchToUserId(createGuest);
                this.mUserManager.removeUser(userInfo.id);
            } catch (RemoteException unused) {
                Log.e("UserSwitcherController", "Couldn't remove guest because ActivityManager or WindowManager is dead");
            }
        } else {
            if (this.mGuestUserAutoCreated) {
                this.mGuestIsResetting.set(true);
            }
            switchToUserId(i2);
            this.mUserManager.removeUser(userInfo.id);
        }
    }

    private void scheduleGuestCreation() {
        if (this.mGuestCreationScheduled.compareAndSet(false, true)) {
            this.mBgExecutor.execute(new UserSwitcherController$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleGuestCreation$1() {
        int createGuest = createGuest();
        this.mGuestCreationScheduled.set(false);
        this.mGuestIsResetting.set(false);
        if (createGuest == -10000) {
            Log.w("UserSwitcherController", "Could not create new guest while exiting existing guest");
            refreshUsers(-10000);
        }
    }

    public void schedulePostBootGuestCreation() {
        if (isDeviceAllowedToAddGuest()) {
            guaranteeGuestPresent();
        } else {
            this.mDeviceProvisionedController.addCallback(this.mGuaranteeGuestPresentAfterProvisioned);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDeviceAllowedToAddGuest() {
        return this.mDeviceProvisionedController.isDeviceProvisioned() && !this.mDevicePolicyManager.isDeviceManaged();
    }

    /* access modifiers changed from: private */
    public void guaranteeGuestPresent() {
        if (isDeviceAllowedToAddGuest() && this.mUserManager.findCurrentGuestUser() == null) {
            scheduleGuestCreation();
        }
    }

    public int createGuest() {
        try {
            UserManager userManager = this.mUserManager;
            Context context = this.mContext;
            UserInfo createGuest = userManager.createGuest(context, context.getString(R$string.guest_nickname));
            if (createGuest != null) {
                return createGuest.id;
            }
            Log.e("UserSwitcherController", "Couldn't create guest, most likely because there already exists one");
            return -10000;
        } catch (UserManager.UserOperationException e) {
            Log.e("UserSwitcherController", "Couldn't create guest user", e);
            return -10000;
        }
    }

    public static abstract class BaseUserAdapter extends BaseAdapter {
        final UserSwitcherController mController;
        private final KeyguardStateController mKeyguardStateController;

        public long getItemId(int i) {
            return (long) i;
        }

        protected BaseUserAdapter(UserSwitcherController userSwitcherController) {
            this.mController = userSwitcherController;
            this.mKeyguardStateController = userSwitcherController.mKeyguardStateController;
            userSwitcherController.addAdapter(new WeakReference(this));
        }

        /* access modifiers changed from: protected */
        public ArrayList<UserRecord> getUsers() {
            return this.mController.getUsers();
        }

        public int getCount() {
            return countUsers(true);
        }

        private int countUsers(boolean z) {
            boolean isShowing = this.mKeyguardStateController.isShowing();
            int size = getUsers().size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                if (!getUsers().get(i2).isGuest || z) {
                    if (getUsers().get(i2).isRestricted && isShowing) {
                        break;
                    }
                    i++;
                }
            }
            return i;
        }

        public UserRecord getItem(int i) {
            return getUsers().get(i);
        }

        public void onUserListItemClicked(UserRecord userRecord) {
            this.mController.onUserListItemClicked(userRecord);
        }

        public String getName(Context context, UserRecord userRecord) {
            int i;
            int i2;
            if (userRecord.isGuest) {
                if (userRecord.isCurrent) {
                    if (this.mController.mGuestUserAutoCreated) {
                        i2 = R$string.guest_reset_guest;
                    } else {
                        i2 = R$string.guest_exit_guest;
                    }
                    return context.getString(i2);
                } else if (userRecord.info != null) {
                    return context.getString(R$string.guest_nickname);
                } else {
                    if (!this.mController.mGuestUserAutoCreated) {
                        return context.getString(R$string.guest_new_guest);
                    }
                    if (this.mController.mGuestIsResetting.get()) {
                        i = R$string.guest_resetting;
                    } else {
                        i = R$string.guest_nickname;
                    }
                    return context.getString(i);
                }
            } else if (userRecord.isAddUser) {
                return context.getString(com.android.systemui.R$string.user_add_user);
            } else {
                return userRecord.info.name;
            }
        }

        protected static ColorFilter getDisabledUserAvatarColorFilter() {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0f);
            return new ColorMatrixColorFilter(colorMatrix);
        }

        protected static Drawable getIconDrawable(Context context, UserRecord userRecord) {
            int i;
            if (userRecord.isAddUser) {
                i = R$drawable.ic_add_circle;
            } else if (userRecord.isGuest) {
                i = R$drawable.ic_avatar_guest_user;
            } else {
                i = R$drawable.ic_avatar_user;
            }
            return context.getDrawable(i);
        }

        public void refresh() {
            this.mController.refreshUsers(-10000);
        }
    }

    /* access modifiers changed from: private */
    public void checkIfAddUserDisallowedByAdminOnly(UserRecord userRecord) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_add_user", this.mUserTracker.getUserId());
        if (checkIfRestrictionEnforced == null || RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_add_user", this.mUserTracker.getUserId())) {
            userRecord.isDisabledByAdmin = false;
            userRecord.enforcedAdmin = null;
            return;
        }
        userRecord.isDisabledByAdmin = true;
        userRecord.enforcedAdmin = checkIfRestrictionEnforced;
    }

    /* access modifiers changed from: private */
    public boolean shouldUseSimpleUserSwitcher() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "lockscreenSimpleUserSwitcher", this.mContext.getResources().getBoolean(17891602) ? 1 : 0) != 0;
    }

    public void startActivity(Intent intent) {
        this.mActivityStarter.startActivity(intent, true);
    }

    public static final class UserRecord {
        public RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        public final UserInfo info;
        public final boolean isAddUser;
        public final boolean isCurrent;
        public boolean isDisabledByAdmin;
        public final boolean isGuest;
        public final boolean isRestricted;
        public boolean isSwitchToEnabled;
        public final Bitmap picture;

        public UserRecord(UserInfo userInfo, Bitmap bitmap, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            this.info = userInfo;
            this.picture = bitmap;
            this.isGuest = z;
            this.isCurrent = z2;
            this.isAddUser = z3;
            this.isRestricted = z4;
            this.isSwitchToEnabled = z5;
        }

        public UserRecord copyWithIsCurrent(boolean z) {
            return new UserRecord(this.info, this.picture, this.isGuest, z, this.isAddUser, this.isRestricted, this.isSwitchToEnabled);
        }

        public int resolveId() {
            UserInfo userInfo;
            if (this.isGuest || (userInfo = this.info) == null) {
                return -10000;
            }
            return userInfo.id;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UserRecord(");
            if (this.info != null) {
                sb.append("name=\"");
                sb.append(this.info.name);
                sb.append("\" id=");
                sb.append(this.info.id);
            } else if (this.isGuest) {
                sb.append("<add guest placeholder>");
            } else if (this.isAddUser) {
                sb.append("<add user placeholder>");
            }
            if (this.isGuest) {
                sb.append(" <isGuest>");
            }
            if (this.isAddUser) {
                sb.append(" <isAddUser>");
            }
            if (this.isCurrent) {
                sb.append(" <isCurrent>");
            }
            if (this.picture != null) {
                sb.append(" <hasPicture>");
            }
            if (this.isRestricted) {
                sb.append(" <isRestricted>");
            }
            if (this.isDisabledByAdmin) {
                sb.append(" <isDisabledByAdmin>");
                sb.append(" enforcedAdmin=");
                sb.append(this.enforcedAdmin);
            }
            if (this.isSwitchToEnabled) {
                sb.append(" <isSwitchToEnabled>");
            }
            sb.append(')');
            return sb.toString();
        }
    }

    public static class UserDetailAdapter implements DetailAdapter {
        private final Intent USER_SETTINGS_INTENT = new Intent("android.settings.USER_SETTINGS");
        private final Context mContext;
        private final Provider<UserDetailView.Adapter> mUserDetailViewAdapterProvider;

        public int getMetricsCategory() {
            return R$styleable.AppCompatTheme_windowMinWidthMinor;
        }

        public Boolean getToggleState() {
            return null;
        }

        public void setToggleState(boolean z) {
        }

        UserDetailAdapter(Context context, Provider<UserDetailView.Adapter> provider) {
            this.mContext = context;
            this.mUserDetailViewAdapterProvider = provider;
        }

        public CharSequence getTitle() {
            return this.mContext.getString(com.android.systemui.R$string.quick_settings_user_title);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            UserDetailView userDetailView;
            if (!(view instanceof UserDetailView)) {
                userDetailView = UserDetailView.inflate(context, viewGroup, false);
                userDetailView.setAdapter(this.mUserDetailViewAdapterProvider.get());
            } else {
                userDetailView = (UserDetailView) view;
            }
            userDetailView.refreshAdapter();
            return userDetailView;
        }

        public Intent getSettingsIntent() {
            return this.USER_SETTINGS_INTENT;
        }

        public int getSettingsText() {
            return com.android.systemui.R$string.quick_settings_more_user_settings;
        }

        public UiEventLogger.UiEventEnum openDetailEvent() {
            return QSUserSwitcherEvent.QS_USER_DETAIL_OPEN;
        }

        public UiEventLogger.UiEventEnum closeDetailEvent() {
            return QSUserSwitcherEvent.QS_USER_DETAIL_CLOSE;
        }

        public UiEventLogger.UiEventEnum moreSettingsEvent() {
            return QSUserSwitcherEvent.QS_USER_MORE_SETTINGS;
        }
    }

    private final class ExitGuestDialog extends SystemUIDialog implements DialogInterface.OnClickListener {
        private final int mGuestId;
        private final int mTargetId;

        public ExitGuestDialog(Context context, int i, int i2) {
            super(context);
            int i3;
            int i4;
            if (UserSwitcherController.this.mGuestUserAutoCreated) {
                i3 = R$string.guest_reset_guest_dialog_title;
            } else {
                i3 = com.android.systemui.R$string.guest_exit_guest_dialog_title;
            }
            setTitle(i3);
            setMessage(context.getString(com.android.systemui.R$string.guest_exit_guest_dialog_message));
            setButton(-2, context.getString(17039360), this);
            if (UserSwitcherController.this.mGuestUserAutoCreated) {
                i4 = R$string.guest_reset_guest_confirm_button;
            } else {
                i4 = com.android.systemui.R$string.guest_exit_guest_dialog_remove;
            }
            setButton(-1, context.getString(i4), this);
            SystemUIDialog.setWindowOnTop(this);
            setCanceledOnTouchOutside(false);
            this.mGuestId = i;
            this.mTargetId = i2;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!UserSwitcherController.this.mFalsingManager.isFalseTap(i == -2 ? 0 : 3)) {
                if (i == -2) {
                    cancel();
                    return;
                }
                UserSwitcherController.this.mUiEventLogger.log(QSUserSwitcherEvent.QS_USER_GUEST_REMOVE);
                dismiss();
                UserSwitcherController.this.removeGuestUser(this.mGuestId, this.mTargetId);
            }
        }
    }

    @VisibleForTesting
    final class AddUserDialog extends SystemUIDialog implements DialogInterface.OnClickListener {
        public AddUserDialog(Context context) {
            super(context);
            setTitle(com.android.systemui.R$string.user_add_user_title);
            setMessage(context.getString(com.android.systemui.R$string.user_add_user_message_short));
            setButton(-2, context.getString(17039360), this);
            setButton(-1, context.getString(17039370), this);
            SystemUIDialog.setWindowOnTop(this);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!UserSwitcherController.this.mFalsingManager.isFalseTap(i == -2 ? 0 : 2)) {
                if (i == -2) {
                    cancel();
                    return;
                }
                dismiss();
                if (!ActivityManager.isUserAMonkey()) {
                    Intent createIntentForStart = CreateUserActivity.createIntentForStart(getContext());
                    if (UserSwitcherController.this.mKeyguardStateController.isUnlocked() || UserSwitcherController.this.mKeyguardStateController.canDismissLockScreen()) {
                        UserSwitcherController.this.mActivityStarter.startActivity(createIntentForStart, true);
                        return;
                    }
                    try {
                        UserSwitcherController.this.mActivityTaskManager.startActivity((IApplicationThread) null, UserSwitcherController.this.mContext.getBasePackageName(), UserSwitcherController.this.mContext.getAttributionTag(), createIntentForStart, createIntentForStart.resolveTypeIfNeeded(UserSwitcherController.this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 0, (ProfilerInfo) null, (Bundle) null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.e("UserSwitcherController", "Couldn't start create user activity", e);
                    }
                }
            }
        }
    }
}
