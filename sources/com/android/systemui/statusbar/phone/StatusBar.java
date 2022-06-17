package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.IApplicationThread;
import android.app.IWallpaperManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProfilerInfo;
import android.app.StatusBarManager;
import android.app.TaskInfo;
import android.app.UiModeManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.AudioAttributes;
import android.media.session.MediaSessionManager;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.view.Display;
import android.view.IRemoteAnimationRunner;
import android.view.IWindowManager;
import android.view.InsetsState;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.widget.DateTimeView;
import android.widget.ImageView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.view.AppearanceRegion;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.startingsurface.SplashscreenContentDrawer;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.EventLogTags;
import com.android.systemui.InitController;
import com.android.systemui.Prefs;
import com.android.systemui.R$array;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.SystemUI;
import com.android.systemui.accessibility.floatingmenu.AccessibilityFloatingMenuController;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.DelegateLaunchAnimatorController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.biometrics.AuthRippleController;
import com.android.systemui.biometrics.UdfpsController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.camera.CameraIntents;
import com.android.systemui.charging.WirelessChargingAnimation;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeCommandReceiver;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.fragments.ExtensionFragmentListener;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.KeyguardService;
import com.android.systemui.keyguard.KeyguardUnlockAnimationController;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.CarrierLanguageApplier;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.p006qs.QSPanelController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.scrim.ScrimView;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.AutoHideUiElement;
import com.android.systemui.statusbar.BackDropView;
import com.android.systemui.statusbar.CircleReveal;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.statusbar.KeyboardShortcuts;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.LiftReveal;
import com.android.systemui.statusbar.LightRevealScrim;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PowerButtonReveal;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.charging.WiredChargingRippleController;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationLaunchAnimatorControllerProvider;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.KeyguardBouncerDelegate;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.statusbar.policy.ImsIconController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.volume.VolumeComponent;
import com.android.systemui.wmshell.BubblesManager;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.gesturetouch.EdgeTouchPillController;
import com.motorola.gesturetouch.SideFpsController;
import com.motorola.internal.app.MotoDesktopManager;
import com.motorola.rro.RROsController;
import com.motorola.systemui.folio.FolioSensorManager;
import com.motorola.taskbar.MotoTaskBarController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.inject.Provider;
import motorola.core_services.misc.WaterfallManager;
import org.json.JSONObject;

