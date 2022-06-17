package com.android.systemui.globalactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.IStopUserCallback;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.sysprop.TelephonyProperties;
import android.telecom.TelecomManager;
import android.telephony.ServiceState;
import android.telephony.TelephonyCallback;
import android.util.ArraySet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.MultiListLayout;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.scrim.ScrimDrawable;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.util.RingerModeTracker;
import com.android.systemui.util.settings.GlobalSettings;
import com.android.systemui.util.settings.SecureSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class GlobalActionsDialogLite implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener, ConfigurationController.ConfigurationListener, GlobalActionsPanelPlugin.Callbacks, LifecycleOwner {
    @VisibleForTesting
    static final String GLOBAL_ACTION_KEY_POWER = "power";
    /* access modifiers changed from: private */
    public static boolean mXrvdFeatureEnabled = false;
    protected MyAdapter mAdapter;
    private final ContentObserver mAirplaneModeObserver;
    /* access modifiers changed from: private */
    public ToggleAction mAirplaneModeOn;
    /* access modifiers changed from: private */
    public ToggleState mAirplaneState;
    /* access modifiers changed from: private */
    public final AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public final Executor mBackgroundExecutor;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private BroadcastReceiver mBroadcastReceiver;
    private final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public boolean mDeviceProvisioned;
    @VisibleForTesting
    protected ActionsDialogLite mDialog;
    /* access modifiers changed from: private */
    public int mDialogPressDelay;
    private final IDreamManager mDreamManager;
    /* access modifiers changed from: private */
    public final EmergencyAffordanceManager mEmergencyAffordanceManager;
    protected final GlobalSettings mGlobalSettings;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public boolean mHasTelephony;
    private boolean mHasVibrator;
    /* access modifiers changed from: private */
    public final IActivityManager mIActivityManager;
    /* access modifiers changed from: private */
    public final IWindowManager mIWindowManager;
    private final GlobalActionsInfoProvider mInfoProvider;
    /* access modifiers changed from: private */
    public boolean mIsWaitingForEcmExit;
    @VisibleForTesting
    protected final ArrayList<Action> mItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing;
    private final KeyguardStateController mKeyguardStateController;
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    protected Handler mMainHandler;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    protected final NotificationShadeWindowController mNotificationShadeWindowController;
    protected MyOverflowAdapter mOverflowAdapter;
    @VisibleForTesting
    protected final ArrayList<Action> mOverflowItems = new ArrayList<>();
    private final TelephonyCallback.ServiceStateListener mPhoneStateListener;
    protected MyPowerOptionsAdapter mPowerAdapter;
    @VisibleForTesting
    protected final ArrayList<Action> mPowerItems = new ArrayList<>();
    protected final Resources mResources;
    private final RingerModeTracker mRingerModeTracker;
    /* access modifiers changed from: private */
    public final ScreenshotHelper mScreenshotHelper;
    protected final SecureSettings mSecureSettings;
    private final boolean mShowSilentToggle;
    private Action mSilentModeAction;
    private int mSmallestScreenWidthDp;
    /* access modifiers changed from: private */
    public final StatusBar mStatusBar;
    private final IStatusBarService mStatusBarService;
    private final SysUiState mSysUiState;
    private final SysuiColorExtractor mSysuiColorExtractor;
    /* access modifiers changed from: private */
    public final TelecomManager mTelecomManager;
    private final TelephonyListenerManager mTelephonyListenerManager;
    private final TrustManager mTrustManager;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final GlobalActions.GlobalActionsManager mWindowManagerFuncs;

    public interface Action {
        View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater);

        Drawable getIcon(Context context);

        CharSequence getMessage();

        int getMessageResId();

        boolean isEnabled();

        void onPress();

        boolean shouldBeSeparated() {
            return false;
        }

        boolean shouldShow() {
            return true;
        }

        boolean showBeforeProvisioning();

        boolean showDuringKeyguard();
    }

    private interface LongPressAction extends Action {
        boolean onLongPress();
    }

    @VisibleForTesting
    public enum GlobalActionsEvent implements UiEventLogger.UiEventEnum {
        GA_POWER_MENU_OPEN(337),
        GA_POWER_MENU_CLOSE(471),
        GA_BUGREPORT_PRESS(344),
        GA_BUGREPORT_LONG_PRESS(345),
        GA_EMERGENCY_DIALER_PRESS(346),
        GA_SCREENSHOT_PRESS(347),
        GA_SCREENSHOT_LONG_PRESS(348),
        GA_SHUTDOWN_PRESS(802),
        GA_SHUTDOWN_LONG_PRESS(803),
        GA_REBOOT_PRESS(349),
        GA_REBOOT_LONG_PRESS(804),
        GA_LOCKDOWN_PRESS(354),
        GA_OPEN_QS(805),
        GA_OPEN_POWER_VOLUP(806),
        GA_OPEN_LONG_PRESS_POWER(807),
        GA_CLOSE_LONG_PRESS_POWER(808),
        GA_CLOSE_BACK(809),
        GA_CLOSE_TAP_OUTSIDE(810),
        GA_CLOSE_POWER_VOLUP(811);
        
        private final int mId;

        private GlobalActionsEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public GlobalActionsDialogLite(Context context, GlobalActions.GlobalActionsManager globalActionsManager, AudioManager audioManager, IDreamManager iDreamManager, DevicePolicyManager devicePolicyManager, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, TelephonyListenerManager telephonyListenerManager, GlobalSettings globalSettings, SecureSettings secureSettings, Vibrator vibrator, Resources resources, ConfigurationController configurationController, KeyguardStateController keyguardStateController, UserManager userManager, TrustManager trustManager, IActivityManager iActivityManager, TelecomManager telecomManager, MetricsLogger metricsLogger, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, NotificationShadeWindowController notificationShadeWindowController, IWindowManager iWindowManager, Executor executor, UiEventLogger uiEventLogger, GlobalActionsInfoProvider globalActionsInfoProvider, RingerModeTracker ringerModeTracker, SysUiState sysUiState, Handler handler, PackageManager packageManager, StatusBar statusBar) {
        BroadcastDispatcher broadcastDispatcher2 = broadcastDispatcher;
        TelephonyListenerManager telephonyListenerManager2 = telephonyListenerManager;
        GlobalSettings globalSettings2 = globalSettings;
        Resources resources2 = resources;
        ConfigurationController configurationController2 = configurationController;
        boolean z = false;
        this.mKeyguardShowing = false;
        this.mDeviceProvisioned = false;
        this.mAirplaneState = ToggleState.Off;
        this.mIsWaitingForEcmExit = false;
        this.mDialogPressDelay = 850;
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) {
                    String stringExtra = intent.getStringExtra("reason");
                    if (!"globalactions".equals(stringExtra)) {
                        GlobalActionsDialogLite.this.mHandler.sendMessage(GlobalActionsDialogLite.this.mHandler.obtainMessage(0, stringExtra));
                    }
                } else if ("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED".equals(action) && !intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false) && GlobalActionsDialogLite.this.mIsWaitingForEcmExit) {
                    boolean unused = GlobalActionsDialogLite.this.mIsWaitingForEcmExit = false;
                    GlobalActionsDialogLite.this.changeAirplaneModeSystemSetting(true);
                }
            }
        };
        C09506 r8 = new TelephonyCallback.ServiceStateListener() {
            public void onServiceStateChanged(ServiceState serviceState) {
                if (GlobalActionsDialogLite.this.mHasTelephony) {
                    if (GlobalActionsDialogLite.this.mAirplaneModeOn == null) {
                        Log.d("GlobalActionsDialogLite", "Service changed before actions created");
                        return;
                    }
                    ToggleState unused = GlobalActionsDialogLite.this.mAirplaneState = serviceState.getState() == 3 ? ToggleState.On : ToggleState.Off;
                    GlobalActionsDialogLite.this.mAirplaneModeOn.updateState(GlobalActionsDialogLite.this.mAirplaneState);
                    GlobalActionsDialogLite.this.mAdapter.notifyDataSetChanged();
                    GlobalActionsDialogLite.this.mOverflowAdapter.notifyDataSetChanged();
                    GlobalActionsDialogLite.this.mPowerAdapter.notifyDataSetChanged();
                }
            }
        };
        this.mPhoneStateListener = r8;
        C09517 r9 = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z) {
                GlobalActionsDialogLite.this.onAirplaneModeChanged();
            }
        };
        this.mAirplaneModeObserver = r9;
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 0) {
                    if (i == 1) {
                        GlobalActionsDialogLite.this.refreshSilentMode();
                        GlobalActionsDialogLite.this.mAdapter.notifyDataSetChanged();
                    }
                } else if (GlobalActionsDialogLite.this.mDialog != null) {
                    if ("dream".equals(message.obj)) {
                        GlobalActionsDialogLite.this.mDialog.completeDismiss();
                    } else {
                        GlobalActionsDialogLite.this.mDialog.lambda$initializeLayout$4();
                    }
                    GlobalActionsDialogLite.this.mDialog = null;
                }
            }
        };
        this.mContext = context;
        this.mWindowManagerFuncs = globalActionsManager;
        this.mAudioManager = audioManager;
        this.mDreamManager = iDreamManager;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mLockPatternUtils = lockPatternUtils;
        this.mTelephonyListenerManager = telephonyListenerManager2;
        this.mKeyguardStateController = keyguardStateController;
        this.mBroadcastDispatcher = broadcastDispatcher2;
        this.mGlobalSettings = globalSettings2;
        this.mSecureSettings = secureSettings;
        this.mResources = resources2;
        this.mConfigurationController = configurationController2;
        this.mUserManager = userManager;
        this.mTrustManager = trustManager;
        this.mIActivityManager = iActivityManager;
        this.mTelecomManager = telecomManager;
        this.mMetricsLogger = metricsLogger;
        this.mUiEventLogger = uiEventLogger;
        this.mInfoProvider = globalActionsInfoProvider;
        this.mSysuiColorExtractor = sysuiColorExtractor;
        this.mStatusBarService = iStatusBarService;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mIWindowManager = iWindowManager;
        this.mBackgroundExecutor = executor;
        this.mRingerModeTracker = ringerModeTracker;
        this.mSysUiState = sysUiState;
        this.mMainHandler = handler;
        this.mSmallestScreenWidthDp = resources.getConfiguration().smallestScreenWidthDp;
        this.mStatusBar = statusBar;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        broadcastDispatcher2.registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mHasTelephony = packageManager.hasSystemFeature("android.hardware.telephony");
        telephonyListenerManager2.addServiceStateListener(r8);
        globalSettings2.registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, r9);
        if (vibrator != null && vibrator.hasVibrator()) {
            z = true;
        }
        this.mHasVibrator = z;
        boolean z2 = !resources2.getBoolean(17891777);
        this.mShowSilentToggle = z2;
        if (z2) {
            ringerModeTracker.getRingerMode().observe(this, new GlobalActionsDialogLite$$ExternalSyntheticLambda0(this));
        }
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
        this.mScreenshotHelper = new ScreenshotHelper(context);
        configurationController2.addCallback(this);
        mXrvdFeatureEnabled = this.mContext.getResources().getBoolean(17891589);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Integer num) {
        this.mHandler.sendEmptyMessage(1);
    }

    public void destroy() {
        this.mBroadcastDispatcher.unregisterReceiver(this.mBroadcastReceiver);
        this.mTelephonyListenerManager.removeServiceStateListener(this.mPhoneStateListener);
        this.mGlobalSettings.unregisterContentObserver(this.mAirplaneModeObserver);
        this.mConfigurationController.removeCallback(this);
    }

    public boolean isShowing() {
        ActionsDialogLite actionsDialogLite = this.mDialog;
        return actionsDialogLite != null && actionsDialogLite.isShowing();
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    public void updateForCli() {
        Context context = this.mContext;
        if (context != null && !MotoFeature.isCliContext(context)) {
            Log.d("GlobalActionsDialogLite", " updateForCli ");
            this.mContext = MotoFeature.getCliContext(this.mContext);
        }
    }

    /* access modifiers changed from: protected */
    public UiEventLogger getEventLogger() {
        return this.mUiEventLogger;
    }

    /* access modifiers changed from: protected */
    public StatusBar getStatusBar() {
        return this.mStatusBar;
    }

    public void showOrHideDialog(boolean z, boolean z2) {
        this.mKeyguardShowing = z;
        this.mDeviceProvisioned = z2;
        ActionsDialogLite actionsDialogLite = this.mDialog;
        if (actionsDialogLite == null || !actionsDialogLite.isShowing()) {
            handleShow();
            return;
        }
        this.mWindowManagerFuncs.onGlobalActionsShown();
        this.mDialog.lambda$initializeLayout$4();
        this.mDialog = null;
    }

    /* access modifiers changed from: protected */
    public boolean isKeyguardShowing() {
        return this.mKeyguardShowing;
    }

    public void dismissDialog() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }

    /* access modifiers changed from: protected */
    public void awakenIfNecessary() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager != null) {
            try {
                if (iDreamManager.isDreaming()) {
                    this.mDreamManager.awaken();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleShow() {
        awakenIfNecessary();
        this.mDialog = createDialog();
        prepareDialog();
        WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
        attributes.setTitle("ActionsDialog");
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
            attributes.setTitle("ActionsDialog");
            Log.d("GlobalActionsDialogLite", " show ActionsDialog");
        } else {
            attributes.setTitle("Cli ActionsDialog");
            Log.d("GlobalActionsDialogLite", " show Cli ActionsDialog");
        }
        attributes.layoutInDisplayCutoutMode = 3;
        this.mDialog.getWindow().setAttributes(attributes);
        this.mDialog.getWindow().setFlags(131072, 131072);
        this.mDialog.show();
        this.mWindowManagerFuncs.onGlobalActionsShown();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean shouldShowAction(Action action) {
        if (this.mKeyguardShowing && !action.showDuringKeyguard()) {
            return false;
        }
        if (this.mDeviceProvisioned || action.showBeforeProvisioning()) {
            return action.shouldShow();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getMaxShownPowerItems() {
        return this.mResources.getInteger(R$integer.power_menu_lite_max_columns) * this.mResources.getInteger(R$integer.power_menu_lite_max_rows);
    }

    private void addActionItem(Action action) {
        if (this.mItems.size() < getMaxShownPowerItems()) {
            this.mItems.add(action);
        } else {
            this.mOverflowItems.add(action);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public String[] getDefaultActions() {
        return this.mResources.getStringArray(17236084);
    }

    private void addIfShouldShowAction(List<Action> list, Action action) {
        if (shouldShowAction(action)) {
            list.add(action);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void createActionItems() {
        if (!this.mHasVibrator) {
            this.mSilentModeAction = new SilentModeToggleAction();
        } else {
            this.mSilentModeAction = new SilentModeTriStateAction(this.mAudioManager, this.mHandler);
        }
        this.mAirplaneModeOn = new AirplaneModeAction();
        onAirplaneModeChanged();
        this.mItems.clear();
        this.mOverflowItems.clear();
        this.mPowerItems.clear();
        String[] defaultActions = getDefaultActions();
        ShutDownAction shutDownAction = new ShutDownAction();
        RestartAction restartAction = new RestartAction();
        ArraySet arraySet = new ArraySet();
        ArrayList<Action> arrayList = new ArrayList<>();
        CurrentUserProvider currentUserProvider = new CurrentUserProvider();
        if ((!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) && this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            addIfShouldShowAction(arrayList, new EmergencyAffordanceAction());
            arraySet.add("emergency");
        }
        for (String str : defaultActions) {
            if (!arraySet.contains(str) && (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext) || GLOBAL_ACTION_KEY_POWER.equals(str) || "restart".equals(str))) {
                if (GLOBAL_ACTION_KEY_POWER.equals(str)) {
                    addIfShouldShowAction(arrayList, shutDownAction);
                } else if ("airplane".equals(str)) {
                    addIfShouldShowAction(arrayList, this.mAirplaneModeOn);
                } else if ("bugreport".equals(str)) {
                    if (shouldDisplayBugReport(currentUserProvider.get())) {
                        addIfShouldShowAction(arrayList, new BugReportAction());
                    }
                } else if ("silent".equals(str)) {
                    if (this.mShowSilentToggle) {
                        addIfShouldShowAction(arrayList, this.mSilentModeAction);
                    }
                } else if ("users".equals(str)) {
                    if (SystemProperties.getBoolean("fw.power_user_switcher", false)) {
                        addUserActions(arrayList, currentUserProvider.get());
                    }
                } else if ("settings".equals(str)) {
                    addIfShouldShowAction(arrayList, getSettingsAction());
                } else if ("lockdown".equals(str)) {
                    if (shouldDisplayLockdown(currentUserProvider.get())) {
                        addIfShouldShowAction(arrayList, new LockDownAction());
                    }
                } else if ("voiceassist".equals(str)) {
                    addIfShouldShowAction(arrayList, getVoiceAssistAction());
                } else if ("assist".equals(str)) {
                    addIfShouldShowAction(arrayList, getAssistAction());
                } else if ("restart".equals(str)) {
                    addIfShouldShowAction(arrayList, restartAction);
                } else if ("screenshot".equals(str)) {
                    addIfShouldShowAction(arrayList, new ScreenshotAction());
                } else if ("logout".equals(str)) {
                    if (!(!this.mDevicePolicyManager.isLogoutEnabled() || currentUserProvider.get() == null || currentUserProvider.get().id == 0)) {
                        addIfShouldShowAction(arrayList, new LogoutAction());
                    }
                } else if ("emergency".equals(str)) {
                    addIfShouldShowAction(arrayList, new EmergencyDialerAction());
                } else {
                    Log.e("GlobalActionsDialogLite", "Invalid global action key " + str);
                }
                arraySet.add(str);
            }
        }
        if (arrayList.contains(shutDownAction) && arrayList.contains(restartAction) && arrayList.size() > getMaxShownPowerItems()) {
            int min = Math.min(arrayList.indexOf(restartAction), arrayList.indexOf(shutDownAction));
            arrayList.remove(shutDownAction);
            arrayList.remove(restartAction);
            this.mPowerItems.add(shutDownAction);
            this.mPowerItems.add(restartAction);
            arrayList.add(min, new PowerOptionsAction());
        }
        for (Action addActionItem : arrayList) {
            addActionItem(addActionItem);
        }
    }

    /* access modifiers changed from: protected */
    public void onRotate() {
        createActionItems();
    }

    /* access modifiers changed from: protected */
    public void initDialogItems() {
        createActionItems();
        this.mAdapter = new MyAdapter();
        this.mOverflowAdapter = new MyOverflowAdapter();
        this.mPowerAdapter = new MyPowerOptionsAdapter();
    }

    /* access modifiers changed from: protected */
    public ActionsDialogLite createDialog() {
        initDialogItems();
        Context context = this.mContext;
        int i = R$style.Theme_SystemUI_Dialog_GlobalActionsLite;
        MyAdapter myAdapter = this.mAdapter;
        MyOverflowAdapter myOverflowAdapter = this.mOverflowAdapter;
        SysuiColorExtractor sysuiColorExtractor = this.mSysuiColorExtractor;
        IStatusBarService iStatusBarService = this.mStatusBarService;
        NotificationShadeWindowController notificationShadeWindowController = this.mNotificationShadeWindowController;
        SysUiState sysUiState = this.mSysUiState;
        GlobalActionsDialogLite$$ExternalSyntheticLambda1 globalActionsDialogLite$$ExternalSyntheticLambda1 = new GlobalActionsDialogLite$$ExternalSyntheticLambda1(this);
        boolean z = this.mKeyguardShowing;
        MyPowerOptionsAdapter myPowerOptionsAdapter = this.mPowerAdapter;
        UiEventLogger uiEventLogger = this.mUiEventLogger;
        GlobalActionsInfoProvider globalActionsInfoProvider = this.mInfoProvider;
        ActionsDialogLite actionsDialogLite = r1;
        ActionsDialogLite actionsDialogLite2 = new ActionsDialogLite(context, i, myAdapter, myOverflowAdapter, sysuiColorExtractor, iStatusBarService, notificationShadeWindowController, sysUiState, globalActionsDialogLite$$ExternalSyntheticLambda1, z, myPowerOptionsAdapter, uiEventLogger, globalActionsInfoProvider, this.mStatusBar);
        actionsDialogLite.setOnDismissListener(this);
        actionsDialogLite.setOnShowListener(this);
        return actionsDialogLite;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldDisplayLockdown(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        int i = userInfo.id;
        if (!this.mKeyguardStateController.isMethodSecure()) {
            return false;
        }
        int strongAuthForUser = this.mLockPatternUtils.getStrongAuthForUser(i);
        if (strongAuthForUser == 0 || strongAuthForUser == 4) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldDisplayBugReport(UserInfo userInfo) {
        if (this.mGlobalSettings.getInt("bugreport_in_power_menu", 0) == 0) {
            return false;
        }
        if (userInfo == null || userInfo.isPrimary()) {
            return true;
        }
        return false;
    }

    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
        ActionsDialogLite actionsDialogLite = this.mDialog;
        if (actionsDialogLite != null && actionsDialogLite.isShowing()) {
            this.mDialog.refreshDialog();
        }
    }

    public void onConfigChanged(Configuration configuration) {
        int i;
        ActionsDialogLite actionsDialogLite = this.mDialog;
        if (actionsDialogLite != null && actionsDialogLite.isShowing() && (i = configuration.smallestScreenWidthDp) != this.mSmallestScreenWidthDp) {
            this.mSmallestScreenWidthDp = i;
            this.mDialog.refreshDialog();
        }
    }

    public void dismissGlobalActionsMenu() {
        dismissDialog();
    }

    @VisibleForTesting
    protected final class PowerOptionsAction extends SinglePressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        private PowerOptionsAction() {
            super(R$drawable.ic_settings_power, 17040358);
        }

        public void onPress() {
            ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
            if (actionsDialogLite != null) {
                actionsDialogLite.showPowerOptionsMenu();
            }
        }
    }

    @VisibleForTesting
    final class ShutDownAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        ShutDownAction() {
            super(17301552, 17040357);
        }

        public boolean onLongPress() {
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_SHUTDOWN_LONG_PRESS);
            if ((MotoFeature.getInstance(GlobalActionsDialogLite.this.mContext).isSupportCli() && MotoFeature.isCliContext(GlobalActionsDialogLite.this.mContext)) || GlobalActionsDialogLite.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                return false;
            }
            GlobalActionsDialogLite.this.mWindowManagerFuncs.reboot(true);
            return true;
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_SHUTDOWN_PRESS);
            GlobalActionsDialogLite.this.mWindowManagerFuncs.shutdown();
        }
    }

    @VisibleForTesting
    protected abstract class EmergencyAction extends SinglePressAction {
        public boolean shouldBeSeparated() {
            return false;
        }

        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        EmergencyAction(int i, int i2) {
            super(i, i2);
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View create = super.create(context, view, viewGroup, layoutInflater);
            int emergencyTextColor = GlobalActionsDialogLite.this.getEmergencyTextColor(context);
            int emergencyIconColor = GlobalActionsDialogLite.this.getEmergencyIconColor(context);
            int emergencyBackgroundColor = GlobalActionsDialogLite.this.getEmergencyBackgroundColor(context);
            TextView textView = (TextView) create.findViewById(16908299);
            textView.setTextColor(emergencyTextColor);
            textView.setSelected(true);
            ImageView imageView = (ImageView) create.findViewById(16908294);
            imageView.getDrawable().setTint(emergencyIconColor);
            imageView.setBackgroundTintList(ColorStateList.valueOf(emergencyBackgroundColor));
            create.setBackgroundTintList(ColorStateList.valueOf(emergencyBackgroundColor));
            return create;
        }
    }

    /* access modifiers changed from: protected */
    public int getEmergencyTextColor(Context context) {
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            return context.getResources().getColor(R$color.prc_qs_custom_text_color);
        }
        return context.getResources().getColor(R$color.global_actions_lite_text);
    }

    /* access modifiers changed from: protected */
    public int getEmergencyIconColor(Context context) {
        return context.getResources().getColor(R$color.global_actions_lite_emergency_icon);
    }

    /* access modifiers changed from: protected */
    public int getEmergencyBackgroundColor(Context context) {
        return context.getResources().getColor(R$color.global_actions_lite_emergency_background);
    }

    private class EmergencyAffordanceAction extends EmergencyAction {
        EmergencyAffordanceAction() {
            super(17303085, 17040353);
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mEmergencyAffordanceManager.performEmergencyCall();
        }
    }

    @VisibleForTesting
    class EmergencyDialerAction extends EmergencyAction {
        private EmergencyDialerAction() {
            super(R$drawable.ic_emergency_star, 17040353);
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mMetricsLogger.action(1569);
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_EMERGENCY_DIALER_PRESS);
            if (GlobalActionsDialogLite.this.mTelecomManager != null) {
                GlobalActionsDialogLite.this.mStatusBar.collapseShade();
                Intent createLaunchEmergencyDialerIntent = GlobalActionsDialogLite.this.mTelecomManager.createLaunchEmergencyDialerIntent((String) null);
                createLaunchEmergencyDialerIntent.addFlags(343932928);
                createLaunchEmergencyDialerIntent.putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 2);
                GlobalActionsDialogLite.this.mContext.startActivityAsUser(createLaunchEmergencyDialerIntent, UserHandle.CURRENT);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public EmergencyDialerAction makeEmergencyDialerActionForTesting() {
        return new EmergencyDialerAction();
    }

    @VisibleForTesting
    final class RestartAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        RestartAction() {
            super(17303702, 17040359);
        }

        public boolean onLongPress() {
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_REBOOT_LONG_PRESS);
            if ((MotoFeature.getInstance(GlobalActionsDialogLite.this.mContext).isSupportCli() && MotoFeature.isCliContext(GlobalActionsDialogLite.this.mContext)) || GlobalActionsDialogLite.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                return false;
            }
            GlobalActionsDialogLite.this.mWindowManagerFuncs.reboot(true);
            return true;
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_REBOOT_PRESS);
            GlobalActionsDialogLite.this.mWindowManagerFuncs.reboot(false);
        }
    }

    @VisibleForTesting
    class ScreenshotAction extends SinglePressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        ScreenshotAction() {
            super(17303704, 17040360);
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    GlobalActionsDialogLite.this.mScreenshotHelper.takeScreenshot(1, true, true, 0, GlobalActionsDialogLite.this.mHandler, (Consumer) null);
                    GlobalActionsDialogLite.this.mMetricsLogger.action(1282);
                    GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_SCREENSHOT_PRESS);
                }
            }, (long) GlobalActionsDialogLite.this.mDialogPressDelay);
        }

        public boolean shouldShow() {
            return is2ButtonNavigationEnabled();
        }

        /* access modifiers changed from: package-private */
        public boolean is2ButtonNavigationEnabled() {
            return 1 == GlobalActionsDialogLite.this.mContext.getResources().getInteger(17694885);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ScreenshotAction makeScreenshotActionForTesting() {
        return new ScreenshotAction();
    }

    @VisibleForTesting
    class BugReportAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        BugReportAction() {
            super(17303362, 17039807);
        }

        public void onPress() {
            if (!ActivityManager.isUserAMonkey()) {
                GlobalActionsDialogLite.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            GlobalActionsDialogLite.this.mMetricsLogger.action(292);
                            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_BUGREPORT_PRESS);
                            if (!GlobalActionsDialogLite.this.mIActivityManager.launchBugReportHandlerApp()) {
                                Log.w("GlobalActionsDialogLite", "Bugreport handler could not be launched");
                                GlobalActionsDialogLite.this.mIActivityManager.requestInteractiveBugReport();
                            }
                            GlobalActionsDialogLite.this.mStatusBar.collapseShade();
                        } catch (RemoteException unused) {
                        }
                    }
                }, (long) GlobalActionsDialogLite.this.mDialogPressDelay);
            }
        }

        public boolean onLongPress() {
            if (ActivityManager.isUserAMonkey()) {
                return false;
            }
            try {
                GlobalActionsDialogLite.this.mMetricsLogger.action(293);
                GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_BUGREPORT_LONG_PRESS);
                GlobalActionsDialogLite.this.mIActivityManager.requestFullBugReport();
                GlobalActionsDialogLite.this.mStatusBar.collapseShade();
            } catch (RemoteException unused) {
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public BugReportAction makeBugReportActionForTesting() {
        return new BugReportAction();
    }

    private final class LogoutAction extends SinglePressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        private LogoutAction() {
            super(17303412, 17040356);
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mHandler.postDelayed(new GlobalActionsDialogLite$LogoutAction$$ExternalSyntheticLambda0(this), (long) GlobalActionsDialogLite.this.mDialogPressDelay);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPress$0() {
            try {
                int i = GlobalActionsDialogLite.this.getCurrentUser().id;
                GlobalActionsDialogLite.this.mIActivityManager.switchUser(0);
                GlobalActionsDialogLite.this.mIActivityManager.stopUser(i, true, (IStopUserCallback) null);
            } catch (RemoteException e) {
                Log.e("GlobalActionsDialogLite", "Couldn't logout user " + e);
            }
        }
    }

    private Action getSettingsAction() {
        return new SinglePressAction(17303711, 17040361) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.settings.SETTINGS");
                intent.addFlags(335544320);
                GlobalActionsDialogLite.this.mContext.startActivity(intent);
            }
        };
    }

    private Action getAssistAction() {
        return new SinglePressAction(17303177, 17040351) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialogLite.this.mContext.startActivity(intent);
            }
        };
    }

    private Action getVoiceAssistAction() {
        return new SinglePressAction(17303753, 17040365) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.VOICE_ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialogLite.this.mContext.startActivity(intent);
            }
        };
    }

    @VisibleForTesting
    class LockDownAction extends SinglePressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        LockDownAction() {
            super(17303365, 17040355);
        }

        public void onPress() {
            GlobalActionsDialogLite.this.mLockPatternUtils.requireStrongAuth(32, -1);
            GlobalActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_LOCKDOWN_PRESS);
            try {
                GlobalActionsDialogLite.this.mIWindowManager.lockNow((Bundle) null);
                GlobalActionsDialogLite.this.mBackgroundExecutor.execute(new GlobalActionsDialogLite$LockDownAction$$ExternalSyntheticLambda0(this));
            } catch (RemoteException e) {
                Log.e("GlobalActionsDialogLite", "Error while trying to lock device.", e);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPress$0() {
            GlobalActionsDialogLite.this.lockProfiles();
        }
    }

    /* access modifiers changed from: private */
    public void lockProfiles() {
        int i = getCurrentUser().id;
        for (int i2 : this.mUserManager.getEnabledProfileIds(i)) {
            if (i2 != i) {
                this.mTrustManager.setDeviceLockedForUser(i2, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public UserInfo getCurrentUser() {
        try {
            return this.mIActivityManager.getCurrentUser();
        } catch (RemoteException unused) {
            return null;
        }
    }

    private class CurrentUserProvider {
        private boolean mFetched;
        private UserInfo mUserInfo;

        private CurrentUserProvider() {
            this.mUserInfo = null;
            this.mFetched = false;
        }

        /* access modifiers changed from: package-private */
        public UserInfo get() {
            if (!this.mFetched) {
                this.mFetched = true;
                this.mUserInfo = GlobalActionsDialogLite.this.getCurrentUser();
            }
            return this.mUserInfo;
        }
    }

    private void addUserActions(List<Action> list, UserInfo userInfo) {
        if (this.mUserManager.isUserSwitcherEnabled()) {
            for (final UserInfo userInfo2 : this.mUserManager.getUsers()) {
                if (userInfo2.supportsSwitchToByUser()) {
                    boolean z = true;
                    if (userInfo != null ? userInfo.id != userInfo2.id : userInfo2.id != 0) {
                        z = false;
                    }
                    String str = userInfo2.iconPath;
                    Drawable createFromPath = str != null ? Drawable.createFromPath(str) : null;
                    StringBuilder sb = new StringBuilder();
                    String str2 = userInfo2.name;
                    if (str2 == null) {
                        str2 = "Primary";
                    }
                    sb.append(str2);
                    sb.append(z ? " ✔" : "");
                    addIfShouldShowAction(list, new SinglePressAction(17303581, createFromPath, sb.toString()) {
                        public boolean showBeforeProvisioning() {
                            return false;
                        }

                        public boolean showDuringKeyguard() {
                            return true;
                        }

                        public void onPress() {
                            try {
                                GlobalActionsDialogLite.this.mIActivityManager.switchUser(userInfo2.id);
                            } catch (RemoteException e) {
                                Log.e("GlobalActionsDialogLite", "Couldn't switch user " + e);
                            }
                        }
                    });
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void prepareDialog() {
        refreshSilentMode();
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
        this.mAdapter.notifyDataSetChanged();
        this.mLifecycle.setCurrentState(Lifecycle.State.RESUMED);
    }

    /* access modifiers changed from: private */
    public void refreshSilentMode() {
        if (!this.mHasVibrator) {
            Integer value = this.mRingerModeTracker.getRingerMode().getValue();
            ((ToggleAction) this.mSilentModeAction).updateState(value != null && value.intValue() != 2 ? ToggleState.On : ToggleState.Off);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mDialog == dialogInterface) {
            this.mDialog = null;
        }
        this.mUiEventLogger.log(GlobalActionsEvent.GA_POWER_MENU_CLOSE);
        this.mWindowManagerFuncs.onGlobalActionsHidden();
        this.mLifecycle.setCurrentState(Lifecycle.State.CREATED);
    }

    public void onShow(DialogInterface dialogInterface) {
        this.mMetricsLogger.visible(1568);
        this.mUiEventLogger.log(GlobalActionsEvent.GA_POWER_MENU_OPEN);
    }

    public class MyAdapter extends MultiListLayout.MultiListAdapter {
        public boolean areAllItemsEnabled() {
            return false;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public MyAdapter() {
        }

        private int countItems(boolean z) {
            int i = 0;
            for (int i2 = 0; i2 < GlobalActionsDialogLite.this.mItems.size(); i2++) {
                if (GlobalActionsDialogLite.this.mItems.get(i2).shouldBeSeparated() == z) {
                    i++;
                }
            }
            return i;
        }

        public int countSeparatedItems() {
            return countItems(true);
        }

        public int countListItems() {
            return countItems(false);
        }

        public int getCount() {
            return countSeparatedItems() + countListItems();
        }

        public boolean isEnabled(int i) {
            return getItem(i).isEnabled();
        }

        public Action getItem(int i) {
            int i2 = 0;
            for (int i3 = 0; i3 < GlobalActionsDialogLite.this.mItems.size(); i3++) {
                Action action = GlobalActionsDialogLite.this.mItems.get(i3);
                if (GlobalActionsDialogLite.this.shouldShowAction(action)) {
                    if (i2 == i) {
                        return action;
                    }
                    i2++;
                }
            }
            throw new IllegalArgumentException("position " + i + " out of range of showable actions, filtered count=" + getCount() + ", keyguardshowing=" + GlobalActionsDialogLite.this.mKeyguardShowing + ", provisioned=" + GlobalActionsDialogLite.this.mDeviceProvisioned);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Action item = getItem(i);
            View create = item.create(GlobalActionsDialogLite.this.mContext, view, viewGroup, LayoutInflater.from(GlobalActionsDialogLite.this.mContext));
            create.setOnClickListener(new GlobalActionsDialogLite$MyAdapter$$ExternalSyntheticLambda0(this, i));
            if (item instanceof LongPressAction) {
                create.setOnLongClickListener(new GlobalActionsDialogLite$MyAdapter$$ExternalSyntheticLambda1(this, i));
            }
            return create;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$0(int i, View view) {
            onClickItem(i);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$getView$1(int i, View view) {
            return onLongClickItem(i);
        }

        public boolean onLongClickItem(int i) {
            Action item = GlobalActionsDialogLite.this.mAdapter.getItem(i);
            if (!(item instanceof LongPressAction)) {
                return false;
            }
            ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
            if (actionsDialogLite != null) {
                actionsDialogLite.lambda$initializeLayout$4();
            } else {
                Log.w("GlobalActionsDialogLite", "Action long-clicked while mDialog is null.");
            }
            return ((LongPressAction) item).onLongPress();
        }

        public void onClickItem(int i) {
            Action item = GlobalActionsDialogLite.this.mAdapter.getItem(i);
            if (!(item instanceof SilentModeTriStateAction)) {
                ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
                if (actionsDialogLite == null) {
                    Log.w("GlobalActionsDialogLite", "Action clicked while mDialog is null.");
                } else if (!(item instanceof PowerOptionsAction)) {
                    actionsDialogLite.lambda$initializeLayout$4();
                }
                item.onPress();
            }
        }

        public boolean shouldBeSeparated(int i) {
            return getItem(i).shouldBeSeparated();
        }
    }

    public class MyPowerOptionsAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return (long) i;
        }

        public MyPowerOptionsAdapter() {
        }

        public int getCount() {
            return GlobalActionsDialogLite.this.mPowerItems.size();
        }

        public Action getItem(int i) {
            return GlobalActionsDialogLite.this.mPowerItems.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Action item = getItem(i);
            if (item == null) {
                Log.w("GlobalActionsDialogLite", "No power options action found at position: " + i);
                return null;
            }
            int i2 = R$layout.global_actions_power_item;
            if (view == null) {
                view = LayoutInflater.from(GlobalActionsDialogLite.this.mContext).inflate(i2, viewGroup, false);
            }
            view.setOnClickListener(new C0971xb6603ee5(this, i));
            if (item instanceof LongPressAction) {
                view.setOnLongClickListener(new C0972xb6603ee6(this, i));
            }
            ImageView imageView = (ImageView) view.findViewById(16908294);
            TextView textView = (TextView) view.findViewById(16908299);
            textView.setSelected(true);
            imageView.setImageDrawable(item.getIcon(GlobalActionsDialogLite.this.mContext));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (item.getMessage() != null) {
                textView.setText(item.getMessage());
            } else {
                textView.setText(item.getMessageResId());
            }
            return view;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$0(int i, View view) {
            onClickItem(i);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$getView$1(int i, View view) {
            return onLongClickItem(i);
        }

        private boolean onLongClickItem(int i) {
            Action item = getItem(i);
            if (!(item instanceof LongPressAction)) {
                return false;
            }
            ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
            if (actionsDialogLite != null) {
                actionsDialogLite.lambda$initializeLayout$4();
            } else {
                Log.w("GlobalActionsDialogLite", "Action long-clicked while mDialog is null.");
            }
            return ((LongPressAction) item).onLongPress();
        }

        private void onClickItem(int i) {
            Action item = getItem(i);
            if (!(item instanceof SilentModeTriStateAction)) {
                ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
                if (actionsDialogLite != null) {
                    actionsDialogLite.lambda$initializeLayout$4();
                } else {
                    Log.w("GlobalActionsDialogLite", "Action clicked while mDialog is null.");
                }
                item.onPress();
            }
        }
    }

    public class MyOverflowAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return (long) i;
        }

        public MyOverflowAdapter() {
        }

        public int getCount() {
            return GlobalActionsDialogLite.this.mOverflowItems.size();
        }

        public Action getItem(int i) {
            return GlobalActionsDialogLite.this.mOverflowItems.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Action item = getItem(i);
            if (item == null) {
                Log.w("GlobalActionsDialogLite", "No overflow action found at position: " + i);
                return null;
            }
            int i2 = R$layout.controls_more_item;
            if (view == null) {
                view = LayoutInflater.from(GlobalActionsDialogLite.this.mContext).inflate(i2, viewGroup, false);
            }
            TextView textView = (TextView) view;
            if (item.getMessageResId() != 0) {
                textView.setText(item.getMessageResId());
            } else {
                textView.setText(item.getMessage());
            }
            return textView;
        }

        /* access modifiers changed from: protected */
        public boolean onLongClickItem(int i) {
            Action item = getItem(i);
            if (!(item instanceof LongPressAction)) {
                return false;
            }
            ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
            if (actionsDialogLite != null) {
                actionsDialogLite.lambda$initializeLayout$4();
            } else {
                Log.w("GlobalActionsDialogLite", "Action long-clicked while mDialog is null.");
            }
            return ((LongPressAction) item).onLongPress();
        }

        /* access modifiers changed from: protected */
        public void onClickItem(int i) {
            Action item = getItem(i);
            if (!(item instanceof SilentModeTriStateAction)) {
                ActionsDialogLite actionsDialogLite = GlobalActionsDialogLite.this.mDialog;
                if (actionsDialogLite != null) {
                    actionsDialogLite.lambda$initializeLayout$4();
                } else {
                    Log.w("GlobalActionsDialogLite", "Action clicked while mDialog is null.");
                }
                item.onPress();
            }
        }
    }

    protected abstract class SinglePressAction implements Action {
        private final Drawable mIcon;
        private final int mIconResId;
        private final CharSequence mMessage;
        private final int mMessageResId;

        public boolean isEnabled() {
            return true;
        }

        protected SinglePressAction(int i, int i2) {
            this.mIconResId = i;
            this.mMessageResId = i2;
            this.mMessage = null;
            this.mIcon = null;
        }

        protected SinglePressAction(int i, Drawable drawable, CharSequence charSequence) {
            this.mIconResId = i;
            this.mMessageResId = 0;
            this.mMessage = charSequence;
            this.mIcon = drawable;
        }

        public int getMessageResId() {
            return this.mMessageResId;
        }

        public CharSequence getMessage() {
            return this.mMessage;
        }

        public Drawable getIcon(Context context) {
            Drawable drawable = this.mIcon;
            if (drawable != null) {
                return drawable;
            }
            return context.getDrawable(this.mIconResId);
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View inflate = layoutInflater.inflate(GlobalActionsDialogLite.this.getGridItemLayoutResource(), viewGroup, false);
            inflate.setId(View.generateViewId());
            ImageView imageView = (ImageView) inflate.findViewById(16908294);
            TextView textView = (TextView) inflate.findViewById(16908299);
            textView.setSelected(true);
            imageView.setImageDrawable(getIcon(context));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (MotoFeature.getInstance(GlobalActionsDialogLite.this.mContext).isCustomPanelView()) {
                int color = context.getResources().getColor(R$color.prc_qs_custom_text_color);
                int color2 = context.getResources().getColor(R$color.prcQSTileInactiveColorForUnfiexed);
                textView.setTextColor(color);
                imageView.getDrawable().setTint(color);
                imageView.setBackgroundTintList(ColorStateList.valueOf(color2));
            }
            CharSequence charSequence = this.mMessage;
            if (charSequence != null) {
                textView.setText(charSequence);
            } else {
                textView.setText(this.mMessageResId);
            }
            return inflate;
        }
    }

    /* access modifiers changed from: protected */
    public int getGridItemLayoutResource() {
        return R$layout.global_actions_grid_item_lite;
    }

    private enum ToggleState {
        Off(false),
        TurningOn(true),
        TurningOff(true),
        On(false);
        
        private final boolean mInTransition;

        private ToggleState(boolean z) {
            this.mInTransition = z;
        }

        public boolean inTransition() {
            return this.mInTransition;
        }
    }

    private abstract class ToggleAction implements Action {
        protected int mDisabledIconResid;
        protected int mDisabledStatusMessageResId;
        protected int mEnabledIconResId;
        protected int mEnabledStatusMessageResId;
        protected int mMessageResId;
        protected ToggleState mState = ToggleState.Off;

        public CharSequence getMessage() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public abstract void onToggle(boolean z);

        /* access modifiers changed from: package-private */
        public void willCreate() {
        }

        ToggleAction(int i, int i2, int i3, int i4, int i5) {
            this.mEnabledIconResId = i;
            this.mDisabledIconResid = i2;
            this.mMessageResId = i3;
            this.mEnabledStatusMessageResId = i4;
            this.mDisabledStatusMessageResId = i5;
        }

        private boolean isOn() {
            ToggleState toggleState = this.mState;
            return toggleState == ToggleState.On || toggleState == ToggleState.TurningOn;
        }

        public int getMessageResId() {
            return isOn() ? this.mEnabledStatusMessageResId : this.mDisabledStatusMessageResId;
        }

        private int getIconResId() {
            return isOn() ? this.mEnabledIconResId : this.mDisabledIconResid;
        }

        public Drawable getIcon(Context context) {
            return context.getDrawable(getIconResId());
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            willCreate();
            View inflate = layoutInflater.inflate(R$layout.global_actions_grid_item_v2, viewGroup, false);
            ViewGroup.LayoutParams layoutParams = inflate.getLayoutParams();
            layoutParams.width = -2;
            inflate.setLayoutParams(layoutParams);
            ImageView imageView = (ImageView) inflate.findViewById(16908294);
            TextView textView = (TextView) inflate.findViewById(16908299);
            boolean isEnabled = isEnabled();
            if (textView != null) {
                textView.setText(getMessageResId());
                textView.setEnabled(isEnabled);
                textView.setSelected(true);
            }
            if (imageView != null) {
                imageView.setImageDrawable(context.getDrawable(getIconResId()));
                imageView.setEnabled(isEnabled);
            }
            inflate.setEnabled(isEnabled);
            return inflate;
        }

        public final void onPress() {
            if (this.mState.inTransition()) {
                Log.w("GlobalActionsDialogLite", "shouldn't be able to toggle when in transition");
                return;
            }
            boolean z = this.mState != ToggleState.On;
            onToggle(z);
            changeStateFromPress(z);
        }

        public boolean isEnabled() {
            return !this.mState.inTransition();
        }

        /* access modifiers changed from: protected */
        public void changeStateFromPress(boolean z) {
            this.mState = z ? ToggleState.On : ToggleState.Off;
        }

        public void updateState(ToggleState toggleState) {
            this.mState = toggleState;
        }
    }

    private class AirplaneModeAction extends ToggleAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        AirplaneModeAction() {
            super(17303358, 17303360, 17040369, 17040368, 17040367);
        }

        /* access modifiers changed from: package-private */
        public void onToggle(boolean z) {
            if (!GlobalActionsDialogLite.this.mHasTelephony || !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                GlobalActionsDialogLite.this.changeAirplaneModeSystemSetting(z);
                return;
            }
            boolean unused = GlobalActionsDialogLite.this.mIsWaitingForEcmExit = true;
            Intent intent = new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null);
            intent.addFlags(268435456);
            GlobalActionsDialogLite.this.mContext.startActivity(intent);
        }

        /* access modifiers changed from: protected */
        public void changeStateFromPress(boolean z) {
            if (GlobalActionsDialogLite.this.mHasTelephony && !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                ToggleState toggleState = z ? ToggleState.TurningOn : ToggleState.TurningOff;
                this.mState = toggleState;
                ToggleState unused = GlobalActionsDialogLite.this.mAirplaneState = toggleState;
            }
        }
    }

    private class SilentModeToggleAction extends ToggleAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        SilentModeToggleAction() {
            super(17303195, 17303194, 17040364, 17040363, 17040362);
        }

        /* access modifiers changed from: package-private */
        public void onToggle(boolean z) {
            if (z) {
                GlobalActionsDialogLite.this.mAudioManager.setRingerMode(0);
            } else {
                GlobalActionsDialogLite.this.mAudioManager.setRingerMode(2);
            }
        }
    }

    private static class SilentModeTriStateAction implements Action, View.OnClickListener {
        private static final int[] ITEM_IDS = {16909265, 16909266, 16909267};
        private final AudioManager mAudioManager;
        private final Handler mHandler;

        private int indexToRingerMode(int i) {
            return i;
        }

        private int ringerModeToIndex(int i) {
            return i;
        }

        public Drawable getIcon(Context context) {
            return null;
        }

        public CharSequence getMessage() {
            return null;
        }

        public int getMessageResId() {
            return 0;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onPress() {
        }

        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        SilentModeTriStateAction(AudioManager audioManager, Handler handler) {
            this.mAudioManager = audioManager;
            this.mHandler = handler;
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View inflate = layoutInflater.inflate(17367169, viewGroup, false);
            int ringerModeToIndex = ringerModeToIndex(this.mAudioManager.getRingerMode());
            int i = 0;
            while (i < 3) {
                View findViewById = inflate.findViewById(ITEM_IDS[i]);
                findViewById.setSelected(ringerModeToIndex == i);
                findViewById.setTag(Integer.valueOf(i));
                findViewById.setOnClickListener(this);
                i++;
            }
            return inflate;
        }

        public void onClick(View view) {
            if (view.getTag() instanceof Integer) {
                this.mAudioManager.setRingerMode(indexToRingerMode(((Integer) view.getTag()).intValue()));
                this.mHandler.sendEmptyMessageDelayed(0, 300);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setZeroDialogPressDelayForTesting() {
        this.mDialogPressDelay = 0;
    }

    /* access modifiers changed from: private */
    public void onAirplaneModeChanged() {
        if (!this.mHasTelephony && this.mAirplaneModeOn != null) {
            boolean z = false;
            if (this.mGlobalSettings.getInt("airplane_mode_on", 0) == 1) {
                z = true;
            }
            ToggleState toggleState = z ? ToggleState.On : ToggleState.Off;
            this.mAirplaneState = toggleState;
            this.mAirplaneModeOn.updateState(toggleState);
        }
    }

    /* access modifiers changed from: private */
    public void changeAirplaneModeSystemSetting(boolean z) {
        this.mGlobalSettings.putInt("airplane_mode_on", z ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        if (!this.mHasTelephony) {
            this.mAirplaneState = z ? ToggleState.On : ToggleState.Off;
        }
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    @VisibleForTesting
    static class ActionsDialogLite extends Dialog implements ColorExtractor.OnColorsChangedListener {
        protected final MyAdapter mAdapter;
        protected Drawable mBackgroundDrawable;
        protected final SysuiColorExtractor mColorExtractor;
        protected ViewGroup mContainer;
        protected final Context mContext;
        private GestureDetector mGestureDetector;
        @VisibleForTesting
        protected GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                ActionsDialogLite.this.mUiEventLogger.log(GlobalActionsEvent.GA_CLOSE_TAP_OUTSIDE);
                ActionsDialogLite actionsDialogLite = ActionsDialogLite.this;
                if (!actionsDialogLite.mTapBlankToDismiss) {
                    return false;
                }
                actionsDialogLite.cancel();
                return false;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (f2 >= 0.0f || f2 <= f || motionEvent.getY() > ((float) ActionsDialogLite.this.mStatusBar.getStatusBarHeight())) {
                    return false;
                }
                ActionsDialogLite.this.openShadeAndDismiss();
                return true;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (f2 <= 0.0f || Math.abs(f2) <= Math.abs(f) || motionEvent.getY() > ((float) ActionsDialogLite.this.mStatusBar.getStatusBarHeight())) {
                    return false;
                }
                ActionsDialogLite.this.openShadeAndDismiss();
                return true;
            }
        };
        protected MultiListLayout mGlobalActionsLayout;
        private GlobalActionsInfoProvider mInfoProvider;
        private boolean mKeyguardShowing;
        protected final NotificationShadeWindowController mNotificationShadeWindowController;
        protected final Runnable mOnRotateCallback;
        protected final MyOverflowAdapter mOverflowAdapter;
        private ListPopupWindow mOverflowPopup;
        protected final MyPowerOptionsAdapter mPowerOptionsAdapter;
        private Dialog mPowerOptionsDialog;
        protected float mScrimAlpha;
        protected boolean mShowing;
        /* access modifiers changed from: private */
        public StatusBar mStatusBar;
        protected final IStatusBarService mStatusBarService;
        protected final SysUiState mSysUiState;
        protected boolean mTapBlankToDismiss = true;
        protected final IBinder mToken = new Binder();
        /* access modifiers changed from: private */
        public UiEventLogger mUiEventLogger;

        ActionsDialogLite(Context context, int i, MyAdapter myAdapter, MyOverflowAdapter myOverflowAdapter, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, NotificationShadeWindowController notificationShadeWindowController, SysUiState sysUiState, Runnable runnable, boolean z, MyPowerOptionsAdapter myPowerOptionsAdapter, UiEventLogger uiEventLogger, GlobalActionsInfoProvider globalActionsInfoProvider, StatusBar statusBar) {
            super(context, i);
            this.mContext = context;
            this.mAdapter = myAdapter;
            this.mOverflowAdapter = myOverflowAdapter;
            this.mPowerOptionsAdapter = myPowerOptionsAdapter;
            this.mColorExtractor = sysuiColorExtractor;
            this.mStatusBarService = iStatusBarService;
            this.mNotificationShadeWindowController = notificationShadeWindowController;
            this.mSysUiState = sysUiState;
            this.mOnRotateCallback = runnable;
            this.mKeyguardShowing = z;
            this.mUiEventLogger = uiEventLogger;
            this.mInfoProvider = globalActionsInfoProvider;
            this.mStatusBar = statusBar;
            this.mGestureDetector = new GestureDetector(context, this.mGestureListener);
            Window window = getWindow();
            window.requestFeature(1);
            window.getDecorView();
            window.getAttributes().systemUiVisibility |= 768;
            window.setLayout(-1, -1);
            window.addFlags(17367296);
            window.setType(2020);
            window.getAttributes().setFitInsetsTypes(0);
            setTitle(17040366);
            initializeLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.mGestureDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: private */
        public void openShadeAndDismiss() {
            this.mUiEventLogger.log(GlobalActionsEvent.GA_CLOSE_TAP_OUTSIDE);
            if (this.mStatusBar.isKeyguardShowing()) {
                this.mStatusBar.animateExpandSettingsPanel((String) null);
            } else {
                this.mStatusBar.animateExpandNotificationsPanel();
            }
            lambda$initializeLayout$4();
        }

        private ListPopupWindow createPowerOverflowPopup() {
            GlobalActionsPopupMenu globalActionsPopupMenu = new GlobalActionsPopupMenu(new ContextThemeWrapper(this.mContext, R$style.Control_ListPopupWindow), false);
            globalActionsPopupMenu.setOnItemClickListener(new C0967x58d1e4b4(this));
            globalActionsPopupMenu.setOnItemLongClickListener(new C0968x58d1e4b5(this));
            globalActionsPopupMenu.setAnchorView(findViewById(R$id.global_actions_overflow_button));
            globalActionsPopupMenu.setAdapter(this.mOverflowAdapter);
            return globalActionsPopupMenu;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$createPowerOverflowPopup$0(AdapterView adapterView, View view, int i, long j) {
            this.mOverflowAdapter.onClickItem(i);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$createPowerOverflowPopup$1(AdapterView adapterView, View view, int i, long j) {
            return this.mOverflowAdapter.onLongClickItem(i);
        }

        public void showPowerOptionsMenu() {
            Dialog create = GlobalActionsPowerDialog.create(this.mContext, this.mPowerOptionsAdapter);
            this.mPowerOptionsDialog = create;
            create.show();
        }

        /* access modifiers changed from: protected */
        public void showPowerOverflowMenu() {
            ListPopupWindow createPowerOverflowPopup = createPowerOverflowPopup();
            this.mOverflowPopup = createPowerOverflowPopup;
            createPowerOverflowPopup.show();
        }

        /* access modifiers changed from: protected */
        public int getLayoutResource() {
            return R$layout.global_actions_grid_lite;
        }

        /* access modifiers changed from: protected */
        public void initializeLayout() {
            setContentView(getLayoutResource());
            fixNavBarClipping();
            if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
                findViewById(16908298).setBackground(this.mContext.getResources().getDrawable(R$drawable.prc_global_actions_lite_background));
            }
            MultiListLayout multiListLayout = (MultiListLayout) findViewById(R$id.global_actions_view);
            this.mGlobalActionsLayout = multiListLayout;
            multiListLayout.setListViewAccessibilityDelegate(new View.AccessibilityDelegate() {
                public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                    accessibilityEvent.getText().add(ActionsDialogLite.this.mContext.getString(17040366));
                    return true;
                }
            });
            this.mGlobalActionsLayout.setRotationListener(new C0969x58d1e4b6(this));
            this.mGlobalActionsLayout.setAdapter(this.mAdapter);
            ViewGroup viewGroup = (ViewGroup) findViewById(R$id.global_actions_container);
            this.mContainer = viewGroup;
            viewGroup.setOnTouchListener(new C0966x58d1e4b3(this));
            View findViewById = findViewById(R$id.global_actions_overflow_button);
            if (findViewById != null) {
                if (this.mOverflowAdapter.getCount() > 0) {
                    findViewById.setOnClickListener(new C0965x58d1e4b2(this));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mGlobalActionsLayout.getLayoutParams();
                    layoutParams.setMarginEnd(0);
                    this.mGlobalActionsLayout.setLayoutParams(layoutParams);
                } else {
                    findViewById.setVisibility(8);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mGlobalActionsLayout.getLayoutParams();
                    layoutParams2.setMarginEnd(this.mContext.getResources().getDimensionPixelSize(R$dimen.global_actions_side_margin));
                    this.mGlobalActionsLayout.setLayoutParams(layoutParams2);
                }
            }
            if (this.mBackgroundDrawable == null) {
                this.mBackgroundDrawable = new ScrimDrawable();
                this.mScrimAlpha = 1.0f;
            }
            GlobalActionsInfoProvider globalActionsInfoProvider = this.mInfoProvider;
            if (globalActionsInfoProvider != null && globalActionsInfoProvider.shouldShowMessage()) {
                this.mInfoProvider.addPanel(this.mContext, this.mContainer, this.mAdapter.getCount(), new C0970x58d1e4b7(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$initializeLayout$2(View view, MotionEvent motionEvent) {
            this.mGestureDetector.onTouchEvent(motionEvent);
            return view.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$initializeLayout$3(View view) {
            showPowerOverflowMenu();
        }

        /* access modifiers changed from: protected */
        public void fixNavBarClipping() {
            ViewGroup viewGroup = (ViewGroup) findViewById(16908290);
            viewGroup.setClipChildren(false);
            viewGroup.setClipToPadding(false);
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
            viewGroup2.setClipChildren(false);
            viewGroup2.setClipToPadding(false);
        }

        private void sendBroadcastEvent(String str) {
            Intent intent = new Intent(str);
            Bundle bundle = new Bundle();
            bundle.putString("event", "GlobalActions");
            intent.putExtras(bundle);
            Log.d("GlobalActionsDialogLite", "ActionsDialog send broadcast with extras: " + intent.toUri(0));
            this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, (String) null, new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Bundle resultExtras = getResultExtras(true);
                    Log.i("GlobalActionsDialogLite", "Final Result Receiver: " + resultExtras);
                }
            }, (Handler) null, -1, (String) null, (Bundle) null);
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.onStart();
            this.mGlobalActionsLayout.updateList();
            if (this.mBackgroundDrawable instanceof ScrimDrawable) {
                this.mColorExtractor.addOnColorsChangedListener(this);
                updateColors(this.mColorExtractor.getNeutralColors(), false);
            }
            if (GlobalActionsDialogLite.mXrvdFeatureEnabled) {
                Log.d("GlobalActionsDialogLite", "ActionsDialog: onStart");
                sendBroadcastEvent("com.qualcomm.qti.xrvd.service.showSystemUiBroadcast");
            }
        }

        private void updateColors(ColorExtractor.GradientColors gradientColors, boolean z) {
            Drawable drawable = this.mBackgroundDrawable;
            if (drawable instanceof ScrimDrawable) {
                ((ScrimDrawable) drawable).setColor(-16777216, z);
                View decorView = getWindow().getDecorView();
                if (gradientColors.supportsDarkText()) {
                    decorView.setSystemUiVisibility(8208);
                } else {
                    decorView.setSystemUiVisibility(0);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            this.mColorExtractor.removeOnColorsChangedListener(this);
            if (GlobalActionsDialogLite.mXrvdFeatureEnabled) {
                Log.d("GlobalActionsDialogLite", "ActionsDialog: onStop");
                sendBroadcastEvent("com.qualcomm.qti.xrvd.service.clearSystemUiBroadcast");
            }
        }

        public void onBackPressed() {
            super.onBackPressed();
            this.mUiEventLogger.log(GlobalActionsEvent.GA_CLOSE_BACK);
        }

        public void show() {
            super.show();
            showDialog();
        }

        /* access modifiers changed from: protected */
        public void showDialog() {
            this.mShowing = true;
            this.mNotificationShadeWindowController.setRequestTopUi(true, "GlobalActionsDialogLite");
            this.mSysUiState.setFlag(32768, true).commitUpdate(this.mContext.getDisplayId());
            ViewGroup viewGroup = (ViewGroup) this.mGlobalActionsLayout.getRootView();
            viewGroup.setOnApplyWindowInsetsListener(new C0964x58d1e4b1(viewGroup));
            this.mBackgroundDrawable.setAlpha(0);
            float animationOffsetX = this.mGlobalActionsLayout.getAnimationOffsetX();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mContainer, "alpha", new float[]{0.0f, 1.0f});
            Interpolator interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
            ofFloat.setInterpolator(interpolator);
            ofFloat.setDuration(183);
            ofFloat.addUpdateListener(new C0963x58d1e4b0(this));
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mContainer, "translationX", new float[]{animationOffsetX, 0.0f});
                ofFloat2.setInterpolator(interpolator);
                ofFloat2.setDuration(350);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
                animatorSet.start();
                return;
            }
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mGlobalActionsLayout, "alpha", new float[]{0.0f, 1.0f});
            ofFloat3.setInterpolator(interpolator);
            ofFloat3.setDuration(350);
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(new Animator[]{ofFloat, ofFloat3});
            animatorSet2.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showDialog$6(ValueAnimator valueAnimator) {
            this.mBackgroundDrawable.setAlpha((int) (valueAnimator.getAnimatedFraction() * this.mScrimAlpha * 255.0f));
        }

        /* renamed from: dismiss */
        public void lambda$initializeLayout$4() {
            dismissWithAnimation(new C0962xc16ab161(this));
        }

        /* access modifiers changed from: protected */
        /* renamed from: dismissInternal */
        public void lambda$dismiss$7() {
            this.mContainer.setTranslationX(0.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mContainer, "alpha", new float[]{1.0f, 0.0f});
            Interpolator interpolator = Interpolators.FAST_OUT_LINEAR_IN;
            ofFloat.setInterpolator(interpolator);
            ofFloat.setDuration(233);
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
                ofFloat.addUpdateListener(new C0960x58d1e4ae(this));
                float animationOffsetX = this.mGlobalActionsLayout.getAnimationOffsetX();
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mContainer, "translationX", new float[]{0.0f, animationOffsetX});
                ofFloat2.setInterpolator(interpolator);
                ofFloat2.setDuration(350);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ActionsDialogLite.this.completeDismiss();
                    }
                });
                animatorSet.start();
            } else {
                ofFloat.addUpdateListener(new C0961x58d1e4af(this));
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.play(ofFloat);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ActionsDialogLite.this.completeDismiss();
                    }
                });
                animatorSet2.start();
            }
            dismissOverflow(false);
            dismissPowerOptions(false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$dismissInternal$8(ValueAnimator valueAnimator) {
            int animatedFraction = (int) ((1.0f - valueAnimator.getAnimatedFraction()) * this.mScrimAlpha * 255.0f);
            this.mGlobalActionsLayout.setAlpha((float) animatedFraction);
            this.mBackgroundDrawable.setAlpha(animatedFraction);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$dismissInternal$9(ValueAnimator valueAnimator) {
            this.mBackgroundDrawable.setAlpha((int) ((1.0f - valueAnimator.getAnimatedFraction()) * this.mScrimAlpha * 255.0f));
        }

        /* access modifiers changed from: package-private */
        public void dismissWithAnimation(Runnable runnable) {
            if (this.mShowing) {
                this.mShowing = false;
                runnable.run();
            }
        }

        /* access modifiers changed from: protected */
        public void completeDismiss() {
            this.mShowing = false;
            dismissOverflow(true);
            dismissPowerOptions(true);
            this.mNotificationShadeWindowController.setRequestTopUi(false, "GlobalActionsDialogLite");
            this.mSysUiState.setFlag(32768, false).commitUpdate(this.mContext.getDisplayId());
            super.dismiss();
        }

        /* access modifiers changed from: protected */
        public final void dismissOverflow(boolean z) {
            ListPopupWindow listPopupWindow = this.mOverflowPopup;
            if (listPopupWindow == null) {
                return;
            }
            if (z) {
                listPopupWindow.dismissImmediate();
            } else {
                listPopupWindow.dismiss();
            }
        }

        /* access modifiers changed from: protected */
        public final void dismissPowerOptions(boolean z) {
            Dialog dialog = this.mPowerOptionsDialog;
            if (dialog == null) {
                return;
            }
            if (z) {
                dialog.dismiss();
            } else {
                dialog.dismiss();
            }
        }

        /* access modifiers changed from: protected */
        public final void setRotationSuggestionsEnabled(boolean z) {
            try {
                this.mStatusBarService.disable2ForUser(z ? 0 : 16, this.mToken, this.mContext.getPackageName(), Binder.getCallingUserHandle().getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void onColorsChanged(ColorExtractor colorExtractor, int i) {
            if (this.mKeyguardShowing) {
                if ((i & 2) != 0) {
                    updateColors(colorExtractor.getColors(2), true);
                }
            } else if ((i & 1) != 0) {
                updateColors(colorExtractor.getColors(1), true);
            }
        }

        public void refreshDialog() {
            dismissOverflow(true);
            dismissPowerOptions(true);
            initializeLayout();
            this.mGlobalActionsLayout.updateList();
        }

        public void onRotate(int i, int i2) {
            if (this.mShowing) {
                this.mOnRotateCallback.run();
                refreshDialog();
            }
        }
    }
}
