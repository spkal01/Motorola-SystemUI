package com.android.systemui.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricSourceType;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationTarget;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.constraintlayout.widget.R$styleable;
import androidx.mediarouter.media.MediaRoute2Provider$$ExternalSyntheticLambda0;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardDisplayManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardViewController;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.util.DeviceConfigProxy;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

public class KeyguardViewMediator extends SystemUI implements StatusBarStateController.StateListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private static final Intent USER_PRESENT_INTENT = new Intent("android.intent.action.USER_PRESENT").addFlags(606076928);
    private boolean isUseHandle;
    private AlarmManager mAlarmManager;
    private boolean mAnimatingScreenOff;
    private boolean mAodShowing;
    private AudioManager mAudioManager;
    private boolean mBootCompleted;
    private boolean mBootSendUserPresent;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    /* access modifiers changed from: private */
    public CharSequence mCustomMessage;
    private final BroadcastReceiver mDelayedLockBroadcastReceiver;
    /* access modifiers changed from: private */
    public int mDelayedProfileShowingSequence;
    /* access modifiers changed from: private */
    public int mDelayedShowingSequence;
    private DeviceConfigProxy mDeviceConfig;
    /* access modifiers changed from: private */
    public boolean mDeviceInteractive;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    private DozeParameters mDozeParameters;
    private boolean mDozing;
    private IKeyguardDrawnCallback mDrawnCallback;
    private IKeyguardExitCallback mExitSecureCallback;
    /* access modifiers changed from: private */
    public boolean mExternallyEnabled = true;
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    private boolean mGoingToSleep;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public Animation mHideAnimation;
    /* access modifiers changed from: private */
    public final Runnable mHideAnimationFinishedRunnable;
    /* access modifiers changed from: private */
    public boolean mHideAnimationRun = false;
    /* access modifiers changed from: private */
    public boolean mHideAnimationRunning = false;
    private boolean mHiding;
    private boolean mInGestureNavigationMode;
    private boolean mInputRestricted = true;
    private boolean mKeyguardChecked = false;
    /* access modifiers changed from: private */
    public final KeyguardDisplayManager mKeyguardDisplayManager;
    /* access modifiers changed from: private */
    public boolean mKeyguardDonePending = false;
    private IRemoteAnimationRunner mKeyguardExitAnimationRunner;
    /* access modifiers changed from: private */
    public boolean mKeyguardGoingAway;
    private final Runnable mKeyguardGoingAwayRunnable;
    /* access modifiers changed from: private */
    public final ArrayList<IKeyguardStateCallback> mKeyguardStateCallbacks = new ArrayList<>();
    private final KeyguardStateController mKeyguardStateController;
    private final Lazy<KeyguardUnlockAnimationController> mKeyguardUnlockAnimationControllerLazy;
    /* access modifiers changed from: private */
    public final Lazy<KeyguardViewController> mKeyguardViewControllerLazy;
    /* access modifiers changed from: private */
    public final SparseIntArray mLastSimStates = new SparseIntArray();
    private int mLockAfterTimeOut;
    private boolean mLockLater;
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    private int mLockSoundId;
    private int mLockSoundStreamId;
    private float mLockSoundVolume;
    private SoundPool mLockSounds;
    private boolean mNeedToReshowWhenReenabled = false;
    private final Lazy<NotificationShadeDepthController> mNotificationShadeDepthController;
    /* access modifiers changed from: private */
    public boolean mOccluded = false;
    private final DeviceConfig.OnPropertiesChangedListener mOnPropertiesChangedListener;
    private final PowerManager mPM;
    private boolean mPendingLock;
    /* access modifiers changed from: private */
    public boolean mPendingPinLock = false;
    private boolean mPendingReset;
    /* access modifiers changed from: private */
    public int mPhoneState = 0;
    private boolean mPowerGestureIntercepted = false;
    /* access modifiers changed from: private */
    public boolean mPulsing;
    /* access modifiers changed from: private */
    public boolean mShowHomeOverLockscreen;
    private PowerManager.WakeLock mShowKeyguardWakeLock;
    /* access modifiers changed from: private */
    public boolean mShowing = true;
    /* access modifiers changed from: private */
    public boolean mShuttingDown;
    /* access modifiers changed from: private */
    public final SparseBooleanArray mSimWasLocked = new SparseBooleanArray();
    private StatusBarManager mStatusBarManager;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private IRemoteAnimationFinishedCallback mSurfaceBehindRemoteAnimationFinishedCallback;
    private boolean mSurfaceBehindRemoteAnimationRequested = false;
    private boolean mSurfaceBehindRemoteAnimationRunning;
    private boolean mSystemReady;
    private final TrustManager mTrustManager;
    private int mTrustedSoundId;
    /* access modifiers changed from: private */
    public final Executor mUiBgExecutor;
    private int mUiSoundsStreamType;
    private int mUnlockSoundId;
    private final UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    KeyguardUpdateMonitorCallback mUpdateCallback;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    private final UserSwitcherController mUserSwitcherController;
    ViewMediatorCallback mViewMediatorCallback;
    private boolean mWaitingUntilKeyguardVisible = false;
    /* access modifiers changed from: private */
    public boolean mWakeAndUnlocking;
    private WorkLockActivityController mWorkLockController;

    public void onShortPowerPressedGoHome() {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardViewMediator(Context context, FalsingCollector falsingCollector, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, Lazy<KeyguardViewController> lazy, DismissCallbackRegistry dismissCallbackRegistry, KeyguardUpdateMonitor keyguardUpdateMonitor, DumpManager dumpManager, Executor executor, PowerManager powerManager, TrustManager trustManager, UserSwitcherController userSwitcherController, DeviceConfigProxy deviceConfigProxy, NavigationModeController navigationModeController, KeyguardDisplayManager keyguardDisplayManager, DozeParameters dozeParameters, SysuiStatusBarStateController sysuiStatusBarStateController, KeyguardStateController keyguardStateController, Lazy<KeyguardUnlockAnimationController> lazy2, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, Lazy<NotificationShadeDepthController> lazy3) {
        super(context);
        DeviceConfigProxy deviceConfigProxy2 = deviceConfigProxy;
        SysuiStatusBarStateController sysuiStatusBarStateController2 = sysuiStatusBarStateController;
        C09931 r4 = new DeviceConfig.OnPropertiesChangedListener() {
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                if (properties.getKeyset().contains("nav_bar_handle_show_over_lockscreen")) {
                    boolean unused = KeyguardViewMediator.this.mShowHomeOverLockscreen = properties.getBoolean("nav_bar_handle_show_over_lockscreen", true);
                }
            }
        };
        this.mOnPropertiesChangedListener = r4;
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            public void onUserInfoChanged(int i) {
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                synchronized (KeyguardViewMediator.this) {
                    if (!z) {
                        if (KeyguardViewMediator.this.mPendingPinLock) {
                            Log.i("KeyguardViewMediator", "PIN lock requested, starting keyguard");
                            boolean unused = KeyguardViewMediator.this.mPendingPinLock = false;
                            KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                        }
                    }
                }
            }

            public void onUserSwitching(int i) {
                if (KeyguardViewMediator.DEBUG) {
                    Log.d("KeyguardViewMediator", String.format("onUserSwitching %d", new Object[]{Integer.valueOf(i)}));
                }
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.resetKeyguardDonePendingLocked();
                    if (KeyguardViewMediator.this.mLockPatternUtils.isLockScreenDisabled(i)) {
                        KeyguardViewMediator.this.dismiss((IKeyguardDismissCallback) null, (CharSequence) null);
                    } else {
                        KeyguardViewMediator.this.resetStateLocked();
                    }
                    KeyguardViewMediator.this.adjustStatusBarLocked();
                }
            }

            public void onUserSwitchComplete(int i) {
                UserInfo userInfo;
                if (KeyguardViewMediator.DEBUG) {
                    Log.d("KeyguardViewMediator", String.format("onUserSwitchComplete %d", new Object[]{Integer.valueOf(i)}));
                }
                if (i != 0 && (userInfo = UserManager.get(KeyguardViewMediator.this.mContext).getUserInfo(i)) != null && !KeyguardViewMediator.this.mLockPatternUtils.isSecure(i)) {
                    if (userInfo.isGuest() || userInfo.isDemo()) {
                        KeyguardViewMediator.this.dismiss((IKeyguardDismissCallback) null, (CharSequence) null);
                    }
                }
            }

            public void onPhoneStateChanged(int i) {
                synchronized (KeyguardViewMediator.this) {
                    int unused = KeyguardViewMediator.this.mPhoneState = i;
                    if (i == 1 && KeyguardViewMediator.this.mOccluded && ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).isBouncerShowing()) {
                        Log.d("KeyguardViewMediator", "Receive incoming call when keyguard is occluded, hide keyguard and collapse notification panel");
                        ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).reset(true);
                    }
                }
            }

            public void onClockVisibilityChanged() {
                KeyguardViewMediator.this.adjustStatusBarLocked();
            }

            public void onDeviceProvisioned() {
                KeyguardViewMediator.this.sendUserPresentBroadcast();
                synchronized (KeyguardViewMediator.this) {
                    if (KeyguardViewMediator.this.mustNotUnlockCurrentUser()) {
                        KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                    }
                }
            }

            public void onSimStateChanged(int i, int i2, int i3) {
                boolean z;
                Log.d("KeyguardViewMediator", "onSimStateChanged(subId=" + i + ", slotId=" + i2 + ",state=" + i3 + ")");
                int size = KeyguardViewMediator.this.mKeyguardStateCallbacks.size();
                boolean isSimPinSecure = KeyguardViewMediator.this.mUpdateMonitor.isSimPinSecure();
                for (int i4 = size - 1; i4 >= 0; i4--) {
                    try {
                        ((IKeyguardStateCallback) KeyguardViewMediator.this.mKeyguardStateCallbacks.get(i4)).onSimSecureStateChanged(isSimPinSecure);
                    } catch (RemoteException e) {
                        Slog.w("KeyguardViewMediator", "Failed to call onSimSecureStateChanged", e);
                        if (e instanceof DeadObjectException) {
                            KeyguardViewMediator.this.mKeyguardStateCallbacks.remove(i4);
                        }
                    }
                }
                synchronized (KeyguardViewMediator.this) {
                    int i5 = KeyguardViewMediator.this.mLastSimStates.get(i2);
                    if (i5 != 2) {
                        if (i5 != 3) {
                            z = false;
                            KeyguardViewMediator.this.mLastSimStates.append(i2, i3);
                        }
                    }
                    z = true;
                    KeyguardViewMediator.this.mLastSimStates.append(i2, i3);
                }
                if (i3 != 1) {
                    if (i3 == 2 || i3 == 3) {
                        synchronized (KeyguardViewMediator.this) {
                            KeyguardViewMediator.this.mSimWasLocked.append(i2, true);
                            if (!KeyguardViewMediator.this.mShowing) {
                                Log.d("KeyguardViewMediator", "INTENT_VALUE_ICC_LOCKED and keygaurd isn't showing; need to show keyguard so user can enter sim pin");
                                KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                            } else {
                                boolean unused = KeyguardViewMediator.this.mPendingPinLock = true;
                                KeyguardViewMediator.this.resetStateLocked();
                            }
                        }
                        return;
                    } else if (i3 == 5) {
                        synchronized (KeyguardViewMediator.this) {
                            Log.d("KeyguardViewMediator", "READY, reset state? " + KeyguardViewMediator.this.mShowing);
                            if (KeyguardViewMediator.this.mShowing && KeyguardViewMediator.this.mSimWasLocked.get(i2, false)) {
                                Log.d("KeyguardViewMediator", "SIM moved to READY when the previously was locked. Reset the state.");
                                KeyguardViewMediator.this.mSimWasLocked.append(i2, false);
                                boolean unused2 = KeyguardViewMediator.this.mPendingPinLock = false;
                                KeyguardViewMediator.this.resetStateLocked();
                            }
                        }
                        return;
                    } else if (i3 != 6) {
                        if (i3 != 7) {
                            Log.v("KeyguardViewMediator", "Unspecific state: " + i3);
                            return;
                        }
                        synchronized (KeyguardViewMediator.this) {
                            if (!KeyguardViewMediator.this.mShowing) {
                                Log.d("KeyguardViewMediator", "PERM_DISABLED and keygaurd isn't showing.");
                                KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                            } else {
                                Log.d("KeyguardViewMediator", "PERM_DISABLED, resetStateLocked toshow permanently disabled message in lockscreen.");
                                KeyguardViewMediator.this.resetStateLocked();
                            }
                        }
                        return;
                    }
                }
                synchronized (KeyguardViewMediator.this) {
                    if (KeyguardViewMediator.this.shouldWaitForProvisioning()) {
                        if (!KeyguardViewMediator.this.mShowing) {
                            Log.d("KeyguardViewMediator", "ICC_ABSENT isn't showing, we need to show the keyguard since the device isn't provisioned yet.");
                            KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                        } else {
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    if (i3 == 1) {
                        if (z) {
                            Log.d("KeyguardViewMediator", "SIM moved to ABSENT when the previous state was locked. Reset the state.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                        KeyguardViewMediator.this.mSimWasLocked.append(i2, false);
                    }
                }
            }

            public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
                int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(currentUser)) {
                    KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportFailedBiometricAttempt(currentUser);
                }
            }

            public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
                if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(i)) {
                    KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportSuccessfulBiometricAttempt(i);
                }
            }

            public void onTrustChanged(int i) {
                if (i == KeyguardUpdateMonitor.getCurrentUser()) {
                    synchronized (KeyguardViewMediator.this) {
                        KeyguardViewMediator keyguardViewMediator = KeyguardViewMediator.this;
                        keyguardViewMediator.notifyTrustedChangedLocked(keyguardViewMediator.mUpdateMonitor.getUserHasTrust(i));
                    }
                }
            }

            public void onHasLockscreenWallpaperChanged(boolean z) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.notifyHasLockscreenWallpaperChanged(z);
                }
            }
        };
        this.mViewMediatorCallback = new ViewMediatorCallback() {
            public void userActivity() {
                KeyguardViewMediator.this.userActivity();
            }

            public void keyguardDone(boolean z, int i) {
                if (i == ActivityManager.getCurrentUser()) {
                    if (KeyguardViewMediator.DEBUG) {
                        Log.d("KeyguardViewMediator", "keyguardDone");
                    }
                    KeyguardViewMediator.this.tryKeyguardDone();
                }
            }

            public void keyguardDoneDrawing() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDoneDrawing");
                KeyguardViewMediator.this.mHandler.sendEmptyMessage(8);
                Trace.endSection();
            }

            public void setNeedsInput(boolean z) {
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).setNeedsInput(z);
            }

            public void keyguardDonePending(boolean z, int i) {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDonePending");
                if (KeyguardViewMediator.DEBUG) {
                    Log.d("KeyguardViewMediator", "keyguardDonePending");
                }
                if (i != ActivityManager.getCurrentUser()) {
                    Trace.endSection();
                    return;
                }
                boolean unused = KeyguardViewMediator.this.mKeyguardDonePending = true;
                boolean unused2 = KeyguardViewMediator.this.mHideAnimationRun = true;
                boolean unused3 = KeyguardViewMediator.this.mHideAnimationRunning = true;
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).startPreHideAnimation(KeyguardViewMediator.this.mHideAnimationFinishedRunnable);
                KeyguardViewMediator.this.mHandler.sendEmptyMessageDelayed(13, 3000);
                Trace.endSection();
            }

            public void keyguardGone() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardGone");
                if (KeyguardViewMediator.DEBUG) {
                    Log.d("KeyguardViewMediator", "keyguardGone");
                }
                boolean unused = KeyguardViewMediator.this.mKeyguardGoingAway = false;
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).setKeyguardGoingAwayState(false);
                KeyguardViewMediator.this.mKeyguardDisplayManager.hide();
                Trace.endSection();
            }

            public void readyForKeyguardDone() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#readyForKeyguardDone");
                if (KeyguardViewMediator.this.mKeyguardDonePending) {
                    boolean unused = KeyguardViewMediator.this.mKeyguardDonePending = false;
                    KeyguardViewMediator.this.tryKeyguardDone();
                }
                Trace.endSection();
            }

            public void resetKeyguard() {
                KeyguardViewMediator.this.resetStateLocked();
            }

            public void onCancelClicked() {
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).onCancelClicked();
            }

            public boolean isExternalEnabled() {
                return KeyguardViewMediator.this.mExternallyEnabled;
            }

            public void onBouncerVisiblityChanged(boolean z) {
                synchronized (KeyguardViewMediator.this) {
                    if (z) {
                        boolean unused = KeyguardViewMediator.this.mPendingPinLock = false;
                    }
                    KeyguardViewMediator.this.adjustStatusBarLocked(z, false);
                }
            }

            public void playTrustedSound() {
                KeyguardViewMediator.this.playTrustedSound();
            }

            public boolean isScreenOn() {
                return KeyguardViewMediator.this.mDeviceInteractive;
            }

            public int getBouncerPromptReason() {
                int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                boolean isTrustUsuallyManaged = KeyguardViewMediator.this.mUpdateMonitor.isTrustUsuallyManaged(currentUser);
                boolean z = isTrustUsuallyManaged || KeyguardViewMediator.this.mUpdateMonitor.isUnlockingWithBiometricsPossible(currentUser);
                KeyguardUpdateMonitor.StrongAuthTracker strongAuthTracker = KeyguardViewMediator.this.mUpdateMonitor.getStrongAuthTracker();
                int strongAuthForUser = strongAuthTracker.getStrongAuthForUser(currentUser);
                if (z && !strongAuthTracker.hasUserAuthenticatedSinceBoot()) {
                    return 1;
                }
                if (z && (strongAuthForUser & 16) != 0) {
                    return 2;
                }
                if (z && (strongAuthForUser & 2) != 0) {
                    return 3;
                }
                if (isTrustUsuallyManaged && (strongAuthForUser & 4) != 0) {
                    return 4;
                }
                if (z && (strongAuthForUser & 8) != 0) {
                    return 5;
                }
                if (z && (strongAuthForUser & 64) != 0) {
                    return 6;
                }
                if (!z || (strongAuthForUser & 128) == 0) {
                    return 0;
                }
                return 7;
            }

            public CharSequence consumeCustomMessage() {
                CharSequence access$3200 = KeyguardViewMediator.this.mCustomMessage;
                CharSequence unused = KeyguardViewMediator.this.mCustomMessage = null;
                return access$3200;
            }
        };
        this.mDelayedLockBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD".equals(intent.getAction())) {
                    int intExtra = intent.getIntExtra("seq", 0);
                    if (KeyguardViewMediator.DEBUG) {
                        Log.d("KeyguardViewMediator", "received DELAYED_KEYGUARD_ACTION with seq = " + intExtra + ", mDelayedShowingSequence = " + KeyguardViewMediator.this.mDelayedShowingSequence);
                    }
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.mDelayedShowingSequence == intExtra) {
                            KeyguardViewMediator.this.doKeyguardLocked((Bundle) null);
                        }
                    }
                } else if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK".equals(intent.getAction())) {
                    int intExtra2 = intent.getIntExtra("seq", 0);
                    int intExtra3 = intent.getIntExtra("android.intent.extra.USER_ID", 0);
                    if (intExtra3 != 0) {
                        synchronized (KeyguardViewMediator.this) {
                            if (KeyguardViewMediator.this.mDelayedProfileShowingSequence == intExtra2) {
                                KeyguardViewMediator.this.lockProfile(intExtra3);
                            }
                        }
                    }
                }
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                    synchronized (KeyguardViewMediator.this) {
                        boolean unused = KeyguardViewMediator.this.mShuttingDown = true;
                    }
                } else if ("com.motorola.internal.policy.impl.REQUEST_UNLOCK".equals(intent.getAction())) {
                    IBinder iBinderExtra = intent.getIBinderExtra("callback");
                    boolean booleanExtra = intent.getBooleanExtra("dismiss", true);
                    boolean booleanExtra2 = intent.getBooleanExtra("collapse", false);
                    if (KeyguardViewMediator.DEBUG) {
                        Log.d("KeyguardViewMediator", "received REQUEST_UNLOCK_ACTION.equals dismiss = " + booleanExtra + ", collapse = " + booleanExtra2);
                    }
                    Message obtainMessage = KeyguardViewMediator.this.mHandler.obtainMessage(100, iBinderExtra);
                    obtainMessage.arg1 = booleanExtra ? 1 : 0;
                    obtainMessage.arg2 = booleanExtra2 ? 1 : 0;
                    KeyguardViewMediator.this.mHandler.sendMessage(obtainMessage);
                }
            }
        };
        this.mHandler = new Handler(Looper.myLooper(), (Handler.Callback) null, true) {
            public void handleMessage(Message message) {
                int i = message.what;
                boolean z = true;
                if (i == 100) {
                    KeyguardViewMediator keyguardViewMediator = KeyguardViewMediator.this;
                    IBinder iBinder = (IBinder) message.obj;
                    boolean z2 = message.arg1 != 0;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    keyguardViewMediator.handleRequestUnlock(iBinder, z2, z);
                } else if (i != 101) {
                    switch (i) {
                        case 1:
                            KeyguardViewMediator.this.handleShow((Bundle) message.obj);
                            return;
                        case 2:
                            KeyguardViewMediator.this.handleHide();
                            return;
                        case 3:
                            KeyguardViewMediator.this.handleReset();
                            return;
                        case 4:
                            Trace.beginSection("KeyguardViewMediator#handleMessage VERIFY_UNLOCK");
                            KeyguardViewMediator.this.handleVerifyUnlock();
                            Trace.endSection();
                            return;
                        case 5:
                            KeyguardViewMediator.this.handleNotifyFinishedGoingToSleep();
                            return;
                        case 6:
                            Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNING_ON");
                            KeyguardViewMediator.this.handleNotifyScreenTurningOn((IKeyguardDrawnCallback) message.obj);
                            Trace.endSection();
                            return;
                        case 7:
                            Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE");
                            KeyguardViewMediator.this.handleKeyguardDone();
                            Trace.endSection();
                            return;
                        case 8:
                            Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_DRAWING");
                            KeyguardViewMediator.this.handleKeyguardDoneDrawing();
                            Trace.endSection();
                            return;
                        case 9:
                            Trace.beginSection("KeyguardViewMediator#handleMessage SET_OCCLUDED");
                            KeyguardViewMediator keyguardViewMediator2 = KeyguardViewMediator.this;
                            boolean z3 = message.arg1 != 0;
                            if (message.arg2 == 0) {
                                z = false;
                            }
                            keyguardViewMediator2.handleSetOccluded(z3, z);
                            Trace.endSection();
                            return;
                        case 10:
                            synchronized (KeyguardViewMediator.this) {
                                KeyguardViewMediator.this.doKeyguardLocked((Bundle) message.obj);
                            }
                            return;
                        case 11:
                            DismissMessage dismissMessage = (DismissMessage) message.obj;
                            KeyguardViewMediator.this.handleDismiss(dismissMessage.getCallback(), dismissMessage.getMessage());
                            return;
                        case 12:
                            Trace.beginSection("KeyguardViewMediator#handleMessage START_KEYGUARD_EXIT_ANIM");
                            StartKeyguardExitAnimParams startKeyguardExitAnimParams = (StartKeyguardExitAnimParams) message.obj;
                            KeyguardViewMediator.this.handleStartKeyguardExitAnimation(startKeyguardExitAnimParams.startTime, startKeyguardExitAnimParams.fadeoutDuration, startKeyguardExitAnimParams.mApps, startKeyguardExitAnimParams.mWallpapers, startKeyguardExitAnimParams.mNonApps, startKeyguardExitAnimParams.mFinishedCallback);
                            KeyguardViewMediator.this.mFalsingCollector.onSuccessfulUnlock();
                            Trace.endSection();
                            return;
                        case 13:
                            Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_PENDING_TIMEOUT");
                            Log.w("KeyguardViewMediator", "Timeout while waiting for activity drawn!");
                            Trace.endSection();
                            return;
                        case 14:
                            Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_STARTED_WAKING_UP");
                            KeyguardViewMediator.this.handleNotifyStartedWakingUp();
                            Trace.endSection();
                            return;
                        case 15:
                            Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNED_ON");
                            KeyguardViewMediator.this.handleNotifyScreenTurnedOn();
                            Trace.endSection();
                            return;
                        case 16:
                            KeyguardViewMediator.this.handleNotifyScreenTurnedOff();
                            return;
                        case 17:
                            KeyguardViewMediator.this.handleNotifyStartedGoingToSleep();
                            return;
                        case 18:
                            KeyguardViewMediator.this.handleSystemReady();
                            return;
                        case 19:
                            Trace.beginSection("KeyguardViewMediator#handleMessage CANCEL_KEYGUARD_EXIT_ANIM");
                            KeyguardViewMediator.this.handleCancelKeyguardExitAnimation();
                            Trace.endSection();
                            return;
                        default:
                            return;
                    }
                } else {
                    Log.w("KeyguardViewMediator", "Timeout waiting to start keygaurd exit animation");
                    KeyguardViewMediator.this.handleStartKeyguardExitAnimation(SystemClock.uptimeMillis() + KeyguardViewMediator.this.mHideAnimation.getStartOffset(), KeyguardViewMediator.this.mHideAnimation.getDuration(), (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (IRemoteAnimationFinishedCallback) null);
                    KeyguardViewMediator.this.mFalsingCollector.onSuccessfulUnlock();
                }
            }
        };
        this.mKeyguardGoingAwayRunnable = new Runnable() {
            public void run() {
                Trace.beginSection("KeyguardViewMediator.mKeyGuardGoingAwayRunnable");
                if (KeyguardViewMediator.DEBUG) {
                    Log.d("KeyguardViewMediator", "keyguardGoingAway");
                }
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).keyguardGoingAway();
                Log.d("KeyguardViewMediator", "Call PhoneWindowManager's keyguardGoingAway, expect PhoneWindowManager to call startKeyguardExitAnimation");
                KeyguardViewMediator.this.mHandler.removeMessages(R$styleable.Constraint_layout_goneMarginRight);
                Message obtainMessage = KeyguardViewMediator.this.mHandler.obtainMessage(R$styleable.Constraint_layout_goneMarginRight);
                int i = 0;
                if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).shouldDisableWindowAnimationsForUnlock() || ((KeyguardViewMediator.this.mWakeAndUnlocking && !KeyguardViewMediator.this.mPulsing) || KeyguardViewMediator.this.isAnimatingBetweenKeyguardAndSurfaceBehindOrWillBe())) {
                    i = 2;
                }
                if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).isGoingToNotificationShade() || (KeyguardViewMediator.this.mWakeAndUnlocking && KeyguardViewMediator.this.mPulsing)) {
                    i |= 1;
                }
                if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).isUnlockWithWallpaper()) {
                    i |= 4;
                }
                if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).shouldSubtleWindowAnimationsForUnlock()) {
                    i |= 8;
                }
                KeyguardViewMediator.this.mUpdateMonitor.setKeyguardGoingAway(true);
                boolean unused = KeyguardViewMediator.this.mKeyguardGoingAway = true;
                ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).setKeyguardGoingAwayState(true);
                if (KeyguardViewMediator.this.mWakeAndUnlocking) {
                    KeyguardViewMediator.this.handleStartKeyguardExitAnimation(0, 0, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (IRemoteAnimationFinishedCallback) null);
                    KeyguardViewMediator.this.mFalsingCollector.onSuccessfulUnlock();
                } else {
                    KeyguardViewMediator.this.mHandler.sendMessageDelayed(obtainMessage, 1000);
                }
                KeyguardViewMediator.this.mUiBgExecutor.execute(new KeyguardViewMediator$7$$ExternalSyntheticLambda0(i));
                Trace.endSection();
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$run$0(int i) {
                try {
                    ActivityTaskManager.getService().keyguardGoingAway(i);
                } catch (RemoteException e) {
                    Log.e("KeyguardViewMediator", "Error while calling WindowManager", e);
                }
            }
        };
        this.mHideAnimationFinishedRunnable = new KeyguardViewMediator$$ExternalSyntheticLambda3(this);
        this.mFalsingCollector = falsingCollector;
        this.mLockPatternUtils = lockPatternUtils;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mKeyguardViewControllerLazy = lazy;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mNotificationShadeDepthController = lazy3;
        this.mUiBgExecutor = executor;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mPM = powerManager;
        this.mTrustManager = trustManager;
        this.mUserSwitcherController = userSwitcherController;
        this.mKeyguardDisplayManager = keyguardDisplayManager;
        dumpManager.registerDumpable(getClass().getName(), this);
        this.mDeviceConfig = deviceConfigProxy2;
        this.mShowHomeOverLockscreen = deviceConfigProxy2.getBoolean("systemui", "nav_bar_handle_show_over_lockscreen", true);
        DeviceConfigProxy deviceConfigProxy3 = this.mDeviceConfig;
        Handler handler = this.mHandler;
        Objects.requireNonNull(handler);
        deviceConfigProxy3.addOnPropertiesChangedListener("systemui", new MediaRoute2Provider$$ExternalSyntheticLambda0(handler), r4);
        this.mInGestureNavigationMode = QuickStepContract.isGesturalMode(navigationModeController.addListener(new KeyguardViewMediator$$ExternalSyntheticLambda1(this)));
        this.mDozeParameters = dozeParameters;
        this.mStatusBarStateController = sysuiStatusBarStateController2;
        sysuiStatusBarStateController2.addCallback(this);
        this.mKeyguardStateController = keyguardStateController;
        this.mKeyguardUnlockAnimationControllerLazy = lazy2;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.mInGestureNavigationMode = QuickStepContract.isGesturalMode(i);
    }

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    /* access modifiers changed from: package-private */
    public boolean mustNotUnlockCurrentUser() {
        return UserManager.isSplitSystemUser() && KeyguardUpdateMonitor.getCurrentUser() == 0;
    }

    private void setupLocked() {
        PowerManager.WakeLock newWakeLock = this.mPM.newWakeLock(1, "show keyguard");
        this.mShowKeyguardWakeLock = newWakeLock;
        boolean z = false;
        newWakeLock.setReferenceCounted(false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        intentFilter.addAction("com.motorola.internal.policy.impl.REQUEST_UNLOCK");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
        this.mContext.registerReceiver(this.mDelayedLockBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", (Handler) null);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUpdateMonitor.setKeyguardDisplayManager(this.mKeyguardDisplayManager);
        KeyguardUpdateMonitor.setCurrentUser(ActivityManager.getCurrentUser());
        if (isKeyguardServiceEnabled()) {
            if (!shouldWaitForProvisioning() && !this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                z = true;
            }
            setShowingLocked(z, true);
        } else {
            setShowingLocked(false, true);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mDeviceInteractive = this.mPM.isInteractive();
        this.mLockSounds = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
        String string = Settings.Global.getString(contentResolver, "lock_sound");
        if (string != null) {
            this.mLockSoundId = this.mLockSounds.load(string, 1);
        }
        if (string == null || this.mLockSoundId == 0) {
            Log.w("KeyguardViewMediator", "failed to load lock sound from " + string);
        }
        String string2 = Settings.Global.getString(contentResolver, "unlock_sound");
        if (string2 != null) {
            this.mUnlockSoundId = this.mLockSounds.load(string2, 1);
        }
        if (string2 == null || this.mUnlockSoundId == 0) {
            Log.w("KeyguardViewMediator", "failed to load unlock sound from " + string2);
        }
        String string3 = Settings.Global.getString(contentResolver, "trusted_sound");
        if (string3 != null) {
            this.mTrustedSoundId = this.mLockSounds.load(string3, 1);
        }
        if (string3 == null || this.mTrustedSoundId == 0) {
            Log.w("KeyguardViewMediator", "failed to load trusted sound from " + string3);
        }
        this.mLockSoundVolume = (float) Math.pow(10.0d, (double) (((float) this.mContext.getResources().getInteger(17694856)) / 20.0f));
        this.mHideAnimation = AnimationUtils.loadAnimation(this.mContext, 17432683);
        this.mWorkLockController = new WorkLockActivityController(this.mContext);
        try {
            this.mLockAfterTimeOut = this.mContext.getResources().getInteger(17694996);
        } catch (Resources.NotFoundException e) {
            this.mLockAfterTimeOut = 5000;
            Log.d("KeyguardViewMediator", "The exception is" + e);
        }
    }

    public void start() {
        synchronized (this) {
            setupLocked();
        }
    }

    public void onSystemReady() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "onSystemReady");
            }
            this.mSystemReady = true;
            doKeyguardChecked((Bundle) null);
        }
        this.mHandler.obtainMessage(18).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void handleSystemReady() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleSystemReady");
            }
            if (this.mKeyguardChecked) {
                showLocked((Bundle) null);
            }
            this.mUpdateMonitor.registerCallback(this.mUpdateCallback);
        }
        maybeSendUserPresentBroadcast();
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004e A[Catch:{ RemoteException -> 0x005d }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0070 A[Catch:{ RemoteException -> 0x005d }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a8 A[Catch:{ RemoteException -> 0x005d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStartedGoingToSleep(int r10) {
        /*
            r9 = this;
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x001f
            java.lang.String r1 = "KeyguardViewMediator"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "onStartedGoingToSleep("
            r2.append(r3)
            r2.append(r10)
            java.lang.String r3 = ")"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r1, r2)
        L_0x001f:
            monitor-enter(r9)
            r1 = 0
            r9.mDeviceInteractive = r1     // Catch:{ all -> 0x00ba }
            r9.mPowerGestureIntercepted = r1     // Catch:{ all -> 0x00ba }
            r2 = 1
            r9.mGoingToSleep = r2     // Catch:{ all -> 0x00ba }
            int r3 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch:{ all -> 0x00ba }
            com.android.internal.widget.LockPatternUtils r4 = r9.mLockPatternUtils     // Catch:{ all -> 0x00ba }
            boolean r4 = r4.getPowerButtonInstantlyLocks(r3)     // Catch:{ all -> 0x00ba }
            if (r4 != 0) goto L_0x003f
            com.android.internal.widget.LockPatternUtils r4 = r9.mLockPatternUtils     // Catch:{ all -> 0x00ba }
            boolean r4 = r4.isSecure(r3)     // Catch:{ all -> 0x00ba }
            if (r4 != 0) goto L_0x003d
            goto L_0x003f
        L_0x003d:
            r4 = r1
            goto L_0x0040
        L_0x003f:
            r4 = r2
        L_0x0040:
            int r5 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch:{ all -> 0x00ba }
            long r5 = r9.getLockTimeout(r5)     // Catch:{ all -> 0x00ba }
            r9.mLockLater = r1     // Catch:{ all -> 0x00ba }
            com.android.internal.policy.IKeyguardExitCallback r7 = r9.mExitSecureCallback     // Catch:{ all -> 0x00ba }
            if (r7 == 0) goto L_0x0070
            if (r0 == 0) goto L_0x0057
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r3 = "pending exit secure callback cancelled"
            android.util.Log.d(r0, r3)     // Catch:{ all -> 0x00ba }
        L_0x0057:
            com.android.internal.policy.IKeyguardExitCallback r0 = r9.mExitSecureCallback     // Catch:{ RemoteException -> 0x005d }
            r0.onKeyguardExitResult(r1)     // Catch:{ RemoteException -> 0x005d }
            goto L_0x0065
        L_0x005d:
            r0 = move-exception
            java.lang.String r3 = "KeyguardViewMediator"
            java.lang.String r4 = "Failed to call onKeyguardExitResult(false)"
            android.util.Slog.w(r3, r4, r0)     // Catch:{ all -> 0x00ba }
        L_0x0065:
            r0 = 0
            r9.mExitSecureCallback = r0     // Catch:{ all -> 0x00ba }
            boolean r0 = r9.mExternallyEnabled     // Catch:{ all -> 0x00ba }
            if (r0 != 0) goto L_0x00a4
            r9.hideLocked()     // Catch:{ all -> 0x00ba }
            goto L_0x00a4
        L_0x0070:
            boolean r0 = r9.mShowing     // Catch:{ all -> 0x00ba }
            if (r0 == 0) goto L_0x007b
            boolean r0 = r9.mKeyguardGoingAway     // Catch:{ all -> 0x00ba }
            if (r0 != 0) goto L_0x007b
            r9.mPendingReset = r2     // Catch:{ all -> 0x00ba }
            goto L_0x00a4
        L_0x007b:
            r0 = 3
            if (r10 != r0) goto L_0x0084
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 > 0) goto L_0x0094
        L_0x0084:
            r0 = 2
            if (r10 != r0) goto L_0x0089
            if (r4 == 0) goto L_0x0094
        L_0x0089:
            r0 = 4
            if (r10 != r0) goto L_0x009a
            com.android.internal.widget.LockPatternUtils r0 = r9.mLockPatternUtils     // Catch:{ all -> 0x00ba }
            boolean r0 = r0.getFingerprintInstantlyLocked(r3)     // Catch:{ all -> 0x00ba }
            if (r0 != 0) goto L_0x009a
        L_0x0094:
            r9.doKeyguardLaterLocked(r5)     // Catch:{ all -> 0x00ba }
            r9.mLockLater = r2     // Catch:{ all -> 0x00ba }
            goto L_0x00a4
        L_0x009a:
            com.android.internal.widget.LockPatternUtils r0 = r9.mLockPatternUtils     // Catch:{ all -> 0x00ba }
            boolean r0 = r0.isLockScreenDisabled(r3)     // Catch:{ all -> 0x00ba }
            if (r0 != 0) goto L_0x00a4
            r9.mPendingLock = r2     // Catch:{ all -> 0x00ba }
        L_0x00a4:
            boolean r0 = r9.mPendingLock     // Catch:{ all -> 0x00ba }
            if (r0 == 0) goto L_0x00ab
            r9.playSounds(r2)     // Catch:{ all -> 0x00ba }
        L_0x00ab:
            monitor-exit(r9)     // Catch:{ all -> 0x00ba }
            com.android.keyguard.KeyguardUpdateMonitor r0 = r9.mUpdateMonitor
            r0.dispatchStartedGoingToSleep(r10)
            com.android.keyguard.KeyguardUpdateMonitor r10 = r9.mUpdateMonitor
            r10.dispatchKeyguardGoingAway(r1)
            r9.notifyStartedGoingToSleep()
            return
        L_0x00ba:
            r10 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00ba }
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.onStartedGoingToSleep(int):void");
    }

    public void onFinishedGoingToSleep(int i, boolean z) {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "onFinishedGoingToSleep(" + i + ")");
        }
        synchronized (this) {
            this.mDeviceInteractive = false;
            this.mGoingToSleep = false;
            this.mWakeAndUnlocking = false;
            this.mAnimatingScreenOff = this.mDozeParameters.shouldControlUnlockedScreenOff();
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            notifyFinishedGoingToSleep();
            if (z) {
                ((PowerManager) this.mContext.getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE_PREVENT_LOCK");
                this.mPendingLock = false;
                this.mPendingReset = false;
            }
            if (this.mPendingReset) {
                resetStateLocked();
                this.mPendingReset = false;
            }
            maybeHandlePendingLock();
            if (!this.mLockLater && !z) {
                doKeyguardForChildProfilesLocked();
            }
        }
        this.mUpdateMonitor.dispatchFinishedGoingToSleep(i);
    }

    public void maybeHandlePendingLock() {
        if (this.mPendingLock && !this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying()) {
            doKeyguardLocked((Bundle) null);
            this.mPendingLock = false;
        }
    }

    private boolean isKeyguardServiceEnabled() {
        try {
            return this.mContext.getPackageManager().getServiceInfo(new ComponentName(this.mContext, KeyguardService.class), 0).isEnabled();
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }

    private long getLockTimeout(int i) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        long j = (long) Settings.Secure.getInt(contentResolver, "lock_screen_lock_after_timeout", this.mLockAfterTimeOut);
        long maximumTimeToLock = this.mLockPatternUtils.getDevicePolicyManager().getMaximumTimeToLock((ComponentName) null, i);
        return maximumTimeToLock <= 0 ? j : Math.max(Math.min(maximumTimeToLock - Math.max((long) Settings.System.getInt(contentResolver, "screen_off_timeout", 30000), 0), j), 0);
    }

    private void doKeyguardLaterLocked() {
        long lockTimeout = getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
        if (lockTimeout == 0) {
            doKeyguardLocked((Bundle) null);
        } else {
            doKeyguardLaterLocked(lockTimeout);
        }
    }

    private void doKeyguardLaterLocked(long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime() + j;
        Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intent.putExtra("seq", this.mDelayedShowingSequence);
        intent.addFlags(268435456);
        this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime, PendingIntent.getBroadcast(this.mContext, 0, intent, 335544320));
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "setting alarm to turn off keyguard, seq = " + this.mDelayedShowingSequence);
        }
        doKeyguardLaterForChildProfilesLocked();
    }

    private void doKeyguardLaterForChildProfilesLocked() {
        for (int i : UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
                long lockTimeout = getLockTimeout(i);
                if (lockTimeout == 0) {
                    doKeyguardForChildProfilesLocked();
                } else {
                    long elapsedRealtime = SystemClock.elapsedRealtime() + lockTimeout;
                    Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
                    intent.putExtra("seq", this.mDelayedProfileShowingSequence);
                    intent.putExtra("android.intent.extra.USER_ID", i);
                    intent.addFlags(268435456);
                    this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime, PendingIntent.getBroadcast(this.mContext, 0, intent, 301989888));
                }
            }
        }
    }

    private void doKeyguardForChildProfilesLocked() {
        for (int i : UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
                lockProfile(i);
            }
        }
    }

    private void cancelDoKeyguardLaterLocked() {
        this.mDelayedShowingSequence++;
    }

    private void cancelDoKeyguardForChildProfilesLocked() {
        this.mDelayedProfileShowingSequence++;
    }

    public void onStartedWakingUp(boolean z) {
        Trace.beginSection("KeyguardViewMediator#onStartedWakingUp");
        ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).hideOnStartedWakingUp();
        synchronized (this) {
            this.mDeviceInteractive = true;
            if (this.mPendingLock && !z) {
                doKeyguardLocked((Bundle) null);
            }
            this.mAnimatingScreenOff = false;
            cancelDoKeyguardLaterLocked();
            cancelDoKeyguardForChildProfilesLocked();
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "onStartedWakingUp, seq = " + this.mDelayedShowingSequence);
            }
            notifyStartedWakingUp();
        }
        this.mUpdateMonitor.dispatchStartedWakingUp();
        maybeSendUserPresentBroadcast();
        Trace.endSection();
    }

    public void onScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#onScreenTurningOn");
        notifyScreenOn(iKeyguardDrawnCallback);
        Trace.endSection();
    }

    public void onScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#onScreenTurnedOn");
        notifyScreenTurnedOn();
        this.mUpdateMonitor.dispatchScreenTurnedOn();
        Trace.endSection();
    }

    public void onScreenTurnedOff() {
        notifyScreenTurnedOff();
        this.mUpdateMonitor.dispatchScreenTurnedOff();
    }

    private void maybeSendUserPresentBroadcast() {
        if (this.mSystemReady && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
            sendUserPresentBroadcast();
        } else if (this.mSystemReady && shouldWaitForProvisioning()) {
            getLockPatternUtils().userPresent(KeyguardUpdateMonitor.getCurrentUser());
        }
    }

    public void onDreamingStarted() {
        this.mUpdateMonitor.dispatchDreamingStarted();
        synchronized (this) {
            if (this.mDeviceInteractive && this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
                doKeyguardLaterLocked();
            }
        }
    }

    public void onDreamingStopped() {
        this.mUpdateMonitor.dispatchDreamingStopped();
        synchronized (this) {
            if (this.mDeviceInteractive) {
                cancelDoKeyguardLaterLocked();
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:39|40|41|42|54|51|37) */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0037, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b5, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0099, code lost:
        continue;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00a1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setKeyguardEnabled(boolean r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = DEBUG     // Catch:{ all -> 0x00b6 }
            if (r0 == 0) goto L_0x0020
            java.lang.String r1 = "KeyguardViewMediator"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
            r2.<init>()     // Catch:{ all -> 0x00b6 }
            java.lang.String r3 = "setKeyguardEnabled("
            r2.append(r3)     // Catch:{ all -> 0x00b6 }
            r2.append(r5)     // Catch:{ all -> 0x00b6 }
            java.lang.String r3 = ")"
            r2.append(r3)     // Catch:{ all -> 0x00b6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00b6 }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x00b6 }
        L_0x0020:
            r4.mExternallyEnabled = r5     // Catch:{ all -> 0x00b6 }
            r1 = 1
            if (r5 != 0) goto L_0x004a
            boolean r2 = r4.mShowing     // Catch:{ all -> 0x00b6 }
            if (r2 == 0) goto L_0x004a
            com.android.internal.policy.IKeyguardExitCallback r5 = r4.mExitSecureCallback     // Catch:{ all -> 0x00b6 }
            if (r5 == 0) goto L_0x0038
            if (r0 == 0) goto L_0x0036
            java.lang.String r5 = "KeyguardViewMediator"
            java.lang.String r0 = "in process of verifyUnlock request, ignoring"
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x00b6 }
        L_0x0036:
            monitor-exit(r4)     // Catch:{ all -> 0x00b6 }
            return
        L_0x0038:
            if (r0 == 0) goto L_0x0041
            java.lang.String r5 = "KeyguardViewMediator"
            java.lang.String r0 = "remembering to reshow, hiding keyguard, disabling status bar expansion"
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x00b6 }
        L_0x0041:
            r4.mNeedToReshowWhenReenabled = r1     // Catch:{ all -> 0x00b6 }
            r4.updateInputRestrictedLocked()     // Catch:{ all -> 0x00b6 }
            r4.hideLocked()     // Catch:{ all -> 0x00b6 }
            goto L_0x00b4
        L_0x004a:
            if (r5 == 0) goto L_0x00b4
            boolean r5 = r4.mNeedToReshowWhenReenabled     // Catch:{ all -> 0x00b6 }
            if (r5 == 0) goto L_0x00b4
            if (r0 == 0) goto L_0x0059
            java.lang.String r5 = "KeyguardViewMediator"
            java.lang.String r2 = "previously hidden, reshowing, reenabling status bar expansion"
            android.util.Log.d(r5, r2)     // Catch:{ all -> 0x00b6 }
        L_0x0059:
            r5 = 0
            r4.mNeedToReshowWhenReenabled = r5     // Catch:{ all -> 0x00b6 }
            r4.updateInputRestrictedLocked()     // Catch:{ all -> 0x00b6 }
            com.android.internal.policy.IKeyguardExitCallback r2 = r4.mExitSecureCallback     // Catch:{ all -> 0x00b6 }
            r3 = 0
            if (r2 == 0) goto L_0x0081
            if (r0 == 0) goto L_0x006d
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "onKeyguardExitResult(false), resetting"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00b6 }
        L_0x006d:
            com.android.internal.policy.IKeyguardExitCallback r0 = r4.mExitSecureCallback     // Catch:{ RemoteException -> 0x0073 }
            r0.onKeyguardExitResult(r5)     // Catch:{ RemoteException -> 0x0073 }
            goto L_0x007b
        L_0x0073:
            r5 = move-exception
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "Failed to call onKeyguardExitResult(false)"
            android.util.Slog.w(r0, r1, r5)     // Catch:{ all -> 0x00b6 }
        L_0x007b:
            r4.mExitSecureCallback = r3     // Catch:{ all -> 0x00b6 }
            r4.resetStateLocked()     // Catch:{ all -> 0x00b6 }
            goto L_0x00b4
        L_0x0081:
            r4.showLocked(r3)     // Catch:{ all -> 0x00b6 }
            r4.mWaitingUntilKeyguardVisible = r1     // Catch:{ all -> 0x00b6 }
            android.os.Handler r5 = r4.mHandler     // Catch:{ all -> 0x00b6 }
            r1 = 8
            r2 = 2000(0x7d0, double:9.88E-321)
            r5.sendEmptyMessageDelayed(r1, r2)     // Catch:{ all -> 0x00b6 }
            if (r0 == 0) goto L_0x0099
            java.lang.String r5 = "KeyguardViewMediator"
            java.lang.String r0 = "waiting until mWaitingUntilKeyguardVisible is false"
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x00b6 }
        L_0x0099:
            boolean r5 = r4.mWaitingUntilKeyguardVisible     // Catch:{ all -> 0x00b6 }
            if (r5 == 0) goto L_0x00a9
            r4.wait()     // Catch:{ InterruptedException -> 0x00a1 }
            goto L_0x0099
        L_0x00a1:
            java.lang.Thread r5 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x00b6 }
            r5.interrupt()     // Catch:{ all -> 0x00b6 }
            goto L_0x0099
        L_0x00a9:
            boolean r5 = DEBUG     // Catch:{ all -> 0x00b6 }
            if (r5 == 0) goto L_0x00b4
            java.lang.String r5 = "KeyguardViewMediator"
            java.lang.String r0 = "done waiting for mWaitingUntilKeyguardVisible"
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x00b6 }
        L_0x00b4:
            monitor-exit(r4)     // Catch:{ all -> 0x00b6 }
            return
        L_0x00b6:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00b6 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.setKeyguardEnabled(boolean):void");
    }

    public void verifyUnlock(IKeyguardExitCallback iKeyguardExitCallback) {
        Trace.beginSection("KeyguardViewMediator#verifyUnlock");
        synchronized (this) {
            boolean z = DEBUG;
            if (z) {
                Log.d("KeyguardViewMediator", "verifyUnlock");
            }
            if (shouldWaitForProvisioning()) {
                if (z) {
                    Log.d("KeyguardViewMediator", "ignoring because device isn't provisioned");
                }
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e);
                }
            } else if (this.mExternallyEnabled) {
                Log.w("KeyguardViewMediator", "verifyUnlock called when not externally disabled");
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e2) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e2);
                }
            } else if (this.mExitSecureCallback != null) {
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e3) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e3);
                }
            } else if (!isSecure()) {
                this.mExternallyEnabled = true;
                this.mNeedToReshowWhenReenabled = false;
                updateInputRestricted();
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(true);
                } catch (RemoteException e4) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e4);
                }
            } else {
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e5) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e5);
                }
            }
        }
        Trace.endSection();
    }

    public boolean isShowingAndNotOccluded() {
        return this.mShowing && !this.mOccluded;
    }

    public void setOccluded(boolean z, boolean z2) {
        Trace.beginSection("KeyguardViewMediator#setOccluded");
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "setOccluded " + z);
        }
        this.mHandler.removeMessages(9);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9, z ? 1 : 0, z2 ? 1 : 0));
        Trace.endSection();
    }

    public boolean isHiding() {
        return this.mHiding;
    }

    /* access modifiers changed from: private */
    public void handleSetOccluded(boolean z, boolean z2) {
        boolean z3;
        Trace.beginSection("KeyguardViewMediator#handleSetOccluded");
        synchronized (this) {
            if (this.mHiding && z) {
                startKeyguardExitAnimation(0, 0);
            }
            if (this.mOccluded != z) {
                this.mOccluded = z;
                this.mUpdateMonitor.setKeyguardOccluded(z);
                KeyguardViewController keyguardViewController = this.mKeyguardViewControllerLazy.get();
                if (!((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isSimPinSecure()) {
                    if (z2 && this.mDeviceInteractive) {
                        z3 = true;
                        keyguardViewController.setOccluded(z, z3);
                        adjustStatusBarLocked();
                    }
                }
                z3 = false;
                keyguardViewController.setOccluded(z, z3);
                adjustStatusBarLocked();
            }
        }
        Trace.endSection();
    }

    public void doKeyguardTimeout(Bundle bundle) {
        this.mHandler.removeMessages(10);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10, bundle));
    }

    public boolean isInputRestricted() {
        return this.mShowing || this.mNeedToReshowWhenReenabled;
    }

    private void updateInputRestricted() {
        synchronized (this) {
            updateInputRestrictedLocked();
        }
    }

    private void updateInputRestrictedLocked() {
        boolean isInputRestricted = isInputRestricted();
        if (this.mInputRestricted != isInputRestricted) {
            this.mInputRestricted = isInputRestricted;
            for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
                IKeyguardStateCallback iKeyguardStateCallback = this.mKeyguardStateCallbacks.get(size);
                try {
                    iKeyguardStateCallback.onInputRestrictedStateChanged(isInputRestricted);
                } catch (RemoteException e) {
                    Slog.w("KeyguardViewMediator", "Failed to call onDeviceProvisioned", e);
                    if (e instanceof DeadObjectException) {
                        this.mKeyguardStateCallbacks.remove(iKeyguardStateCallback);
                    }
                }
            }
        }
    }

    private boolean isInLockTaskMode() {
        boolean z;
        try {
            z = ActivityManager.getService().isInLockTaskMode();
        } catch (RemoteException unused) {
            z = false;
        }
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "isLockdTaskMode = " + z);
        }
        return z;
    }

    private void doKeyguardChecked(Bundle bundle) {
        this.mKeyguardChecked = false;
        if (KeyguardUpdateMonitor.CORE_APPS_ONLY) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "doKeyguard: not showing because booting to cryptkeeper");
            }
        } else if (!this.mExternallyEnabled && (!isSecure() || isInLockTaskMode())) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "doKeyguard: not showing because externally disabled");
            }
            this.mNeedToReshowWhenReenabled = true;
        } else if (!this.mShowing || !this.mKeyguardViewControllerLazy.get().isShowing()) {
            if (!mustNotUnlockCurrentUser() || !this.mUpdateMonitor.isDeviceProvisioned()) {
                boolean z = this.mUpdateMonitor.isSimPinSecure() || ((SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(1)) || SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(7))) && (SystemProperties.getBoolean("keyguard.no_require_sim", false) ^ true));
                if (z || !shouldWaitForProvisioning()) {
                    if (!"trigger_restart_min_framework".equals(SystemProperties.get("vold.decrypt"))) {
                        boolean z2 = bundle != null && bundle.getBoolean("force_show", false);
                        if (!this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()) || z || z2) {
                            if (this.mLockPatternUtils.checkVoldPassword(KeyguardUpdateMonitor.getCurrentUser())) {
                                if (DEBUG) {
                                    Log.d("KeyguardViewMediator", "Not showing lock screen since just decrypted");
                                }
                                setShowingLocked(false);
                                hideLocked();
                                return;
                            }
                        } else if (DEBUG) {
                            Log.d("KeyguardViewMediator", "doKeyguard: not showing because lockscreen is off");
                            return;
                        } else {
                            return;
                        }
                    } else if (DEBUG) {
                        Log.d("KeyguardViewMediator", "doKeyguard: don't lock because device is encrypted and in encryption lock screen");
                        return;
                    } else {
                        return;
                    }
                } else if (DEBUG) {
                    Log.d("KeyguardViewMediator", "doKeyguard: not showing because device isn't provisioned and the sim is not locked or missing");
                    return;
                } else {
                    return;
                }
            }
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "doKeyguard: showing the lock screen");
            }
            this.mKeyguardChecked = true;
        } else {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "doKeyguard: not showing because it is already showing");
            }
            resetStateLocked();
        }
    }

    /* access modifiers changed from: private */
    public void doKeyguardLocked(Bundle bundle) {
        doKeyguardChecked(bundle);
        if (this.mKeyguardChecked) {
            showLocked(bundle);
        }
    }

    /* access modifiers changed from: private */
    public void lockProfile(int i) {
        this.mTrustManager.setDeviceLockedForUser(i, true);
    }

    /* access modifiers changed from: private */
    public boolean shouldWaitForProvisioning() {
        return !this.mUpdateMonitor.isDeviceProvisioned() && !isSecure();
    }

    /* access modifiers changed from: private */
    public void handleDismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        if (this.mShowing) {
            if (iKeyguardDismissCallback != null) {
                this.mDismissCallbackRegistry.addCallback(iKeyguardDismissCallback);
            }
            this.mCustomMessage = charSequence;
            this.mKeyguardViewControllerLazy.get().dismissAndCollapse();
        } else if (iKeyguardDismissCallback != null) {
            new DismissCallbackWrapper(iKeyguardDismissCallback).notifyDismissError();
        }
    }

    public void dismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        this.mHandler.obtainMessage(11, new DismissMessage(iKeyguardDismissCallback, charSequence)).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void resetStateLocked() {
        if (DEBUG) {
            Log.e("KeyguardViewMediator", "resetStateLocked");
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    private void notifyStartedGoingToSleep() {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyStartedGoingToSleep");
        }
        this.mHandler.sendEmptyMessage(17);
    }

    private void notifyFinishedGoingToSleep() {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyFinishedGoingToSleep");
        }
        this.mHandler.sendEmptyMessage(5);
    }

    private void notifyStartedWakingUp() {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyStartedWakingUp");
        }
        this.mHandler.sendEmptyMessage(14);
    }

    private void notifyScreenOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyScreenOn mWakeAndUnlocking = " + this.mWakeAndUnlocking);
        }
        if (this.mWakeAndUnlocking) {
            handleNotifyScreenTurningOn(iKeyguardDrawnCallback);
            return;
        }
        this.isUseHandle = true;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, iKeyguardDrawnCallback));
    }

    private void notifyScreenTurnedOn() {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyScreenTurnedOn mWakeAndUnlocking = " + this.mWakeAndUnlocking);
        }
        if (this.isUseHandle) {
            this.isUseHandle = false;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(15));
            return;
        }
        handleNotifyScreenTurnedOn();
    }

    private void notifyScreenTurnedOff() {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "notifyScreenTurnedOff");
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(16));
    }

    private void showLocked(Bundle bundle) {
        Trace.beginSection("KeyguardViewMediator#showLocked aqcuiring mShowKeyguardWakeLock");
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "showLocked");
        }
        this.mShowKeyguardWakeLock.acquire();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, bundle));
        int currentUser = ActivityManager.getCurrentUser();
        Log.d("KeyguardViewMediator", "USER_LOCKED intent sent");
        UserHandle userHandle = new UserHandle(currentUser);
        Intent intent = new Intent("com.motorola.internal.intent.action.USER_LOCKED");
        intent.putExtra("security_mode", this.mLockPatternUtils.isSecure(currentUser));
        intent.addFlags(603979776);
        this.mContext.sendBroadcastAsUser(intent, userHandle);
        Trace.endSection();
    }

    private void hideLocked() {
        Trace.beginSection("KeyguardViewMediator#hideLocked");
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "hideLocked");
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
        Trace.endSection();
    }

    public void hideWithAnimation(IRemoteAnimationRunner iRemoteAnimationRunner) {
        if (this.mKeyguardDonePending) {
            this.mKeyguardExitAnimationRunner = iRemoteAnimationRunner;
            this.mViewMediatorCallback.readyForKeyguardDone();
        }
    }

    public void setBlursDisabledForAppLaunch(boolean z) {
        this.mNotificationShadeDepthController.get().setBlursDisabledForAppLaunch(z);
    }

    public boolean isSecure() {
        return isSecure(KeyguardUpdateMonitor.getCurrentUser());
    }

    public boolean isSecure(int i) {
        return this.mLockPatternUtils.isSecure(i) || this.mUpdateMonitor.isSimPinSecure();
    }

    public void setSwitchingUser(boolean z) {
        this.mUpdateMonitor.setSwitchingUser(z);
    }

    public void setCurrentUser(int i) {
        KeyguardUpdateMonitor.setCurrentUser(i);
        synchronized (this) {
            notifyTrustedChangedLocked(this.mUpdateMonitor.getUserHasTrust(i));
        }
    }

    public void keyguardDone() {
        Trace.beginSection("KeyguardViewMediator#keyguardDone");
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "keyguardDone()");
        }
        userActivity();
        EventLog.writeEvent(70000, 2);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7));
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void tryKeyguardDone() {
        boolean z = DEBUG;
        if (z) {
            Log.d("KeyguardViewMediator", "tryKeyguardDone: pending - " + this.mKeyguardDonePending + ", animRan - " + this.mHideAnimationRun + " animRunning - " + this.mHideAnimationRunning);
        }
        if (!this.mKeyguardDonePending && this.mHideAnimationRun && !this.mHideAnimationRunning) {
            handleKeyguardDone();
        } else if (!this.mHideAnimationRun) {
            if (z) {
                Log.d("KeyguardViewMediator", "tryKeyguardDone: starting pre-hide animation");
            }
            this.mHideAnimationRun = true;
            this.mHideAnimationRunning = true;
            this.mKeyguardViewControllerLazy.get().startPreHideAnimation(this.mHideAnimationFinishedRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void handleKeyguardDone() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDone");
        this.mUiBgExecutor.execute(new KeyguardViewMediator$$ExternalSyntheticLambda4(this, KeyguardUpdateMonitor.getCurrentUser()));
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "handleKeyguardDone");
        }
        synchronized (this) {
            resetKeyguardDonePendingLocked();
        }
        if (this.mGoingToSleep) {
            this.mUpdateMonitor.clearBiometricRecognized();
            Log.i("KeyguardViewMediator", "Device is going to sleep, aborting keyguardDone");
            return;
        }
        IKeyguardExitCallback iKeyguardExitCallback = this.mExitSecureCallback;
        if (iKeyguardExitCallback != null) {
            try {
                iKeyguardExitCallback.onKeyguardExitResult(true);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult()", e);
            }
            this.mExitSecureCallback = null;
            this.mExternallyEnabled = true;
            this.mNeedToReshowWhenReenabled = false;
            updateInputRestricted();
        }
        handleHide();
        this.mUpdateMonitor.clearBiometricRecognized();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleKeyguardDone$1(int i) {
        if (this.mLockPatternUtils.isSecure(i)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardDismissed(i);
        }
    }

    /* access modifiers changed from: private */
    public void sendUserPresentBroadcast() {
        synchronized (this) {
            if (this.mBootCompleted) {
                int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                this.mUiBgExecutor.execute(new KeyguardViewMediator$$ExternalSyntheticLambda6(this, (UserManager) this.mContext.getSystemService("user"), new UserHandle(currentUser), currentUser));
            } else {
                this.mBootSendUserPresent = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendUserPresentBroadcast$2(UserManager userManager, UserHandle userHandle, int i) {
        for (int of : userManager.getProfileIdsWithDisabled(userHandle.getIdentifier())) {
            this.mContext.sendBroadcastAsUser(USER_PRESENT_INTENT, UserHandle.of(of));
        }
        getLockPatternUtils().userPresent(i);
    }

    /* access modifiers changed from: private */
    public void handleKeyguardDoneDrawing() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDoneDrawing");
        synchronized (this) {
            boolean z = DEBUG;
            if (z) {
                Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing");
            }
            if (this.mWaitingUntilKeyguardVisible) {
                if (z) {
                    Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing: notifying mWaitingUntilKeyguardVisible");
                }
                this.mWaitingUntilKeyguardVisible = false;
                notifyAll();
                this.mHandler.removeMessages(8);
            }
        }
        Trace.endSection();
    }

    private void playSounds(boolean z) {
        playSound(z ? this.mLockSoundId : this.mUnlockSoundId);
    }

    private void playSound(int i) {
        if (i != 0 && Settings.System.getInt(this.mContext.getContentResolver(), "lockscreen_sounds_enabled", 1) == 1) {
            this.mLockSounds.stop(this.mLockSoundStreamId);
            if (this.mAudioManager == null) {
                AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
                this.mAudioManager = audioManager;
                if (audioManager != null) {
                    this.mUiSoundsStreamType = audioManager.getUiSoundsStreamType();
                } else {
                    return;
                }
            }
            this.mUiBgExecutor.execute(new KeyguardViewMediator$$ExternalSyntheticLambda5(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playSound$3(int i) {
        if (!this.mAudioManager.isStreamMute(this.mUiSoundsStreamType)) {
            SoundPool soundPool = this.mLockSounds;
            float f = this.mLockSoundVolume;
            int play = soundPool.play(i, f, f, 1, 0, 1.0f);
            synchronized (this) {
                this.mLockSoundStreamId = play;
            }
        }
    }

    /* access modifiers changed from: private */
    public void playTrustedSound() {
        playSound(this.mTrustedSoundId);
    }

    private void updateActivityLockScreenState(boolean z, boolean z2) {
        this.mUiBgExecutor.execute(new KeyguardViewMediator$$ExternalSyntheticLambda9(z, z2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateActivityLockScreenState$4(boolean z, boolean z2) {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "updateActivityLockScreenState(" + z + ", " + z2 + ")");
        }
        try {
            ActivityTaskManager.getService().setLockScreenShown(z, z2);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleShow(android.os.Bundle r3) {
        /*
            r2 = this;
            java.lang.String r0 = "KeyguardViewMediator#handleShow"
            android.os.Trace.beginSection(r0)
            int r0 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()
            com.android.internal.widget.LockPatternUtils r1 = r2.mLockPatternUtils
            boolean r1 = r1.isSecure(r0)
            if (r1 == 0) goto L_0x001a
            com.android.internal.widget.LockPatternUtils r1 = r2.mLockPatternUtils
            android.app.admin.DevicePolicyManager r1 = r1.getDevicePolicyManager()
            r1.reportKeyguardSecured(r0)
        L_0x001a:
            monitor-enter(r2)
            boolean r0 = r2.mSystemReady     // Catch:{ all -> 0x0080 }
            if (r0 != 0) goto L_0x002c
            boolean r3 = DEBUG     // Catch:{ all -> 0x0080 }
            if (r3 == 0) goto L_0x002a
            java.lang.String r3 = "KeyguardViewMediator"
            java.lang.String r0 = "ignoring handleShow because system is not ready."
            android.util.Log.d(r3, r0)     // Catch:{ all -> 0x0080 }
        L_0x002a:
            monitor-exit(r2)     // Catch:{ all -> 0x0080 }
            return
        L_0x002c:
            boolean r0 = DEBUG     // Catch:{ all -> 0x0080 }
            if (r0 == 0) goto L_0x0037
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "handleShow"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x0080 }
        L_0x0037:
            com.android.keyguard.KeyguardDisplayManager r0 = r2.mKeyguardDisplayManager     // Catch:{ all -> 0x0080 }
            r0.show()     // Catch:{ all -> 0x0080 }
            r0 = 0
            r2.mHiding = r0     // Catch:{ all -> 0x0080 }
            r1 = 0
            r2.mKeyguardExitAnimationRunner = r1     // Catch:{ all -> 0x0080 }
            r2.mWakeAndUnlocking = r0     // Catch:{ all -> 0x0080 }
            r2.mPendingLock = r0     // Catch:{ all -> 0x0080 }
            r1 = 1
            r2.setShowingLocked(r1)     // Catch:{ all -> 0x0080 }
            dagger.Lazy<com.android.keyguard.KeyguardViewController> r1 = r2.mKeyguardViewControllerLazy     // Catch:{ all -> 0x0080 }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x0080 }
            com.android.keyguard.KeyguardViewController r1 = (com.android.keyguard.KeyguardViewController) r1     // Catch:{ all -> 0x0080 }
            r1.show(r3)     // Catch:{ all -> 0x0080 }
            r2.resetKeyguardDonePendingLocked()     // Catch:{ all -> 0x0080 }
            r2.mHideAnimationRun = r0     // Catch:{ all -> 0x0080 }
            r2.adjustStatusBarLocked()     // Catch:{ all -> 0x0080 }
            r2.userActivity()     // Catch:{ all -> 0x0080 }
            r2.mKeyguardGoingAway = r0     // Catch:{ all -> 0x0080 }
            dagger.Lazy<com.android.keyguard.KeyguardViewController> r3 = r2.mKeyguardViewControllerLazy     // Catch:{ all -> 0x0080 }
            java.lang.Object r3 = r3.get()     // Catch:{ all -> 0x0080 }
            com.android.keyguard.KeyguardViewController r3 = (com.android.keyguard.KeyguardViewController) r3     // Catch:{ all -> 0x0080 }
            r3.setKeyguardGoingAwayState(r0)     // Catch:{ all -> 0x0080 }
            android.os.PowerManager$WakeLock r3 = r2.mShowKeyguardWakeLock     // Catch:{ all -> 0x0080 }
            r3.release()     // Catch:{ all -> 0x0080 }
            monitor-exit(r2)     // Catch:{ all -> 0x0080 }
            com.android.internal.widget.LockPatternUtils r2 = r2.mLockPatternUtils
            int r3 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()
            r2.scheduleNonStrongBiometricIdleTimeout(r3)
            android.os.Trace.endSection()
            return
        L_0x0080:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0080 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.handleShow(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        Log.e("KeyguardViewMediator", "mHideAnimationFinishedRunnable#run");
        this.mHideAnimationRunning = false;
        tryKeyguardDone();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0069, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleHide() {
        /*
            r13 = this;
            java.lang.String r0 = "KeyguardViewMediator#handleHide"
            android.os.Trace.beginSection(r0)
            boolean r0 = r13.mAodShowing
            if (r0 == 0) goto L_0x001d
            android.content.Context r0 = r13.mContext
            java.lang.Class<android.os.PowerManager> r1 = android.os.PowerManager.class
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.PowerManager r0 = (android.os.PowerManager) r0
            long r1 = android.os.SystemClock.uptimeMillis()
            r3 = 4
            java.lang.String r4 = "com.android.systemui:BOUNCER_DOZING"
            r0.wakeUp(r1, r3, r4)
        L_0x001d:
            monitor-enter(r13)
            boolean r0 = DEBUG     // Catch:{ all -> 0x006d }
            if (r0 == 0) goto L_0x0029
            java.lang.String r1 = "KeyguardViewMediator"
            java.lang.String r2 = "handleHide"
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x006d }
        L_0x0029:
            boolean r1 = r13.mustNotUnlockCurrentUser()     // Catch:{ all -> 0x006d }
            if (r1 == 0) goto L_0x003d
            if (r0 == 0) goto L_0x0038
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "Split system user, quit unlocking."
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x006d }
        L_0x0038:
            r0 = 0
            r13.mKeyguardExitAnimationRunner = r0     // Catch:{ all -> 0x006d }
            monitor-exit(r13)     // Catch:{ all -> 0x006d }
            return
        L_0x003d:
            r0 = 1
            r13.mHiding = r0     // Catch:{ all -> 0x006d }
            boolean r0 = r13.mShowing     // Catch:{ all -> 0x006d }
            if (r0 == 0) goto L_0x004e
            boolean r0 = r13.mOccluded     // Catch:{ all -> 0x006d }
            if (r0 != 0) goto L_0x004e
            java.lang.Runnable r0 = r13.mKeyguardGoingAwayRunnable     // Catch:{ all -> 0x006d }
            r0.run()     // Catch:{ all -> 0x006d }
            goto L_0x0068
        L_0x004e:
            long r0 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x006d }
            android.view.animation.Animation r2 = r13.mHideAnimation     // Catch:{ all -> 0x006d }
            long r2 = r2.getStartOffset()     // Catch:{ all -> 0x006d }
            long r5 = r0 + r2
            android.view.animation.Animation r0 = r13.mHideAnimation     // Catch:{ all -> 0x006d }
            long r7 = r0.getDuration()     // Catch:{ all -> 0x006d }
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r4 = r13
            r4.handleStartKeyguardExitAnimation(r5, r7, r9, r10, r11, r12)     // Catch:{ all -> 0x006d }
        L_0x0068:
            monitor-exit(r13)     // Catch:{ all -> 0x006d }
            android.os.Trace.endSection()
            return
        L_0x006d:
            r0 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x006d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.handleHide():void");
    }

    /* access modifiers changed from: private */
    public void handleStartKeyguardExitAnimation(long j, long j2, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
        long j3 = j;
        long j4 = j2;
        RemoteAnimationTarget[] remoteAnimationTargetArr4 = remoteAnimationTargetArr;
        final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback2 = iRemoteAnimationFinishedCallback;
        Trace.beginSection("KeyguardViewMediator#handleStartKeyguardExitAnimation");
        Log.i("KeyguardViewMediator", "handleStartKeyguardExitAnimation startTime=" + j + " fadeoutDuration=" + j4);
        synchronized (this) {
            if (this.mHiding || this.mSurfaceBehindRemoteAnimationRequested || this.mKeyguardStateController.isFlingingToDismissKeyguardDuringSwipeGesture()) {
                this.mHiding = false;
                IRemoteAnimationRunner iRemoteAnimationRunner = this.mKeyguardExitAnimationRunner;
                this.mKeyguardExitAnimationRunner = null;
                if (this.mWakeAndUnlocking && this.mDrawnCallback != null) {
                    this.mKeyguardViewControllerLazy.get().getViewRootImpl().setReportNextDraw();
                    notifyDrawn(this.mDrawnCallback);
                    this.mDrawnCallback = null;
                }
                LatencyTracker.getInstance(this.mContext).onActionEnd(11);
                boolean z = KeyguardService.sEnableRemoteKeyguardGoingAwayAnimation;
                if (z && iRemoteAnimationRunner != null && iRemoteAnimationFinishedCallback2 != null) {
                    C10008 r7 = new IRemoteAnimationFinishedCallback() {
                        public void onAnimationFinished() throws RemoteException {
                            try {
                                iRemoteAnimationFinishedCallback2.onAnimationFinished();
                            } catch (RemoteException e) {
                                Slog.w("KeyguardViewMediator", "Failed to call onAnimationFinished", e);
                            }
                            KeyguardViewMediator.this.onKeyguardExitFinished();
                            ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).hide(0, 0);
                            InteractionJankMonitor.getInstance().end(29);
                        }

                        public IBinder asBinder() {
                            return iRemoteAnimationFinishedCallback2.asBinder();
                        }
                    };
                    try {
                        InteractionJankMonitor.getInstance().begin(createInteractionJankMonitorConf("RunRemoteAnimation"));
                        iRemoteAnimationRunner.onAnimationStart(7, remoteAnimationTargetArr, remoteAnimationTargetArr2, remoteAnimationTargetArr3, r7);
                    } catch (RemoteException e) {
                        Slog.w("KeyguardViewMediator", "Failed to call onAnimationStart", e);
                    }
                } else if (!z || this.mStatusBarStateController.leaveOpenOnKeyguardHide() || remoteAnimationTargetArr4 == null || remoteAnimationTargetArr4.length <= 0) {
                    InteractionJankMonitor.getInstance().begin(createInteractionJankMonitorConf("RemoteAnimationDisabled"));
                    this.mKeyguardViewControllerLazy.get().hide(j, j4);
                    this.mContext.getMainExecutor().execute(new KeyguardViewMediator$$ExternalSyntheticLambda7(this, iRemoteAnimationFinishedCallback2, remoteAnimationTargetArr4));
                    onKeyguardExitFinished();
                    if (this.mUpdateMonitor.isSimPinOrPuk()) {
                        Log.i("KeyguardViewMediator", "handleStartKeyguardExitAnimation isSimPinSecure");
                        doKeyguardLocked((Bundle) null);
                    }
                } else {
                    this.mSurfaceBehindRemoteAnimationFinishedCallback = iRemoteAnimationFinishedCallback2;
                    this.mSurfaceBehindRemoteAnimationRunning = true;
                    InteractionJankMonitor.getInstance().begin(createInteractionJankMonitorConf("DismissPanel"));
                    this.mKeyguardUnlockAnimationControllerLazy.get().notifyStartKeyguardExitAnimation(remoteAnimationTargetArr4[0], j, this.mSurfaceBehindRemoteAnimationRequested);
                }
            } else {
                if (iRemoteAnimationFinishedCallback2 != null) {
                    try {
                        iRemoteAnimationFinishedCallback.onAnimationFinished();
                    } catch (RemoteException e2) {
                        Slog.w("KeyguardViewMediator", "Failed to call onAnimationFinished", e2);
                    }
                }
                setShowingLocked(this.mShowing, true);
                return;
            }
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleStartKeyguardExitAnimation$7(final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback, RemoteAnimationTarget[] remoteAnimationTargetArr) {
        if (iRemoteAnimationFinishedCallback == null) {
            InteractionJankMonitor.getInstance().end(29);
            return;
        }
        SyncRtSurfaceTransactionApplier syncRtSurfaceTransactionApplier = new SyncRtSurfaceTransactionApplier(this.mKeyguardViewControllerLazy.get().getViewRootImpl().getView());
        RemoteAnimationTarget remoteAnimationTarget = remoteAnimationTargetArr[0];
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addUpdateListener(new KeyguardViewMediator$$ExternalSyntheticLambda0(remoteAnimationTarget, syncRtSurfaceTransactionApplier));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                try {
                    iRemoteAnimationFinishedCallback.onAnimationFinished();
                } catch (RemoteException unused) {
                    Slog.e("KeyguardViewMediator", "RemoteException");
                } catch (Throwable th) {
                    InteractionJankMonitor.getInstance().end(29);
                    throw th;
                }
                InteractionJankMonitor.getInstance().end(29);
            }

            public void onAnimationCancel(Animator animator) {
                try {
                    iRemoteAnimationFinishedCallback.onAnimationFinished();
                } catch (RemoteException unused) {
                    Slog.e("KeyguardViewMediator", "RemoteException");
                } catch (Throwable th) {
                    InteractionJankMonitor.getInstance().cancel(29);
                    throw th;
                }
                InteractionJankMonitor.getInstance().cancel(29);
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void onKeyguardExitFinished() {
        if (this.mPhoneState == 0) {
            playSounds(false);
        }
        setShowingLocked(false);
        this.mWakeAndUnlocking = false;
        this.mDismissCallbackRegistry.notifyDismissSucceeded();
        resetKeyguardDonePendingLocked();
        this.mHideAnimationRun = false;
        adjustStatusBarLocked();
        sendUserPresentBroadcast();
    }

    private InteractionJankMonitor.Configuration.Builder createInteractionJankMonitorConf(String str) {
        return new InteractionJankMonitor.Configuration.Builder(29).setView(this.mKeyguardViewControllerLazy.get().getViewRootImpl().getView()).setTag(str);
    }

    public boolean isAnimatingBetweenKeyguardAndSurfaceBehindOrWillBe() {
        return this.mSurfaceBehindRemoteAnimationRunning || this.mKeyguardStateController.isFlingingToDismissKeyguard();
    }

    /* access modifiers changed from: private */
    public void handleCancelKeyguardExitAnimation() {
        showSurfaceBehindKeyguard();
        onKeyguardExitRemoteAnimationFinished(true);
    }

    public void onKeyguardExitRemoteAnimationFinished(boolean z) {
        if (this.mSurfaceBehindRemoteAnimationRunning || this.mSurfaceBehindRemoteAnimationRequested) {
            this.mKeyguardViewControllerLazy.get().blockPanelExpansionFromCurrentTouch();
            boolean z2 = this.mShowing;
            onKeyguardExitFinished();
            if (this.mKeyguardStateController.isDismissingFromSwipe() || !z2) {
                this.mKeyguardUnlockAnimationControllerLazy.get().hideKeyguardViewAfterRemoteAnimation();
            }
            finishSurfaceBehindRemoteAnimation(z);
            this.mSurfaceBehindRemoteAnimationRequested = false;
            this.mKeyguardUnlockAnimationControllerLazy.get().notifyFinishedKeyguardExitAnimation();
            InteractionJankMonitor.getInstance().end(29);
        }
    }

    public void showSurfaceBehindKeyguard() {
        this.mSurfaceBehindRemoteAnimationRequested = true;
        try {
            ActivityTaskManager.getService().keyguardGoingAway(6);
        } catch (RemoteException e) {
            this.mSurfaceBehindRemoteAnimationRequested = false;
            e.printStackTrace();
        }
    }

    public void hideSurfaceBehindKeyguard() {
        this.mSurfaceBehindRemoteAnimationRequested = false;
        if (this.mShowing) {
            setShowingLocked(true, true);
        }
    }

    public boolean requestedShowSurfaceBehindKeyguard() {
        return this.mSurfaceBehindRemoteAnimationRequested;
    }

    public void finishSurfaceBehindRemoteAnimation(boolean z) {
        this.mSurfaceBehindRemoteAnimationRunning = false;
        IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback = this.mSurfaceBehindRemoteAnimationFinishedCallback;
        if (iRemoteAnimationFinishedCallback != null) {
            if (!z) {
                try {
                    iRemoteAnimationFinishedCallback.onAnimationFinished();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.mSurfaceBehindRemoteAnimationFinishedCallback = null;
        }
    }

    /* access modifiers changed from: private */
    public void adjustStatusBarLocked() {
        adjustStatusBarLocked(false, false);
    }

    /* access modifiers changed from: private */
    public void adjustStatusBarLocked(boolean z, boolean z2) {
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) this.mContext.getSystemService("statusbar");
        }
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager == null) {
            Log.w("KeyguardViewMediator", "Could not get status bar manager");
            return;
        }
        int i = 0;
        if (z2) {
            statusBarManager.disable(0);
        }
        if (z || isShowingAndNotOccluded()) {
            if (!this.mShowHomeOverLockscreen || !this.mInGestureNavigationMode) {
                i = 2097152;
            }
            i |= 16777216;
        }
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "adjustStatusBarLocked: mShowing=" + this.mShowing + " mOccluded=" + this.mOccluded + " isSecure=" + isSecure() + " force=" + z + " --> flags=0x" + Integer.toHexString(i));
        }
        this.mStatusBarManager.disable(i);
    }

    /* access modifiers changed from: private */
    public void handleReset() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleReset");
            }
            this.mKeyguardViewControllerLazy.get().reset(true);
        }
    }

    /* access modifiers changed from: private */
    public void handleVerifyUnlock() {
        Trace.beginSection("KeyguardViewMediator#handleVerifyUnlock");
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleVerifyUnlock");
            }
            setShowingLocked(true);
            this.mKeyguardViewControllerLazy.get().dismissAndCollapse();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyStartedGoingToSleep() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyStartedGoingToSleep");
            }
            this.mKeyguardViewControllerLazy.get().onStartedGoingToSleep();
        }
    }

    /* access modifiers changed from: private */
    public void handleNotifyFinishedGoingToSleep() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyFinishedGoingToSleep");
            }
            this.mKeyguardViewControllerLazy.get().onFinishedGoingToSleep();
        }
    }

    /* access modifiers changed from: private */
    public void handleNotifyStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#handleMotifyStartedWakingUp");
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyWakingUp");
            }
            this.mKeyguardViewControllerLazy.get().onStartedWakingUp();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurningOn");
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyScreenTurningOn");
            }
            this.mKeyguardViewControllerLazy.get().onScreenTurningOn();
            if (iKeyguardDrawnCallback != null) {
                if (this.mWakeAndUnlocking) {
                    this.mDrawnCallback = iKeyguardDrawnCallback;
                } else {
                    notifyDrawn(iKeyguardDrawnCallback);
                }
            }
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurnedOn");
        if (LatencyTracker.isEnabled(this.mContext)) {
            LatencyTracker.getInstance(this.mContext).onActionEnd(5);
        }
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOn");
            }
            this.mKeyguardViewControllerLazy.get().onScreenTurnedOn();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurnedOff() {
        synchronized (this) {
            if (DEBUG) {
                Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOff");
            }
            this.mDrawnCallback = null;
        }
    }

    private void notifyDrawn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#notifyDrawn");
        Log.v("KeyguardViewMediator", "notifyDrawn");
        try {
            iKeyguardDrawnCallback.onDrawn();
        } catch (RemoteException e) {
            Slog.w("KeyguardViewMediator", "Exception calling onDrawn():", e);
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void resetKeyguardDonePendingLocked() {
        this.mKeyguardDonePending = false;
        this.mHandler.removeMessages(13);
    }

    public void onBootCompleted() {
        synchronized (this) {
            if (this.mContext.getResources().getBoolean(17891622)) {
                this.mUserSwitcherController.schedulePostBootGuestCreation();
            }
            this.mBootCompleted = true;
            adjustStatusBarLocked(false, true);
            if (this.mBootSendUserPresent) {
                sendUserPresentBroadcast();
            }
        }
    }

    public void onWakeAndUnlocking() {
        Trace.beginSection("KeyguardViewMediator#onWakeAndUnlocking");
        this.mWakeAndUnlocking = true;
        keyguardDone();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleRequestUnlock(IBinder iBinder, boolean z, boolean z2) {
        if (DEBUG) {
            Log.d("KeyguardViewMediator", "requestUnlock - callback: " + iBinder + " dismiss: " + z + " collapsePanels: " + z2);
        }
        if (this.mKeyguardViewControllerLazy.get().isShowing()) {
            this.mKeyguardViewControllerLazy.get().requestUnlock(IRemoteCallback.Stub.asInterface(iBinder), z, z2);
        }
    }

    @Deprecated
    public void startKeyguardExitAnimation(long j, long j2) {
        startKeyguardExitAnimation(0, j, j2, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, (IRemoteAnimationFinishedCallback) null);
    }

    public void startKeyguardExitAnimation(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
        startKeyguardExitAnimation(i, 0, 0, remoteAnimationTargetArr, remoteAnimationTargetArr2, remoteAnimationTargetArr3, iRemoteAnimationFinishedCallback);
    }

    private void startKeyguardExitAnimation(int i, long j, long j2, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
        Trace.beginSection("KeyguardViewMediator#startKeyguardExitAnimation");
        this.mHandler.removeMessages(R$styleable.Constraint_layout_goneMarginRight);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(12, new StartKeyguardExitAnimParams(i, j, j2, remoteAnimationTargetArr, remoteAnimationTargetArr2, remoteAnimationTargetArr3, iRemoteAnimationFinishedCallback)));
        Trace.endSection();
    }

    public void cancelKeyguardExitAnimation() {
        Trace.beginSection("KeyguardViewMediator#cancelKeyguardExitAnimation");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(19));
        Trace.endSection();
    }

    public ViewMediatorCallback getViewMediatorCallback() {
        return this.mViewMediatorCallback;
    }

    public LockPatternUtils getLockPatternUtils() {
        return this.mLockPatternUtils;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mSystemReady: ");
        printWriter.println(this.mSystemReady);
        printWriter.print("  mBootCompleted: ");
        printWriter.println(this.mBootCompleted);
        printWriter.print("  mBootSendUserPresent: ");
        printWriter.println(this.mBootSendUserPresent);
        printWriter.print("  mExternallyEnabled: ");
        printWriter.println(this.mExternallyEnabled);
        printWriter.print("  mShuttingDown: ");
        printWriter.println(this.mShuttingDown);
        printWriter.print("  mNeedToReshowWhenReenabled: ");
        printWriter.println(this.mNeedToReshowWhenReenabled);
        printWriter.print("  mShowing: ");
        printWriter.println(this.mShowing);
        printWriter.print("  mInputRestricted: ");
        printWriter.println(this.mInputRestricted);
        printWriter.print("  mOccluded: ");
        printWriter.println(this.mOccluded);
        printWriter.print("  mDelayedShowingSequence: ");
        printWriter.println(this.mDelayedShowingSequence);
        printWriter.print("  mExitSecureCallback: ");
        printWriter.println(this.mExitSecureCallback);
        printWriter.print("  mDeviceInteractive: ");
        printWriter.println(this.mDeviceInteractive);
        printWriter.print("  mGoingToSleep: ");
        printWriter.println(this.mGoingToSleep);
        printWriter.print("  mHiding: ");
        printWriter.println(this.mHiding);
        printWriter.print("  mDozing: ");
        printWriter.println(this.mDozing);
        printWriter.print("  mAodShowing: ");
        printWriter.println(this.mAodShowing);
        printWriter.print("  mWaitingUntilKeyguardVisible: ");
        printWriter.println(this.mWaitingUntilKeyguardVisible);
        printWriter.print("  mKeyguardDonePending: ");
        printWriter.println(this.mKeyguardDonePending);
        printWriter.print("  mHideAnimationRun: ");
        printWriter.println(this.mHideAnimationRun);
        printWriter.print("  mPendingReset: ");
        printWriter.println(this.mPendingReset);
        printWriter.print("  mPendingLock: ");
        printWriter.println(this.mPendingLock);
        printWriter.print("  mWakeAndUnlocking: ");
        printWriter.println(this.mWakeAndUnlocking);
        printWriter.print("  mDrawnCallback: ");
        printWriter.println(this.mDrawnCallback);
    }

    public void setDozing(boolean z) {
        if (z != this.mDozing) {
            this.mDozing = z;
            if (!z) {
                this.mAnimatingScreenOff = false;
            }
            if (this.mShowing || !this.mPendingLock || !this.mDozeParameters.canControlUnlockedScreenOff()) {
                setShowingLocked(this.mShowing);
            }
            this.mKeyguardDisplayManager.updateDozingState(z);
            ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).setDozing(z);
        }
    }

    public void onDozeAmountChanged(float f, float f2) {
        if (this.mAnimatingScreenOff && this.mDozing && f == 1.0f) {
            this.mAnimatingScreenOff = false;
            setShowingLocked(this.mShowing, true);
        }
    }

    public void setPulsing(boolean z) {
        this.mPulsing = z;
    }

    private static class StartKeyguardExitAnimParams {
        long fadeoutDuration;
        RemoteAnimationTarget[] mApps;
        IRemoteAnimationFinishedCallback mFinishedCallback;
        RemoteAnimationTarget[] mNonApps;
        int mTransit;
        RemoteAnimationTarget[] mWallpapers;
        long startTime;

        private StartKeyguardExitAnimParams(int i, long j, long j2, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            this.mTransit = i;
            this.startTime = j;
            this.fadeoutDuration = j2;
            this.mApps = remoteAnimationTargetArr;
            this.mWallpapers = remoteAnimationTargetArr2;
            this.mNonApps = remoteAnimationTargetArr3;
            this.mFinishedCallback = iRemoteAnimationFinishedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public void setShowingLocked(boolean z) {
        setShowingLocked(z, false);
    }

    private void setShowingLocked(boolean z, boolean z2) {
        boolean z3 = true;
        boolean z4 = this.mDozing && !this.mWakeAndUnlocking;
        if (z == this.mShowing && z4 == this.mAodShowing && !z2) {
            z3 = false;
        }
        this.mShowing = z;
        this.mAodShowing = z4;
        if (z3) {
            notifyDefaultDisplayCallbacks(z);
            updateActivityLockScreenState(z, z4);
        }
    }

    private void notifyDefaultDisplayCallbacks(boolean z) {
        DejankUtils.whitelistIpcs((Runnable) new KeyguardViewMediator$$ExternalSyntheticLambda8(this, z));
        updateInputRestrictedLocked();
        this.mUiBgExecutor.execute(new KeyguardViewMediator$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyDefaultDisplayCallbacks$8(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            IKeyguardStateCallback iKeyguardStateCallback = this.mKeyguardStateCallbacks.get(size);
            try {
                iKeyguardStateCallback.onShowingStateChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onShowingStateChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(iKeyguardStateCallback);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyDefaultDisplayCallbacks$9() {
        this.mTrustManager.reportKeyguardShowingChanged();
    }

    /* access modifiers changed from: private */
    public void notifyTrustedChangedLocked(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            try {
                this.mKeyguardStateCallbacks.get(size).onTrustedChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call notifyTrustedChangedLocked", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(size);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyHasLockscreenWallpaperChanged(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            try {
                this.mKeyguardStateCallbacks.get(size).onHasLockscreenWallpaperChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onHasLockscreenWallpaperChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(size);
                }
            }
        }
    }

    public void addStateMonitorCallback(IKeyguardStateCallback iKeyguardStateCallback) {
        synchronized (this) {
            this.mKeyguardStateCallbacks.add(iKeyguardStateCallback);
            try {
                iKeyguardStateCallback.onSimSecureStateChanged(this.mUpdateMonitor.isSimPinSecure());
                iKeyguardStateCallback.onShowingStateChanged(this.mShowing);
                iKeyguardStateCallback.onInputRestrictedStateChanged(this.mInputRestricted);
                iKeyguardStateCallback.onTrustedChanged(this.mUpdateMonitor.getUserHasTrust(KeyguardUpdateMonitor.getCurrentUser()));
                iKeyguardStateCallback.onHasLockscreenWallpaperChanged(this.mUpdateMonitor.hasLockscreenWallpaper());
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call to IKeyguardStateCallback", e);
            }
        }
    }

    private static class DismissMessage {
        private final IKeyguardDismissCallback mCallback;
        private final CharSequence mMessage;

        DismissMessage(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
            this.mCallback = iKeyguardDismissCallback;
            this.mMessage = charSequence;
        }

        public IKeyguardDismissCallback getCallback() {
            return this.mCallback;
        }

        public CharSequence getMessage() {
            return this.mMessage;
        }
    }
}