public class StatusBar extends SystemUI implements DemoMode, ActivityStarter, KeyguardStateController.Callback, OnHeadsUpChangedListener, CommandQueue.Callbacks, ColorExtractor.OnColorsChangedListener, ConfigurationController.ConfigurationListener, StatusBarStateController.StateListener, LifecycleOwner, BatteryController.BatteryStateChangeCallback, ActivityLaunchAnimator.Callback {
    public static final int[] CAMERA_LAUNCH_GESTURE_VIBRATION_AMPLITUDES = {39, 82, 139, 213, 0, 127};
    public static final long[] CAMERA_LAUNCH_GESTURE_VIBRATION_TIMINGS = {20, 20, 20, 20, 100, 20};
    public static final boolean ONLY_CORE_APPS;
    public static final boolean USERDEBUG = (!"user".equals(Build.TYPE));
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private static final UiEventLogger sUiEventLogger = new UiEventLoggerImpl();
    private final boolean DEBUG_STYLUS = (!Build.IS_USER);
    private final int[] mAbsPos = new int[2];
    protected AccessibilityManager mAccessibilityManager;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityLaunchAnimator mActivityLaunchAnimator;
    private View mAmbientIndicationContainer;
    private final SystemStatusAnimationScheduler mAnimationScheduler;
    private int mAppearance;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private AuthRippleController mAuthRippleController;
    private final AutoHideController mAutoHideController;
    AutoHideUiElement mAutoHideUIElement = new AutoHideUiElement() {
        public void synchronizeState() {
            StatusBar.this.checkBarModes();
        }

        public boolean shouldHideOnTouch() {
            return !StatusBar.this.mRemoteInputManager.getController().isRemoteInputActive();
        }

        public boolean isVisible() {
            return StatusBar.this.isTransientShown();
        }

        public void hide() {
            StatusBar.this.clearTransient(1);
        }
    };
    private final BroadcastReceiver mBannerActionBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.android.systemui.statusbar.banner_action_cancel".equals(action) || "com.android.systemui.statusbar.banner_action_setup".equals(action)) {
                ((NotificationManager) StatusBar.this.mContext.getSystemService("notification")).cancel(5);
                Settings.Secure.putInt(StatusBar.this.mContext.getContentResolver(), "show_note_about_notification_hiding", 0);
                if ("com.android.systemui.statusbar.banner_action_setup".equals(action)) {
                    StatusBar.this.mShadeController.animateCollapsePanels(2, true);
                    StatusBar.this.mContext.startActivity(new Intent("android.settings.ACTION_APP_NOTIFICATION_REDACTION").addFlags(268435456));
                }
            }
        }
    };
    protected IStatusBarService mBarService;
    protected final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private final Lazy<BiometricUnlockController> mBiometricUnlockControllerLazy;
    protected boolean mBouncerShowing;
    private boolean mBouncerWasShowingWhenHidden;
    private BrightnessMirrorController mBrightnessMirrorController;
    private boolean mBrightnessMirrorVisible;
    private final BrightnessSlider.Factory mBrightnessSliderFactory;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Trace.beginSection("StatusBar#onReceive");
            String action = intent.getAction();
            int i = 0;
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action)) {
                KeyboardShortcuts.dismiss();
                if (StatusBar.this.mRemoteInputManager.getController() != null) {
                    StatusBar.this.mRemoteInputManager.getController().closeRemoteInputs();
                }
                if (StatusBar.this.mBubblesOptional.isPresent() && ((Bubbles) StatusBar.this.mBubblesOptional.get()).isStackExpanded()) {
                    ((Bubbles) StatusBar.this.mBubblesOptional.get()).collapseStack();
                }
                if (StatusBar.this.mLockscreenUserManager.isCurrentProfile(getSendingUserId())) {
                    String stringExtra = intent.getStringExtra("reason");
                    if (stringExtra != null && stringExtra.equals("recentapps")) {
                        i = 2;
                    }
                    StatusBar.this.mShadeController.animateCollapsePanels(i);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                NotificationShadeWindowController notificationShadeWindowController = StatusBar.this.mNotificationShadeWindowController;
                if (notificationShadeWindowController != null) {
                    notificationShadeWindowController.setNotTouchable(false);
                }
                if (StatusBar.this.mBubblesOptional.isPresent() && ((Bubbles) StatusBar.this.mBubblesOptional.get()).isStackExpanded()) {
                    StatusBar.this.mMainThreadHandler.post(new StatusBar$16$$ExternalSyntheticLambda0(this));
                }
                StatusBar.this.finishBarAnimations();
                StatusBar.this.resetUserExpandedStates();
            } else if ("android.app.action.SHOW_DEVICE_MONITORING_DIALOG".equals(action)) {
                StatusBar.this.mQSPanelController.showDeviceMonitoringDialog();
            }
            Trace.endSection();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0() {
            ((Bubbles) StatusBar.this.mBubblesOptional.get()).collapseStack();
        }
    };
    private final Bubbles.BubbleExpandListener mBubbleExpandListener;
    private final Optional<BubblesManager> mBubblesManagerOptional;
    /* access modifiers changed from: private */
    public final Optional<Bubbles> mBubblesOptional;
    /* access modifiers changed from: private */
    public final BypassHeadsUpNotifier mBypassHeadsUpNotifier;
    private VibrationEffect mCameraLaunchGestureVibrationEffect;
    private CarrierLabelUpdateMonitor mCarrierLabelUpdateMonitor = null;
    private WiredChargingRippleController mChargingRippleAnimationController;
    private final Runnable mCheckBarModes = new StatusBar$$ExternalSyntheticLambda15(this);
    private final SysuiColorExtractor mColorExtractor;
    protected final CommandQueue mCommandQueue;
    private final ConfigurationController mConfigurationController;
    private final Point mCurrentDisplaySize = new Point();
    private final DemoModeController mDemoModeController;
    private final BroadcastReceiver mDemoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            "fake_artwork".equals(intent.getAction());
        }
    };
    protected boolean mDeviceInteractive;
    protected DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    private int mDisabled1 = 0;
    private int mDisabled2 = 0;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    protected Display mDisplay;
    /* access modifiers changed from: private */
    public int mDisplayId;
    private final DisplayMetrics mDisplayMetrics;
    /* access modifiers changed from: private */
    public final DozeParameters mDozeParameters;
    protected DozeScrimController mDozeScrimController;
    @VisibleForTesting
    DozeServiceHost mDozeServiceHost;
    protected boolean mDozing;
    private IDreamManager mDreamManager;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private EdgeTouchPillController mEdgeTouchPillController = null;
    private NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public boolean mExpandedVisible;
    private final List<ExpansionChangedListener> mExpansionChangedListeners;
    private final ExtensionController mExtensionController;
    private final FalsingManager.FalsingBeliefListener mFalsingBeliefListener = new FalsingManager.FalsingBeliefListener() {
        public void onFalse() {
            if (MotoFeature.getInstance(StatusBar.this.mContext).isCustomPanelView() && StatusBar.USERDEBUG) {
                Log.i("StatusBar", "PrcPanel onFalse()");
            }
            StatusBar.this.mStatusBarKeyguardViewManager.reset(true);
        }
    };
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    private final FalsingManager mFalsingManager;
    private final FeatureFlags mFeatureFlags;
    private boolean mFolioClosed = false;
    private FolioSensorManager.Callback mFolioStateCallback = new StatusBar$$ExternalSyntheticLambda11(this);
    private final GestureRecorder mGestureRec;
    protected PowerManager.WakeLock mGestureWakeLock;
    private final NotificationGutsManager mGutsManager;
    protected final C1935H mHandler = createHandler();
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    /* access modifiers changed from: private */
    public final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mHideIconsForBouncer;
    private PhoneStatusBarPolicy mIconPolicy;
    private final InitController mInitController;
    private int mInteractingWindows;
    protected boolean mIsKeyguard;
    private boolean mIsOccluded;
    /* access modifiers changed from: private */
    public final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    private boolean mKeyguardFadingWhilePulsing;
    KeyguardIndicationController mKeyguardIndicationController;
    private final KeyguardLiftController mKeyguardLiftController;
    protected KeyguardManager mKeyguardManager;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final KeyguardUnlockAnimationController mKeyguardUnlockAnimationController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private ViewMediatorCallback mKeyguardViewMediatorCallback;
    /* access modifiers changed from: private */
    public int mLastCameraLaunchSource;
    private int mLastLoggedStateFingerprint;
    private long mLastStylusClickTime;
    /* access modifiers changed from: private */
    public boolean mLaunchCameraOnFinishedGoingToSleep;
    /* access modifiers changed from: private */
    public boolean mLaunchCameraWhenFinishedWaking;
    /* access modifiers changed from: private */
    public boolean mLaunchEmergencyActionOnFinishedGoingToSleep;
    /* access modifiers changed from: private */
    public boolean mLaunchEmergencyActionWhenFinishedWaking;
    private Runnable mLaunchTransitionEndRunnable;
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    private final LightBarController mLightBarController;
    private LightRevealScrim mLightRevealScrim;
    private final LightsOutNotifController mLightsOutNotifController;
    private LockscreenShadeTransitionController mLockscreenShadeTransitionController;
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    protected LockscreenWallpaper mLockscreenWallpaper;
    private final Lazy<LockscreenWallpaper> mLockscreenWallpaperLazy;
    /* access modifiers changed from: private */
    public final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private MotoDisplayManager mMotoDisplayManager;
    private final NavigationBarController mNavigationBarController;
    private final NetworkController mNetworkController;
    private boolean mNoAnimationOnNextBarModeChange;
    private NotificationActivityStarter mNotificationActivityStarter;
    private NotificationLaunchAnimatorControllerProvider mNotificationAnimationProvider;
    protected final NotificationIconAreaController mNotificationIconAreaController;
    protected final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationLogger mNotificationLogger;
    protected NotificationPanelViewController mNotificationPanelViewController;
    private Lazy<NotificationShadeDepthController> mNotificationShadeDepthControllerLazy;
    protected NotificationShadeWindowController mNotificationShadeWindowController;
    protected NotificationShadeWindowView mNotificationShadeWindowView;
    protected NotificationShadeWindowViewController mNotificationShadeWindowViewController;
    protected NotificationShelfController mNotificationShelfController;
    /* access modifiers changed from: private */
    public NotificationsController mNotificationsController;
    private final OngoingCallController mOngoingCallController;
    protected boolean mPanelExpanded;
    protected StatusBarWindowView mPhoneStatusBarWindow;
    private final PluginDependencyProvider mPluginDependencyProvider;
    private final PluginManager mPluginManager;
    private PowerButtonReveal mPowerButtonReveal;
    private final PowerManager mPowerManager;
    protected StatusBarNotificationPresenter mPresenter;
    /* access modifiers changed from: private */
    public final PulseExpansionHandler mPulseExpansionHandler;
    /* access modifiers changed from: private */
    public QSPanelController mQSPanelController;
    private final Object mQueueLock = new Object();
    private ContentObserver mQuickLaunchCameraSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z, Uri uri, int i) {
            StatusBar.this.updateQuickLaunchCameraSetting();
        }
    };
    private int mQuickLaunchDefaultCameraValue;
    /* access modifiers changed from: private */
    public boolean mQuickLaunchUseFrontCamWhenFinishedWaking = false;
    private boolean mQuickLaunchUseFrontCamera = false;
    private RDPUnlockController mRDPUnlockController;
    private RROsController mRROsController;
    /* access modifiers changed from: private */
    public final NotificationRemoteInputManager mRemoteInputManager;
    private final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private View mReportRejectedTouch;
    private boolean mRestoreNotificationPanelAlphaIfNeed;
    private final ScreenLifecycle mScreenLifecycle;
    final ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurningOn() {
            StatusBar.this.mFalsingCollector.delayOnScreenTurningOn();
            StatusBar.this.mNotificationPanelViewController.onScreenTurningOn();
        }

        public void onScreenTurnedOn() {
            StatusBar.this.mScrimController.onScreenTurnedOn();
        }

        public void onScreenTurnedOff() {
            StatusBar.this.mFalsingCollector.onScreenOff();
            StatusBar.this.mScrimController.onScreenTurnedOff();
            StatusBar.this.updateIsKeyguard();
            if (MotoFeature.getInstance(StatusBar.this.mContext).isCustomPanelView()) {
                if (StatusBar.USERDEBUG) {
                    Log.i("StatusBar", "PrcPanel Screen Turned Off");
                }
                StatusBar.this.mNotificationPanelViewController.updatePanelViewState(0);
            }
        }
    };
    private final ScreenPinningRequest mScreenPinningRequest;
    private final ScreenshotHelper mScreenshotHelper;
    /* access modifiers changed from: private */
    public final ScrimController mScrimController;
    /* access modifiers changed from: private */
    public final ShadeController mShadeController;
    private SideFpsController mSideFpsController = null;
    private StatusBarSignalPolicy mSignalPolicy;
    private final Optional<LegacySplitScreen> mSplitScreenOptional;
    protected NotificationStackScrollLayout mStackScroller;
    private NotificationStackScrollLayoutController mStackScrollerController;
    final Runnable mStartTracing = new Runnable() {
        public void run() {
            StatusBar.this.vibrate();
            SystemClock.sleep(250);
            Log.d("StatusBar", "startTracing");
            Debug.startMethodTracing("/data/statusbar-traces/trace");
            StatusBar statusBar = StatusBar.this;
            statusBar.mHandler.postDelayed(statusBar.mStopTracing, 10000);
        }
    };
    private final Optional<StartingSurface> mStartingSurfaceOptional;
    protected int mState;
    private final Provider<StatusBarComponent.Builder> mStatusBarComponentBuilder;
    private final StatusBarIconController mStatusBarIconController;
    protected StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarLocationPublisher mStatusBarLocationPublisher;
    private int mStatusBarMode;
    private final StatusBarNotificationActivityStarter.Builder mStatusBarNotificationActivityStarterBuilder;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private LogMaker mStatusBarStateLog;
    private final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    protected PhoneStatusBarView mStatusBarView;
    protected StatusBarWindowController mStatusBarWindowController;
    private boolean mStatusBarWindowHidden;
    private int mStatusBarWindowState = 0;
    final Runnable mStopTracing = new StatusBar$$ExternalSyntheticLambda26(this);
    private final SuperStatusBarViewFactory mSuperStatusBarViewFactory;
    BroadcastReceiver mTaskbarChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (StatusBar.this.mBubblesOptional.isPresent()) {
                ((Bubbles) StatusBar.this.mBubblesOptional.get()).onTaskbarChanged(intent.getExtras());
            }
        }
    };
    private final int[] mTmpInt2 = new int[2];
    private boolean mTopHidesStatusBar;
    private boolean mTransientShown;
    private UdfpsController mUdfpsController;
    private final Executor mUiBgExecutor;
    private UiModeManager mUiModeManager;
    private final ScrimController.Callback mUnlockScrimCallback = new ScrimController.Callback() {
        public void onFinished() {
            StatusBar statusBar = StatusBar.this;
            if (statusBar.mStatusBarKeyguardViewManager == null) {
                Log.w("StatusBar", "Tried to notify keyguard visibility when mStatusBarKeyguardViewManager was null");
            } else if (statusBar.mKeyguardStateController.isKeyguardFadingAway()) {
                StatusBar.this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
            }
        }

        public void onCancelled() {
            onFinished();
        }
    };
    /* access modifiers changed from: private */
    public final UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    private final KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onDreamingStateChanged(boolean z) {
            if (z) {
                StatusBar.this.maybeEscalateHeadsUp();
            }
        }

        public void onStrongAuthStateChanged(int i) {
            super.onStrongAuthStateChanged(i);
            StatusBar.this.mNotificationsController.requestNotificationUpdate("onStrongAuthStateChanged");
        }
    };
    private boolean mUseMotoFaceUnlock = SystemProperties.getBoolean("ro.face.moto_unlock_service", false);
    private final UserInfoControllerImpl mUserInfoControllerImpl;
    @VisibleForTesting
    protected boolean mUserSetup = false;
    private final DeviceProvisionedController.DeviceProvisionedListener mUserSetupObserver = new DeviceProvisionedController.DeviceProvisionedListener() {
        public void onUserSetupChanged() {
            boolean isUserSetup = StatusBar.this.mDeviceProvisionedController.isUserSetup(StatusBar.this.mDeviceProvisionedController.getCurrentUser());
            Log.d("StatusBar", "mUserSetupObserver - DeviceProvisionedListener called for user " + StatusBar.this.mDeviceProvisionedController.getCurrentUser());
            StatusBar statusBar = StatusBar.this;
            if (isUserSetup != statusBar.mUserSetup) {
                statusBar.mUserSetup = isUserSetup;
                if (!isUserSetup && statusBar.mStatusBarView != null) {
                    statusBar.animateCollapseQuickSettings();
                }
                StatusBar statusBar2 = StatusBar.this;
                NotificationPanelViewController notificationPanelViewController = statusBar2.mNotificationPanelViewController;
                if (notificationPanelViewController != null) {
                    notificationPanelViewController.setUserSetupComplete(statusBar2.mUserSetup);
                }
                StatusBar.this.updateQsExpansionEnabled();
            }
        }
    };
    private final UserSwitcherController mUserSwitcherController;
    private boolean mVibrateOnOpening;
    private Vibrator mVibrator;
    private final VibratorHelper mVibratorHelper;
    private final NotificationViewHierarchyManager mViewHierarchyManager;
    protected boolean mVisible;
    private boolean mVisibleToUser;
    private final VisualStabilityManager mVisualStabilityManager;
    private final VolumeComponent mVolumeComponent;
    /* access modifiers changed from: private */
    public boolean mWakeUpComingFromTouch;
    /* access modifiers changed from: private */
    public final NotificationWakeUpCoordinator mWakeUpCoordinator;
    /* access modifiers changed from: private */
    public PointF mWakeUpTouchLocation;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
        public void onFinishedGoingToSleep() {
            StatusBar.this.setWakingUpFromAod(false);
            StatusBar.this.mNotificationPanelViewController.onAffordanceLaunchEnded();
            StatusBar.this.releaseGestureWakeLock();
            if (StatusBar.this.mLaunchCameraWhenFinishedWaking) {
                StatusBar.this.mScrimController.setLaunchCameraWhenFinishedWaking(false);
            }
            boolean unused = StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
            StatusBar statusBar = StatusBar.this;
            statusBar.mDeviceInteractive = false;
            boolean unused2 = statusBar.mWakeUpComingFromTouch = false;
            PointF unused3 = StatusBar.this.mWakeUpTouchLocation = null;
            StatusBar.this.updateVisibleToUser();
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.mNotificationShadeWindowViewController.cancelCurrentTouch();
            if (StatusBar.this.mLaunchCameraOnFinishedGoingToSleep) {
                boolean unused4 = StatusBar.this.mLaunchCameraOnFinishedGoingToSleep = false;
                StatusBar.this.mHandler.post(new StatusBar$19$$ExternalSyntheticLambda0(this));
            }
            if (StatusBar.this.mLaunchEmergencyActionOnFinishedGoingToSleep) {
                boolean unused5 = StatusBar.this.mLaunchEmergencyActionOnFinishedGoingToSleep = false;
                StatusBar.this.mHandler.post(new StatusBar$19$$ExternalSyntheticLambda1(this));
            }
            StatusBar.this.updateIsKeyguard();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFinishedGoingToSleep$0() {
            StatusBar statusBar = StatusBar.this;
            statusBar.onCameraLaunchGestureDetected(statusBar.mLastCameraLaunchSource);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFinishedGoingToSleep$1() {
            StatusBar.this.onEmergencyActionLaunchGestureDetected();
        }

        public void onStartedGoingToSleep() {
            DejankUtils.startDetectingBlockingIpcs("StatusBar#onStartedGoingToSleep");
            StatusBar.this.updateRevealEffect(false);
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.notifyHeadsUpGoingToSleep();
            StatusBar.this.dismissVolumeDialog();
            StatusBar.this.mWakeUpCoordinator.setFullyAwake(false);
            StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(false);
            StatusBar.this.mKeyguardBypassController.onStartedGoingToSleep();
            if (StatusBar.this.mDozeParameters.shouldControlUnlockedScreenOff()) {
                StatusBar.this.makeExpandedVisible(true);
            }
            DejankUtils.stopDetectingBlockingIpcs("StatusBar#onStartedGoingToSleep");
        }

        public void onStartedWakingUp() {
            DejankUtils.startDetectingBlockingIpcs("StatusBar#onStartedWakingUp");
            StatusBar statusBar = StatusBar.this;
            statusBar.mDeviceInteractive = true;
            statusBar.mWakeUpCoordinator.setWakingUp(true);
            if (!StatusBar.this.mKeyguardBypassController.getBypassEnabled()) {
                StatusBar.this.mHeadsUpManager.releaseAllImmediately();
            }
            StatusBar.this.updateVisibleToUser();
            if (!StatusBar.this.mWakingUpFromAod) {
                StatusBar.this.updateIsKeyguard();
            }
            boolean unused = StatusBar.this.mWakingUpFromAod = false;
            StatusBar.this.mDozeServiceHost.stopDozing();
            StatusBar.this.updateRevealEffect(true);
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.mPulseExpansionHandler.onStartedWakingUp();
            if (StatusBar.this.mUnlockedScreenOffAnimationController.isScreenOffLightRevealAnimationPlaying()) {
                StatusBar.this.makeExpandedInvisible();
            }
            DejankUtils.stopDetectingBlockingIpcs("StatusBar#onStartedWakingUp");
        }

        public void onFinishedWakingUp() {
            StatusBar.this.mWakeUpCoordinator.setFullyAwake(true);
            StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(true);
            StatusBar.this.mWakeUpCoordinator.setWakingUp(false);
            if (StatusBar.this.mLaunchCameraWhenFinishedWaking) {
                StatusBar statusBar = StatusBar.this;
                statusBar.mNotificationPanelViewController.launchCamera(true, statusBar.mLastCameraLaunchSource, StatusBar.this.mQuickLaunchUseFrontCamWhenFinishedWaking, true);
                boolean unused = StatusBar.this.mQuickLaunchUseFrontCamWhenFinishedWaking = false;
            }
            if (StatusBar.this.mLaunchEmergencyActionWhenFinishedWaking) {
                boolean unused2 = StatusBar.this.mLaunchEmergencyActionWhenFinishedWaking = false;
                Intent access$3900 = StatusBar.this.getEmergencyActionIntent();
                if (access$3900 != null) {
                    StatusBar.this.mContext.startActivityAsUser(access$3900, UserHandle.CURRENT);
                }
            }
            StatusBar.this.updateScrimController();
            if (StatusBar.this.mLaunchCameraWhenFinishedWaking) {
                boolean unused3 = StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
                StatusBar.this.mScrimController.setLaunchCameraWhenFinishedWaking(StatusBar.this.mLaunchCameraWhenFinishedWaking);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mWakingUpFromAod = false;
    private final BroadcastReceiver mWallpaperChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            CliStatusBar cliStatusBar;
            if (!StatusBar.this.mWallpaperSupported) {
                Log.wtf("StatusBar", "WallpaperManager not supported");
                return;
            }
            WallpaperInfo wallpaperInfo = ((WallpaperManager) context.getSystemService(WallpaperManager.class)).getWallpaperInfo(-2);
            boolean z = StatusBar.this.mContext.getResources().getBoolean(17891546) && wallpaperInfo != null && wallpaperInfo.supportsAmbientMode();
            StatusBar.this.mNotificationShadeWindowController.setWallpaperSupportsAmbientMode(z);
            StatusBar.this.mScrimController.setWallpaperSupportsAmbientMode(z);
            if (MotoFeature.getInstance(StatusBar.this.mContext).isSupportCli() && (cliStatusBar = (CliStatusBar) Dependency.get(CliStatusBar.class)) != null) {
                cliStatusBar.wallpaperChanged();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mWallpaperSupported;
    private boolean mWereIconsJustHidden;
    protected WindowManager mWindowManager;
    protected IWindowManager mWindowManagerService;

    public interface ExpansionChangedListener {
        void onExpansionChanged(float f, boolean z);
    }

    private static int barMode(boolean z, int i) {
        if (z) {
            return 1;
        }
        if ((i & 5) == 5) {
            return 3;
        }
        if ((i & 4) != 0) {
            return 6;
        }
        if ((i & 1) != 0) {
            return 4;
        }
        return (i & 32) != 0 ? 1 : 0;
    }

    private String getCameraSourceString(int i) {
        if (i == 0) {
            return "wiggle_gesture";
        }
        if (i != 1) {
            return null;
        }
        return "power_double_tap";
    }

    private static int getLoggingFingerprint(int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        return (i & 255) | ((z ? 1 : 0) << true) | ((z2 ? 1 : 0) << true) | ((z3 ? 1 : 0) << true) | ((z4 ? 1 : 0) << true) | ((z5 ? 1 : 0) << true);
    }

    public boolean isFalsingThresholdNeeded() {
        return true;
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
    }

    static {
        boolean z;
        try {
            z = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        } catch (RemoteException unused) {
            z = false;
        }
        ONLY_CORE_APPS = z;
    }

    @VisibleForTesting
    public enum StatusBarUiEvent implements UiEventLogger.UiEventEnum {
        LOCKSCREEN_OPEN_SECURE(405),
        LOCKSCREEN_OPEN_INSECURE(406),
        LOCKSCREEN_CLOSE_SECURE(407),
        LOCKSCREEN_CLOSE_INSECURE(408),
        BOUNCER_OPEN_SECURE(409),
        BOUNCER_OPEN_INSECURE(410),
        BOUNCER_CLOSE_SECURE(411),
        BOUNCER_CLOSE_INSECURE(412);
        
        private final int mId;

        private StatusBarUiEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    /* access modifiers changed from: private */
    public void updateQuickLaunchCameraSetting() {
        int intForUser = MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), MotorolaSettings.Secure.getUriFor("quick_capture_default_camera").getPathSegments().get(1), this.mQuickLaunchDefaultCameraValue, this.mLockscreenUserManager.getCurrentUserId());
        if (USERDEBUG) {
            Log.d("StatusBar", "updateQuickLaunchCameraSetting: value = " + intForUser);
        }
        if (intForUser == 0) {
            this.mQuickLaunchUseFrontCamera = true;
        } else {
            this.mQuickLaunchUseFrontCamera = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z) {
        this.mFolioClosed = z;
    }

    public void setUdfpsController(UdfpsController udfpsController) {
        this.mUdfpsController = udfpsController;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StatusBar(Context context, NotificationsController notificationsController, LightBarController lightBarController, AutoHideController autoHideController, KeyguardUpdateMonitor keyguardUpdateMonitor, StatusBarSignalPolicy statusBarSignalPolicy, PulseExpansionHandler pulseExpansionHandler, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, KeyguardStateController keyguardStateController, HeadsUpManagerPhone headsUpManagerPhone, DynamicPrivacyController dynamicPrivacyController, BypassHeadsUpNotifier bypassHeadsUpNotifier, FalsingManager falsingManager, FalsingCollector falsingCollector, BroadcastDispatcher broadcastDispatcher, RemoteInputQuickSettingsDisabler remoteInputQuickSettingsDisabler, NotificationGutsManager notificationGutsManager, NotificationLogger notificationLogger, NotificationInterruptStateProvider notificationInterruptStateProvider, NotificationViewHierarchyManager notificationViewHierarchyManager, KeyguardViewMediator keyguardViewMediator, DisplayMetrics displayMetrics, MetricsLogger metricsLogger, Executor executor, NotificationMediaManager notificationMediaManager, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationRemoteInputManager notificationRemoteInputManager, UserSwitcherController userSwitcherController, NetworkController networkController, BatteryController batteryController, SysuiColorExtractor sysuiColorExtractor, ScreenLifecycle screenLifecycle, WakefulnessLifecycle wakefulnessLifecycle, SysuiStatusBarStateController sysuiStatusBarStateController, VibratorHelper vibratorHelper, Optional<BubblesManager> optional, Optional<Bubbles> optional2, VisualStabilityManager visualStabilityManager, DeviceProvisionedController deviceProvisionedController, NavigationBarController navigationBarController, AccessibilityFloatingMenuController accessibilityFloatingMenuController, Lazy<AssistManager> lazy, ConfigurationController configurationController, NotificationShadeWindowController notificationShadeWindowController, DozeParameters dozeParameters, ScrimController scrimController, KeyguardLiftController keyguardLiftController, Lazy<LockscreenWallpaper> lazy2, Lazy<BiometricUnlockController> lazy3, DozeServiceHost dozeServiceHost, PowerManager powerManager, ScreenPinningRequest screenPinningRequest, DozeScrimController dozeScrimController, VolumeComponent volumeComponent, CommandQueue commandQueue, Provider<StatusBarComponent.Builder> provider, PluginManager pluginManager, Optional<LegacySplitScreen> optional3, LightsOutNotifController lightsOutNotifController, StatusBarNotificationActivityStarter.Builder builder, ShadeController shadeController, SuperStatusBarViewFactory superStatusBarViewFactory, StatusBarKeyguardViewManager statusBarKeyguardViewManager, ViewMediatorCallback viewMediatorCallback, InitController initController, Handler handler, PluginDependencyProvider pluginDependencyProvider, KeyguardDismissUtil keyguardDismissUtil, ExtensionController extensionController, UserInfoControllerImpl userInfoControllerImpl, PhoneStatusBarPolicy phoneStatusBarPolicy, KeyguardIndicationController keyguardIndicationController, DismissCallbackRegistry dismissCallbackRegistry, DemoModeController demoModeController, Lazy<NotificationShadeDepthController> lazy4, StatusBarTouchableRegionManager statusBarTouchableRegionManager, NotificationIconAreaController notificationIconAreaController, BrightnessSlider.Factory factory, WiredChargingRippleController wiredChargingRippleController, OngoingCallController ongoingCallController, SystemStatusAnimationScheduler systemStatusAnimationScheduler, StatusBarLocationPublisher statusBarLocationPublisher, StatusBarIconController statusBarIconController, LockscreenShadeTransitionController lockscreenShadeTransitionController, FeatureFlags featureFlags, KeyguardUnlockAnimationController keyguardUnlockAnimationController, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, Optional<StartingSurface> optional4) {
        super(context);
        LockscreenShadeTransitionController lockscreenShadeTransitionController2 = lockscreenShadeTransitionController;
        this.mNotificationsController = notificationsController;
        this.mLightBarController = lightBarController;
        this.mAutoHideController = autoHideController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mSignalPolicy = statusBarSignalPolicy;
        this.mPulseExpansionHandler = pulseExpansionHandler;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mKeyguardStateController = keyguardStateController;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mKeyguardIndicationController = keyguardIndicationController;
        this.mStatusBarTouchableRegionManager = statusBarTouchableRegionManager;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mBypassHeadsUpNotifier = bypassHeadsUpNotifier;
        this.mFalsingCollector = falsingCollector;
        this.mFalsingManager = falsingManager;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mRemoteInputQuickSettingsDisabler = remoteInputQuickSettingsDisabler;
        this.mGutsManager = notificationGutsManager;
        this.mNotificationLogger = notificationLogger;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mViewHierarchyManager = notificationViewHierarchyManager;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mDisplayMetrics = displayMetrics;
        this.mMetricsLogger = metricsLogger;
        this.mUiBgExecutor = executor;
        this.mMediaManager = notificationMediaManager;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mUserSwitcherController = userSwitcherController;
        this.mNetworkController = networkController;
        this.mBatteryController = batteryController;
        this.mColorExtractor = sysuiColorExtractor;
        this.mScreenLifecycle = screenLifecycle;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mVibratorHelper = vibratorHelper;
        this.mBubblesManagerOptional = optional;
        this.mBubblesOptional = optional2;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mNavigationBarController = navigationBarController;
        this.mAssistManagerLazy = lazy;
        this.mConfigurationController = configurationController;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mDozeServiceHost = dozeServiceHost;
        this.mPowerManager = powerManager;
        this.mDozeParameters = dozeParameters;
        this.mScrimController = scrimController;
        this.mKeyguardLiftController = keyguardLiftController;
        this.mLockscreenWallpaperLazy = lazy2;
        this.mScreenPinningRequest = screenPinningRequest;
        this.mDozeScrimController = dozeScrimController;
        this.mBiometricUnlockControllerLazy = lazy3;
        this.mNotificationShadeDepthControllerLazy = lazy4;
        this.mVolumeComponent = volumeComponent;
        this.mCommandQueue = commandQueue;
        this.mStatusBarComponentBuilder = provider;
        this.mPluginManager = pluginManager;
        this.mSplitScreenOptional = optional3;
        this.mStatusBarNotificationActivityStarterBuilder = builder;
        this.mShadeController = shadeController;
        this.mSuperStatusBarViewFactory = superStatusBarViewFactory;
        this.mLightsOutNotifController = lightsOutNotifController;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mKeyguardViewMediatorCallback = viewMediatorCallback;
        this.mInitController = initController;
        this.mPluginDependencyProvider = pluginDependencyProvider;
        this.mKeyguardDismissUtil = keyguardDismissUtil;
        this.mExtensionController = extensionController;
        this.mUserInfoControllerImpl = userInfoControllerImpl;
        this.mIconPolicy = phoneStatusBarPolicy;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mDemoModeController = demoModeController;
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mBrightnessSliderFactory = factory;
        this.mChargingRippleAnimationController = wiredChargingRippleController;
        this.mOngoingCallController = ongoingCallController;
        this.mAnimationScheduler = systemStatusAnimationScheduler;
        this.mStatusBarLocationPublisher = statusBarLocationPublisher;
        this.mStatusBarIconController = statusBarIconController;
        this.mFeatureFlags = featureFlags;
        this.mKeyguardUnlockAnimationController = keyguardUnlockAnimationController;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
        this.mLockscreenShadeTransitionController = lockscreenShadeTransitionController2;
        this.mStartingSurfaceOptional = optional4;
        if (lockscreenShadeTransitionController2 != null) {
            lockscreenShadeTransitionController2.setStatusbar(this);
        }
        this.mExpansionChangedListeners = new ArrayList();
        this.mBubbleExpandListener = new StatusBar$$ExternalSyntheticLambda10(this);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.mContext);
        if (handler != null) {
            DateTimeView.setReceiverHandler(handler);
        }
        if (this.mContext.getResources().getBoolean(R$bool.zz_moto_folio_product) && !DesktopFeature.isDesktopDisplayContext(this.mContext)) {
            FolioSensorManager.getInstance(this.mContext).addSensorChangeListener(this.mFolioStateCallback);
        }
        Context context2 = context;
        this.mScreenshotHelper = new ScreenshotHelper(context);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(boolean z, String str) {
        this.mContext.getMainExecutor().execute(new StatusBar$$ExternalSyntheticLambda23(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mNotificationsController.requestNotificationUpdate("onBubbleExpandChanged");
        updateScrimController();
    }

    public void start() {
        RegisterStatusBarResult registerStatusBarResult;
        CarrierLanguageApplier.Companion.updateLanguageIfNecessary(this.mContext);
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        this.mBypassHeadsUpNotifier.setUp();
        if (this.mBubblesOptional.isPresent()) {
            this.mBubblesOptional.get().setExpandListener(this.mBubbleExpandListener);
            this.mBroadcastDispatcher.registerReceiver(this.mTaskbarChangeReceiver, new IntentFilter("taskbarChanged"));
        }
        this.mKeyguardIndicationController.init();
        this.mColorExtractor.addOnColorsChangedListener(this);
        this.mStatusBarStateController.addCallback(this, 0);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
        Display defaultDisplay = this.mWindowManager.getDefaultDisplay();
        this.mDisplay = defaultDisplay;
        this.mDisplayId = defaultDisplay.getDisplayId();
        updateDisplaySize();
        this.mVibrateOnOpening = this.mContext.getResources().getBoolean(R$bool.config_vibrateOnIconAnimation);
        this.mCarrierLabelUpdateMonitor = CarrierLabelUpdateMonitor.getInstance();
        this.mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        this.mKeyguardUpdateMonitor.setKeyguardBypassController(this.mKeyguardBypassController);
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        this.mWallpaperSupported = ((WallpaperManager) this.mContext.getSystemService(WallpaperManager.class)).isWallpaperSupported();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mDemoModeController.addCallback((DemoMode) this);
        try {
            registerStatusBarResult = this.mBarService.registerStatusBar(this.mCommandQueue);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            registerStatusBarResult = null;
        }
        createAndAddWindows(registerStatusBarResult);
        if (this.mWallpaperSupported) {
            this.mBroadcastDispatcher.registerReceiver(this.mWallpaperChangedReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"), (Executor) null, UserHandle.ALL);
            this.mWallpaperChangedReceiver.onReceive(this.mContext, (Intent) null);
        }
        setUpPresenter();
        if (InsetsState.containsType(registerStatusBarResult.mTransientBarTypes, 0)) {
            showTransientUnchecked(this.mDisplayId);
        }
        onSystemBarAttributesChanged(this.mDisplayId, registerStatusBarResult.mAppearance, registerStatusBarResult.mAppearanceRegions, registerStatusBarResult.mNavbarColorManagedByIme, registerStatusBarResult.mBehavior, registerStatusBarResult.mAppFullscreen);
        setImeWindowStatus(this.mDisplayId, registerStatusBarResult.mImeToken, registerStatusBarResult.mImeWindowVis, registerStatusBarResult.mImeBackDisposition, registerStatusBarResult.mShowImeSwitcher);
        int size = registerStatusBarResult.mIcons.size();
        for (int i = 0; i < size; i++) {
            this.mCommandQueue.setIcon((String) registerStatusBarResult.mIcons.keyAt(i), (StatusBarIcon) registerStatusBarResult.mIcons.valueAt(i));
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.systemui.statusbar.banner_action_cancel");
        intentFilter.addAction("com.android.systemui.statusbar.banner_action_setup");
        this.mContext.registerReceiver(this.mBannerActionBroadcastReceiver, intentFilter, "com.android.systemui.permission.SELF", (Handler) null);
        if (this.mWallpaperSupported) {
            try {
                IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper")).setInAmbientMode(false, 0);
            } catch (RemoteException unused) {
            }
        }
        this.mIconPolicy.init();
        ImsIconController.getInstance(this.mContext).initialize();
        this.mKeyguardStateController.addCallback(this);
        startKeyguard();
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateCallback);
        this.mDozeServiceHost.initialize(this, this.mStatusBarKeyguardViewManager, this.mNotificationShadeWindowViewController, this.mNotificationPanelViewController, this.mAmbientIndicationContainer);
        this.mDozeParameters.addCallback(new StatusBar$$ExternalSyntheticLambda7(this));
        this.mConfigurationController.addCallback(this);
        this.mBatteryController.observe((Lifecycle) this.mLifecycle, this);
        this.mLifecycle.setCurrentState(Lifecycle.State.RESUMED);
        this.mMotoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
        this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mRROsController = (RROsController) Dependency.get(RROsController.class);
        this.mInitController.addPostInitTask(new StatusBar$$ExternalSyntheticLambda28(this, registerStatusBarResult.mDisabledFlags1, registerStatusBarResult.mDisabledFlags2));
        this.mFalsingManager.addFalsingBeliefListener(this.mFalsingBeliefListener);
        this.mPluginManager.addPluginListener(new PluginListener<OverlayPlugin>() {
            /* access modifiers changed from: private */
            public ArraySet<OverlayPlugin> mOverlays = new ArraySet<>();

            public void onPluginConnected(OverlayPlugin overlayPlugin, Context context) {
                StatusBar.this.mMainThreadHandler.post(new StatusBar$8$$ExternalSyntheticLambda0(this, overlayPlugin));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPluginConnected$0(OverlayPlugin overlayPlugin) {
                overlayPlugin.setup(StatusBar.this.getNotificationShadeWindowView(), StatusBar.this.getNavigationBarView(), new Callback(overlayPlugin), StatusBar.this.mDozeParameters);
            }

            public void onPluginDisconnected(OverlayPlugin overlayPlugin) {
                StatusBar.this.mMainThreadHandler.post(new StatusBar$8$$ExternalSyntheticLambda1(this, overlayPlugin));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPluginDisconnected$1(OverlayPlugin overlayPlugin) {
                this.mOverlays.remove(overlayPlugin);
                StatusBar.this.mNotificationShadeWindowController.setForcePluginOpen(this.mOverlays.size() != 0, this);
            }

            /* renamed from: com.android.systemui.statusbar.phone.StatusBar$8$Callback */
            class Callback implements OverlayPlugin.Callback {
                private final OverlayPlugin mPlugin;

                Callback(OverlayPlugin overlayPlugin) {
                    this.mPlugin = overlayPlugin;
                }

                public void onHoldStatusBarOpenChange() {
                    if (this.mPlugin.holdStatusBarOpen()) {
                        C19338.this.mOverlays.add(this.mPlugin);
                    } else {
                        C19338.this.mOverlays.remove(this.mPlugin);
                    }
                    StatusBar.this.mMainThreadHandler.post(new StatusBar$8$Callback$$ExternalSyntheticLambda1(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onHoldStatusBarOpenChange$2() {
                    StatusBar.this.mNotificationShadeWindowController.setStateListener(new StatusBar$8$Callback$$ExternalSyntheticLambda0(this));
                    C19338 r0 = C19338.this;
                    StatusBar.this.mNotificationShadeWindowController.setForcePluginOpen(r0.mOverlays.size() != 0, this);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onHoldStatusBarOpenChange$1(boolean z) {
                    C19338.this.mOverlays.forEach(new StatusBar$8$Callback$$ExternalSyntheticLambda2(z));
                }
            }
        }, (Class<?>) OverlayPlugin.class, true);
        this.mQuickLaunchDefaultCameraValue = this.mContext.getResources().getInteger(17694921);
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("quick_capture_default_camera"), false, this.mQuickLaunchCameraSettingObserver, this.mLockscreenUserManager.getCurrentUserId());
        updateQuickLaunchCameraSetting();
    }

    /* access modifiers changed from: protected */
    public void makeStatusBarView(RegisterStatusBarResult registerStatusBarResult) {
        Context context = this.mContext;
        updateDisplaySize();
        updateResources();
        updateTheme();
        inflateStatusBarWindow();
        this.mNotificationShadeWindowViewController.setService(this, this.mNotificationShadeWindowController);
        this.mNotificationShadeWindowView.setOnTouchListener(getStatusBarWindowTouchListener());
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.mNotificationPanelViewController.getNotificationStackScrollLayoutController();
        this.mStackScrollerController = notificationStackScrollLayoutController;
        this.mStackScroller = notificationStackScrollLayoutController.getView();
        this.mNotificationLogger.setUpWithContainer(this.mStackScrollerController.getNotificationListContainer());
        inflateShelf();
        this.mNotificationIconAreaController.setupShelf(this.mNotificationShelfController);
        this.mNotificationPanelViewController.addExpansionListener(this.mWakeUpCoordinator);
        this.mNotificationPanelViewController.addExpansionListener(new StatusBar$$ExternalSyntheticLambda9(this));
        this.mPluginDependencyProvider.allowPluginDependency(DarkIconDispatcher.class);
        this.mPluginDependencyProvider.allowPluginDependency(StatusBarStateController.class);
        FragmentTransaction beginTransaction = FragmentHostManager.get(this.mPhoneStatusBarWindow).addTagListener("CollapsedStatusBarFragment", new StatusBar$$ExternalSyntheticLambda4(this)).getFragmentManager().beginTransaction();
        int i = R$id.status_bar_container;
        Context context2 = context;
        CollapsedStatusBarFragment collapsedStatusBarFragment = r0;
        Class<C1129QS> cls = C1129QS.class;
        CollapsedStatusBarFragment collapsedStatusBarFragment2 = new CollapsedStatusBarFragment(this.mOngoingCallController, this.mAnimationScheduler, this.mStatusBarLocationPublisher, this.mNotificationIconAreaController, this.mFeatureFlags, this.mStatusBarIconController, this.mKeyguardStateController, this.mNetworkController, this.mStatusBarStateController, this, this.mCommandQueue);
        beginTransaction.replace(i, collapsedStatusBarFragment, "CollapsedStatusBarFragment").commit();
        this.mHeadsUpManager.setup(this.mVisualStabilityManager);
        this.mStatusBarTouchableRegionManager.setup(this, this.mNotificationShadeWindowView);
        this.mHeadsUpManager.addListener(this);
        this.mHeadsUpManager.addListener(this.mNotificationPanelViewController.getOnHeadsUpChangedListener());
        this.mHeadsUpManager.addListener(this.mVisualStabilityManager);
        this.mNotificationPanelViewController.setHeadsUpManager(this.mHeadsUpManager);
        createNavigationBar(registerStatusBarResult);
        if (this.mWallpaperSupported) {
            this.mLockscreenWallpaper = this.mLockscreenWallpaperLazy.get();
        }
        this.mNotificationPanelViewController.setKeyguardIndicationController(this.mKeyguardIndicationController);
        this.mAmbientIndicationContainer = this.mNotificationShadeWindowView.findViewById(R$id.ambient_indication_container);
        this.mAutoHideController.setStatusBar(new AutoHideUiElement() {
            public void synchronizeState() {
                StatusBar.this.checkBarModes();
            }

            public boolean shouldHideOnTouch() {
                return !StatusBar.this.mRemoteInputManager.getController().isRemoteInputActive();
            }

            public boolean isVisible() {
                return StatusBar.this.isTransientShown();
            }

            public void hide() {
                StatusBar statusBar = StatusBar.this;
                statusBar.clearTransient(statusBar.mDisplayId);
            }
        });
        ScrimView scrimView = (ScrimView) this.mNotificationShadeWindowView.findViewById(R$id.scrim_behind);
        ScrimView scrimView2 = (ScrimView) this.mNotificationShadeWindowView.findViewById(R$id.scrim_notifications);
        ScrimView scrimView3 = (ScrimView) this.mNotificationShadeWindowView.findViewById(R$id.scrim_in_front);
        ScrimView scrimForBubble = this.mBubblesManagerOptional.isPresent() ? this.mBubblesManagerOptional.get().getScrimForBubble() : null;
        this.mScrimController.setScrimVisibleListener(new StatusBar$$ExternalSyntheticLambda42(this));
        this.mScrimController.attachViews(scrimView, scrimView2, scrimView3, scrimForBubble);
        LightRevealScrim lightRevealScrim = (LightRevealScrim) this.mNotificationShadeWindowView.findViewById(R$id.light_reveal_scrim);
        this.mLightRevealScrim = lightRevealScrim;
        NotificationShadeWindowController notificationShadeWindowController = this.mNotificationShadeWindowController;
        Objects.requireNonNull(notificationShadeWindowController);
        lightRevealScrim.setRevealAmountListener(new StatusBar$$ExternalSyntheticLambda39(notificationShadeWindowController));
        this.mUnlockedScreenOffAnimationController.initialize(this, this.mLightRevealScrim);
        updateLightRevealScrimVisibility();
        this.mNotificationPanelViewController.initDependencies(this, this.mNotificationShelfController);
        BackDropView backDropView = (BackDropView) this.mNotificationShadeWindowView.findViewById(R$id.backdrop);
        this.mMediaManager.setup(backDropView, (ImageView) backDropView.findViewById(R$id.backdrop_front), (ImageView) backDropView.findViewById(R$id.backdrop_back), this.mScrimController, this.mLockscreenWallpaper);
        this.mNotificationShadeDepthControllerLazy.get().addListener(new StatusBar$$ExternalSyntheticLambda6(this.mContext.getResources().getFloat(17105124), backDropView));
        this.mNotificationPanelViewController.setUserSetupComplete(this.mUserSetup);
        NotificationShadeWindowView notificationShadeWindowView = this.mNotificationShadeWindowView;
        int i2 = R$id.qs_frame;
        View findViewById = notificationShadeWindowView.findViewById(i2);
        if (findViewById != null) {
            FragmentHostManager fragmentHostManager = FragmentHostManager.get(findViewById);
            Class<C1129QS> cls2 = cls;
            ExtensionFragmentListener.attachExtensonToFragment(findViewById, C1129QS.TAG, i2, this.mExtensionController.newExtension(cls2).withPlugin(cls2).withDefault(new StatusBar$$ExternalSyntheticLambda46(this)).build());
            this.mBrightnessMirrorController = new BrightnessMirrorController(this.mNotificationShadeWindowView, this.mNotificationPanelViewController, this.mNotificationShadeDepthControllerLazy.get(), this.mBrightnessSliderFactory, new StatusBar$$ExternalSyntheticLambda41(this));
            fragmentHostManager.addTagListener(C1129QS.TAG, new StatusBar$$ExternalSyntheticLambda3(this));
        }
        View findViewById2 = this.mNotificationShadeWindowView.findViewById(R$id.report_rejected_touch);
        this.mReportRejectedTouch = findViewById2;
        if (findViewById2 != null) {
            updateReportRejectedTouchVisibility();
            this.mReportRejectedTouch.setOnClickListener(new StatusBar$$ExternalSyntheticLambda0(this));
        }
        if (!this.mPowerManager.isScreenOn()) {
            this.mBroadcastReceiver.onReceive(this.mContext, new Intent("android.intent.action.SCREEN_OFF"));
        }
        this.mGestureWakeLock = this.mPowerManager.newWakeLock(10, "GestureWakeLock");
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Vibrator.class);
        this.mVibrator = vibrator;
        this.mCameraLaunchGestureVibrationEffect = getCameraGestureVibrationEffect(vibrator, context2.getResources());
        registerBroadcastReceiver();
        if (MotoFeature.getInstance(this.mContext).supportSideFps()) {
            this.mSideFpsController = new SideFpsController(this.mContext, this.mNotificationPanelViewController);
        } else if (MotoFeature.getInstance(this.mContext).supportEdgeTouch()) {
            this.mEdgeTouchPillController = new EdgeTouchPillController(this.mContext, this.mNotificationPanelViewController);
        }
        context2.registerReceiverAsUser(this.mDemoReceiver, UserHandle.ALL, new IntentFilter(), "android.permission.DUMP", (Handler) null);
        this.mDeviceProvisionedController.addCallback(this.mUserSetupObserver);
        this.mUserSetupObserver.onUserSetupChanged();
        ThreadedRenderer.overrideProperty("disableProfileBars", "true");
        ThreadedRenderer.overrideProperty("ambientRatio", String.valueOf(1.5f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeStatusBarView$4(String str, Fragment fragment) {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        PhoneStatusBarView phoneStatusBarView2 = (PhoneStatusBarView) ((CollapsedStatusBarFragment) fragment).getView();
        this.mStatusBarView = phoneStatusBarView2;
        phoneStatusBarView2.setBar(this);
        this.mStatusBarView.setPanel(this.mNotificationPanelViewController);
        this.mStatusBarView.setScrimController(this.mScrimController);
        this.mStatusBarView.setExpansionChangedListeners(this.mExpansionChangedListeners);
        if (this.mHeadsUpManager.hasPinnedHeadsUp()) {
            this.mNotificationPanelViewController.notifyBarPanelExpansionChanged();
        }
        this.mStatusBarView.setBouncerShowing(this.mBouncerShowing);
        if (phoneStatusBarView != null) {
            this.mStatusBarView.panelExpansionChanged(phoneStatusBarView.getExpansionFraction(), phoneStatusBarView.isExpanded());
        }
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (headsUpAppearanceController != null) {
            headsUpAppearanceController.destroy();
        }
        HeadsUpAppearanceController headsUpAppearanceController2 = new HeadsUpAppearanceController(this.mNotificationIconAreaController, this.mHeadsUpManager, this.mStackScroller.getController(), this.mStatusBarStateController, this.mKeyguardBypassController, this.mKeyguardStateController, this.mWakeUpCoordinator, this.mCommandQueue, this.mNotificationPanelViewController, this.mStatusBarView, this.mCarrierLabelUpdateMonitor);
        this.mHeadsUpAppearanceController = headsUpAppearanceController2;
        headsUpAppearanceController2.readFrom(headsUpAppearanceController);
        this.mLightsOutNotifController.setLightsOutNotifView(this.mStatusBarView.findViewById(R$id.notification_lights_out));
        this.mNotificationShadeWindowViewController.setStatusBarView(this.mStatusBarView);
        checkBarModes();
        this.mCarrierLabelUpdateMonitor.setStatusBar(this, this.mPhoneStatusBarWindow, this.mNotificationShadeWindowView, this.mStatusBarKeyguardViewManager, this.mNotificationPanelViewController, this.mNetworkController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeStatusBarView$5(Integer num) {
        this.mNotificationShadeWindowController.setScrimsVisibility(num.intValue());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeStatusBarView$6(float f, BackDropView backDropView, float f2) {
        float lerp = MathUtils.lerp(f, 1.0f, f2);
        backDropView.setPivotX(((float) backDropView.getWidth()) / 2.0f);
        backDropView.setPivotY(((float) backDropView.getHeight()) / 2.0f);
        backDropView.setScaleX(lerp);
        backDropView.setScaleY(lerp);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeStatusBarView$7(Boolean bool) {
        this.mBrightnessMirrorVisible = bool.booleanValue();
        updateScrimController();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeStatusBarView$8(String str, Fragment fragment) {
        C1129QS qs = (C1129QS) fragment;
        if (qs instanceof QSFragment) {
            QSPanelController qSPanelController = ((QSFragment) qs).getQSPanelController();
            this.mQSPanelController = qSPanelController;
            qSPanelController.setBrightnessMirror(this.mBrightnessMirrorController);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeStatusBarView$9(View view) {
        Uri reportRejectedTouch = this.mFalsingManager.reportRejectedTouch();
        if (reportRejectedTouch != null) {
            StringWriter stringWriter = new StringWriter();
            stringWriter.write("Build info: ");
            stringWriter.write(SystemProperties.get("ro.build.description"));
            stringWriter.write("\nSerial number: ");
            stringWriter.write(SystemProperties.get("ro.serialno"));
            stringWriter.write("\n");
            startActivityDismissingKeyguard(Intent.createChooser(new Intent("android.intent.action.SEND").setType("*/*").putExtra("android.intent.extra.SUBJECT", "Rejected touch report").putExtra("android.intent.extra.STREAM", reportRejectedTouch).putExtra("android.intent.extra.TEXT", stringWriter.toString()), "Share rejected touch report").addFlags(268435456), true, true);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchPanelExpansionForKeyguardDismiss(float f, boolean z) {
        if (isKeyguardShowing() && this.mKeyguardStateController.canDismissLockScreen()) {
            if (this.mNotificationPanelViewController.isQsExpanded() && z) {
                return;
            }
            if (z || this.mKeyguardViewMediator.isAnimatingBetweenKeyguardAndSurfaceBehindOrWillBe() || this.mKeyguardUnlockAnimationController.isUnlockingWithSmartSpaceTransition()) {
                this.mKeyguardStateController.notifyKeyguardDismissAmountChanged(1.0f - f, z);
            }
        }
    }

    public AutoHideUiElement getStatusBarUIElement() {
        return this.mAutoHideUIElement;
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public void onPowerSaveChanged(boolean z) {
        this.mHandler.post(this.mCheckBarModes);
        DozeServiceHost dozeServiceHost = this.mDozeServiceHost;
        if (dozeServiceHost != null) {
            dozeServiceHost.firePowerSaveChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.app.action.SHOW_DEVICE_MONITORING_DIALOG");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter, (Executor) null, UserHandle.ALL);
    }

    /* access modifiers changed from: protected */
    public C1129QS createDefaultQSFragment() {
        return (C1129QS) FragmentHostManager.get(this.mNotificationShadeWindowView).create(QSFragment.class);
    }

    private void setUpPresenter() {
        this.mActivityLaunchAnimator = new ActivityLaunchAnimator(this, this.mContext);
        this.mNotificationAnimationProvider = new NotificationLaunchAnimatorControllerProvider(this.mNotificationShadeWindowViewController, this.mStackScrollerController.getNotificationListContainer(), this.mHeadsUpManager);
        Context context = this.mContext;
        StatusBarNotificationPresenter statusBarNotificationPresenter = r0;
        Context context2 = context;
        Context context3 = context2;
        StatusBarNotificationPresenter statusBarNotificationPresenter2 = new StatusBarNotificationPresenter(context3, this.mNotificationPanelViewController, this.mHeadsUpManager, this.mNotificationShadeWindowView, this.mStackScrollerController, this.mDozeScrimController, this.mScrimController, this.mNotificationShadeWindowController, this.mDynamicPrivacyController, this.mKeyguardStateController, this.mKeyguardIndicationController, this, this.mShadeController, this.mLockscreenShadeTransitionController, this.mCommandQueue, this.mInitController, this.mNotificationInterruptStateProvider);
        StatusBarNotificationPresenter statusBarNotificationPresenter3 = statusBarNotificationPresenter;
        this.mPresenter = statusBarNotificationPresenter3;
        this.mNotificationShelfController.setOnActivatedListener(statusBarNotificationPresenter3);
        this.mRemoteInputManager.getController().addCallback(this.mNotificationShadeWindowController);
        StatusBarNotificationActivityStarter build = this.mStatusBarNotificationActivityStarterBuilder.setStatusBar(this).setActivityLaunchAnimator(this.mActivityLaunchAnimator).setNotificationAnimatorControllerProvider(this.mNotificationAnimationProvider).setNotificationPresenter(this.mPresenter).setNotificationPanelViewController(this.mNotificationPanelViewController).build();
        this.mNotificationActivityStarter = build;
        this.mStackScroller.setNotificationActivityStarter(build);
        this.mGutsManager.setNotificationActivityStarter(this.mNotificationActivityStarter);
        this.mNotificationsController.initialize(this, this.mBubblesOptional, this.mPresenter, this.mStackScrollerController.getNotificationListContainer(), this.mNotificationActivityStarter, this.mPresenter);
    }

    /* access modifiers changed from: protected */
    /* renamed from: setUpDisableFlags */
    public void lambda$start$3(int i, int i2) {
        try {
            int[] totalDisableFlags = this.mBarService.getTotalDisableFlags(this.mDisplayId);
            Log.d("StatusBar", "setUpDisableFlags: disableFlags = [0x" + Integer.toHexString(totalDisableFlags[0]) + ", 0x" + Integer.toHexString(totalDisableFlags[1]) + "]");
            this.mCommandQueue.disable(this.mDisplayId, totalDisableFlags[0], totalDisableFlags[1]);
        } catch (RemoteException e) {
            Log.d("StatusBar", "setUpDisableFlags: load disable flags error", e);
        }
    }

    public void wakeUpIfDozing(long j, View view, String str) {
        if (this.mDozing && !this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying()) {
            PowerManager powerManager = this.mPowerManager;
            powerManager.wakeUp(j, 4, "com.android.systemui:" + str);
            this.mWakeUpComingFromTouch = true;
            view.getLocationInWindow(this.mTmpInt2);
            this.mWakeUpTouchLocation = new PointF((float) (this.mTmpInt2[0] + (view.getWidth() / 2)), (float) (this.mTmpInt2[1] + (view.getHeight() / 2)));
            this.mFalsingCollector.onScreenOnFromTouch();
        }
    }

    /* access modifiers changed from: protected */
    public void createNavigationBar(RegisterStatusBarResult registerStatusBarResult) {
        ((MotoTaskBarController) Dependency.get(MotoTaskBarController.class)).init();
        this.mNavigationBarController.createNavigationBars(true, registerStatusBarResult);
    }

    /* access modifiers changed from: protected */
    public View.OnTouchListener getStatusBarWindowTouchListener() {
        return new StatusBar$$ExternalSyntheticLambda1(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getStatusBarWindowTouchListener$10(View view, MotionEvent motionEvent) {
        this.mAutoHideController.checkUserAutoHide(motionEvent);
        this.mRemoteInputManager.checkRemoteInputOutside(motionEvent);
        if (motionEvent.getAction() == 0 && this.mExpandedVisible) {
            this.mShadeController.animateCollapsePanels();
        }
        return this.mNotificationShadeWindowView.onTouchEvent(motionEvent);
    }

    private void inflateShelf() {
        this.mNotificationShelfController = this.mSuperStatusBarViewFactory.getNotificationShelfController(this.mStackScroller);
    }

    public void onDensityOrFontScaleChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onDensityOrFontScaleChanged();
        }
        this.mUserInfoControllerImpl.onDensityOrFontScaleChanged();
        this.mUserSwitcherController.onDensityOrFontScaleChanged();
        this.mNotificationIconAreaController.onDensityOrFontScaleChanged(this.mContext);
        this.mHeadsUpManager.onDensityOrFontScaleChanged();
    }

    public void onThemeChanged() {
        onThemeChangedInternal(false);
    }

    private void onThemeChangedInternal(boolean z) {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null && !z) {
            statusBarKeyguardViewManager.onThemeChanged();
        }
        View view = this.mAmbientIndicationContainer;
        if (view instanceof AutoReinflateContainer) {
            ((AutoReinflateContainer) view).inflateLayout();
        }
        this.mNotificationIconAreaController.onThemeChanged();
    }

    public void onOverlayChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onOverlayChanged();
        }
        this.mNotificationPanelViewController.onThemeChanged();
        onThemeChangedInternal(true);
    }

    public void onUiModeChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onUiModeChanged();
        }
    }

    private void inflateStatusBarWindow() {
        this.mNotificationShadeWindowView = this.mSuperStatusBarViewFactory.getNotificationShadeWindowView();
        StatusBarComponent build = this.mStatusBarComponentBuilder.get().statusBarWindowView(this.mNotificationShadeWindowView).build();
        this.mNotificationShadeWindowViewController = build.getNotificationShadeWindowViewController();
        this.mNotificationShadeWindowController.setNotificationShadeView(this.mNotificationShadeWindowView);
        this.mNotificationShadeWindowViewController.setupExpandedStatusBar();
        this.mStatusBarWindowController = build.getStatusBarWindowController();
        this.mPhoneStatusBarWindow = this.mSuperStatusBarViewFactory.getStatusBarWindowView();
        this.mNotificationPanelViewController = build.getNotificationPanelViewController();
        build.getLockIconViewController().init();
        AuthRippleController authRippleController = build.getAuthRippleController();
        this.mAuthRippleController = authRippleController;
        authRippleController.init();
    }

    /* access modifiers changed from: protected */
    public void startKeyguard() {
        Trace.beginSection("StatusBar#startKeyguard");
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockControllerLazy.get();
        this.mBiometricUnlockController = biometricUnlockController;
        biometricUnlockController.setBiometricModeListener(new BiometricUnlockController.BiometricModeListener() {
            public void onResetMode() {
                setWakeAndUnlocking(false);
            }

            public void onModeChanged(int i) {
                if (i == 1 || i == 2 || i == 6) {
                    setWakeAndUnlocking(true);
                }
            }

            public void notifyBiometricAuthModeChanged() {
                StatusBar.this.notifyBiometricAuthModeChanged();
            }

            private void setWakeAndUnlocking(boolean z) {
                if (StatusBar.this.getNavigationBarView() != null) {
                    StatusBar.this.getNavigationBarView().setWakeAndUnlocking(z);
                }
            }
        });
        this.mBiometricUnlockController.setBiometricUnlockWithoutAnimListener(new BiometricUnlockController.BiometricUnlockWithoutAnimListener() {
            public void resetPanelViewForBiometric() {
                StatusBar.this.mNotificationPanelViewController.resetPanelViewForBiometric();
            }

            public void expandedInvisible() {
                StatusBar.this.makeExpandedInvisible();
            }
        });
        this.mStatusBarKeyguardViewManager.registerStatusBar(this, getBouncerContainer(), this.mNotificationPanelViewController, this.mBiometricUnlockController, this.mStackScroller, this.mKeyguardBypassController);
        this.mKeyguardIndicationController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        if (DesktopFeature.isDesktopSupported()) {
            this.mRDPUnlockController = new RDPUnlockController(this.mContext, this, this.mKeyguardViewMediator, this.mDozeScrimController, this.mKeyguardUpdateMonitor, this.mKeyguardStateController, this.mNotificationShadeWindowController, this.mShadeController, this.mStatusBarKeyguardViewManager);
        }
        this.mBiometricUnlockController.setKeyguardViewController(this.mStatusBarKeyguardViewManager);
        this.mRemoteInputManager.getController().addCallback(this.mStatusBarKeyguardViewManager);
        this.mDynamicPrivacyController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mLightBarController.setBiometricUnlockController(this.mBiometricUnlockController);
        this.mMediaManager.setBiometricUnlockController(this.mBiometricUnlockController);
        this.mKeyguardDismissUtil.setDismissHandler(new StatusBar$$ExternalSyntheticLambda8(this));
        Trace.endSection();
    }

    /* access modifiers changed from: protected */
    public View getStatusBarView() {
        return this.mStatusBarView;
    }

    public NotificationShadeWindowView getNotificationShadeWindowView() {
        return this.mNotificationShadeWindowView;
    }

    public NotificationShadeWindowViewController getNotificationShadeWindowViewController() {
        return this.mNotificationShadeWindowViewController;
    }

    public NotificationPanelViewController getNotificationPanelViewController() {
        return this.mNotificationPanelViewController;
    }

    /* access modifiers changed from: protected */
    public ViewGroup getBouncerContainer() {
        return this.mNotificationShadeWindowView;
    }

    public int getStatusBarHeight() {
        return this.mStatusBarWindowController.getStatusBarHeight();
    }

    public boolean toggleSplitScreenMode(int i, int i2) {
        if (this.mBubblesOptional.isPresent() && this.mBubblesOptional.get().isStackExpanded()) {
            this.mMainThreadHandler.post(new StatusBar$$ExternalSyntheticLambda24(this));
        }
        if (!this.mSplitScreenOptional.isPresent()) {
            return false;
        }
        LegacySplitScreen legacySplitScreen = this.mSplitScreenOptional.get();
        if (legacySplitScreen.isDividerVisible()) {
            if (legacySplitScreen.isMinimized() && !legacySplitScreen.isHomeStackResizable()) {
                return false;
            }
            legacySplitScreen.onUndockingTask();
            if (i2 != -1) {
                this.mMetricsLogger.action(i2);
            }
            return true;
        } else if (!legacySplitScreen.splitPrimaryTask()) {
            return false;
        } else {
            if (i != -1) {
                this.mMetricsLogger.action(i);
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleSplitScreenMode$11() {
        this.mBubblesOptional.get().collapseStack();
    }

    /* access modifiers changed from: private */
    public void updateQsExpansionEnabled() {
        UserSwitcherController userSwitcherController;
        boolean z = true;
        if (!this.mDeviceProvisionedController.isDeviceProvisioned() || ((!this.mUserSetup && (userSwitcherController = this.mUserSwitcherController) != null && userSwitcherController.isSimpleUserSwitcher()) || isShadeDisabled() || (this.mDisabled2 & 1) != 0 || this.mDozing || ONLY_CORE_APPS)) {
            z = false;
        }
        this.mNotificationPanelViewController.setQsExpansionEnabledPolicy(z);
        Log.d("StatusBar", "updateQsExpansionEnabled - QS Expand enabled: " + z);
    }

    public boolean isShadeDisabled() {
        return (this.mDisabled2 & 4) != 0;
    }

    public void addQsTile(ComponentName componentName) {
        QSPanelController qSPanelController = this.mQSPanelController;
        if (qSPanelController != null && qSPanelController.getHost() != null) {
            this.mQSPanelController.getHost().addTile(componentName);
        }
    }

    public void remQsTile(ComponentName componentName) {
        QSPanelController qSPanelController = this.mQSPanelController;
        if (qSPanelController != null && qSPanelController.getHost() != null) {
            this.mQSPanelController.getHost().removeTile(componentName);
        }
    }

    public void clickTile(ComponentName componentName) {
        this.mQSPanelController.clickTile(componentName);
    }

    public void requestNotificationUpdate(String str) {
        this.mNotificationsController.requestNotificationUpdate(str);
    }

    public void requestFaceAuth(boolean z) {
        if (!this.mKeyguardStateController.canDismissLockScreen()) {
            this.mKeyguardUpdateMonitor.requestFaceAuth(z);
        }
    }

    private void updateReportRejectedTouchVisibility() {
        View view = this.mReportRejectedTouch;
        if (view != null) {
            view.setVisibility((this.mState != 1 || this.mDozing || !this.mFalsingCollector.isReportingEnabled()) ? 4 : 0);
        }
    }

    public void disable(int i, int i2, int i3, boolean z) {
        int i4 = i2;
        if (i == this.mDisplayId) {
            int adjustDisableFlags = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(i3);
            int i5 = this.mDisabled1 ^ i4;
            this.mDisabled1 = i4;
            int i6 = this.mDisabled2 ^ adjustDisableFlags;
            this.mDisabled2 = adjustDisableFlags;
            StringBuilder sb = new StringBuilder();
            sb.append("disable<");
            int i7 = i4 & 65536;
            sb.append(i7 != 0 ? 'E' : 'e');
            int i8 = 65536 & i5;
            sb.append(i8 != 0 ? '!' : ' ');
            char c = 'I';
            sb.append((i4 & 131072) != 0 ? 'I' : 'i');
            sb.append((131072 & i5) != 0 ? '!' : ' ');
            sb.append((i4 & 262144) != 0 ? 'A' : 'a');
            int i9 = 262144 & i5;
            sb.append(i9 != 0 ? '!' : ' ');
            char c2 = 'S';
            sb.append((i4 & 1048576) != 0 ? 'S' : 's');
            sb.append((1048576 & i5) != 0 ? '!' : ' ');
            sb.append((i4 & 4194304) != 0 ? 'B' : 'b');
            sb.append((4194304 & i5) != 0 ? '!' : ' ');
            sb.append((i4 & 2097152) != 0 ? 'H' : 'h');
            sb.append((2097152 & i5) != 0 ? '!' : ' ');
            int i10 = i4 & 16777216;
            sb.append(i10 != 0 ? 'R' : 'r');
            int i11 = i5 & 16777216;
            sb.append(i11 != 0 ? '!' : ' ');
            sb.append((i4 & 8388608) != 0 ? 'C' : 'c');
            sb.append((i5 & 8388608) != 0 ? '!' : ' ');
            if ((i4 & 33554432) == 0) {
                c2 = 's';
            }
            sb.append(c2);
            sb.append((i5 & 33554432) != 0 ? '!' : ' ');
            sb.append("> disable2<");
            sb.append((adjustDisableFlags & 1) != 0 ? 'Q' : 'q');
            int i12 = i6 & 1;
            sb.append(i12 != 0 ? '!' : ' ');
            if ((adjustDisableFlags & 2) == 0) {
                c = 'i';
            }
            sb.append(c);
            sb.append((i6 & 2) != 0 ? '!' : ' ');
            int i13 = adjustDisableFlags & 4;
            sb.append(i13 != 0 ? 'N' : 'n');
            int i14 = i6 & 4;
            sb.append(i14 != 0 ? '!' : ' ');
            sb.append('>');
            Log.d("StatusBar", sb.toString());
            if (!(i8 == 0 || i7 == 0)) {
                this.mShadeController.animateCollapsePanels();
            }
            if (!(i11 == 0 || i10 == 0)) {
                this.mHandler.removeMessages(1020);
                this.mHandler.sendEmptyMessage(1020);
            }
            if (i9 != 0 && areNotificationAlertsDisabled()) {
                this.mHeadsUpManager.releaseAllImmediately();
            }
            if (i12 != 0) {
                updateQsExpansionEnabled();
            }
            if (i14 != 0) {
                updateQsExpansionEnabled();
                if (i13 != 0) {
                    this.mShadeController.animateCollapsePanels();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean areNotificationAlertsDisabled() {
        return (this.mDisabled1 & 262144) != 0;
    }

    /* access modifiers changed from: protected */
    public C1935H createHandler() {
        return new C1935H();
    }

    public void startActivity(Intent intent, boolean z, boolean z2, int i) {
        startActivityDismissingKeyguard(intent, z, z2, i);
    }

    public void startActivity(Intent intent, boolean z) {
        startActivityDismissingKeyguard(intent, false, z);
    }

    public void startActivity(Intent intent, boolean z, ActivityLaunchAnimator.Controller controller) {
        startActivityDismissingKeyguard(intent, false, z, false, (ActivityStarter.Callback) null, 0, controller);
    }

    public void startActivity(Intent intent, boolean z, boolean z2) {
        startActivityDismissingKeyguard(intent, z, z2);
    }

    public void startActivity(Intent intent, boolean z, ActivityStarter.Callback callback) {
        startActivityDismissingKeyguard(intent, false, z, false, callback, 0, (ActivityLaunchAnimator.Controller) null);
    }

    public void setQsExpanded(boolean z) {
        this.mNotificationShadeWindowController.setQsExpanded(z);
        this.mNotificationPanelViewController.setStatusAccessibilityImportance(z ? 4 : 0);
        if (getNavigationBarView() != null) {
            getNavigationBarView().onStatusBarPanelStateChanged();
        }
    }

    public boolean isWakeUpComingFromTouch() {
        return this.mWakeUpComingFromTouch;
    }

    public void onKeyguardViewManagerStatesUpdated() {
        logStateToEventlog();
    }

    public void onGlobalActionsStatesUpdated(boolean z) {
        EdgeTouchPillController edgeTouchPillController = this.mEdgeTouchPillController;
        if (edgeTouchPillController != null) {
            edgeTouchPillController.onGlobalActionsStateChanged(z);
        }
    }

    public void onUnlockedChanged() {
        updateKeyguardState();
        logStateToEventlog();
    }

    public void onHeadsUpPinnedModeChanged(boolean z) {
        if (z) {
            this.mNotificationShadeWindowController.setHeadsUpShowing(true);
            this.mStatusBarWindowController.setForceStatusBarVisible(true);
            if (this.mNotificationPanelViewController.isFullyCollapsed()) {
                this.mNotificationPanelViewController.getView().requestLayout();
                this.mNotificationShadeWindowController.setForceWindowCollapsed(true);
                this.mNotificationPanelViewController.getView().post(new StatusBar$$ExternalSyntheticLambda22(this));
                return;
            }
            return;
        }
        boolean z2 = this.mKeyguardBypassController.getBypassEnabled() && this.mState == 1;
        if (!this.mNotificationPanelViewController.isFullyCollapsed() || this.mNotificationPanelViewController.isTracking() || z2) {
            this.mNotificationShadeWindowController.setHeadsUpShowing(false);
            if (z2) {
                this.mStatusBarWindowController.setForceStatusBarVisible(false);
                return;
            }
            return;
        }
        this.mHeadsUpManager.setHeadsUpGoingAway(true);
        this.mNotificationPanelViewController.runAfterAnimationFinished(new StatusBar$$ExternalSyntheticLambda19(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onHeadsUpPinnedModeChanged$12() {
        this.mNotificationShadeWindowController.setForceWindowCollapsed(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onHeadsUpPinnedModeChanged$13() {
        if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
            this.mNotificationShadeWindowController.setHeadsUpShowing(false);
            this.mHeadsUpManager.setHeadsUpGoingAway(false);
        }
        this.mRemoteInputManager.onPanelCollapsed();
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        this.mNotificationsController.requestNotificationUpdate("onHeadsUpStateChanged");
        if (this.mStatusBarStateController.isDozing() && z) {
            notificationEntry.setPulseSuppressed(false);
            this.mDozeServiceHost.fireNotificationPulse(notificationEntry);
            if (this.mDozeServiceHost.isPulsing()) {
                this.mDozeScrimController.cancelPendingPulseTimeout();
            }
        }
        if (!z && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }

    public void setPanelExpanded(boolean z) {
        if (this.mPanelExpanded != z) {
            this.mNotificationLogger.onPanelExpandedChanged(z);
        }
        this.mPanelExpanded = z;
        updateHideIconsForBouncer(false);
        this.mNotificationShadeWindowController.setPanelExpanded(z);
        this.mStatusBarStateController.setPanelExpanded(z);
        if (z && this.mStatusBarStateController.getState() != 1) {
            clearNotificationEffects();
        }
        if (!z) {
            this.mRemoteInputManager.onPanelCollapsed();
        }
    }

    public boolean isPulsing() {
        return this.mDozeServiceHost.isPulsing();
    }

    public boolean hideStatusBarIconsWhenExpanded() {
        return this.mNotificationPanelViewController.hideStatusBarIconsWhenExpanded();
    }

    public void onColorsChanged(ColorExtractor colorExtractor, int i) {
        updateTheme();
    }

    public View getAmbientIndicationContainer() {
        return this.mAmbientIndicationContainer;
    }

    public boolean isOccluded() {
        return this.mIsOccluded;
    }

    public void setOccluded(boolean z) {
        this.mIsOccluded = z;
        this.mScrimController.setKeyguardOccluded(z);
        updateHideIconsForBouncer(false);
    }

    public boolean hideStatusBarIconsForBouncer() {
        return this.mHideIconsForBouncer || this.mWereIconsJustHidden;
    }

    private void updateHideIconsForBouncer(boolean z) {
        boolean z2 = false;
        boolean z3 = this.mTopHidesStatusBar && this.mIsOccluded && (this.mStatusBarWindowHidden || this.mBouncerShowing);
        boolean z4 = !this.mPanelExpanded && !this.mIsOccluded && this.mBouncerShowing;
        if (z3 || z4) {
            z2 = true;
        }
        if (this.mHideIconsForBouncer != z2) {
            this.mHideIconsForBouncer = z2;
            if (z2 || !this.mBouncerWasShowingWhenHidden) {
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, z);
            } else {
                this.mWereIconsJustHidden = true;
                this.mHandler.postDelayed(new StatusBar$$ExternalSyntheticLambda25(this), 500);
            }
        }
        if (z2) {
            this.mBouncerWasShowingWhenHidden = this.mBouncerShowing;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateHideIconsForBouncer$14() {
        this.mWereIconsJustHidden = false;
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
    }

    public boolean headsUpShouldBeVisible() {
        return this.mHeadsUpAppearanceController.shouldBeVisible();
    }

    public void onLaunchAnimationCancelled(boolean z) {
        if (!this.mPresenter.isPresenterFullyCollapsed() || this.mPresenter.isCollapsing() || !z) {
            this.mShadeController.collapsePanel(true);
        } else {
            onClosingFinished();
        }
    }

    public void onLaunchAnimationEnd(boolean z) {
        if (!this.mPresenter.isCollapsing()) {
            onClosingFinished();
        }
        if (z) {
            instantCollapseNotificationPanel();
        }
    }

    public boolean shouldAnimateLaunch(boolean z) {
        if (isOccluded()) {
            return false;
        }
        if (!this.mKeyguardStateController.isShowing()) {
            return true;
        }
        if (!z || !KeyguardService.sEnableRemoteKeyguardGoingAwayAnimation) {
            return false;
        }
        return true;
    }

    public boolean isOnKeyguard() {
        return this.mKeyguardStateController.isShowing();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideKeyguardWithAnimation$15(IRemoteAnimationRunner iRemoteAnimationRunner) {
        this.mKeyguardViewMediator.hideWithAnimation(iRemoteAnimationRunner);
    }

    public void hideKeyguardWithAnimation(IRemoteAnimationRunner iRemoteAnimationRunner) {
        this.mMainThreadHandler.post(new StatusBar$$ExternalSyntheticLambda32(this, iRemoteAnimationRunner));
    }

    public void setBlursDisabledForAppLaunch(boolean z) {
        this.mKeyguardViewMediator.setBlursDisabledForAppLaunch(z);
    }

    public int getBackgroundColor(TaskInfo taskInfo) {
        if (this.mStartingSurfaceOptional.isPresent()) {
            return this.mStartingSurfaceOptional.get().getBackgroundColor(taskInfo);
        }
        Log.w("StatusBar", "No starting surface, defaulting to SystemBGColor");
        return SplashscreenContentDrawer.getSystemBGColor();
    }

    public boolean isDeviceInVrMode() {
        return this.mPresenter.isDeviceInVrMode();
    }

    public NotificationPresenter getPresenter() {
        return this.mPresenter;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setBarStateForTest(int i) {
        this.mState = i;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setUserSetupForTest(boolean z) {
        this.mUserSetup = z;
    }

    /* renamed from: com.android.systemui.statusbar.phone.StatusBar$H */
    protected class C1935H extends Handler {
        protected C1935H() {
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1026) {
                StatusBar.this.toggleKeyboardShortcuts(message.arg1);
            } else if (i != 1027) {
                switch (i) {
                    case 1000:
                        StatusBar.this.animateExpandNotificationsPanel();
                        return;
                    case 1001:
                        StatusBar.this.mShadeController.animateCollapsePanels();
                        return;
                    case 1002:
                        StatusBar.this.animateExpandSettingsPanel((String) message.obj);
                        return;
                    case 1003:
                        StatusBar.this.onLaunchTransitionTimeout();
                        return;
                    default:
                        return;
                }
            } else {
                StatusBar.this.dismissKeyboardShortcuts();
            }
        }
    }

    public void maybeEscalateHeadsUp() {
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            this.mHeadsUpManager.getAllEntries().forEach(new StatusBar$$ExternalSyntheticLambda40(this));
            this.mHeadsUpManager.releaseAllImmediately();
        } else if (!this.mHeadsUpManager.getAllEntries().anyMatch(new StatusBar$$ExternalSyntheticLambda45(this))) {
            this.mHeadsUpManager.releaseAllImmediately();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$maybeEscalateHeadsUp$16(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        Notification notification = sbn.getNotification();
        if (notification.fullScreenIntent == null) {
            return false;
        }
        if (HeadsUpUtil.shouldCliHeadsUpForLock(this.mContext, sbn)) {
            return true;
        }
        try {
            EventLog.writeEvent(36003, sbn.getKey());
            if (MotoDesktopManager.isDesktopSupported()) {
                ActivityOptions makeBasic = ActivityOptions.makeBasic();
                makeBasic.setFullscreenIntentFromStatusBar(this.mContext, true);
                notification.fullScreenIntent.send((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, makeBasic.toBundle());
            } else {
                notification.fullScreenIntent.send();
            }
            notificationEntry.notifyFullScreenIntentLaunched();
            return false;
        } catch (PendingIntent.CanceledException unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeEscalateHeadsUp$17(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        Notification notification = sbn.getNotification();
        if (notification.fullScreenIntent != null) {
            try {
                EventLog.writeEvent(36003, sbn.getKey());
                if (MotoDesktopManager.isDesktopSupported()) {
                    ActivityOptions makeBasic = ActivityOptions.makeBasic();
                    makeBasic.setFullscreenIntentFromStatusBar(this.mContext, true);
                    notification.fullScreenIntent.send((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, makeBasic.toBundle());
                } else {
                    notification.fullScreenIntent.send();
                }
                notificationEntry.notifyFullScreenIntentLaunched();
            } catch (PendingIntent.CanceledException unused) {
            }
        }
    }

    public void handleSystemKey(int i) {
        UdfpsController udfpsController;
        if (MotoFeature.getInstance(this.mContext).supportSideFps() && i == 26) {
            this.mKeyguardUpdateMonitor.updateFingerprintListeningAfterPowerKeyDown();
        }
        if (USERDEBUG) {
            Log.d("StatusBar", "handleSystemKey: key = " + i);
        }
        if (!MotoFeature.getInstance(this.mContext).isSupportUdfps() || (udfpsController = this.mUdfpsController) == null || udfpsController.shouldPauseAuth() || MotoFeature.getInstance(this.mContext).udfpsUseAospTriggerFingerDown()) {
            if (this.mContext.getResources().getBoolean(R$bool.zz_moto_folio_product) && i == 26 && this.mMotoDisplayManager != null) {
                Log.i("StatusBar", "Send power key event to Folio AOD.");
                this.mMotoDisplayManager.notifyEvent("FOLIO_POWER_KEY_ID", false, (String) null, (String) null, (Bundle) null);
            }
            if (this.mCommandQueue.panelsEnabled() && this.mKeyguardUpdateMonitor.isDeviceInteractive()) {
                if ((!this.mKeyguardStateController.isShowing() || this.mKeyguardStateController.isOccluded()) && this.mUserSetup) {
                    if (this.mSideFpsController != null) {
                        if (this.mContext.getResources().getConfiguration().orientation != 2) {
                            if ((!(Settings.Secure.getInt(this.mContext.getContentResolver(), "one_handed_mode_enabled", 0) != 0) || i != 281) && (((this.mSideFpsController.isSideGestureEnabled() || this.mSideFpsController.isTutorialMode()) && this.mSideFpsController.handleSideFpsGesture(i)) || !this.mSideFpsController.isSystemGestureEnabled())) {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    if (280 == i) {
                        this.mMetricsLogger.action(493);
                        this.mNotificationPanelViewController.collapse(false, 1.0f);
                    } else if (281 == i) {
                        this.mMetricsLogger.action(494);
                        handleSystemNavigationDown();
                    }
                }
            }
        } else {
            Log.i("StatusBar", "Udfps - handleSystemKey: key = " + i);
            if (i == 281) {
                this.mUdfpsController.onFingerDown(0, 0, 0.0f, 0.0f);
            } else if (i == 280) {
                this.mUdfpsController.onFingerUp();
            }
        }
    }

    public void handleSystemNavigationDown() {
        if (this.mNotificationPanelViewController.isFullyCollapsed()) {
            if (this.mVibrateOnOpening && this.mSideFpsController == null && this.mEdgeTouchPillController == null) {
                this.mVibratorHelper.vibrate(2);
            }
            if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
                if (USERDEBUG) {
                    Log.i("StatusBar", "PrcPanel handleSystemNavigationDown");
                }
                this.mNotificationPanelViewController.updatePanelViewState(1);
            }
            this.mNotificationPanelViewController.expand(true);
            this.mStackScroller.setWillExpand(true);
            this.mHeadsUpManager.unpinAll(true);
            this.mMetricsLogger.count("panel_open", 1);
        } else if (!this.mNotificationPanelViewController.isInSettings() && !this.mNotificationPanelViewController.isExpanding()) {
            this.mNotificationPanelViewController.flingSettings(0.0f, 0);
            this.mMetricsLogger.count("panel_open_qs", 1);
        }
    }

    public void showPinningEnterExitToast(boolean z) {
        if (getNavigationBarView() != null) {
            getNavigationBarView().showPinningEnterExitToast(z);
        }
    }

    public void showPinningEscapeToast() {
        if (getNavigationBarView() != null) {
            getNavigationBarView().showPinningEscapeToast();
        }
    }

    /* access modifiers changed from: package-private */
    public void makeExpandedVisible(boolean z) {
        if (z || (!this.mExpandedVisible && this.mCommandQueue.panelsEnabled())) {
            this.mCarrierLabelUpdateMonitor.setOnsShown(false);
            this.mExpandedVisible = true;
            this.mNotificationShadeWindowController.setPanelVisible(true);
            visibilityChanged(true);
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, !z);
            setInteracting(1, true);
        }
    }

    public void postAnimateCollapsePanels() {
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            C1935H h = this.mHandler;
            ShadeController shadeController = this.mShadeController;
            Objects.requireNonNull(shadeController);
            h.post(new StatusBar$$ExternalSyntheticLambda13(shadeController));
            return;
        }
        ((CliStatusBar) Dependency.get(CliStatusBar.class)).animateExpandedVisible(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postAnimateForceCollapsePanels$18() {
        this.mShadeController.animateCollapsePanels(0, true);
    }

    public void postAnimateForceCollapsePanels() {
        this.mHandler.post(new StatusBar$$ExternalSyntheticLambda17(this));
    }

    public void postAnimateOpenPanels() {
        this.mHandler.sendEmptyMessage(1002);
    }

    public void togglePanel() {
        if (this.mPanelExpanded) {
            this.mShadeController.animateCollapsePanels();
        } else {
            animateExpandNotificationsPanel();
        }
    }

    public void animateCollapsePanels(int i, boolean z) {
        this.mShadeController.animateCollapsePanels(i, z, false, 1.0f);
    }

    /* access modifiers changed from: package-private */
    public void postHideRecentApps() {
        if (!this.mHandler.hasMessages(1020)) {
            this.mHandler.removeMessages(1020);
            this.mHandler.sendEmptyMessage(1020);
        }
    }

    public boolean isPanelExpanded() {
        return this.mPanelExpanded;
    }

    public void onInputFocusTransfer(boolean z, boolean z2, float f) {
        if (this.mCommandQueue.panelsEnabled()) {
            if (z) {
                this.mNotificationPanelViewController.startWaitingForOpenPanelGesture();
            } else {
                this.mNotificationPanelViewController.stopWaitingForOpenPanelGesture(z2, f);
            }
        }
    }

    public void animateExpandNotificationsPanel() {
        if (this.mCommandQueue.panelsEnabled()) {
            this.mNotificationPanelViewController.expandWithoutQs();
        }
    }

    public void animateExpandSettingsPanel(String str) {
        if (this.mCommandQueue.panelsEnabled() && this.mUserSetup) {
            if (str != null) {
                this.mQSPanelController.openDetails(str);
            }
            this.mNotificationPanelViewController.expandWithQs();
        }
    }

    public void animateCollapseQuickSettings() {
        if (this.mState == 0) {
            this.mStatusBarView.collapsePanel(true, false, 1.0f);
        }
    }

    /* access modifiers changed from: package-private */
    public void makeExpandedInvisible() {
        makeExpandedInvisible(true);
    }

    public void makeExpandedInvisible(boolean z) {
        if (this.mExpandedVisible && this.mNotificationShadeWindowView != null) {
            this.mStatusBarView.collapsePanel(false, false, 1.0f);
            this.mNotificationPanelViewController.closeQs();
            this.mExpandedVisible = false;
            visibilityChanged(false);
            this.mNotificationShadeWindowController.setPanelVisible(false);
            this.mStatusBarWindowController.setForceStatusBarVisible(false);
            this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
            this.mShadeController.runPostCollapseRunnables();
            setInteracting(1, false);
            if (z && !this.mNotificationActivityStarter.isCollapsingToShowActivityOverLockscreen()) {
                showBouncerIfKeyguard();
            }
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, this.mNotificationPanelViewController.hideStatusBarIconsWhenExpanded());
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                WindowManagerGlobal.getInstance().trimMemory(20);
            }
            updateNotificationPanelAlphaIfNeed();
        }
    }

    public boolean interceptTouchEvent(MotionEvent motionEvent) {
        if (this.mStatusBarWindowState == 0) {
            if (!(motionEvent.getAction() == 1 || motionEvent.getAction() == 3) || this.mExpandedVisible) {
                setInteracting(1, true);
            } else {
                setInteracting(1, false);
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isSameStatusBarState(int i) {
        return this.mStatusBarWindowState == i;
    }

    public GestureRecorder getGestureRecorder() {
        return this.mGestureRec;
    }

    public void setWindowState(int i, int i2, int i3) {
        EdgeTouchPillController edgeTouchPillController;
        if (i == this.mDisplayId) {
            boolean z = true;
            boolean z2 = i3 == 0;
            if (!(this.mNotificationShadeWindowView == null || i2 != 1 || this.mStatusBarWindowState == i3)) {
                this.mStatusBarWindowState = i3;
                PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
                if (phoneStatusBarView != null) {
                    if (!z2 && this.mState == 0) {
                        phoneStatusBarView.collapsePanel(false, false, 1.0f);
                    }
                    this.mStatusBarWindowHidden = i3 == 2;
                    updateHideIconsForBouncer(false);
                }
            }
            if (i2 == 2 && (edgeTouchPillController = this.mEdgeTouchPillController) != null) {
                if (i3 != 0) {
                    z = false;
                }
                edgeTouchPillController.onNavigationBarVisibilityChanged(z);
            }
            updateBubblesVisibility();
        }
    }

    public void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, boolean z2) {
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            if (MotoFeature.isLidClosed(this.mContext) && i != 1) {
                return;
            }
            if (!MotoFeature.isLidClosed(this.mContext) && i != 0) {
                return;
            }
        } else if (i != this.mDisplayId) {
            return;
        }
        boolean z3 = false;
        if (this.mAppearance != i2) {
            this.mAppearance = i2;
            z3 = updateBarMode(barMode(this.mTransientShown, i2), i);
            if (USERDEBUG) {
                Log.i("StatusBar", "onSystemBarAttributesChanged statusBarColor appearance = " + i2 + " mTransientShown = " + this.mTransientShown + " mStatusBarMode = " + this.mStatusBarMode + " barModeChanged = " + z3);
            }
        }
        this.mLightBarController.onStatusBarAppearanceChanged(appearanceRegionArr, z3, this.mStatusBarMode, z);
        updateBubblesVisibility();
        this.mStatusBarStateController.setFullscreenState(z2);
    }

    public void showTransient(int i, int[] iArr) {
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            if (MotoFeature.isLidClosed(this.mContext) && i != 1) {
                return;
            }
            if (!MotoFeature.isLidClosed(this.mContext) && i != 0) {
                return;
            }
        } else if (i != this.mDisplayId) {
            return;
        }
        if (InsetsState.containsType(iArr, 0)) {
            showTransientUnchecked(i);
        }
    }

    private void showTransientUnchecked(int i) {
        if (!this.mTransientShown) {
            this.mTransientShown = true;
            this.mNoAnimationOnNextBarModeChange = true;
            if (USERDEBUG) {
                Log.i("StatusBar", "statusBarColor showTransientUnchecked displayId = " + i);
            }
            handleTransientChanged(i);
        }
    }

    public void abortTransient(int i, int[] iArr) {
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            if (MotoFeature.isLidClosed(this.mContext) && i != 1) {
                return;
            }
            if (!MotoFeature.isLidClosed(this.mContext) && i != 0) {
                return;
            }
        } else if (i != this.mDisplayId) {
            return;
        }
        if (InsetsState.containsType(iArr, 0)) {
            clearTransient(i);
        }
    }

    /* access modifiers changed from: private */
    public void clearTransient(int i) {
        if (this.mTransientShown) {
            this.mTransientShown = false;
            if (USERDEBUG) {
                Log.i("StatusBar", "statusBarColor clearTransient displayId = " + i);
            }
            handleTransientChanged(i);
        }
    }

    private void handleTransientChanged(int i) {
        int barMode = barMode(this.mTransientShown, this.mAppearance);
        if (updateBarMode(barMode, i)) {
            this.mLightBarController.onStatusBarModeChanged(barMode);
            updateBubblesVisibility();
        }
    }

    private boolean updateBarMode(int i, int i2) {
        NavigationBarView navigationBarView;
        if (this.mStatusBarMode == i) {
            return false;
        }
        this.mStatusBarMode = i;
        checkBarModes();
        if (!(!MotoFeature.getInstance(this.mContext).isSupportCli() || i2 == 0 || (navigationBarView = this.mNavigationBarController.getNavigationBarView(i2)) == null)) {
            navigationBarView.getAutoHideController().touchAutoHide();
        }
        this.mAutoHideController.touchAutoHide();
        return true;
    }

    public void showWirelessChargingAnimation(int i) {
        showChargingAnimation(i, -1, 0);
    }

    /* access modifiers changed from: protected */
    public void showChargingAnimation(int i, int i2, long j) {
        if (!WaterfallManager.hasWaterfallDisplay(this.mContext)) {
            WirelessChargingAnimation.makeWirelessChargingAnimation(this.mContext, (Looper) null, i2, i, new WirelessChargingAnimation.Callback() {
                public void onAnimationStarting() {
                    StatusBar.this.mNotificationShadeWindowController.setRequestTopUi(true, "StatusBar");
                }

                public void onAnimationEnded() {
                    StatusBar.this.mNotificationShadeWindowController.setRequestTopUi(false, "StatusBar");
                }
            }, false, sUiEventLogger).show(j);
        }
    }

    public void onRecentsAnimationStateChanged(boolean z) {
        setInteracting(2, z);
    }

    /* access modifiers changed from: protected */
    public BarTransitions getStatusBarTransitions() {
        return this.mNotificationShadeWindowViewController.getBarTransitions();
    }

    public void checkBarModes() {
        if (!this.mDemoModeController.isInDemoMode()) {
            if (!(this.mNotificationShadeWindowViewController == null || getStatusBarTransitions() == null)) {
                checkBarMode(this.mStatusBarMode, this.mStatusBarWindowState, getStatusBarTransitions());
            }
            this.mNavigationBarController.checkNavBarModes(this.mDisplayId);
            this.mNoAnimationOnNextBarModeChange = false;
        }
    }

    public void setQsScrimEnabled(boolean z) {
        this.mNotificationPanelViewController.setQsScrimEnabled(z);
    }

    private void updateBubblesVisibility() {
        if (this.mBubblesOptional.isPresent()) {
            Bubbles bubbles = this.mBubblesOptional.get();
            int i = this.mStatusBarMode;
            bubbles.onStatusBarVisibilityChanged((i == 3 || i == 6 || this.mStatusBarWindowHidden) ? false : true);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkBarMode(int i, int i2, BarTransitions barTransitions) {
        barTransitions.transitionTo(i, !this.mNoAnimationOnNextBarModeChange && this.mDeviceInteractive && i2 != 2);
    }

    /* access modifiers changed from: private */
    public void finishBarAnimations() {
        if (!(this.mNotificationShadeWindowController == null || this.mNotificationShadeWindowViewController.getBarTransitions() == null)) {
            this.mNotificationShadeWindowViewController.getBarTransitions().finishAnimations();
        }
        this.mNavigationBarController.finishBarAnimations(this.mDisplayId);
    }

    public void setInteracting(int i, boolean z) {
        int i2;
        if (z) {
            i2 = i | this.mInteractingWindows;
        } else {
            i2 = (~i) & this.mInteractingWindows;
        }
        this.mInteractingWindows = i2;
        if (i2 != 0) {
            this.mAutoHideController.suspendAutoHide();
        } else {
            this.mAutoHideController.resumeSuspendedAutoHide();
        }
        checkBarModes();
    }

    /* access modifiers changed from: private */
    public void dismissVolumeDialog() {
        VolumeComponent volumeComponent = this.mVolumeComponent;
        if (volumeComponent != null) {
            volumeComponent.dismissNow();
        }
    }

    public static String viewInfo(View view) {
        return "[(" + view.getLeft() + "," + view.getTop() + ")(" + view.getRight() + "," + view.getBottom() + ") " + view.getWidth() + "x" + view.getHeight() + "]";
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        synchronized (this.mQueueLock) {
            printWriter.println("Current Status Bar state:");
            printWriter.println("  mExpandedVisible=" + this.mExpandedVisible);
            printWriter.println("  mDisplayMetrics=" + this.mDisplayMetrics);
            printWriter.println("  mStackScroller: " + viewInfo(this.mStackScroller));
            printWriter.println("  mStackScroller: " + viewInfo(this.mStackScroller) + " scroll " + this.mStackScroller.getScrollX() + "," + this.mStackScroller.getScrollY());
        }
        printWriter.print("  mInteractingWindows=");
        printWriter.println(this.mInteractingWindows);
        printWriter.print("  mStatusBarWindowState=");
        printWriter.println(StatusBarManager.windowStateToString(this.mStatusBarWindowState));
        printWriter.print("  mStatusBarMode=");
        printWriter.println(BarTransitions.modeToString(this.mStatusBarMode));
        printWriter.print("  mDozing=");
        printWriter.println(this.mDozing);
        printWriter.print("  mWallpaperSupported= ");
        printWriter.println(this.mWallpaperSupported);
        printWriter.println("  StatusBarWindowView: ");
        NotificationShadeWindowViewController notificationShadeWindowViewController = this.mNotificationShadeWindowViewController;
        if (notificationShadeWindowViewController != null) {
            notificationShadeWindowViewController.dump(fileDescriptor, printWriter, strArr);
            dumpBarTransitions(printWriter, "PhoneStatusBarTransitions", this.mNotificationShadeWindowViewController.getBarTransitions());
        }
        printWriter.println("  mMediaManager: ");
        NotificationMediaManager notificationMediaManager = this.mMediaManager;
        if (notificationMediaManager != null) {
            notificationMediaManager.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("  Panels: ");
        if (this.mNotificationPanelViewController != null) {
            printWriter.println("    mNotificationPanel=" + this.mNotificationPanelViewController.getView() + " params=" + this.mNotificationPanelViewController.getView().getLayoutParams().debug(""));
            printWriter.print("      ");
            this.mNotificationPanelViewController.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("  mStackScroller: ");
        if (this.mStackScroller instanceof Dumpable) {
            printWriter.print("      ");
            this.mStackScroller.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("  Theme:");
        if (this.mUiModeManager == null) {
            str = "null";
        } else {
            str = this.mUiModeManager.getNightMode() + "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("    dark theme: ");
        sb.append(str);
        sb.append(" (auto: ");
        boolean z = false;
        sb.append(0);
        sb.append(", yes: ");
        sb.append(2);
        sb.append(", no: ");
        sb.append(1);
        sb.append(")");
        printWriter.println(sb.toString());
        if (this.mContext.getThemeResId() == R$style.Theme_SystemUI_LightWallpaper) {
            z = true;
        }
        printWriter.println("    light wallpaper theme: " + z);
        KeyguardIndicationController keyguardIndicationController = this.mKeyguardIndicationController;
        if (keyguardIndicationController != null) {
            keyguardIndicationController.dump(fileDescriptor, printWriter, strArr);
        }
        ScrimController scrimController = this.mScrimController;
        if (scrimController != null) {
            scrimController.dump(fileDescriptor, printWriter, strArr);
        }
        if (this.mLightRevealScrim != null) {
            printWriter.println("mLightRevealScrim.getRevealAmount(): " + this.mLightRevealScrim.getRevealAmount());
        }
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            statusBarKeyguardViewManager.dump(printWriter);
        }
        this.mNotificationsController.dump(fileDescriptor, printWriter, strArr, true);
        HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
        if (headsUpManagerPhone != null) {
            headsUpManagerPhone.dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.println("  mHeadsUpManager: null");
        }
        StatusBarTouchableRegionManager statusBarTouchableRegionManager = this.mStatusBarTouchableRegionManager;
        if (statusBarTouchableRegionManager != null) {
            statusBarTouchableRegionManager.dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.println("  mStatusBarTouchableRegionManager: null");
        }
        LightBarController lightBarController = this.mLightBarController;
        if (lightBarController != null) {
            lightBarController.dump(fileDescriptor, printWriter, strArr);
        }
        this.mRROsController.dump(printWriter);
        ImsIconController.getInstance(this.mContext).dump(printWriter);
        printWriter.println("SharedPreferences:");
        for (Map.Entry next : Prefs.getAll(this.mContext).entrySet()) {
            printWriter.print("  ");
            printWriter.print((String) next.getKey());
            printWriter.print("=");
            printWriter.println(next.getValue());
        }
        printWriter.println("Camera gesture intents:");
        printWriter.println("   Insecure camera: " + CameraIntents.getInsecureCameraIntent(this.mContext));
        printWriter.println("   Secure camera: " + CameraIntents.getSecureCameraIntent(this.mContext));
        printWriter.println("   Override package: " + String.valueOf(CameraIntents.getOverrideCameraPackage(this.mContext)));
    }

    public static void dumpBarTransitions(PrintWriter printWriter, String str, BarTransitions barTransitions) {
        printWriter.print("  ");
        printWriter.print(str);
        printWriter.print(".BarTransitions.mMode=");
        if (barTransitions != null) {
            printWriter.println(BarTransitions.modeToString(barTransitions.getMode()));
        } else {
            printWriter.println("Unknown");
        }
    }

    public void createAndAddWindows(RegisterStatusBarResult registerStatusBarResult) {
        makeStatusBarView(registerStatusBarResult);
        this.mNotificationShadeWindowController.attach();
        this.mStatusBarWindowController.attach();
    }

    /* access modifiers changed from: package-private */
    public void updateDisplaySize() {
        this.mDisplay.getMetrics(this.mDisplayMetrics);
        this.mDisplay.getSize(this.mCurrentDisplaySize);
    }

    /* access modifiers changed from: package-private */
    public float getDisplayDensity() {
        return this.mDisplayMetrics.density;
    }

    public float getDisplayWidth() {
        return (float) this.mDisplayMetrics.widthPixels;
    }

    public float getDisplayHeight() {
        return (float) this.mDisplayMetrics.heightPixels;
    }

    /* access modifiers changed from: package-private */
    public int getRotation() {
        return this.mDisplay.getRotation();
    }

    /* access modifiers changed from: package-private */
    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void startActivityDismissingKeyguard(Intent intent, boolean z, boolean z2, int i) {
        startActivityDismissingKeyguard(intent, z, z2, false, (ActivityStarter.Callback) null, i, (ActivityLaunchAnimator.Controller) null);
    }

    public void startActivityDismissingKeyguard(Intent intent, boolean z, boolean z2) {
        startActivityDismissingKeyguard(intent, z, z2, 0);
    }

    private void startActivityDismissingKeyguard(Intent intent, boolean z, boolean z2, boolean z3, ActivityStarter.Callback callback, int i, ActivityLaunchAnimator.Controller controller) {
        boolean z4 = z2;
        ActivityLaunchAnimator.Controller controller2 = controller;
        if (!z || this.mDeviceProvisionedController.isDeviceProvisioned()) {
            Intent intent2 = intent;
            boolean wouldLaunchResolverActivity = this.mActivityIntentHelper.wouldLaunchResolverActivity(intent, this.mLockscreenUserManager.getCurrentUserId());
            boolean z5 = controller2 != null && !wouldLaunchResolverActivity && shouldAnimateLaunch(true);
            ActivityLaunchAnimator.Controller wrapAnimationController = z5 ? wrapAnimationController(controller2, z4) : null;
            executeRunnableDismissingKeyguard(new StatusBar$$ExternalSyntheticLambda30(this, intent, i, wrapAnimationController, z5, z3, callback), new StatusBar$$ExternalSyntheticLambda12(callback), z4 && wrapAnimationController == null, wouldLaunchResolverActivity, true, z5);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startActivityDismissingKeyguard$21(Intent intent, int i, ActivityLaunchAnimator.Controller controller, boolean z, boolean z2, ActivityStarter.Callback callback) {
        int i2;
        ActivityStarter.Callback callback2 = callback;
        this.mAssistManagerLazy.get().hideAssist();
        String motoCameraAppPackageName = getKeyguardBottomAreaView().getMotoCameraAppPackageName(this.mContext);
        if (intent.getPackage() == null || !intent.getPackage().equals(motoCameraAppPackageName)) {
            Intent intent2 = intent;
            intent.setFlags(335544320);
            intent.addFlags(i);
            i2 = 268435456;
        } else {
            Intent intent3 = intent;
            i2 = 0;
        }
        int[] iArr = {-96};
        ActivityLaunchAnimator.Controller controller2 = controller;
        this.mActivityLaunchAnimator.startIntentWithAnimation(controller, z, intent.getPackage(), new StatusBar$$ExternalSyntheticLambda47(this, z2, intent, iArr, i2));
        if (callback2 != null) {
            callback2.onActivityStarted(iArr[0]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Integer lambda$startActivityDismissingKeyguard$20(boolean z, Intent intent, int[] iArr, int i, RemoteAnimationAdapter remoteAnimationAdapter) {
        Intent intent2 = intent;
        ActivityOptions activityOptions = new ActivityOptions(getActivityOptions(this.mDisplayId, remoteAnimationAdapter));
        activityOptions.setDisallowEnterPictureInPictureWhileLaunching(z);
        if (CameraIntents.isInsecureCameraIntent(intent)) {
            activityOptions.setRotationAnimationHint(3);
            if (MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isLidClosed(this.mContext)) {
                KeyguardBottomAreaView.convertCliCameraIntentIfNeeded(intent2, activityOptions);
            }
        }
        if (intent.getAction() == "android.settings.panel.action.VOLUME") {
            activityOptions.setDisallowEnterPictureInPictureWhileLaunching(true);
        }
        try {
            iArr[0] = ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, this.mContext.getBasePackageName(), this.mContext.getAttributionTag(), intent, intent2.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, i, (ProfilerInfo) null, activityOptions.toBundle(), UserHandle.CURRENT.getIdentifier());
        } catch (RemoteException e) {
            Log.w("StatusBar", "Unable to start activity", e);
        }
        return Integer.valueOf(iArr[0]);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$startActivityDismissingKeyguard$22(ActivityStarter.Callback callback) {
        if (callback != null) {
            callback.onActivityStarted(-96);
        }
    }

    private ActivityLaunchAnimator.Controller wrapAnimationController(ActivityLaunchAnimator.Controller controller, boolean z) {
        View rootView = controller.getLaunchContainer().getRootView();
        if (rootView != this.mSuperStatusBarViewFactory.getStatusBarWindowView()) {
            return (!z || rootView != this.mNotificationShadeWindowView) ? controller : new StatusBarLaunchAnimatorController(controller, this, true);
        }
        controller.setLaunchContainer(this.mStatusBarWindowController.getLaunchAnimationContainer());
        return new DelegateLaunchAnimatorController(controller) {
            public void onLaunchAnimationStart(boolean z) {
                getDelegate().onLaunchAnimationStart(z);
                StatusBar.this.mStatusBarWindowController.setLaunchAnimationRunning(true);
            }

            public void onLaunchAnimationEnd(boolean z) {
                getDelegate().onLaunchAnimationEnd(z);
                StatusBar.this.mStatusBarWindowController.setLaunchAnimationRunning(false);
            }
        };
    }

    public void readyForKeyguardDone() {
        this.mStatusBarKeyguardViewManager.readyForKeyguardDone();
    }

    public void executeRunnableDismissingKeyguard(Runnable runnable, Runnable runnable2, boolean z, boolean z2, boolean z3) {
        executeRunnableDismissingKeyguard(runnable, runnable2, z, z2, z3, false);
    }

    public void executeRunnableDismissingKeyguard(Runnable runnable, Runnable runnable2, boolean z, boolean z2, boolean z3, boolean z4) {
        final Runnable runnable3 = runnable;
        final boolean z5 = z;
        final boolean z6 = z3;
        final boolean z7 = z4;
        dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() {
            public boolean onDismiss() {
                if (runnable3 != null) {
                    if (!StatusBar.this.mStatusBarKeyguardViewManager.isShowing() || !StatusBar.this.mStatusBarKeyguardViewManager.isOccluded()) {
                        AsyncTask.execute(runnable3);
                    } else {
                        StatusBar.this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable(runnable3);
                    }
                }
                if (z5) {
                    if (StatusBar.this.mExpandedVisible) {
                        StatusBar statusBar = StatusBar.this;
                        if (!statusBar.mBouncerShowing) {
                            statusBar.mShadeController.animateCollapsePanels(2, true, true);
                        }
                    }
                    StatusBar statusBar2 = StatusBar.this;
                    C1935H h = statusBar2.mHandler;
                    ShadeController access$1500 = statusBar2.mShadeController;
                    Objects.requireNonNull(access$1500);
                    h.post(new StatusBar$15$$ExternalSyntheticLambda0(access$1500));
                } else if (StatusBar.this.isInLaunchTransition() && StatusBar.this.mNotificationPanelViewController.isLaunchTransitionFinished()) {
                    StatusBar statusBar3 = StatusBar.this;
                    C1935H h2 = statusBar3.mHandler;
                    StatusBarKeyguardViewManager statusBarKeyguardViewManager = statusBar3.mStatusBarKeyguardViewManager;
                    Objects.requireNonNull(statusBarKeyguardViewManager);
                    h2.post(new StatusBar$15$$ExternalSyntheticLambda1(statusBarKeyguardViewManager));
                }
                return z6;
            }

            public boolean willRunAnimationOnKeyguard() {
                return z7;
            }
        }, runnable2, z2);
    }

    public void resetUserExpandedStates() {
        this.mNotificationsController.resetUserExpandedStates();
    }

    /* access modifiers changed from: private */
    public void executeWhenUnlocked(ActivityStarter.OnDismissAction onDismissAction, boolean z, boolean z2) {
        if (this.mStatusBarKeyguardViewManager.isShowing() && z) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        dismissKeyguardThenExecute(onDismissAction, (Runnable) null, z2);
    }

    /* access modifiers changed from: protected */
    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction onDismissAction, boolean z) {
        dismissKeyguardThenExecute(onDismissAction, (Runnable) null, z);
    }

    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        if (this.mWakefulnessLifecycle.getWakefulness() == 0 && this.mKeyguardStateController.canDismissLockScreen() && !this.mStatusBarStateController.leaveOpenOnKeyguardHide() && this.mDozeServiceHost.isPulsing()) {
            this.mBiometricUnlockController.startWakeAndUnlock(2);
        }
        if (this.mStatusBarKeyguardViewManager.isShowing()) {
            this.mStatusBarKeyguardViewManager.dismissWithAction(onDismissAction, runnable, z);
        } else {
            onDismissAction.onDismiss();
        }
    }

    public void onConfigChanged(Configuration configuration) {
        updateResources();
        updateDisplaySize();
        this.mViewHierarchyManager.updateRowStates();
        this.mScreenPinningRequest.onConfigurationChanged();
        EdgeTouchPillController edgeTouchPillController = this.mEdgeTouchPillController;
        if (edgeTouchPillController != null) {
            boolean z = true;
            if (configuration.orientation != 1) {
                z = false;
            }
            edgeTouchPillController.updateOrientation(z);
        }
    }

    public void setLockscreenUser(int i) {
        LockscreenWallpaper lockscreenWallpaper = this.mLockscreenWallpaper;
        if (lockscreenWallpaper != null) {
            lockscreenWallpaper.setCurrentUser(i);
        }
        this.mScrimController.setCurrentUser(i);
        if (this.mWallpaperSupported) {
            this.mWallpaperChangedReceiver.onReceive(this.mContext, (Intent) null);
        }
        updateQuickLaunchCameraSetting();
    }

    /* access modifiers changed from: package-private */
    public void updateResources() {
        QSPanelController qSPanelController = this.mQSPanelController;
        if (qSPanelController != null) {
            qSPanelController.updateResources();
        }
        StatusBarWindowController statusBarWindowController = this.mStatusBarWindowController;
        if (statusBarWindowController != null) {
            statusBarWindowController.refreshStatusBarHeight();
        }
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            phoneStatusBarView.updateResources();
        }
        NotificationPanelViewController notificationPanelViewController = this.mNotificationPanelViewController;
        if (notificationPanelViewController != null) {
            notificationPanelViewController.updateResources();
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.updateResources();
        }
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            statusBarKeyguardViewManager.updateResources();
        }
        this.mPowerButtonReveal = new PowerButtonReveal((float) this.mContext.getResources().getDimensionPixelSize(R$dimen.physical_power_button_center_screen_location_y));
    }

    /* access modifiers changed from: protected */
    public void handleVisibleToUserChanged(boolean z) {
        if (z) {
            handleVisibleToUserChangedImpl(z);
            this.mNotificationLogger.startNotificationLogging();
            return;
        }
        this.mNotificationLogger.stopNotificationLogging();
        handleVisibleToUserChangedImpl(z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0011, code lost:
        r0 = r3.mState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleVisibleToUserChangedImpl(boolean r4) {
        /*
            r3 = this;
            if (r4 == 0) goto L_0x0038
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r4 = r3.mHeadsUpManager
            boolean r4 = r4.hasPinnedHeadsUp()
            com.android.systemui.statusbar.phone.StatusBarNotificationPresenter r0 = r3.mPresenter
            boolean r0 = r0.isPresenterFullyCollapsed()
            r1 = 1
            if (r0 != 0) goto L_0x001a
            int r0 = r3.mState
            if (r0 == 0) goto L_0x0018
            r2 = 2
            if (r0 != r2) goto L_0x001a
        L_0x0018:
            r0 = r1
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            com.android.systemui.statusbar.notification.init.NotificationsController r2 = r3.mNotificationsController
            int r2 = r2.getActiveNotificationsCount()
            if (r4 == 0) goto L_0x002c
            com.android.systemui.statusbar.phone.StatusBarNotificationPresenter r4 = r3.mPresenter
            boolean r4 = r4.isPresenterFullyCollapsed()
            if (r4 == 0) goto L_0x002c
            goto L_0x002d
        L_0x002c:
            r1 = r2
        L_0x002d:
            java.util.concurrent.Executor r4 = r3.mUiBgExecutor
            com.android.systemui.statusbar.phone.StatusBar$$ExternalSyntheticLambda38 r2 = new com.android.systemui.statusbar.phone.StatusBar$$ExternalSyntheticLambda38
            r2.<init>(r3, r0, r1)
            r4.execute(r2)
            goto L_0x0042
        L_0x0038:
            java.util.concurrent.Executor r4 = r3.mUiBgExecutor
            com.android.systemui.statusbar.phone.StatusBar$$ExternalSyntheticLambda18 r0 = new com.android.systemui.statusbar.phone.StatusBar$$ExternalSyntheticLambda18
            r0.<init>(r3)
            r4.execute(r0)
        L_0x0042:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBar.handleVisibleToUserChangedImpl(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleVisibleToUserChangedImpl$23(boolean z, int i) {
        try {
            this.mBarService.onPanelRevealed(z, i);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleVisibleToUserChangedImpl$24() {
        try {
            this.mBarService.onPanelHidden();
        } catch (RemoteException unused) {
        }
    }

    private void logStateToEventlog() {
        boolean isShowing = this.mStatusBarKeyguardViewManager.isShowing();
        boolean isOccluded = this.mStatusBarKeyguardViewManager.isOccluded();
        boolean isBouncerShowing = this.mStatusBarKeyguardViewManager.isBouncerShowing();
        boolean isMethodSecure = this.mKeyguardStateController.isMethodSecure();
        boolean canDismissLockScreen = this.mKeyguardStateController.canDismissLockScreen();
        int loggingFingerprint = getLoggingFingerprint(this.mState, isShowing, isOccluded, isBouncerShowing, isMethodSecure, canDismissLockScreen);
        if (loggingFingerprint != this.mLastLoggedStateFingerprint) {
            if (this.mStatusBarStateLog == null) {
                this.mStatusBarStateLog = new LogMaker(0);
            }
            this.mMetricsLogger.write(this.mStatusBarStateLog.setCategory(isBouncerShowing ? 197 : 196).setType(isShowing ? 1 : 2).setSubtype(isMethodSecure ? 1 : 0));
            EventLogTags.writeSysuiStatusBarState(this.mState, isShowing ? 1 : 0, isOccluded ? 1 : 0, isBouncerShowing ? 1 : 0, isMethodSecure ? 1 : 0, canDismissLockScreen ? 1 : 0);
            this.mLastLoggedStateFingerprint = loggingFingerprint;
            StringBuilder sb = new StringBuilder();
            sb.append(isBouncerShowing ? "BOUNCER" : "LOCKSCREEN");
            sb.append(isShowing ? "_OPEN" : "_CLOSE");
            sb.append(isMethodSecure ? "_SECURE" : "_INSECURE");
            sUiEventLogger.log(StatusBarUiEvent.valueOf(sb.toString()));
        }
    }

    /* access modifiers changed from: package-private */
    public void vibrate() {
        ((Vibrator) this.mContext.getSystemService("vibrator")).vibrate(250, VIBRATION_ATTRIBUTES);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$25() {
        Debug.stopMethodTracing();
        Log.d("StatusBar", "stopTracing");
        vibrate();
    }

    public void postQSRunnableDismissingKeyguard(Runnable runnable) {
        this.mHandler.post(new StatusBar$$ExternalSyntheticLambda35(this, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postQSRunnableDismissingKeyguard$27(Runnable runnable) {
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        executeRunnableDismissingKeyguard(new StatusBar$$ExternalSyntheticLambda36(this, runnable), (Runnable) null, false, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postQSRunnableDismissingKeyguard$26(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent) {
        postStartActivityDismissingKeyguard(pendingIntent, (ActivityLaunchAnimator.Controller) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postStartActivityDismissingKeyguard$28(PendingIntent pendingIntent, ActivityLaunchAnimator.Controller controller) {
        startPendingIntentDismissingKeyguard(pendingIntent, (Runnable) null, controller);
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent, ActivityLaunchAnimator.Controller controller) {
        this.mHandler.post(new StatusBar$$ExternalSyntheticLambda29(this, pendingIntent, controller));
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i) {
        postStartActivityDismissingKeyguard(intent, i, (ActivityLaunchAnimator.Controller) null);
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i, ActivityLaunchAnimator.Controller controller) {
        this.mHandler.postDelayed(new StatusBar$$ExternalSyntheticLambda31(this, intent, controller), (long) i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postStartActivityDismissingKeyguard$29(Intent intent, ActivityLaunchAnimator.Controller controller) {
        startActivityDismissingKeyguard(intent, true, true, false, (ActivityStarter.Callback) null, 0, controller);
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("bars");
        arrayList.add("clock");
        arrayList.add("operator");
        return arrayList;
    }

    public void onDemoModeStarted() {
        dispatchDemoModeStartedToView(R$id.clock);
        dispatchDemoModeStartedToView(R$id.operator_name);
    }

    public void onDemoModeFinished() {
        dispatchDemoModeFinishedToView(R$id.clock);
        dispatchDemoModeFinishedToView(R$id.operator_name);
        checkBarModes();
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        int i;
        if (str.equals("clock")) {
            dispatchDemoCommandToView(str, bundle, R$id.clock);
        }
        if (str.equals("bars")) {
            String string = bundle.getString("mode");
            if ("opaque".equals(string)) {
                i = 4;
            } else if ("translucent".equals(string)) {
                i = 2;
            } else if ("semi-transparent".equals(string)) {
                i = 1;
            } else if ("transparent".equals(string)) {
                i = 0;
            } else {
                i = "warning".equals(string) ? 5 : -1;
            }
            if (i != -1) {
                if (!(this.mNotificationShadeWindowController == null || this.mNotificationShadeWindowViewController.getBarTransitions() == null)) {
                    this.mNotificationShadeWindowViewController.getBarTransitions().transitionTo(i, true);
                }
                this.mNavigationBarController.transitionTo(this.mDisplayId, i, true);
            }
        }
        if (str.equals("operator")) {
            dispatchDemoCommandToView(str, bundle, R$id.operator_name);
        }
    }

    private void dispatchDemoCommandToView(String str, Bundle bundle, int i) {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            View findViewById = phoneStatusBarView.findViewById(i);
            if (findViewById instanceof DemoModeCommandReceiver) {
                ((DemoModeCommandReceiver) findViewById).dispatchDemoCommand(str, bundle);
            }
        }
    }

    private void dispatchDemoModeStartedToView(int i) {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            View findViewById = phoneStatusBarView.findViewById(i);
            if (findViewById instanceof DemoModeCommandReceiver) {
                ((DemoModeCommandReceiver) findViewById).onDemoModeStarted();
            }
        }
    }

    private void dispatchDemoModeFinishedToView(int i) {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            View findViewById = phoneStatusBarView.findViewById(i);
            if (findViewById instanceof DemoModeCommandReceiver) {
                ((DemoModeCommandReceiver) findViewById).onDemoModeFinished();
            }
        }
    }

    public void showKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(true);
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
        updateIsKeyguard();
        this.mAssistManagerLazy.get().onLockscreenShown();
    }

    public boolean hideKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(false);
        return updateIsKeyguard();
    }

    public void setWakingUpFromAod(boolean z) {
        this.mWakingUpFromAod = z;
    }

    public boolean isFullScreenUserSwitcherState() {
        return this.mState == 3;
    }

    /* access modifiers changed from: package-private */
    public boolean updateIsKeyguard() {
        return updateIsKeyguard(false);
    }

    /* access modifiers changed from: package-private */
    public boolean updateIsKeyguard(boolean z) {
        boolean z2 = true;
        boolean z3 = this.mBiometricUnlockController.getMode() == 1;
        boolean z4 = this.mDozeServiceHost.getDozingRequested() && (!this.mDeviceInteractive || (isGoingToSleep() && (isScreenFullyOff() || this.mIsKeyguard)));
        if ((!this.mStatusBarStateController.isKeyguardRequested() && !z4) || z3) {
            z2 = false;
        }
        if (z4) {
            updatePanelExpansionForKeyguard();
        }
        if (!z2) {
            return hideKeyguardImpl(z);
        }
        if (!this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying() && (!isGoingToSleep() || this.mScreenLifecycle.getScreenState() != 3)) {
            showKeyguardImpl();
        }
        return false;
    }

    public void showKeyguardImpl() {
        this.mIsKeyguard = true;
        if (this.mKeyguardStateController.isLaunchTransitionFadingAway()) {
            this.mNotificationPanelViewController.cancelAnimation();
            onLaunchTransitionFadingEnded();
        }
        this.mHandler.removeMessages(1003);
        UserSwitcherController userSwitcherController = this.mUserSwitcherController;
        if (userSwitcherController != null && userSwitcherController.useFullscreenUserSwitcher()) {
            this.mStatusBarStateController.setState(3);
        } else if (!this.mPulseExpansionHandler.isWakingToShadeLocked()) {
            this.mStatusBarStateController.setState(1);
        }
        if (this.mState == 1) {
            this.mNotificationPanelViewController.resetViews(false);
        }
        updatePanelExpansionForKeyguard();
        EdgeTouchPillController edgeTouchPillController = this.mEdgeTouchPillController;
        if (edgeTouchPillController != null) {
            edgeTouchPillController.updateKeyguardState(this.mIsKeyguard);
        }
    }

    private void updatePanelExpansionForKeyguard() {
        if (this.mState == 1 && this.mBiometricUnlockController.getMode() != 1 && !this.mBouncerShowing) {
            this.mShadeController.instantExpandNotificationsPanel();
        } else if (this.mState == 3) {
            instantCollapseNotificationPanel();
        }
    }

    /* access modifiers changed from: private */
    public void onLaunchTransitionFadingEnded() {
        this.mNotificationPanelViewController.setAlpha(1.0f);
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        releaseGestureWakeLock();
        runLaunchTransitionEndRunnable();
        this.mKeyguardStateController.setLaunchTransitionFadingAway(false);
        this.mPresenter.updateMediaMetaData(true, true);
    }

    public boolean isInLaunchTransition() {
        return this.mNotificationPanelViewController.isLaunchTransitionRunning() || this.mNotificationPanelViewController.isLaunchTransitionFinished();
    }

    public void fadeKeyguardAfterLaunchTransition(Runnable runnable, Runnable runnable2) {
        this.mHandler.removeMessages(1003);
        this.mLaunchTransitionEndRunnable = runnable2;
        StatusBar$$ExternalSyntheticLambda34 statusBar$$ExternalSyntheticLambda34 = new StatusBar$$ExternalSyntheticLambda34(this, runnable);
        if (this.mNotificationPanelViewController.isLaunchTransitionRunning()) {
            this.mNotificationPanelViewController.setLaunchTransitionEndRunnable(statusBar$$ExternalSyntheticLambda34);
        } else {
            statusBar$$ExternalSyntheticLambda34.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fadeKeyguardAfterLaunchTransition$30(Runnable runnable) {
        this.mKeyguardStateController.setLaunchTransitionFadingAway(true);
        if (runnable != null) {
            runnable.run();
        }
        updateScrimController();
        this.mPresenter.updateMediaMetaData(false, true);
        this.mNotificationPanelViewController.setAlpha(1.0f);
        this.mNotificationPanelViewController.fadeOut(100, 300, new StatusBar$$ExternalSyntheticLambda16(this));
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, SystemClock.uptimeMillis(), 120, true);
    }

    public void fadeKeyguardWhilePulsing() {
        this.mKeyguardFadingWhilePulsing = true;
        if (this.mUdfpsController != null) {
            if (USERDEBUG) {
                Log.d("StatusBar", "fadeKeyguardWhilePulsing: hide Notification Panel View");
            }
            this.mRestoreNotificationPanelAlphaIfNeed = true;
            this.mNotificationPanelViewController.setAlpha(0.0f);
            this.mMotoDisplayManager.hide(0);
            hideKeyguard();
            this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
            return;
        }
        this.mNotificationPanelViewController.fadeOut(0, 96, new StatusBar$$ExternalSyntheticLambda21(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fadeKeyguardWhilePulsing$31() {
        hideKeyguard();
        this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
    }

    public void animateKeyguardUnoccluding() {
        this.mNotificationPanelViewController.setExpandedFraction(0.0f);
        if (!this.mBouncerShowing) {
            animateExpandNotificationsPanel();
        }
    }

    public void startLaunchTransitionTimeout() {
        this.mHandler.sendEmptyMessageDelayed(1003, 5000);
    }

    /* access modifiers changed from: private */
    public void onLaunchTransitionTimeout() {
        Log.w("StatusBar", "Launch transition: Timeout!");
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        releaseGestureWakeLock();
        if (!this.mBouncerShowing) {
            this.mNotificationPanelViewController.resetViews(false);
        }
    }

    private void runLaunchTransitionEndRunnable() {
        Runnable runnable = this.mLaunchTransitionEndRunnable;
        if (runnable != null) {
            this.mLaunchTransitionEndRunnable = null;
            runnable.run();
        }
    }

    public boolean hideKeyguardImpl(boolean z) {
        this.mIsKeyguard = false;
        Trace.beginSection("StatusBar#hideKeyguard");
        boolean leaveOpenOnKeyguardHide = this.mStatusBarStateController.leaveOpenOnKeyguardHide();
        int state = this.mStatusBarStateController.getState();
        if (!this.mStatusBarStateController.setState(0, z)) {
            this.mLockscreenUserManager.updatePublicMode();
        }
        if (this.mStatusBarStateController.leaveOpenOnKeyguardHide()) {
            if (!this.mStatusBarStateController.isKeyguardRequested()) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
            }
            long calculateGoingToFullShadeDelay = this.mKeyguardStateController.calculateGoingToFullShadeDelay();
            this.mLockscreenShadeTransitionController.onHideKeyguard(calculateGoingToFullShadeDelay, state);
            this.mNavigationBarController.disableAnimationsDuringHide(this.mDisplayId, calculateGoingToFullShadeDelay);
        } else if (!this.mNotificationPanelViewController.isCollapsing()) {
            instantCollapseNotificationPanel();
        }
        QSPanelController qSPanelController = this.mQSPanelController;
        if (qSPanelController != null) {
            qSPanelController.refreshAllTiles();
        }
        this.mHandler.removeMessages(1003);
        releaseGestureWakeLock();
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        this.mNotificationPanelViewController.cancelAnimation();
        if (this.mUdfpsController == null) {
            this.mNotificationPanelViewController.setAlpha(1.0f);
        } else if (!this.mRestoreNotificationPanelAlphaIfNeed) {
            this.mNotificationPanelViewController.setAlpha(1.0f);
        }
        this.mNotificationPanelViewController.resetViewGroupFade();
        updateDozingState();
        updateScrimController();
        Trace.endSection();
        EdgeTouchPillController edgeTouchPillController = this.mEdgeTouchPillController;
        if (edgeTouchPillController != null) {
            edgeTouchPillController.updateKeyguardState(this.mIsKeyguard);
        }
        return leaveOpenOnKeyguardHide;
    }

    /* access modifiers changed from: private */
    public void releaseGestureWakeLock() {
        if (this.mGestureWakeLock.isHeld()) {
            this.mGestureWakeLock.release();
        }
    }

    public void keyguardGoingAway() {
        this.mKeyguardStateController.notifyKeyguardGoingAway(true);
        this.mCommandQueue.appTransitionPending(this.mDisplayId, true);
    }

    public void setKeyguardFadingAway(long j, long j2, long j3, boolean z) {
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, (j + j3) - 120, 120, true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, j3 > 0);
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, j - 120, 120, true);
        this.mKeyguardStateController.notifyKeyguardFadingAway(j2, j3, z);
    }

    public boolean isKeyguardFadingWhilePulsing() {
        return this.mKeyguardFadingWhilePulsing;
    }

    public void finishKeyguardFadingAway() {
        if (this.mKeyguardFadingWhilePulsing) {
            getNavigationBarView().getRootView().setVisibility(0);
        }
        this.mKeyguardFadingWhilePulsing = false;
        this.mKeyguardStateController.notifyKeyguardDoneFading();
        this.mScrimController.setExpansionAffectsAlpha(true);
    }

    private void updateNotificationPanelAlphaIfNeed() {
        if (this.mRestoreNotificationPanelAlphaIfNeed) {
            this.mNotificationPanelViewController.setAlpha(1.0f);
            this.mRestoreNotificationPanelAlphaIfNeed = false;
        }
    }

    /* access modifiers changed from: protected */
    public void updateTheme() {
        int i;
        if (this.mColorExtractor.getNeutralColors().supportsDarkText()) {
            i = R$style.Theme_SystemUI_LightWallpaper;
        } else {
            i = R$style.Theme_SystemUI;
        }
        if (this.mContext.getThemeResId() != i) {
            this.mContext.setTheme(i);
            this.mConfigurationController.notifyThemeChanged();
        }
    }

    private void updateDozingState() {
        Trace.traceCounter(4096, "dozing", this.mDozing ? 1 : 0);
        Trace.beginSection("StatusBar#updateDozingState");
        boolean z = true;
        this.mNotificationPanelViewController.setDozing(this.mDozing, (!this.mDozing && this.mDozeServiceHost.shouldAnimateWakeup() && !(this.mBiometricUnlockController.getMode() == 1)) || (this.mDozing && this.mDozeServiceHost.shouldAnimateScreenOff() && ((this.mStatusBarKeyguardViewManager.isShowing() && !this.mStatusBarKeyguardViewManager.isOccluded()) || (this.mDozing && this.mDozeParameters.shouldControlUnlockedScreenOff()))), this.mWakeUpTouchLocation);
        updateQsExpansionEnabled();
        StatusBarNotificationPresenter statusBarNotificationPresenter = this.mPresenter;
        if (this.mState == 1) {
            z = false;
        }
        statusBarNotificationPresenter.updateMediaMetaData(false, z);
        Trace.endSection();
    }

    public void userActivity() {
        if (this.mState == 1) {
            this.mKeyguardViewMediatorCallback.userActivity();
        }
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        if (this.mState != 1 || !this.mStatusBarKeyguardViewManager.interceptMediaKey(keyEvent)) {
            return false;
        }
        return true;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && this.mState == 1 && this.mStatusBarKeyguardViewManager.dispatchBackKeyEventPreIme()) {
            return onBackPressed();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean shouldUnlockOnMenuPressed() {
        return this.mDeviceInteractive && this.mState != 0 && this.mStatusBarKeyguardViewManager.shouldDismissOnMenuPressed();
    }

    public boolean onMenuPressed() {
        if (!shouldUnlockOnMenuPressed()) {
            return false;
        }
        this.mShadeController.animateCollapsePanels(2, true);
        return true;
    }

    public void endAffordanceLaunch() {
        releaseGestureWakeLock();
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
    }

    public boolean onBackPressed() {
        boolean z = this.mScrimController.getState() == ScrimState.BOUNCER_SCRIMMED;
        if (this.mStatusBarKeyguardViewManager.onBackPressed(z)) {
            if (z) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
            } else {
                this.mNotificationPanelViewController.expandWithoutQs();
            }
            return true;
        } else if (this.mNotificationPanelViewController.isQsExpanded()) {
            if (this.mNotificationPanelViewController.isQsDetailShowing()) {
                this.mNotificationPanelViewController.closeQsDetail();
            } else {
                this.mNotificationPanelViewController.animateCloseQs(false);
            }
            return true;
        } else if (this.mNotificationPanelViewController.closeUserSwitcherIfOpen()) {
            return true;
        } else {
            int i = this.mState;
            if (i == 1 || i == 2) {
                return false;
            }
            if (this.mNotificationPanelViewController.canPanelBeCollapsed()) {
                this.mShadeController.animateCollapsePanels();
            }
            return true;
        }
    }

    public boolean onSpacePressed() {
        if (!this.mDeviceInteractive || this.mState == 0) {
            return false;
        }
        this.mShadeController.animateCollapsePanels(2, true);
        return true;
    }

    private void showBouncerIfKeyguard() {
        if (this.mKeyguardViewMediator.isHiding()) {
            return;
        }
        if (this.mState == 1 && !this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing()) {
            this.mStatusBarKeyguardViewManager.showGenericBouncer(true);
        } else if (this.mState == 2) {
            this.mStatusBarKeyguardViewManager.showBouncer(true);
        }
    }

    public void showBouncerWithDimissAndCancelIfKeyguard(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable) {
        int i = this.mState;
        if ((i == 1 || i == 2) && !this.mKeyguardViewMediator.isHiding()) {
            this.mStatusBarKeyguardViewManager.dismissWithAction(onDismissAction, runnable, false);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public void instantCollapseNotificationPanel() {
        this.mNotificationPanelViewController.instantCollapse();
        this.mShadeController.runPostCollapseRunnables();
    }

    /* access modifiers changed from: package-private */
    public void collapsePanelOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mShadeController.collapsePanel();
            return;
        }
        Executor mainExecutor = this.mContext.getMainExecutor();
        ShadeController shadeController = this.mShadeController;
        Objects.requireNonNull(shadeController);
        mainExecutor.execute(new StatusBar$$ExternalSyntheticLambda14(shadeController));
    }

    /* access modifiers changed from: package-private */
    public void collapsePanelWithDuration(int i) {
        this.mNotificationPanelViewController.collapseWithDuration(i);
    }

    public void onStatePreChange(int i, int i2) {
        if (this.mVisible && (i2 == 2 || this.mStatusBarStateController.goingToFullShade())) {
            clearNotificationEffects();
        }
        if (i2 == 1) {
            this.mRemoteInputManager.onPanelCollapsed();
            maybeEscalateHeadsUp();
        }
    }

    public void onStateChanged(int i) {
        PhoneStatusBarView phoneStatusBarView;
        boolean z = true;
        boolean z2 = this.mState != i;
        this.mState = i;
        updateReportRejectedTouchVisibility();
        this.mDozeServiceHost.updateDozing();
        updateTheme();
        this.mNavigationBarController.touchAutoDim(this.mDisplayId);
        Trace.beginSection("StatusBar#updateKeyguardState");
        if (this.mState == 1 && (phoneStatusBarView = this.mStatusBarView) != null) {
            phoneStatusBarView.removePendingHideExpandedRunnables();
        }
        updateDozingState();
        checkBarModes();
        updateScrimController();
        StatusBarNotificationPresenter statusBarNotificationPresenter = this.mPresenter;
        if (this.mState == 1) {
            z = false;
        }
        statusBarNotificationPresenter.updateMediaMetaData(false, z);
        updateKeyguardState();
        Trace.endSection();
        this.mCarrierLabelUpdateMonitor.updateBarState(z2);
    }

    public void onDozeAmountChanged(float f, float f2) {
        if (this.mFeatureFlags.useNewLockscreenAnimations() && !(this.mLightRevealScrim.getRevealEffect() instanceof CircleReveal)) {
            this.mLightRevealScrim.setRevealAmount(1.0f - f);
        }
    }

    public void onDozingChanged(boolean z) {
        Trace.beginSection("StatusBar#updateDozing");
        this.mDozing = z;
        this.mNotificationPanelViewController.resetViews(this.mDozeServiceHost.getDozingRequested() && this.mDozeParameters.shouldControlScreenOff());
        updateQsExpansionEnabled();
        this.mKeyguardViewMediator.setDozing(this.mDozing);
        this.mNotificationsController.requestNotificationUpdate("onDozingChanged");
        updateDozingState();
        this.mDozeServiceHost.updateDozing();
        updateScrimController();
        updateReportRejectedTouchVisibility();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void updateRevealEffect(boolean z) {
        if (this.mLightRevealScrim != null) {
            if ((z && this.mWakefulnessLifecycle.getLastWakeReason() == 1) || (!z && this.mWakefulnessLifecycle.getLastSleepReason() == 4)) {
                this.mLightRevealScrim.setRevealEffect(this.mPowerButtonReveal);
            } else if (!z || !(this.mLightRevealScrim.getRevealEffect() instanceof CircleReveal)) {
                this.mLightRevealScrim.setRevealEffect(LiftReveal.INSTANCE);
            }
        }
    }

    public LightRevealScrim getLightRevealScrim() {
        return this.mLightRevealScrim;
    }

    private void updateKeyguardState() {
        this.mKeyguardStateController.notifyKeyguardState(this.mStatusBarKeyguardViewManager.isShowing(), this.mStatusBarKeyguardViewManager.isOccluded());
    }

    public void onTrackingStarted() {
        this.mShadeController.runPostCollapseRunnables();
    }

    public void onClosingFinished() {
        this.mShadeController.runPostCollapseRunnables();
        if (!this.mPresenter.isPresenterFullyCollapsed()) {
            this.mNotificationShadeWindowController.setNotificationShadeFocusable(true);
        }
    }

    public void onUnlockHintStarted() {
        this.mFalsingCollector.onUnlockHintStarted();
        this.mKeyguardIndicationController.showActionToUnlock();
    }

    public void onHintFinished() {
        this.mKeyguardIndicationController.hideTransientIndicationDelayed(1200);
    }

    public void onCameraHintStarted() {
        this.mFalsingCollector.onCameraHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.camera_hint);
    }

    public void onVoiceAssistHintStarted() {
        this.mFalsingCollector.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.voice_hint);
    }

    public void onPhoneHintStarted() {
        this.mFalsingCollector.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.phone_hint);
    }

    public void onTrackingStopped(boolean z) {
        int i = this.mState;
        if ((i == 1 || i == 2) && !z && !this.mKeyguardStateController.canDismissLockScreen()) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
    }

    public NavigationBarView getNavigationBarView() {
        return this.mNavigationBarController.getNavigationBarView(this.mDisplayId);
    }

    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mNotificationPanelViewController.getKeyguardBottomAreaView();
    }

    public void setBouncerShowing(boolean z) {
        this.mBouncerShowing = z;
        this.mKeyguardBypassController.setBouncerShowing(z);
        this.mPulseExpansionHandler.setBouncerShowing(z);
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            phoneStatusBarView.setBouncerShowing(z);
        }
        updateHideIconsForBouncer(true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        updateScrimController();
        if (!this.mBouncerShowing) {
            updatePanelExpansionForKeyguard();
        }
    }

    public void collapseShade() {
        if (this.mNotificationPanelViewController.isTracking()) {
            this.mNotificationShadeWindowViewController.cancelCurrentTouch();
        }
        if (this.mPanelExpanded && this.mState == 0) {
            this.mShadeController.animateCollapsePanels();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateNotificationPanelTouchState() {
        boolean z = false;
        boolean z2 = isGoingToSleep() && !this.mDozeParameters.shouldControlScreenOff();
        if ((!this.mDeviceInteractive && !this.mDozeServiceHost.isPulsing()) || z2) {
            z = true;
        }
        this.mNotificationPanelViewController.setTouchAndAnimationDisabled(z);
        this.mNotificationIconAreaController.setAnimationsEnabled(!z);
    }

    private void vibrateForCameraGesture() {
        this.mVibrator.vibrate(this.mCameraLaunchGestureVibrationEffect, VIBRATION_ATTRIBUTES);
    }

    private static VibrationEffect getCameraGestureVibrationEffect(Vibrator vibrator, Resources resources) {
        if (vibrator.areAllPrimitivesSupported(new int[]{4, 1})) {
            return VibrationEffect.startComposition().addPrimitive(4).addPrimitive(1, 1.0f, 50).compose();
        }
        if (vibrator.hasAmplitudeControl()) {
            return VibrationEffect.createWaveform(CAMERA_LAUNCH_GESTURE_VIBRATION_TIMINGS, CAMERA_LAUNCH_GESTURE_VIBRATION_AMPLITUDES, -1);
        }
        int[] intArray = resources.getIntArray(R$array.config_cameraLaunchGestureVibePattern);
        long[] jArr = new long[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            jArr[i] = (long) intArray[i];
        }
        return VibrationEffect.createWaveform(jArr, -1);
    }

    public boolean isScreenFullyOff() {
        return this.mScreenLifecycle.getScreenState() == 0;
    }

    public void showScreenPinningRequest(int i) {
        if (!this.mKeyguardStateController.isShowing()) {
            showScreenPinningRequest(i, true);
        }
    }

    public void showScreenPinningRequest(int i, boolean z) {
        this.mScreenPinningRequest.showPrompt(i, z);
    }

    public void appTransitionCancelled(int i) {
        if (i == this.mDisplayId) {
            this.mSplitScreenOptional.ifPresent(StatusBar$$ExternalSyntheticLambda44.INSTANCE);
        }
    }

    public void appTransitionFinished(int i) {
        if (i == this.mDisplayId) {
            this.mSplitScreenOptional.ifPresent(StatusBar$$ExternalSyntheticLambda43.INSTANCE);
        }
    }

    public void onCameraLaunchGestureDetectedForAutoQuickCapture(int i, boolean z) {
        if (USERDEBUG) {
            Log.d("StatusBar", "Calling onCameraLaunchGestureDetected with Automatic cam selection. useFrontCam: " + z);
        }
        onCameraLaunchGestureDetected(i, z);
    }

    private Intent getSecureBroadcastIntent(int i, boolean z) {
        Intent cloneFilter = CameraIntents.getSecureCameraIntent().cloneFilter();
        cloneFilter.setAction("motorola.camera.intent.action.STILL_IMAGE_PREVIEW_SECURE");
        cloneFilter.setFlags(268435456);
        cloneFilter.setPackage(getKeyguardBottomAreaView().getMotoCameraAppPackageName(this.mContext));
        if (z) {
            cloneFilter.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        }
        String cameraSourceString = getCameraSourceString(i);
        if (cameraSourceString != null) {
            cloneFilter.putExtra("com.android.systemui.camera_launch_source", cameraSourceString);
        }
        if (getKeyguardBottomAreaView().resolveCameraIntent() == null) {
            return null;
        }
        return cloneFilter;
    }

    public void onCameraLaunchGestureDetected(int i) {
        onCameraLaunchGestureDetected(i, this.mQuickLaunchUseFrontCamera);
    }

    private void onCameraLaunchGestureDetected(int i, boolean z) {
        Intent secureBroadcastIntent;
        int i2 = i;
        boolean z2 = z;
        boolean z3 = USERDEBUG;
        if (z3) {
            Log.d("StatusBar", "onCameraLaunchGestureDetected source: " + i2 + " useFrontCam = " + z2);
        }
        this.mLastCameraLaunchSource = i2;
        if (isGoingToSleep()) {
            if (z3) {
                Slog.d("StatusBar", "Finish going to sleep before launching camera");
            }
            this.mLaunchCameraOnFinishedGoingToSleep = true;
        } else if (this.mNotificationPanelViewController.canCameraGestureBeLaunched()) {
            if (!this.mDeviceInteractive && !isFolioClosedAndDozing()) {
                this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE");
                if (this.mUseMotoFaceUnlock) {
                    this.mKeyguardUpdateMonitor.setCameraGestureTriggered(true);
                }
            }
            if (i2 == 0) {
                String preloadedCamera = getKeyguardBottomAreaView().getPreloadedCamera(this.mContext);
                String motoCameraAppPackageName = getKeyguardBottomAreaView().getMotoCameraAppPackageName(this.mContext);
                if (!(preloadedCamera == null || motoCameraAppPackageName == null || !preloadedCamera.equals(motoCameraAppPackageName))) {
                    boolean isForegroundApp = this.mNotificationPanelViewController.isForegroundApp(preloadedCamera);
                    boolean isShowing = this.mStatusBarKeyguardViewManager.isShowing();
                    boolean isOccluded = this.mStatusBarKeyguardViewManager.isOccluded();
                    if (isForegroundApp && ((!isShowing || isOccluded) && (secureBroadcastIntent = getSecureBroadcastIntent(i, z)) != null)) {
                        if (z3) {
                            Log.d("StatusBar", preloadedCamera + " is on foreground");
                        }
                        this.mContext.sendOrderedBroadcastAsUser(secureBroadcastIntent, UserHandle.CURRENT, (String) null, (BroadcastReceiver) null, (Handler) null, -1, (String) null, (Bundle) null);
                        return;
                    }
                }
                if (this.mUseMotoFaceUnlock) {
                    Intent intent = new Intent("com.motorola.faceunlock.camera_gesture");
                    intent.addFlags(16777216);
                    this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT, "com.motorola.faceunlock.FACE_UNLOCK");
                }
            }
            Intent checkConvertCameraIntentForMotoCamera = getKeyguardBottomAreaView().checkConvertCameraIntentForMotoCamera(CameraIntents.getInsecureCameraIntent().cloneFilter(), this.mContext);
            checkConvertCameraIntentForMotoCamera.setFlags(268435456);
            if (getKeyguardBottomAreaView().resolveCameraIntent() != null) {
                if (i2 == 1) {
                    Log.v("StatusBar", "Camera launch");
                    this.mKeyguardUpdateMonitor.onCameraLaunched();
                }
                if (z2) {
                    checkConvertCameraIntentForMotoCamera.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                }
                String cameraSourceString = getCameraSourceString(i);
                if (cameraSourceString != null) {
                    checkConvertCameraIntentForMotoCamera.putExtra("com.android.systemui.camera_launch_source", cameraSourceString);
                }
                vibrateForCameraGesture();
                if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                    if (isFolioClosedAndDozing()) {
                        if (z3) {
                            Log.d("StatusBar", "Keyguard is not shown, launching camera in folio closed");
                        }
                        this.mNotificationPanelViewController.launchCameraOnFolioClosed(i2, z2);
                        return;
                    }
                    startActivityDismissingKeyguard(checkConvertCameraIntentForMotoCamera, false, true, true, (ActivityStarter.Callback) null, 0, (ActivityLaunchAnimator.Controller) null);
                } else if (isFolioClosedAndDozing()) {
                    if (z3) {
                        Log.d("StatusBar", "Keyguard is shown, launching camera in folio closed");
                    }
                    if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                        this.mStatusBarKeyguardViewManager.reset(true);
                    }
                    this.mNotificationPanelViewController.launchCameraOnFolioClosed(i2, z2);
                } else {
                    if (!this.mDeviceInteractive) {
                        this.mGestureWakeLock.acquire(6000);
                    }
                    if (isWakingUpOrAwake()) {
                        if (z3) {
                            Slog.d("StatusBar", "Launching camera");
                        }
                        if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                            this.mStatusBarKeyguardViewManager.reset(true);
                        }
                        this.mNotificationPanelViewController.launchCamera(this.mDeviceInteractive, i2, z2, false);
                        updateScrimController();
                        return;
                    }
                    if (z3) {
                        Slog.d("StatusBar", "Deferring until screen turns on");
                    }
                    this.mLaunchCameraWhenFinishedWaking = true;
                    this.mScrimController.setLaunchCameraWhenFinishedWaking(true);
                    this.mQuickLaunchUseFrontCamWhenFinishedWaking = z2;
                }
            }
        } else if (z3) {
            Slog.d("StatusBar", "Can't launch camera right now");
        }
    }

    public void onEmergencyActionLaunchGestureDetected() {
        Intent emergencyActionIntent = getEmergencyActionIntent();
        if (emergencyActionIntent == null) {
            Log.wtf("StatusBar", "Couldn't find an app to process the emergency intent.");
        } else if (isGoingToSleep()) {
            this.mLaunchEmergencyActionOnFinishedGoingToSleep = true;
        } else {
            if (!this.mDeviceInteractive) {
                this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:EMERGENCY_GESTURE");
            }
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                startActivityDismissingKeyguard(emergencyActionIntent, false, true, true, (ActivityStarter.Callback) null, 0, (ActivityLaunchAnimator.Controller) null);
                return;
            }
            if (!this.mDeviceInteractive) {
                this.mGestureWakeLock.acquire(6000);
            }
            if (isWakingUpOrAwake()) {
                if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    this.mStatusBarKeyguardViewManager.reset(true);
                }
                this.mContext.startActivityAsUser(emergencyActionIntent, UserHandle.CURRENT);
                return;
            }
            this.mLaunchEmergencyActionWhenFinishedWaking = true;
        }
    }

    /* access modifiers changed from: private */
    public Intent getEmergencyActionIntent() {
        Intent intent = new Intent("com.android.systemui.action.LAUNCH_EMERGENCY");
        ResolveInfo topEmergencySosInfo = getTopEmergencySosInfo(this.mContext.getPackageManager().queryIntentActivities(intent, 1048576));
        if (topEmergencySosInfo == null) {
            Log.wtf("StatusBar", "Couldn't find an app to process the emergency intent.");
            return null;
        }
        ActivityInfo activityInfo = topEmergencySosInfo.activityInfo;
        intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
        intent.setFlags(268435456);
        return intent;
    }

    private ResolveInfo getTopEmergencySosInfo(List<ResolveInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String string = this.mContext.getString(R$string.config_preferredEmergencySosPackage);
        if (TextUtils.isEmpty(string)) {
            return list.get(0);
        }
        for (ResolveInfo next : list) {
            if (TextUtils.equals(next.activityInfo.packageName, string)) {
                return next;
            }
        }
        return list.get(0);
    }

    /* access modifiers changed from: package-private */
    public boolean isCameraAllowedByAdmin() {
        if (this.mDevicePolicyManager.getCameraDisabled((ComponentName) null, this.mLockscreenUserManager.getCurrentUserId())) {
            return false;
        }
        if (this.mStatusBarKeyguardViewManager != null && (!isKeyguardShowing() || !isKeyguardSecure())) {
            return true;
        }
        if ((this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, this.mLockscreenUserManager.getCurrentUserId()) & 2) == 0) {
            return true;
        }
        return false;
    }

    private boolean isGoingToSleep() {
        return this.mWakefulnessLifecycle.getWakefulness() == 3;
    }

    private boolean isWakingUpOrAwake() {
        if (this.mWakefulnessLifecycle.getWakefulness() == 2 || this.mWakefulnessLifecycle.getWakefulness() == 1) {
            return true;
        }
        return false;
    }

    public void notifyBiometricAuthModeChanged() {
        this.mDozeServiceHost.updateDozing();
        updateScrimController();
    }

    @VisibleForTesting
    public void updateScrimController() {
        Trace.beginSection("StatusBar#updateScrimController");
        boolean z = this.mBiometricUnlockController.isBiometricUnlock() || this.mKeyguardStateController.isKeyguardFadingAway();
        this.mScrimController.setExpansionAffectsAlpha(true ^ this.mBiometricUnlockController.isBiometricUnlock());
        boolean isLaunchingAffordanceWithPreview = this.mNotificationPanelViewController.isLaunchingAffordanceWithPreview();
        this.mScrimController.setLaunchingAffordanceWithPreview(isLaunchingAffordanceWithPreview);
        if (this.mStatusBarKeyguardViewManager.isShowingAlternateAuth()) {
            this.mScrimController.transitionTo(ScrimState.AUTH_SCRIMMED);
        } else if (this.mBouncerShowing) {
            this.mScrimController.transitionTo(this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming() ? ScrimState.BOUNCER_SCRIMMED : ScrimState.BOUNCER);
        } else if (isInLaunchTransition() || this.mLaunchCameraWhenFinishedWaking || isLaunchingAffordanceWithPreview) {
            this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
        } else if (this.mBrightnessMirrorVisible) {
            this.mScrimController.transitionTo(ScrimState.BRIGHTNESS_MIRROR);
        } else if (this.mState == 2) {
            this.mScrimController.transitionTo(ScrimState.SHADE_LOCKED);
        } else if (this.mDozeServiceHost.isPulsing()) {
            this.mScrimController.transitionTo(ScrimState.PULSING, this.mDozeScrimController.getScrimCallback());
        } else if (this.mDozeServiceHost.hasPendingScreenOffCallback()) {
            this.mScrimController.transitionTo(ScrimState.OFF, new ScrimController.Callback() {
                public void onFinished() {
                    StatusBar.this.mDozeServiceHost.executePendingScreenOffCallback();
                }
            });
        } else if (this.mDozing && !z) {
            this.mScrimController.transitionTo(ScrimState.AOD);
        } else if (this.mIsKeyguard && !z) {
            this.mScrimController.transitionTo(ScrimState.KEYGUARD);
        } else if (!this.mBubblesOptional.isPresent() || !this.mBubblesOptional.get().isStackExpanded()) {
            this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
        } else {
            this.mScrimController.transitionTo(ScrimState.BUBBLE_EXPANDED, this.mUnlockScrimCallback);
        }
        updateLightRevealScrimVisibility();
        Trace.endSection();
    }

    public boolean isKeyguardShowing() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            return statusBarKeyguardViewManager.isShowing();
        }
        Slog.i("StatusBar", "isKeyguardShowing() called before startKeyguard(), returning true");
        return true;
    }

    public boolean shouldIgnoreTouch() {
        return (this.mStatusBarStateController.isDozing() && this.mDozeServiceHost.getIgnoreTouchWhilePulsing()) || this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying();
    }

    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }

    public void onBootCompleted() {
        this.mNetworkController.playEriSoundAfterBoot();
    }

    public void setNotificationSnoozed(StatusBarNotification statusBarNotification, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        this.mNotificationsController.setNotificationSnoozed(statusBarNotification, snoozeOption);
    }

    public void toggleSplitScreen() {
        toggleSplitScreenMode(-1, -1);
    }

    public void awakenDreams() {
        this.mUiBgExecutor.execute(new StatusBar$$ExternalSyntheticLambda20(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$awakenDreams$34() {
        try {
            this.mDreamManager.awaken();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void preloadRecentApps() {
        this.mHandler.removeMessages(1022);
        this.mHandler.sendEmptyMessage(1022);
    }

    public void cancelPreloadRecentApps() {
        this.mHandler.removeMessages(1023);
        this.mHandler.sendEmptyMessage(1023);
    }

    public void dismissKeyboardShortcutsMenu() {
        this.mHandler.removeMessages(1027);
        this.mHandler.sendEmptyMessage(1027);
    }

    public void toggleKeyboardShortcutsMenu(int i) {
        this.mHandler.removeMessages(1026);
        this.mHandler.obtainMessage(1026, i, 0).sendToTarget();
    }

    public void setTopAppHidesStatusBar(boolean z) {
        this.mTopHidesStatusBar = z;
        if (!z && this.mWereIconsJustHidden) {
            this.mWereIconsJustHidden = false;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        }
        updateHideIconsForBouncer(true);
    }

    private boolean triggerMediaControlIfNeed(int i) {
        int i2;
        boolean z = MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), "stylus_report_play_pause", 1, -2) == 1;
        if (this.DEBUG_STYLUS) {
            Log.d("StatusBar", "triggerMediaControlIfNeed: mediaControlEnabled = " + z);
        }
        if (!z) {
            return false;
        }
        MediaSessionManager mediaSessionManager = (MediaSessionManager) this.mContext.getSystemService(MediaSessionManager.class);
        if (mediaSessionManager.getMediaKeyEventSession() == null) {
            if (this.DEBUG_STYLUS) {
                Log.d("StatusBar", "No activity media session");
            }
            return false;
        }
        if (i == 10086) {
            i2 = 85;
        } else if (i != 10087) {
            return false;
        } else {
            i2 = 87;
        }
        if (this.DEBUG_STYLUS) {
            Log.d("StatusBar", "Send key event: keycode = " + i2);
        }
        KeyEvent keyEvent = new KeyEvent(0, i2);
        KeyEvent keyEvent2 = new KeyEvent(1, i2);
        try {
            mediaSessionManager.dispatchMediaKeyEvent(keyEvent, false);
            mediaSessionManager.dispatchMediaKeyEvent(keyEvent2, false);
        } catch (Throwable th) {
            if (this.DEBUG_STYLUS) {
                Log.d("StatusBar", "Send key event failed.", th);
            }
        }
        return true;
    }

    public void onStylusButtonEvent(int i) {
        this.mUiBgExecutor.execute(new StatusBar$$ExternalSyntheticLambda27(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStylusButtonEvent$35(int i) {
        String str;
        if (triggerMediaControlIfNeed(i) || i != 10086) {
            return;
        }
        if (!isScreenFullyOff() && this.mScreenLifecycle.getScreenState() != 3 && isWakingUpOrAwake()) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.mLastStylusClickTime > 1500) {
                this.mLastStylusClickTime = currentTimeMillis;
                try {
                    str = MotorolaSettings.Secure.getStringForUser(this.mContext.getContentResolver(), "stylus_single_click_action", -2);
                } catch (Exception e) {
                    if (this.DEBUG_STYLUS) {
                        Log.d("StatusBar", "onClickStylusButton: get config failed", e);
                    }
                    str = "";
                }
                if (this.DEBUG_STYLUS) {
                    Log.d("StatusBar", "onClickStylusButton: config = " + str);
                }
                if (str != null && str.length() != 0) {
                    try {
                        JSONObject jSONObject = new JSONObject(str);
                        String string = jSONObject.getString("type");
                        String string2 = jSONObject.getString("value");
                        if (this.DEBUG_STYLUS) {
                            Log.d("StatusBar", "onClickStylusButton: type, value = " + string + ", " + string2);
                        }
                        Intent intent = new Intent();
                        char c = 65535;
                        int hashCode = string.hashCode();
                        if (hashCode != -1422950858) {
                            if (hashCode == -1399907075) {
                                if (string.equals("component")) {
                                    c = 0;
                                }
                            }
                        } else if (string.equals("action")) {
                            c = 1;
                        }
                        if (c == 0) {
                            intent.setComponent(ComponentName.unflattenFromString(string2));
                            if (this.DEBUG_STYLUS) {
                                Log.d("StatusBar", "onClickStylusButton: start activity -> " + intent.toString());
                            }
                            startActivity(intent, true);
                        } else if (c == 1) {
                            if (this.DEBUG_STYLUS) {
                                Log.d("StatusBar", "onClickStylusButton: handler action " + string2);
                            }
                            if ("com.motorola.systemui.action.TAKE_SCREENSHOT".equals(string2)) {
                                if (this.DEBUG_STYLUS) {
                                    Log.d("StatusBar", "onClickStylusButton: take screenshot");
                                }
                                this.mScreenshotHelper.takeScreenshot(1, true, true, 0, this.mMainThreadHandler, (Consumer) null);
                            } else if ("com.motorola.systemui.action.TAKE_SCREENSHOT_AND_EDIT".equals(string2)) {
                                if (this.DEBUG_STYLUS) {
                                    Log.d("StatusBar", "onClickStylusButton: take screenshot and edit");
                                }
                                this.mScreenshotHelper.takeScreenshot(1, true, true, this.mMainThreadHandler, (Consumer) null, 3);
                            } else {
                                intent.setAction(string2);
                                if (this.DEBUG_STYLUS) {
                                    Log.d("StatusBar", "onClickStylusButton: send broadcast -> " + intent.toString());
                                }
                                this.mContext.sendBroadcastAsUser(intent, new UserHandle(-2));
                            }
                        } else if (this.DEBUG_STYLUS) {
                            Log.d("StatusBar", "onClickStylusButton: unknow action type -> " + string);
                        }
                    } catch (Exception e2) {
                        Log.d("StatusBar", "onClickStylusButton: can not handle config -> " + str, e2);
                    }
                }
            } else if (this.DEBUG_STYLUS) {
                Log.d("StatusBar", "onClickStylusButton: ignore event because it's close to last event");
            }
        } else if (this.DEBUG_STYLUS) {
            Log.d("StatusBar", "onClickStylusButton: ignore event because screen is off or dozing");
        }
    }

    /* access modifiers changed from: protected */
    public void toggleKeyboardShortcuts(int i) {
        KeyboardShortcuts.toggle(this.mContext, i);
    }

    /* access modifiers changed from: protected */
    public void dismissKeyboardShortcuts() {
        KeyboardShortcuts.dismiss();
    }

    private void executeActionDismissingKeyguard(final Runnable runnable, boolean z, final boolean z2, final boolean z3) {
        if (this.mDeviceProvisionedController.isDeviceProvisioned()) {
            dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() {
                public boolean onDismiss() {
                    new Thread(new StatusBar$23$$ExternalSyntheticLambda0(runnable)).start();
                    return z2 ? StatusBar.this.mShadeController.collapsePanel() : z3;
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onDismiss$0(Runnable runnable) {
                    try {
                        ActivityManager.getService().resumeAppSwitches();
                    } catch (RemoteException unused) {
                    }
                    runnable.run();
                }

                public boolean willRunAnimationOnKeyguard() {
                    return z3;
                }
            }, z);
        }
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent) {
        startPendingIntentDismissingKeyguard(pendingIntent, (Runnable) null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable) {
        startPendingIntentDismissingKeyguard(pendingIntent, runnable, (ActivityLaunchAnimator.Controller) null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, View view) {
        startPendingIntentDismissingKeyguard(pendingIntent, runnable, (ActivityLaunchAnimator.Controller) view instanceof ExpandableNotificationRow ? this.mNotificationAnimationProvider.getAnimatorController((ExpandableNotificationRow) view) : null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, ActivityLaunchAnimator.Controller controller) {
        boolean z = false;
        boolean z2 = pendingIntent.isActivity() && this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
        if (!z2 && controller != null && shouldAnimateLaunch(pendingIntent.isActivity())) {
            z = true;
        }
        boolean z3 = !z;
        executeActionDismissingKeyguard(new StatusBar$$ExternalSyntheticLambda33(this, controller, pendingIntent, z, z3, runnable), z2, z3, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startPendingIntentDismissingKeyguard$37(ActivityLaunchAnimator.Controller controller, PendingIntent pendingIntent, boolean z, boolean z2, Runnable runnable) {
        StatusBarLaunchAnimatorController statusBarLaunchAnimatorController;
        if (controller != null) {
            try {
                statusBarLaunchAnimatorController = new StatusBarLaunchAnimatorController(controller, this, pendingIntent.isActivity());
            } catch (PendingIntent.CanceledException e) {
                Log.w("StatusBar", "Sending intent failed: " + e);
                if (!z2) {
                    collapsePanelOnMainThread();
                }
            }
        } else {
            statusBarLaunchAnimatorController = null;
        }
        this.mActivityLaunchAnimator.startPendingIntentWithAnimation(statusBarLaunchAnimatorController, z, pendingIntent.getCreatorPackage(), new StatusBar$$ExternalSyntheticLambda2(this, pendingIntent));
        if (pendingIntent.isActivity()) {
            this.mAssistManagerLazy.get().hideAssist();
        }
        if (runnable != null) {
            postOnUiThread(runnable);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$startPendingIntentDismissingKeyguard$36(PendingIntent pendingIntent, RemoteAnimationAdapter remoteAnimationAdapter) throws PendingIntent.CanceledException {
        return pendingIntent.sendAndReturnResult((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, getActivityOptions(this.mDisplayId, remoteAnimationAdapter));
    }

    private void postOnUiThread(Runnable runnable) {
        this.mMainThreadHandler.post(runnable);
    }

    public static Bundle getActivityOptions(int i, RemoteAnimationAdapter remoteAnimationAdapter) {
        ActivityOptions defaultActivityOptions = getDefaultActivityOptions(remoteAnimationAdapter);
        defaultActivityOptions.setLaunchDisplayId(i);
        defaultActivityOptions.setCallerDisplayId(i);
        return defaultActivityOptions.toBundle();
    }

    public static Bundle getActivityOptions(int i, RemoteAnimationAdapter remoteAnimationAdapter, boolean z, long j) {
        ActivityOptions defaultActivityOptions = getDefaultActivityOptions(remoteAnimationAdapter);
        defaultActivityOptions.setSourceInfo(z ? 3 : 2, j);
        defaultActivityOptions.setLaunchDisplayId(i);
        defaultActivityOptions.setCallerDisplayId(i);
        return defaultActivityOptions.toBundle();
    }

    public static ActivityOptions getDefaultActivityOptions(RemoteAnimationAdapter remoteAnimationAdapter) {
        if (remoteAnimationAdapter != null) {
            return ActivityOptions.makeRemoteAnimation(remoteAnimationAdapter);
        }
        return ActivityOptions.makeBasic();
    }

    /* access modifiers changed from: package-private */
    public void visibilityChanged(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            if (!z) {
                this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
            }
        }
        updateVisibleToUser();
    }

    /* access modifiers changed from: protected */
    public void updateVisibleToUser() {
        boolean z = this.mVisibleToUser;
        boolean z2 = this.mVisible && this.mDeviceInteractive;
        this.mVisibleToUser = z2;
        if (z != z2) {
            handleVisibleToUserChanged(z2);
        }
    }

    public void clearNotificationEffects() {
        try {
            this.mBarService.clearNotificationEffects();
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void notifyHeadsUpGoingToSleep() {
        maybeEscalateHeadsUp();
    }

    public boolean isBouncerShowing() {
        return this.mBouncerShowing;
    }

    public boolean isBouncerShowingScrimmed() {
        return isBouncerShowing() && this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming();
    }

    public void onBouncerPreHideAnimation() {
        this.mNotificationPanelViewController.onBouncerPreHideAnimation();
    }

    public static PackageManager getPackageManagerForUser(Context context, int i) {
        if (i >= 0) {
            try {
                context = context.createPackageContextAsUser(context.getPackageName(), 4, new UserHandle(i));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return context.getPackageManager();
    }

    public boolean isKeyguardSecure() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            return statusBarKeyguardViewManager.isSecure();
        }
        Slog.w("StatusBar", "isKeyguardSecure() called before startKeyguard(), returning false", new Throwable());
        return false;
    }

    public void showAssistDisclosure() {
        this.mAssistManagerLazy.get().showDisclosure();
    }

    public NotificationPanelViewController getPanelController() {
        return this.mNotificationPanelViewController;
    }

    public void startAssist(Bundle bundle) {
        this.mAssistManagerLazy.get().startAssist(bundle);
    }

    public NotificationGutsManager getGutsManager() {
        return this.mGutsManager;
    }

    /* access modifiers changed from: private */
    public boolean isTransientShown() {
        return this.mTransientShown;
    }

    public void suppressAmbientDisplay(boolean z) {
        this.mDozeServiceHost.setDozeSuppressed(z);
    }

    public int getBarState() {
        return this.mState;
    }

    public void addExpansionChangedListener(ExpansionChangedListener expansionChangedListener) {
        this.mExpansionChangedListeners.add(expansionChangedListener);
    }

    public void removeExpansionChangedListener(ExpansionChangedListener expansionChangedListener) {
        this.mExpansionChangedListeners.remove(expansionChangedListener);
    }

    /* access modifiers changed from: private */
    public void updateLightRevealScrimVisibility() {
        LightRevealScrim lightRevealScrim = this.mLightRevealScrim;
        if (lightRevealScrim != null) {
            lightRevealScrim.setAlpha(this.mScrimController.getState().getMaxLightRevealScrimAlpha());
            if (!this.mFeatureFlags.useNewLockscreenAnimations() || (!this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.isQuickPickupEnabled())) {
                this.mLightRevealScrim.setVisibility(8);
            } else {
                this.mLightRevealScrim.setVisibility(0);
            }
        }
    }

    public void handleDozeUnlock(IRemoteCallback iRemoteCallback, boolean z, boolean z2) {
        if (Build.IS_DEBUGGABLE) {
            Log.d("StatusBar", "Handle doze unlock. callback: " + iRemoteCallback + ", dismiss: " + z + ", collapse: " + z2);
        }
        if (this.mStatusBarKeyguardViewManager != null && this.mKeyguardViewMediatorCallback.isExternalEnabled()) {
            if (isKeyguardSecure()) {
                this.mStatusBarKeyguardViewManager.requestUnlock(iRemoteCallback, z, z2);
            } else {
                this.mStatusBarKeyguardViewManager.showBouncer(true);
            }
        }
    }

    public boolean isForceShowBouncer() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            return statusBarKeyguardViewManager.isForceShowBouncer();
        }
        return false;
    }

    private boolean startPendingIntent(PendingIntent pendingIntent, Intent intent) {
        try {
            Intent intent2 = intent;
            this.mContext.startIntentSender(pendingIntent.getIntentSender(), intent2, 0, 0, 0, ActivityOptions.makeBasic().toBundle());
            return true;
        } catch (IntentSender.SendIntentException e) {
            Log.e("StatusBar", "Cannot send pending intent: ", e);
            return false;
        } catch (Exception e2) {
            Log.e("StatusBar", "Cannot send pending intent due to unknown exception: ", e2);
            return false;
        }
    }

    public void triggerNotificationClickAndRequestUnlockInternal(String str, PendingIntent pendingIntent, Intent intent) {
        if (TextUtils.isEmpty(str) && pendingIntent == null) {
            Log.e("StatusBar", "triggerNotificationClickAndRequestUnlockInternal not key & pi");
        } else if (TextUtils.isEmpty(str) || pendingIntent != null) {
            try {
                ActivityManager.getService().resumeAppSwitches();
            } catch (RemoteException unused) {
            }
            ((ActivityStarter) Dependency.get(ActivityStarter.class)).dismissKeyguardThenExecute(new StatusBar$$ExternalSyntheticLambda5(this, str, pendingIntent, intent), (Runnable) null, true);
        } else {
            NotificationEntry entry = getEntry(str);
            if (entry != null) {
                this.mNotificationActivityStarter.onNotificationClicked(entry.getSbn(), entry.getRow());
                return;
            }
            Log.w("StatusBar", "triggerNotificationClickAndRequestUnlockInternal get entry fail: " + str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$triggerNotificationClickAndRequestUnlockInternal$39(String str, PendingIntent pendingIntent, Intent intent) {
        StatusBar$$ExternalSyntheticLambda37 statusBar$$ExternalSyntheticLambda37 = new StatusBar$$ExternalSyntheticLambda37(this, str, pendingIntent, intent);
        if (!this.mStatusBarKeyguardViewManager.isShowing() || !this.mStatusBarKeyguardViewManager.isOccluded()) {
            if (Build.IS_DEBUGGABLE) {
                Log.d("StatusBar", "triggerNotificationClickAndRequestUnlockInternal keyguard is goned");
            }
            this.mHandler.post(statusBar$$ExternalSyntheticLambda37);
        } else {
            if (Build.IS_DEBUGGABLE) {
                Log.d("StatusBar", "triggerNotificationClickAndRequestUnlockInternal keyguard is showing");
            }
            this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable(statusBar$$ExternalSyntheticLambda37);
        }
        return this.mShadeController.closeShadeIfOpen();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerNotificationClickAndRequestUnlockInternal$38(String str, PendingIntent pendingIntent, Intent intent) {
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
        if (!TextUtils.isEmpty(str)) {
            NotificationEntry entry = getEntry(str);
            if (entry != null) {
                this.mNotificationActivityStarter.onNotificationClicked(entry.getSbn(), entry.getRow());
            } else {
                Log.w("StatusBar", "triggerNotificationClickAndRequestUnlockInternal get entry fail: " + str);
            }
        }
        if (pendingIntent != null) {
            startPendingIntent(pendingIntent, intent);
        }
    }

    private NotificationEntry getEntry(String str) {
        for (NotificationEntry next : this.mEntryManager.getVisibleNotifications()) {
            if (str.equals(next.getKey())) {
                return next;
            }
        }
        return null;
    }

    public CliStatusBar getCliStatusbar() {
        return (CliStatusBar) Dependency.get(CliStatusBar.class);
    }

    public NotificationActivityStarter getNotificationAcitvityStarter() {
        return this.mNotificationActivityStarter;
    }

    public void setOnSensor(KeyguardBouncerDelegate.Callback callback) {
        this.mStatusBarKeyguardViewManager.setOnSensor(callback);
    }

    public boolean isFolioClosedAndDozing() {
        return this.mFolioClosed && this.mDozing;
    }
}
