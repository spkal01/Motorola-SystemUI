package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import android.view.WindowManagerGlobal;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardViewController;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R$style;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.keyguard.FaceAuthScreenBrightnessController;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.events.PrivacyDotViewController;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.KeyguardBouncerDelegate;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.motorola.android.provider.MotorolaSettings;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class StatusBarKeyguardViewManager implements RemoteInputController.Callback, StatusBarStateController.StateListener, ConfigurationController.ConfigurationListener, PanelExpansionListener, NavigationModeController.ModeChangedListener, KeyguardViewController, WakefulnessLifecycle.Observer {
    /* access modifiers changed from: private */
    public static String TAG = "StatusBarKeyguardViewManager";
    private ActivityStarter.OnDismissAction mAfterKeyguardGoneAction;
    private final ArrayList<Runnable> mAfterKeyguardGoneRunnables = new ArrayList<>();
    /* access modifiers changed from: private */
    public AlternateAuthInterceptor mAlternateAuthInterceptor;
    /* access modifiers changed from: private */
    public boolean mAnimatedToSleep;
    private BiometricUnlockController mBiometricUnlockController;
    protected KeyguardBouncerDelegate mBouncer = new KeyguardBouncerDelegate();
    private KeyguardBypassController mBypassController;
    private ViewGroup mCliContainer;
    protected CliStatusBar mCliStatusBar;
    private final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public ViewGroup mContainer;
    protected final Context mContext;
    private boolean mDismissActionWillAnimateOnKeyguard;
    private final DockManager.DockEventListener mDockEventListener = new DockManager.DockEventListener() {
    };
    private final DockManager mDockManager;
    private boolean mDozing;
    private final KeyguardBouncer.BouncerExpansionCallback mExpansionCallback = new KeyguardBouncer.BouncerExpansionCallback() {
        public void onFullyHidden() {
        }

        public void onFullyShown() {
            StatusBarKeyguardViewManager.this.updateStates();
            StatusBarKeyguardViewManager.this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), StatusBarKeyguardViewManager.this.mContainer, "BOUNCER_VISIBLE");
        }

        public void onStartingToHide() {
            StatusBarKeyguardViewManager.this.updateStates();
        }

        public void onStartingToShow() {
            StatusBarKeyguardViewManager.this.updateStates();
        }

        public void onExpansionChanged(float f) {
            if (StatusBarKeyguardViewManager.this.mAlternateAuthInterceptor != null) {
                StatusBarKeyguardViewManager.this.mAlternateAuthInterceptor.setBouncerExpansionChanged(f);
            }
            StatusBarKeyguardViewManager.this.updateStates();
        }

        public void onVisibilityChanged(boolean z) {
            if (!z) {
                StatusBarKeyguardViewManager statusBarKeyguardViewManager = StatusBarKeyguardViewManager.this;
                if (statusBarKeyguardViewManager.mShowing) {
                    statusBarKeyguardViewManager.cancelPostAuthActions();
                }
            }
            if (StatusBarKeyguardViewManager.this.mAlternateAuthInterceptor != null) {
                StatusBarKeyguardViewManager.this.mAlternateAuthInterceptor.onBouncerVisibilityChanged();
            }
        }

        public void hideBouncerFromCli() {
            StatusBarKeyguardViewManager.this.mBouncer.startPreHideAnimation(new StatusBarKeyguardViewManager$1$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$hideBouncerFromCli$0() {
            StatusBarKeyguardViewManager.this.hideBouncer(true);
            StatusBarKeyguardViewManager.this.updateStates();
        }
    };
    private final Optional<FaceAuthScreenBrightnessController> mFaceAuthScreenBrightnessController;
    protected boolean mFirstUpdate = true;
    private boolean mForceShowBouncer;
    private boolean mGesturalNav;
    private boolean mGlobalActionsVisible = false;
    private boolean mIsDocked;
    private final KeyguardBouncer.Factory mKeyguardBouncerFactory;
    private Runnable mKeyguardGoneCancelAction;
    private KeyguardMessageAreaController mKeyguardMessageAreaController;
    private final KeyguardMessageAreaController.Factory mKeyguardMessageAreaFactory;
    private KeyguardSecurityModel mKeyguardSecurityModel;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateManager;
    private int mLastBiometricMode;
    private boolean mLastBouncerDismissible;
    private boolean mLastBouncerIsOrWillBeShowing;
    private boolean mLastBouncerShowing;
    private boolean mLastDozing;
    private boolean mLastGesturalNav;
    private boolean mLastGlobalActionsVisible = false;
    private boolean mLastIsDocked;
    protected boolean mLastOccluded;
    private boolean mLastPulsing;
    protected boolean mLastRemoteInputActive;
    protected boolean mLastShowing;
    protected LockPatternUtils mLockPatternUtils;
    private Runnable mMakeNavigationBarVisibleRunnable = new Runnable() {
        public void run() {
            if (!StatusBarKeyguardViewManager.this.mStatusBar.isKeyguardFadingWhilePulsing()) {
                StatusBarKeyguardViewManager.this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().show(WindowInsets.Type.navigationBars());
            }
        }
    };
    private final NotificationMediaManager mMediaManager;
    private final NavigationModeController mNavigationModeController;
    private View mNotificationContainer;
    private NotificationPanelViewController mNotificationPanelViewController;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController mNotificationShadeWindowController;
    protected boolean mOccluded;
    private DismissWithActionRequest mPendingWakeupAction;
    private boolean mPulsing;
    private boolean mQsExpanded;
    protected boolean mRemoteInputActive;
    protected boolean mShowing;
    protected StatusBar mStatusBar;
    private final SysuiStatusBarStateController mStatusBarStateController;
    /* access modifiers changed from: private */
    public boolean mSubsidyLocked;
    /* access modifiers changed from: private */
    public final UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onEmergencyCallAction() {
            StatusBarKeyguardViewManager statusBarKeyguardViewManager = StatusBarKeyguardViewManager.this;
            if (statusBarKeyguardViewManager.mOccluded) {
                statusBarKeyguardViewManager.reset(true);
            }
        }
    };
    protected ViewMediatorCallback mViewMediatorCallback;
    private final WakefulnessLifecycle mWakefulnessLifecycle;

    public interface AlternateAuthInterceptor {
        void dump(PrintWriter printWriter);

        boolean hideAlternateAuthBouncer();

        boolean isAnimating();

        boolean isShowingAlternateAuthBouncer();

        void onBouncerVisibilityChanged();

        boolean onTouch(MotionEvent motionEvent);

        void requestUdfps(boolean z, int i);

        void setBouncerExpansionChanged(float f);

        void setQsExpanded(boolean z);

        boolean showAlternateAuthBouncer();
    }

    public void onCancelClicked() {
    }

    /* access modifiers changed from: protected */
    public boolean shouldDestroyViewOnReset() {
        return false;
    }

    public StatusBarKeyguardViewManager(Context context, ViewMediatorCallback viewMediatorCallback, LockPatternUtils lockPatternUtils, SysuiStatusBarStateController sysuiStatusBarStateController, ConfigurationController configurationController, KeyguardUpdateMonitor keyguardUpdateMonitor, NavigationModeController navigationModeController, DockManager dockManager, NotificationShadeWindowController notificationShadeWindowController, KeyguardStateController keyguardStateController, Optional<FaceAuthScreenBrightnessController> optional, NotificationMediaManager notificationMediaManager, KeyguardBouncer.Factory factory, WakefulnessLifecycle wakefulnessLifecycle, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, KeyguardMessageAreaController.Factory factory2) {
        Context context2 = context;
        this.mContext = context2;
        this.mViewMediatorCallback = viewMediatorCallback;
        this.mLockPatternUtils = lockPatternUtils;
        this.mConfigurationController = configurationController;
        this.mNavigationModeController = navigationModeController;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mKeyguardStateController = keyguardStateController;
        this.mMediaManager = notificationMediaManager;
        this.mKeyguardUpdateManager = keyguardUpdateMonitor;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mDockManager = dockManager;
        this.mFaceAuthScreenBrightnessController = optional;
        this.mKeyguardBouncerFactory = factory;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
        this.mKeyguardMessageAreaFactory = factory2;
        this.mForceShowBouncer = false;
        if (context2 != null) {
            this.mKeyguardSecurityModel = (KeyguardSecurityModel) Dependency.get(KeyguardSecurityModel.class);
            rigisterObserverSubsidyLock();
        }
    }

    public void registerStatusBar(StatusBar statusBar, ViewGroup viewGroup, NotificationPanelViewController notificationPanelViewController, BiometricUnlockController biometricUnlockController, View view, KeyguardBypassController keyguardBypassController) {
        this.mStatusBar = statusBar;
        this.mContainer = viewGroup;
        this.mBiometricUnlockController = biometricUnlockController;
        this.mBouncer.setMainBouncer(this.mKeyguardBouncerFactory.create(viewGroup, this.mExpansionCallback), this.mViewMediatorCallback, this.mExpansionCallback);
        this.mNotificationPanelViewController = notificationPanelViewController;
        notificationPanelViewController.addExpansionListener(this);
        this.mBypassController = keyguardBypassController;
        this.mNotificationContainer = view;
        this.mKeyguardMessageAreaController = this.mKeyguardMessageAreaFactory.create(KeyguardMessageArea.findSecurityMessageDisplay(viewGroup));
        this.mFaceAuthScreenBrightnessController.ifPresent(new StatusBarKeyguardViewManager$$ExternalSyntheticLambda4(this, viewGroup));
        registerListeners();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerStatusBar$0(ViewGroup viewGroup, FaceAuthScreenBrightnessController faceAuthScreenBrightnessController) {
        View view = new View(this.mContext);
        viewGroup.addView(view);
        faceAuthScreenBrightnessController.attach(view);
    }

    public void removeAlternateAuthInterceptor(AlternateAuthInterceptor alternateAuthInterceptor) {
        if (Objects.equals(this.mAlternateAuthInterceptor, alternateAuthInterceptor)) {
            this.mAlternateAuthInterceptor = null;
            resetAlternateAuth(true);
        }
    }

    public void setAlternateAuthInterceptor(AlternateAuthInterceptor alternateAuthInterceptor) {
        this.mAlternateAuthInterceptor = alternateAuthInterceptor;
        resetAlternateAuth(false);
    }

    public void registerCliStatusBar(CliStatusBar cliStatusBar, ViewGroup viewGroup) {
        this.mCliStatusBar = cliStatusBar;
        this.mCliContainer = viewGroup;
        Context cliContext = MotoFeature.getCliContext(this.mContext);
        cliContext.setTheme(R$style.Theme_SystemUI);
        this.mBouncer.setCliBouncer(this.mKeyguardBouncerFactory.createForCli(cliContext, viewGroup, this.mBouncer.getCliViewMediatorCallback(), this.mBouncer.getCliBouncerExpansionCallback()), cliContext, viewGroup);
        this.mBouncer.setBiometricUnlockController(this.mBiometricUnlockController);
    }

    private void registerListeners() {
        this.mKeyguardUpdateManager.registerCallback(this.mUpdateMonitorCallback);
        this.mStatusBarStateController.addCallback(this);
        this.mConfigurationController.addCallback(this);
        this.mGesturalNav = QuickStepContract.isGesturalMode(this.mNavigationModeController.addListener(this));
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
            this.mIsDocked = this.mDockManager.isDocked();
        }
        this.mWakefulnessLifecycle.addObserver(new WakefulnessLifecycle.Observer() {
            public void onFinishedWakingUp() {
                boolean unused = StatusBarKeyguardViewManager.this.mAnimatedToSleep = false;
                StatusBarKeyguardViewManager.this.updateStates();
            }

            public void onFinishedGoingToSleep() {
                StatusBarKeyguardViewManager statusBarKeyguardViewManager = StatusBarKeyguardViewManager.this;
                boolean unused = statusBarKeyguardViewManager.mAnimatedToSleep = statusBarKeyguardViewManager.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying();
                StatusBarKeyguardViewManager.this.updateStates();
            }
        });
    }

    public void setForceShowBouncer(boolean z) {
        this.mForceShowBouncer = z;
    }

    public boolean isForceShowBouncer() {
        return this.mForceShowBouncer;
    }

    public void onDensityOrFontScaleChanged() {
        hideBouncer(true);
    }

    public void onPanelExpansionChanged(float f, boolean z) {
        if (this.mNotificationPanelViewController.isUnlockHintRunning()) {
            this.mBouncer.setExpansion(1.0f);
        } else if (bouncerNeedsScrimming()) {
            this.mBouncer.setExpansion(0.0f);
        } else if (this.mShowing) {
            if (!isWakeAndUnlocking() && !this.mStatusBar.isInLaunchTransition()) {
                this.mBouncer.setExpansion(f);
            }
            if (f != 1.0f && z && !this.mKeyguardStateController.canDismissLockScreen() && !this.mBouncer.isShowing() && !this.mBouncer.isAnimatingAway()) {
                this.mBouncer.show(false, false);
            }
        } else if (this.mPulsing && f == 0.0f) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), this.mContainer, "BOUNCER_VISIBLE");
        }
    }

    public void setGlobalActionsVisible(boolean z) {
        this.mGlobalActionsVisible = z;
        this.mStatusBar.onGlobalActionsStatesUpdated(z);
        updateStates();
    }

    public void show(Bundle bundle) {
        this.mShowing = true;
        this.mNotificationShadeWindowController.setKeyguardShowing(true);
        KeyguardStateController keyguardStateController = this.mKeyguardStateController;
        keyguardStateController.notifyKeyguardState(this.mShowing, keyguardStateController.isOccluded());
        reset(true);
        SysUiStatsLog.write(62, 2);
    }

    /* access modifiers changed from: protected */
    public void showBouncerOrKeyguard(boolean z) {
        if ((!this.mBouncer.needsFullscreenBouncer() || this.mDozing) && (!this.mForceShowBouncer || !isSecure())) {
            this.mStatusBar.showKeyguard();
            if (z) {
                hideBouncer(shouldDestroyViewOnReset());
                this.mBouncer.prepare();
            }
        } else {
            this.mStatusBar.hideKeyguard();
            this.mBouncer.show(true);
        }
        updateStates();
    }

    public void showGenericBouncer(boolean z) {
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        if (alternateAuthInterceptor != null) {
            updateAlternateAuthShowing(alternateAuthInterceptor.showAlternateAuthBouncer());
        } else {
            showBouncer(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideBouncer(boolean z) {
        if (this.mBouncer != null) {
            if (this.mShowing) {
                cancelPostAuthActions();
            }
            setForceShowBouncer(false);
            this.mBouncer.hide(z);
            cancelPendingWakeupAction();
        }
    }

    public void showBouncer(boolean z) {
        if (this.mShowing && !this.mBouncer.isShowing()) {
            this.mBouncer.show(false, z);
        }
        updateStates();
    }

    public void dismissWithAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        dismissWithAction(onDismissAction, runnable, z, (String) null);
    }

    public void dismissWithAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z, String str) {
        if (this.mShowing) {
            cancelPendingWakeupAction();
            if (!this.mDozing || isWakeAndUnlocking()) {
                this.mAfterKeyguardGoneAction = onDismissAction;
                this.mKeyguardGoneCancelAction = runnable;
                this.mDismissActionWillAnimateOnKeyguard = onDismissAction != null && onDismissAction.willRunAnimationOnKeyguard();
                if (this.mAlternateAuthInterceptor != null) {
                    if (!z) {
                        this.mBouncer.setDismissAction(this.mAfterKeyguardGoneAction, this.mKeyguardGoneCancelAction);
                        this.mAfterKeyguardGoneAction = null;
                        this.mKeyguardGoneCancelAction = null;
                    }
                    updateAlternateAuthShowing(this.mAlternateAuthInterceptor.showAlternateAuthBouncer());
                    return;
                } else if (z) {
                    this.mBouncer.show(false);
                } else {
                    this.mBouncer.showWithDismissAction(this.mAfterKeyguardGoneAction, this.mKeyguardGoneCancelAction);
                    this.mAfterKeyguardGoneAction = null;
                    this.mKeyguardGoneCancelAction = null;
                }
            } else {
                this.mPendingWakeupAction = new DismissWithActionRequest(onDismissAction, runnable, z, str);
                return;
            }
        }
        updateStates();
    }

    private boolean isWakeAndUnlocking() {
        int mode = this.mBiometricUnlockController.getMode();
        return mode == 1 || mode == 2;
    }

    public void addAfterKeyguardGoneRunnable(Runnable runnable) {
        this.mAfterKeyguardGoneRunnables.add(runnable);
    }

    public void reset(boolean z) {
        if (this.mShowing) {
            this.mNotificationPanelViewController.resetViews(true);
            if (isSimPinPukModeInSubsidylock() || !this.mOccluded || this.mDozing) {
                showBouncerOrKeyguard(z);
            } else {
                this.mStatusBar.hideKeyguard();
                if (z || this.mBouncer.needsFullscreenBouncer()) {
                    hideBouncer(false);
                }
            }
            resetAlternateAuth(false);
            this.mKeyguardUpdateManager.sendKeyguardReset();
            updateStates();
            cancelAfterKeyguardGoneAction();
        }
    }

    private void rigisterObserverSubsidyLock() {
        C19435 r0 = new ContentObserver((Handler) null) {
            public void onChange(boolean z) {
                StatusBarKeyguardViewManager statusBarKeyguardViewManager = StatusBarKeyguardViewManager.this;
                boolean z2 = false;
                if (MotorolaSettings.Global.getInt(statusBarKeyguardViewManager.mContext.getContentResolver(), "jio_subsidy_locked", 0) == 1) {
                    z2 = true;
                }
                boolean unused = statusBarKeyguardViewManager.mSubsidyLocked = z2;
                Log.d(StatusBarKeyguardViewManager.TAG, "Subsidy locked: " + StatusBarKeyguardViewManager.this.mSubsidyLocked);
            }
        };
        boolean z = true;
        if (MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "jio_subsidy_locked", 0) != 1) {
            z = false;
        }
        this.mSubsidyLocked = z;
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Global.getUriFor("jio_subsidy_locked"), false, r0, -1);
    }

    private boolean isSimPinPukModeInSubsidylock() {
        if (!this.mSubsidyLocked) {
            return false;
        }
        KeyguardSecurityModel.SecurityMode securityMode = this.mKeyguardSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        if (securityMode != KeyguardSecurityModel.SecurityMode.SimPin && securityMode != KeyguardSecurityModel.SecurityMode.SimPuk) {
            return false;
        }
        if (!KeyguardConstants.DEBUG) {
            return true;
        }
        Log.d(TAG, "Should show SIM pin/puk when jio subsidy locked.");
        return true;
    }

    public void resetAlternateAuth(boolean z) {
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        updateAlternateAuthShowing((alternateAuthInterceptor != null && alternateAuthInterceptor.hideAlternateAuthBouncer()) || z);
    }

    private void updateAlternateAuthShowing(boolean z) {
        KeyguardMessageAreaController keyguardMessageAreaController = this.mKeyguardMessageAreaController;
        if (keyguardMessageAreaController != null) {
            keyguardMessageAreaController.setAltBouncerShowing(isShowingAlternateAuth());
        }
        this.mBypassController.setAltBouncerShowing(isShowingAlternateAuth());
        if (z) {
            this.mStatusBar.updateScrimController();
        }
    }

    public void onStartedWakingUp() {
        this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().setAnimationsDisabled(false);
    }

    public void onStartedGoingToSleep() {
        this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().setAnimationsDisabled(true);
    }

    public void onFinishedGoingToSleep() {
        setForceShowBouncer(false);
        this.mBouncer.onScreenTurnedOff();
    }

    public void onRemoteInputActive(boolean z) {
        this.mRemoteInputActive = z;
        updateStates();
    }

    private void setDozing(boolean z) {
        if (this.mDozing != z) {
            this.mDozing = z;
            if (z || this.mBouncer.needsFullscreenBouncer() || this.mOccluded) {
                reset(z);
            }
            updateStates();
            if (!z) {
                launchPendingWakeupAction();
            }
        }
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
            updateStates();
        }
    }

    public void setNeedsInput(boolean z) {
        this.mNotificationShadeWindowController.setKeyguardNeedsInput(z);
    }

    public boolean isUnlockWithWallpaper() {
        return this.mNotificationShadeWindowController.isShowingWallpaper();
    }

    public void setOccluded(boolean z, boolean z2) {
        this.mStatusBar.setOccluded(z);
        boolean z3 = true;
        if (z && !this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 3);
            if (this.mStatusBar.isInLaunchTransition()) {
                this.mOccluded = true;
                this.mStatusBar.fadeKeyguardAfterLaunchTransition((Runnable) null, new Runnable() {
                    public void run() {
                        StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardOccluded(StatusBarKeyguardViewManager.this.mOccluded);
                        StatusBarKeyguardViewManager.this.reset(true);
                    }
                });
                return;
            }
        } else if (!z && this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 2);
        }
        boolean z4 = !this.mOccluded && z;
        this.mOccluded = z;
        if (this.mShowing) {
            NotificationMediaManager notificationMediaManager = this.mMediaManager;
            if (!z2 || z) {
                z3 = false;
            }
            notificationMediaManager.updateMediaMetaData(false, z3);
        }
        this.mNotificationShadeWindowController.setKeyguardOccluded(z);
        if (!this.mDozing) {
            reset(z4);
        }
        if (z2 && !z && this.mShowing && !this.mBouncer.isShowing()) {
            this.mStatusBar.animateKeyguardUnoccluding();
        }
    }

    public boolean isOccluded() {
        return this.mOccluded;
    }

    public void startPreHideAnimation(Runnable runnable) {
        if (this.mBouncer.isShowing()) {
            this.mBouncer.startPreHideAnimation(runnable);
            this.mStatusBar.onBouncerPreHideAnimation();
            if (this.mDismissActionWillAnimateOnKeyguard) {
                updateStates();
            }
        } else if (runnable != null) {
            runnable.run();
        }
        this.mNotificationPanelViewController.blockExpansionForCurrentTouch();
    }

    public void blockPanelExpansionFromCurrentTouch() {
        this.mNotificationPanelViewController.blockExpansionForCurrentTouch();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0095  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void hide(long r19, long r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = 0
            r0.mShowing = r1
            com.android.systemui.statusbar.policy.KeyguardStateController r2 = r0.mKeyguardStateController
            boolean r3 = r2.isOccluded()
            r2.notifyKeyguardState(r1, r3)
            r18.launchPendingWakeupAction()
            r0.setForceShowBouncer(r1)
            com.android.keyguard.KeyguardUpdateMonitor r2 = r0.mKeyguardUpdateManager
            boolean r2 = r2.needsSlowUnlockTransition()
            if (r2 == 0) goto L_0x001f
            r2 = 2000(0x7d0, double:9.88E-321)
            goto L_0x0021
        L_0x001f:
            r2 = r21
        L_0x0021:
            long r4 = android.os.SystemClock.uptimeMillis()
            r6 = -48
            long r6 = r19 + r6
            long r6 = r6 - r4
            r4 = 0
            long r6 = java.lang.Math.max(r4, r6)
            com.android.systemui.statusbar.phone.StatusBar r8 = r0.mStatusBar
            boolean r8 = r8.isInLaunchTransition()
            r15 = 1
            if (r8 != 0) goto L_0x00e0
            com.android.systemui.statusbar.policy.KeyguardStateController r8 = r0.mKeyguardStateController
            boolean r8 = r8.isFlingingToDismissKeyguard()
            if (r8 == 0) goto L_0x0043
            goto L_0x00e0
        L_0x0043:
            r18.executeAfterKeyguardGoneAction()
            com.android.systemui.statusbar.phone.BiometricUnlockController r8 = r0.mBiometricUnlockController
            int r8 = r8.getMode()
            r9 = 2
            if (r8 != r9) goto L_0x0052
            r16 = r15
            goto L_0x0054
        L_0x0052:
            r16 = r1
        L_0x0054:
            boolean r17 = r18.needsBypassFading()
            if (r17 == 0) goto L_0x005e
            r2 = 67
        L_0x005c:
            r11 = r4
            goto L_0x0064
        L_0x005e:
            if (r16 == 0) goto L_0x0063
            r2 = 240(0xf0, double:1.186E-321)
            goto L_0x005c
        L_0x0063:
            r11 = r6
        L_0x0064:
            com.android.systemui.statusbar.phone.StatusBar r8 = r0.mStatusBar
            r9 = r19
            r13 = r2
            r4 = r15
            r15 = r17
            r8.setKeyguardFadingAway(r9, r11, r13, r15)
            com.android.systemui.statusbar.phone.BiometricUnlockController r5 = r0.mBiometricUnlockController
            r5.startKeyguardFadingAway()
            r0.hideBouncer(r4)
            if (r16 == 0) goto L_0x0095
            if (r17 == 0) goto L_0x008c
            com.android.systemui.statusbar.phone.NotificationPanelViewController r5 = r0.mNotificationPanelViewController
            android.view.ViewGroup r5 = r5.getView()
            android.view.View r6 = r0.mNotificationContainer
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$$ExternalSyntheticLambda1 r7 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$$ExternalSyntheticLambda1
            r7.<init>(r0)
            com.android.systemui.statusbar.notification.ViewGroupFadeHelper.fadeOutAllChildrenExcept(r5, r6, r2, r7)
            goto L_0x0091
        L_0x008c:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.fadeKeyguardWhilePulsing()
        L_0x0091:
            r18.wakeAndUnlockDejank()
            goto L_0x00d2
        L_0x0095:
            com.android.systemui.statusbar.SysuiStatusBarStateController r5 = r0.mStatusBarStateController
            boolean r5 = r5.leaveOpenOnKeyguardHide()
            if (r5 != 0) goto L_0x00c3
            com.android.systemui.statusbar.NotificationShadeWindowController r5 = r0.mNotificationShadeWindowController
            r5.setKeyguardFadingAway(r4)
            if (r17 == 0) goto L_0x00b5
            com.android.systemui.statusbar.phone.NotificationPanelViewController r5 = r0.mNotificationPanelViewController
            android.view.ViewGroup r5 = r5.getView()
            android.view.View r6 = r0.mNotificationContainer
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$$ExternalSyntheticLambda2 r7 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$$ExternalSyntheticLambda2
            r7.<init>(r0)
            com.android.systemui.statusbar.notification.ViewGroupFadeHelper.fadeOutAllChildrenExcept(r5, r6, r2, r7)
            goto L_0x00ba
        L_0x00b5:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.hideKeyguard()
        L_0x00ba:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.updateScrimController()
            r18.wakeAndUnlockDejank()
            goto L_0x00d2
        L_0x00c3:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.hideKeyguard()
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.finishKeyguardFadingAway()
            com.android.systemui.statusbar.phone.BiometricUnlockController r2 = r0.mBiometricUnlockController
            r2.finishKeyguardFadingAway()
        L_0x00d2:
            r18.updateStates()
            com.android.systemui.statusbar.NotificationShadeWindowController r2 = r0.mNotificationShadeWindowController
            r2.setKeyguardShowing(r1)
            com.android.keyguard.ViewMediatorCallback r0 = r0.mViewMediatorCallback
            r0.keyguardGone()
            goto L_0x00f6
        L_0x00e0:
            r4 = r15
            com.android.systemui.statusbar.policy.KeyguardStateController r1 = r0.mKeyguardStateController
            boolean r1 = r1.isFlingingToDismissKeyguard()
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$7 r3 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$7
            r3.<init>()
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$8 r5 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$8
            r5.<init>(r1)
            r2.fadeKeyguardAfterLaunchTransition(r3, r5)
        L_0x00f6:
            r0 = 62
            com.android.systemui.shared.system.SysUiStatsLog.write(r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.hide(long, long):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$1() {
        this.mStatusBar.hideKeyguard();
        onKeyguardFadedAway();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$2() {
        this.mStatusBar.hideKeyguard();
    }

    private boolean needsBypassFading() {
        if ((this.mBiometricUnlockController.getMode() == 7 || this.mBiometricUnlockController.getMode() == 2 || this.mBiometricUnlockController.getMode() == 1) && this.mBypassController.getBypassEnabled()) {
            return true;
        }
        return false;
    }

    public void onNavigationModeChanged(int i) {
        boolean isGesturalMode = QuickStepContract.isGesturalMode(i);
        if (isGesturalMode != this.mGesturalNav) {
            this.mGesturalNav = isGesturalMode;
            updateStates();
        }
    }

    public void onThemeChanged() {
        boolean isShowing = this.mBouncer.isShowing();
        boolean isScrimmed = this.mBouncer.isScrimmed();
        boolean isForceShowBouncer = isForceShowBouncer();
        hideBouncer(true);
        this.mBouncer.prepare();
        if (isForceShowBouncer) {
            showBouncer(false);
        } else if (isShowing) {
            showBouncer(isScrimmed);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onKeyguardFadedAway$3() {
        this.mNotificationShadeWindowController.setKeyguardFadingAway(false);
    }

    public void onKeyguardFadedAway() {
        this.mContainer.postDelayed(new StatusBarKeyguardViewManager$$ExternalSyntheticLambda0(this), 100);
        ViewGroupFadeHelper.reset(this.mNotificationPanelViewController.getView());
        this.mStatusBar.finishKeyguardFadingAway();
        this.mBiometricUnlockController.finishKeyguardFadingAway();
        WindowManagerGlobal.getInstance().trimMemory(20);
    }

    private void wakeAndUnlockDejank() {
        if (this.mBiometricUnlockController.getMode() == 1 && LatencyTracker.isEnabled(this.mContext)) {
            DejankUtils.postAfterTraversal(new StatusBarKeyguardViewManager$$ExternalSyntheticLambda3(this, this.mBiometricUnlockController.getBiometricType()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$wakeAndUnlockDejank$4(BiometricSourceType biometricSourceType) {
        LatencyTracker.getInstance(this.mContext).onActionEnd(biometricSourceType == BiometricSourceType.FACE ? 7 : 2);
    }

    /* access modifiers changed from: private */
    public void executeAfterKeyguardGoneAction() {
        ActivityStarter.OnDismissAction onDismissAction = this.mAfterKeyguardGoneAction;
        if (onDismissAction != null) {
            onDismissAction.onDismiss();
            this.mAfterKeyguardGoneAction = null;
        }
        this.mKeyguardGoneCancelAction = null;
        this.mDismissActionWillAnimateOnKeyguard = false;
        for (int i = 0; i < this.mAfterKeyguardGoneRunnables.size(); i++) {
            this.mAfterKeyguardGoneRunnables.get(i).run();
        }
        this.mAfterKeyguardGoneRunnables.clear();
    }

    private void cancelAfterKeyguardGoneAction() {
        this.mAfterKeyguardGoneAction = null;
        this.mAfterKeyguardGoneRunnables.clear();
    }

    public void dismissAndCollapse() {
        this.mStatusBar.executeRunnableDismissingKeyguard((Runnable) null, (Runnable) null, true, false, true);
    }

    public boolean isSecure() {
        return this.mBouncer.isSecure();
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean onBackPressed(boolean z) {
        if (!this.mBouncer.isShowing()) {
            return false;
        }
        this.mStatusBar.endAffordanceLaunch();
        setForceShowBouncer(false);
        reset(z);
        return true;
    }

    public boolean isBouncerShowing() {
        return this.mBouncer.isShowing() || isShowingAlternateAuth();
    }

    public boolean bouncerIsOrWillBeShowing() {
        return this.mBouncer.isShowing() || this.mBouncer.getShowingSoon();
    }

    public void cancelPostAuthActions() {
        if (!bouncerIsOrWillBeShowing()) {
            this.mAfterKeyguardGoneAction = null;
            this.mDismissActionWillAnimateOnKeyguard = false;
            Runnable runnable = this.mKeyguardGoneCancelAction;
            if (runnable != null) {
                runnable.run();
                this.mKeyguardGoneCancelAction = null;
            }
        }
    }

    private long getNavBarShowDelay() {
        if (this.mKeyguardStateController.isKeyguardFadingAway()) {
            return this.mKeyguardStateController.getKeyguardFadingAwayDelay();
        }
        return this.mBouncer.isShowing() ? 320 : 0;
    }

    /* access modifiers changed from: protected */
    public void updateStates() {
        ViewGroup viewGroup = this.mContainer;
        if (viewGroup != null) {
            int systemUiVisibility = viewGroup.getSystemUiVisibility();
            boolean z = this.mShowing;
            boolean z2 = this.mOccluded;
            boolean isShowing = this.mBouncer.isShowing();
            boolean bouncerIsOrWillBeShowing = bouncerIsOrWillBeShowing();
            boolean z3 = true;
            boolean z4 = !this.mBouncer.isFullscreenBouncer();
            boolean z5 = this.mRemoteInputActive;
            if ((z4 || !z || z5) != (this.mLastBouncerDismissible || !this.mLastShowing || this.mLastRemoteInputActive) || this.mFirstUpdate) {
                if (z4 || !z || z5) {
                    this.mContainer.setSystemUiVisibility(systemUiVisibility & -4194305);
                } else {
                    this.mContainer.setSystemUiVisibility(systemUiVisibility | 4194304);
                }
            }
            boolean isNavBarVisible = isNavBarVisible();
            if (isNavBarVisible != getLastNavBarVisible() || this.mFirstUpdate) {
                updateNavigationBarVisibility(isNavBarVisible);
            }
            if (isShowing != this.mLastBouncerShowing || this.mFirstUpdate) {
                this.mNotificationShadeWindowController.setBouncerShowing(isShowing);
                this.mStatusBar.setBouncerShowing(isShowing);
                ((PrivacyDotViewController) Dependency.get(PrivacyDotViewController.class)).setBouncerShowing(isShowing);
            }
            if (z != this.mLastShowing || this.mFirstUpdate) {
                this.mKeyguardUpdateManager.onKeyguardShowingChanged(z);
            }
            if ((z && !z2) != (this.mLastShowing && !this.mLastOccluded) || this.mFirstUpdate) {
                this.mKeyguardUpdateManager.setKeyguardBouncerShowingForFPS(isShowing);
                KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateManager;
                if (!z || z2) {
                    z3 = false;
                }
                keyguardUpdateMonitor.onKeyguardVisibilityChanged(z3);
            }
            if (bouncerIsOrWillBeShowing != this.mLastBouncerIsOrWillBeShowing || this.mFirstUpdate) {
                this.mKeyguardUpdateManager.sendKeyguardBouncerChanged(bouncerIsOrWillBeShowing);
            }
            this.mFirstUpdate = false;
            this.mLastShowing = z;
            this.mLastGlobalActionsVisible = this.mGlobalActionsVisible;
            this.mLastOccluded = z2;
            this.mLastBouncerShowing = isShowing;
            this.mLastBouncerIsOrWillBeShowing = bouncerIsOrWillBeShowing;
            this.mLastBouncerDismissible = z4;
            this.mLastRemoteInputActive = z5;
            this.mLastDozing = this.mDozing;
            this.mLastPulsing = this.mPulsing;
            this.mLastBiometricMode = this.mBiometricUnlockController.getMode();
            this.mLastGesturalNav = this.mGesturalNav;
            this.mLastIsDocked = this.mIsDocked;
            this.mStatusBar.onKeyguardViewManagerStatesUpdated();
        }
    }

    /* access modifiers changed from: protected */
    public void updateNavigationBarVisibility(boolean z) {
        if (this.mStatusBar.getNavigationBarView() == null) {
            return;
        }
        if (z) {
            long navBarShowDelay = getNavBarShowDelay();
            if (navBarShowDelay == 0) {
                this.mMakeNavigationBarVisibleRunnable.run();
            } else {
                this.mContainer.postOnAnimationDelayed(this.mMakeNavigationBarVisibleRunnable, navBarShowDelay);
            }
        } else {
            this.mContainer.removeCallbacks(this.mMakeNavigationBarVisibleRunnable);
            this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().hide(WindowInsets.Type.navigationBars());
        }
    }

    /* access modifiers changed from: protected */
    public boolean isNavBarVisible() {
        int mode = this.mBiometricUnlockController.getMode();
        boolean z = this.mShowing && !this.mOccluded;
        boolean z2 = this.mDozing;
        boolean z3 = z2 && mode != 2;
        boolean z4 = ((z && !z2) || (this.mPulsing && !this.mIsDocked && MotoDisplayManager.isAospAD())) && this.mGesturalNav;
        if (!this.mKeyguardUpdateManager.isDeviceProvisioned() && z) {
            z4 = false;
        }
        if ((this.mAnimatedToSleep || z || z3) && !this.mBouncer.isShowing() && !this.mRemoteInputActive && !z4 && !this.mGlobalActionsVisible) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean getLastNavBarVisible() {
        boolean z = this.mLastShowing && !this.mLastOccluded;
        boolean z2 = this.mLastDozing;
        boolean z3 = z2 && this.mLastBiometricMode != 2;
        boolean z4 = ((z && !z2) || (this.mLastPulsing && !this.mLastIsDocked && MotoDisplayManager.isAospAD())) && this.mLastGesturalNav;
        if (!this.mKeyguardUpdateManager.isDeviceProvisioned() && z) {
            z4 = false;
        }
        if ((z || z3) && !this.mLastBouncerShowing && !this.mLastRemoteInputActive && !z4 && !this.mLastGlobalActionsVisible) {
            return false;
        }
        return true;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mBouncer.shouldDismissOnMenuPressed();
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        return this.mBouncer.interceptMediaKey(keyEvent);
    }

    public boolean dispatchBackKeyEventPreIme() {
        return this.mBouncer.dispatchBackKeyEventPreIme();
    }

    public void readyForKeyguardDone() {
        this.mViewMediatorCallback.readyForKeyguardDone();
    }

    public boolean shouldDisableWindowAnimationsForUnlock() {
        return this.mStatusBar.isInLaunchTransition();
    }

    public boolean shouldSubtleWindowAnimationsForUnlock() {
        return needsBypassFading();
    }

    public boolean isGoingToNotificationShade() {
        return this.mStatusBarStateController.leaveOpenOnKeyguardHide();
    }

    public void keyguardGoingAway() {
        this.mStatusBar.keyguardGoingAway();
    }

    public void setKeyguardGoingAwayState(boolean z) {
        this.mNotificationShadeWindowController.setKeyguardGoingAway(z);
    }

    public void notifyKeyguardAuthenticated(boolean z) {
        this.mBouncer.notifyKeyguardAuthenticated(z);
        if (this.mAlternateAuthInterceptor != null && isShowingAlternateAuthOrAnimating()) {
            resetAlternateAuth(false);
        }
    }

    public void showBouncerMessage(String str, ColorStateList colorStateList) {
        if (isShowingAlternateAuth()) {
            KeyguardMessageAreaController keyguardMessageAreaController = this.mKeyguardMessageAreaController;
            if (keyguardMessageAreaController != null) {
                keyguardMessageAreaController.setNextMessageColor(colorStateList);
                this.mKeyguardMessageAreaController.setMessage((CharSequence) str);
                return;
            }
            return;
        }
        this.mBouncer.showMessage(str, colorStateList);
    }

    public ViewRootImpl getViewRootImpl() {
        return this.mStatusBar.getStatusBarView().getViewRootImpl();
    }

    public void launchPendingWakeupAction() {
        DismissWithActionRequest dismissWithActionRequest = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (dismissWithActionRequest == null) {
            return;
        }
        if (this.mShowing) {
            dismissWithAction(dismissWithActionRequest.dismissAction, dismissWithActionRequest.cancelAction, dismissWithActionRequest.afterKeyguardGone, dismissWithActionRequest.message);
            return;
        }
        ActivityStarter.OnDismissAction onDismissAction = dismissWithActionRequest.dismissAction;
        if (onDismissAction != null) {
            onDismissAction.onDismiss();
        }
    }

    public void cancelPendingWakeupAction() {
        Runnable runnable;
        DismissWithActionRequest dismissWithActionRequest = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (dismissWithActionRequest != null && (runnable = dismissWithActionRequest.cancelAction) != null) {
            runnable.run();
        }
    }

    public boolean bouncerNeedsScrimming() {
        return this.mOccluded || this.mBouncer.willDismissWithAction() || this.mStatusBar.isFullScreenUserSwitcherState() || (this.mBouncer.isShowing() && this.mBouncer.isScrimmed()) || this.mBouncer.isFullscreenBouncer();
    }

    public void updateResources() {
        KeyguardBouncerDelegate keyguardBouncerDelegate = this.mBouncer;
        if (keyguardBouncerDelegate != null) {
            keyguardBouncerDelegate.updateResources();
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("StatusBarKeyguardViewManager:");
        printWriter.println("  mShowing: " + this.mShowing);
        printWriter.println("  mOccluded: " + this.mOccluded);
        printWriter.println("  mRemoteInputActive: " + this.mRemoteInputActive);
        printWriter.println("  mDozing: " + this.mDozing);
        printWriter.println("  mAfterKeyguardGoneAction: " + this.mAfterKeyguardGoneAction);
        printWriter.println("  mAfterKeyguardGoneRunnables: " + this.mAfterKeyguardGoneRunnables);
        printWriter.println("  mPendingWakeupAction: " + this.mPendingWakeupAction);
        KeyguardBouncerDelegate keyguardBouncerDelegate = this.mBouncer;
        if (keyguardBouncerDelegate != null) {
            keyguardBouncerDelegate.dump(printWriter);
        }
        if (this.mAlternateAuthInterceptor != null) {
            printWriter.println("AltAuthInterceptor: ");
            this.mAlternateAuthInterceptor.dump(printWriter);
        }
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }

    public boolean isQsExpanded() {
        return this.mQsExpanded;
    }

    public void setQsExpanded(boolean z) {
        this.mQsExpanded = z;
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        if (alternateAuthInterceptor != null) {
            alternateAuthInterceptor.setQsExpanded(z);
        }
    }

    public boolean isShowingAlternateAuth() {
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        return alternateAuthInterceptor != null && alternateAuthInterceptor.isShowingAlternateAuthBouncer();
    }

    public boolean isShowingAlternateAuthOrAnimating() {
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        return alternateAuthInterceptor != null && (alternateAuthInterceptor.isShowingAlternateAuthBouncer() || this.mAlternateAuthInterceptor.isAnimating());
    }

    public boolean onTouch(MotionEvent motionEvent) {
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        if (alternateAuthInterceptor == null) {
            return false;
        }
        return alternateAuthInterceptor.onTouch(motionEvent);
    }

    public void updateKeyguardPosition(float f) {
        KeyguardBouncerDelegate keyguardBouncerDelegate = this.mBouncer;
        if (keyguardBouncerDelegate != null) {
            keyguardBouncerDelegate.updateKeyguardPosition(f);
        }
    }

    private static class DismissWithActionRequest {
        final boolean afterKeyguardGone;
        final Runnable cancelAction;
        final ActivityStarter.OnDismissAction dismissAction;
        final String message;

        DismissWithActionRequest(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z, String str) {
            this.dismissAction = onDismissAction;
            this.cancelAction = runnable;
            this.afterKeyguardGone = z;
            this.message = str;
        }
    }

    public void requestFace(boolean z) {
        this.mKeyguardUpdateManager.requestFaceAuthOnOccludingApp(z);
    }

    public void requestFp(boolean z, int i) {
        this.mKeyguardUpdateManager.requestFingerprintAuthOnOccludingApp(z);
        AlternateAuthInterceptor alternateAuthInterceptor = this.mAlternateAuthInterceptor;
        if (alternateAuthInterceptor != null) {
            alternateAuthInterceptor.requestUdfps(z, i);
        }
    }

    public void requestUnlock(final IRemoteCallback iRemoteCallback, final boolean z, boolean z2) {
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.userActivity();
        }
        setForceShowBouncer(true);
        if (z2) {
            this.mStatusBar.makeExpandedInvisible(false);
            String str = TAG;
            Slog.d(str, "requestUnlock collapse=" + z2);
            this.mStatusBar.setWakingUpFromAod(true);
        }
        if (iRemoteCallback != null) {
            dismissWithAction(new ActivityStarter.OnDismissAction() {
                public boolean onDismiss() {
                    try {
                        iRemoteCallback.sendResult((Bundle) null);
                    } catch (RemoteException unused) {
                        Slog.e(StatusBarKeyguardViewManager.TAG, "remote error from callback of request unlock");
                    }
                    if (!z) {
                        DejankUtils.postAfterTraversal(new Runnable() {
                            public void run() {
                                StatusBarKeyguardViewManager.this.readyForKeyguardDone();
                            }
                        });
                    }
                    return !z;
                }
            }, (Runnable) null, false);
        } else {
            showBouncer(false);
        }
        if (iRemoteCallback != null) {
            DejankUtils.postAfterTraversal(new Runnable() {
                public void run() {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putInt("callbackType", 2);
                        iRemoteCallback.sendResult(bundle);
                    } catch (RemoteException unused) {
                        Slog.e(StatusBarKeyguardViewManager.TAG, "remote error from callback of request unlock");
                    }
                }
            });
        }
    }

    public void setOnSensor(KeyguardBouncerDelegate.Callback callback) {
        this.mBouncer.setOnSensor(callback);
    }
}
