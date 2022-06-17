package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricSourceType;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardViewController;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BiometricUnlockController extends KeyguardUpdateMonitorCallback implements Dumpable {
    private static final UiEventLogger UI_EVENT_LOGGER = new UiEventLoggerImpl();
    private final AuthController mAuthController;
    private BiometricModeListener mBiometricModeListener;
    private BiometricSourceType mBiometricType;
    private BiometricUnlockWithoutAnimListener mBiometricUnlockWithoutAnimListener;
    private CliStatusBarWindowController mCliStatusBarWindowController;
    private final Context mContext;
    private final DozeParameters mDozeParameters;
    private DozeScrimController mDozeScrimController;
    private boolean mFadedAwayAfterWakeAndUnlock;
    private boolean mFpsLockedOut;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public boolean mHasScreenTurnedOnSinceAuthenticating;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private KeyguardViewController mKeyguardViewController;
    private KeyguardViewMediator mKeyguardViewMediator;
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private int mMode;
    private MotoDisplayManager mMotoDisplayManager;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController mNotificationShadeWindowController;
    private PendingAuthenticated mPendingAuthenticated = null;
    /* access modifiers changed from: private */
    public boolean mPendingShowBouncer;
    private final PowerManager mPowerManager;
    private final Runnable mReleaseBiometricWakeLockRunnable = new Runnable() {
        public void run() {
            Log.i("BiometricUnlockCtrl", "biometric wakelock: TIMEOUT!!");
            BiometricUnlockController.this.releaseBiometricWakeLock();
        }
    };
    private final ScreenLifecycle.Observer mScreenObserver;
    private ScrimController mScrimController;
    private final ShadeController mShadeController;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private PowerManager.WakeLock mWakeLock;
    private final int mWakeUpDelay;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver;

    public interface BiometricModeListener {
        void notifyBiometricAuthModeChanged();

        void onModeChanged(int i);

        void onResetMode();
    }

    public interface BiometricUnlockWithoutAnimListener {
        void expandedInvisible();

        void resetPanelViewForBiometric();
    }

    private static final class PendingAuthenticated {
        public final BiometricSourceType biometricSourceType;
        public final boolean isStrongBiometric;
        public final int userId;

        PendingAuthenticated(int i, BiometricSourceType biometricSourceType2, boolean z) {
            this.userId = i;
            this.biometricSourceType = biometricSourceType2;
            this.isStrongBiometric = z;
        }
    }

    @VisibleForTesting
    public enum BiometricUiEvent implements UiEventLogger.UiEventEnum {
        BIOMETRIC_FINGERPRINT_SUCCESS(396),
        BIOMETRIC_FINGERPRINT_FAILURE(397),
        BIOMETRIC_FINGERPRINT_ERROR(398),
        BIOMETRIC_FACE_SUCCESS(399),
        BIOMETRIC_FACE_FAILURE(400),
        BIOMETRIC_FACE_ERROR(401),
        BIOMETRIC_IRIS_SUCCESS(402),
        BIOMETRIC_IRIS_FAILURE(403),
        BIOMETRIC_IRIS_ERROR(404);
        
        static final Map<BiometricSourceType, BiometricUiEvent> ERROR_EVENT_BY_SOURCE_TYPE = null;
        static final Map<BiometricSourceType, BiometricUiEvent> FAILURE_EVENT_BY_SOURCE_TYPE = null;
        static final Map<BiometricSourceType, BiometricUiEvent> SUCCESS_EVENT_BY_SOURCE_TYPE = null;
        private final int mId;

        static {
            BiometricUiEvent biometricUiEvent;
            BiometricUiEvent biometricUiEvent2;
            BiometricUiEvent biometricUiEvent3;
            BiometricUiEvent biometricUiEvent4;
            BiometricUiEvent biometricUiEvent5;
            BiometricUiEvent biometricUiEvent6;
            BiometricUiEvent biometricUiEvent7;
            BiometricUiEvent biometricUiEvent8;
            BiometricUiEvent biometricUiEvent9;
            ERROR_EVENT_BY_SOURCE_TYPE = Map.of(BiometricSourceType.FINGERPRINT, biometricUiEvent3, BiometricSourceType.FACE, biometricUiEvent6, BiometricSourceType.IRIS, biometricUiEvent9);
            SUCCESS_EVENT_BY_SOURCE_TYPE = Map.of(BiometricSourceType.FINGERPRINT, biometricUiEvent, BiometricSourceType.FACE, biometricUiEvent4, BiometricSourceType.IRIS, biometricUiEvent7);
            FAILURE_EVENT_BY_SOURCE_TYPE = Map.of(BiometricSourceType.FINGERPRINT, biometricUiEvent2, BiometricSourceType.FACE, biometricUiEvent5, BiometricSourceType.IRIS, biometricUiEvent8);
        }

        private BiometricUiEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public BiometricUnlockController(Context context, DozeScrimController dozeScrimController, KeyguardViewMediator keyguardViewMediator, ScrimController scrimController, ShadeController shadeController, NotificationShadeWindowController notificationShadeWindowController, KeyguardStateController keyguardStateController, Handler handler, KeyguardUpdateMonitor keyguardUpdateMonitor, Resources resources, KeyguardBypassController keyguardBypassController, DozeParameters dozeParameters, MetricsLogger metricsLogger, DumpManager dumpManager, PowerManager powerManager, NotificationMediaManager notificationMediaManager, WakefulnessLifecycle wakefulnessLifecycle, ScreenLifecycle screenLifecycle, AuthController authController) {
        KeyguardUpdateMonitor keyguardUpdateMonitor2 = keyguardUpdateMonitor;
        KeyguardBypassController keyguardBypassController2 = keyguardBypassController;
        C17673 r3 = new WakefulnessLifecycle.Observer() {
            public void onFinishedWakingUp() {
                if (BiometricUnlockController.this.mPendingShowBouncer) {
                    BiometricUnlockController.this.showBouncer();
                }
            }
        };
        this.mWakefulnessObserver = r3;
        C17684 r4 = new ScreenLifecycle.Observer() {
            public void onScreenTurnedOn() {
                boolean unused = BiometricUnlockController.this.mHasScreenTurnedOnSinceAuthenticating = true;
            }
        };
        this.mScreenObserver = r4;
        this.mContext = context;
        this.mPowerManager = powerManager;
        this.mShadeController = shadeController;
        this.mUpdateMonitor = keyguardUpdateMonitor2;
        this.mDozeParameters = dozeParameters;
        keyguardUpdateMonitor2.registerCallback(this);
        this.mMediaManager = notificationMediaManager;
        wakefulnessLifecycle.addObserver(r3);
        screenLifecycle.addObserver(r4);
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mDozeScrimController = dozeScrimController;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mScrimController = scrimController;
        this.mKeyguardStateController = keyguardStateController;
        this.mHandler = handler;
        this.mWakeUpDelay = resources.getInteger(17694984);
        this.mKeyguardBypassController = keyguardBypassController2;
        keyguardBypassController2.setUnlockController(this);
        this.mMetricsLogger = metricsLogger;
        this.mAuthController = authController;
        dumpManager.registerDumpable(BiometricUnlockController.class.getName(), this);
        if (MotoFeature.getInstance(context).isSupportCli()) {
            this.mCliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        }
    }

    public void setKeyguardViewController(KeyguardViewController keyguardViewController) {
        this.mKeyguardViewController = keyguardViewController;
    }

    public void setBiometricModeListener(BiometricModeListener biometricModeListener) {
        this.mBiometricModeListener = biometricModeListener;
    }

    /* access modifiers changed from: private */
    public void releaseBiometricWakeLock() {
        if (this.mWakeLock != null) {
            this.mHandler.removeCallbacks(this.mReleaseBiometricWakeLockRunnable);
            Log.i("BiometricUnlockCtrl", "releasing biometric wakelock");
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    public void onBiometricAcquired(BiometricSourceType biometricSourceType) {
        Trace.beginSection("BiometricUnlockController#onBiometricAcquired");
        releaseBiometricWakeLock();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (LatencyTracker.isEnabled(this.mContext)) {
                int i = 2;
                if (biometricSourceType == BiometricSourceType.FACE) {
                    i = 7;
                }
                LatencyTracker.getInstance(this.mContext).onActionStart(i);
            }
            this.mWakeLock = this.mPowerManager.newWakeLock(1, "wake-and-unlock:wakelock");
            Trace.beginSection("acquiring wake-and-unlock");
            this.mWakeLock.acquire();
            Trace.endSection();
            Log.i("BiometricUnlockCtrl", "biometric acquired, grabbing biometric wakelock");
            this.mHandler.postDelayed(this.mReleaseBiometricWakeLockRunnable, 15000);
        }
        Trace.endSection();
    }

    private boolean pulsingOrAod() {
        ScrimState state = this.mScrimController.getState();
        return state == ScrimState.AOD || state == ScrimState.PULSING;
    }

    public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
        Trace.beginSection("BiometricUnlockController#onBiometricAuthenticated");
        boolean z2 = false;
        this.mFpsLockedOut = false;
        if (this.mUpdateMonitor.isGoingToSleep()) {
            this.mPendingAuthenticated = new PendingAuthenticated(i, biometricSourceType, z);
            Trace.endSection();
            Log.v("BiometricUnlockCtrl", "isGoingToSleep return");
            return;
        }
        this.mBiometricType = biometricSourceType;
        this.mMetricsLogger.write(new LogMaker(1697).setType(10).setSubtype(toSubtype(biometricSourceType)));
        Optional ofNullable = Optional.ofNullable(BiometricUiEvent.SUCCESS_EVENT_BY_SOURCE_TYPE.get(biometricSourceType));
        UiEventLogger uiEventLogger = UI_EVENT_LOGGER;
        Objects.requireNonNull(uiEventLogger);
        ofNullable.ifPresent(new BiometricUnlockController$$ExternalSyntheticLambda2(uiEventLogger));
        if (this.mKeyguardStateController.isOccluded() || this.mKeyguardBypassController.onBiometricAuthenticated(biometricSourceType, z)) {
            z2 = true;
        }
        if (z2) {
            this.mKeyguardViewMediator.userActivity();
            startWakeAndUnlock(biometricSourceType, z);
            return;
        }
        Log.d("BiometricUnlockCtrl", "onBiometricAuthenticated aborted by bypass controller");
    }

    public void startWakeAndUnlock(BiometricSourceType biometricSourceType, boolean z) {
        startWakeAndUnlock(calculateMode(biometricSourceType, z));
    }

    public void startWakeAndUnlock(int i) {
        Log.v("BiometricUnlockCtrl", "startWakeAndUnlock(" + i + ")");
        boolean isDeviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        this.mMode = i;
        this.mHasScreenTurnedOnSinceAuthenticating = false;
        if (i == 2 && pulsingOrAod()) {
            this.mNotificationShadeWindowController.setForceDozeBrightness(true);
        }
        boolean z = i == 1 && this.mDozeParameters.getAlwaysOn() && this.mWakeUpDelay > 0;
        BiometricUnlockController$$ExternalSyntheticLambda1 biometricUnlockController$$ExternalSyntheticLambda1 = new BiometricUnlockController$$ExternalSyntheticLambda1(this, isDeviceInteractive, z);
        if (!z && this.mMode != 0) {
            biometricUnlockController$$ExternalSyntheticLambda1.run();
        }
        switch (this.mMode) {
            case 1:
            case 2:
            case 6:
                this.mMediaManager.updateMediaMetaData(false, true);
                int i2 = this.mMode;
                if (i2 == 2) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_PULSING");
                } else if (i2 == 1) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK");
                } else {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_FROM_DREAM");
                    this.mUpdateMonitor.awakenFromDream();
                }
                this.mNotificationShadeWindowController.setNotificationShadeFocusable(false);
                if (z) {
                    this.mHandler.postDelayed(biometricUnlockController$$ExternalSyntheticLambda1, (long) this.mWakeUpDelay);
                } else {
                    this.mKeyguardViewMediator.onWakeAndUnlocking();
                }
                Trace.endSection();
                break;
            case 3:
                Trace.beginSection("MODE_SHOW_BOUNCER");
                if (!isDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                } else {
                    showBouncer();
                }
                if (MotoFeature.getInstance(this.mContext).isSupportCli() && this.mMode == 5) {
                    this.mCliStatusBarWindowController.setBiometricUnlockCollapsing(true);
                }
                this.mBiometricUnlockWithoutAnimListener.resetPanelViewForBiometric();
                this.mBiometricUnlockWithoutAnimListener.expandedInvisible();
                Trace.endSection();
                break;
            case 5:
                Trace.beginSection("MODE_UNLOCK_COLLAPSING");
                if (!isDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                } else {
                    this.mPendingShowBouncer = false;
                    this.mKeyguardViewController.notifyKeyguardAuthenticated(false);
                }
                Trace.endSection();
                break;
            case 7:
            case 8:
                Trace.beginSection("MODE_DISMISS_BOUNCER or MODE_UNLOCK_FADING");
                this.mKeyguardViewController.notifyKeyguardAuthenticated(false);
                Trace.endSection();
                break;
        }
        onModeChanged(this.mMode);
        BiometricModeListener biometricModeListener = this.mBiometricModeListener;
        if (biometricModeListener != null) {
            biometricModeListener.notifyBiometricAuthModeChanged();
        }
        int i3 = this.mMode;
        if (!(i3 == 3 || i3 == 5)) {
            Intent intent = new Intent();
            intent.setAction("com.motorola.UNLOCK_SUCCESS");
            intent.addFlags(16777216);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
        }
        if (!(getMode() == 3 || getMode() == 0)) {
            this.mUpdateMonitor.reportFingerprintUnlock(true);
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startWakeAndUnlock$0(boolean z, boolean z2) {
        if (!z) {
            Log.i("BiometricUnlockCtrl", "bio wakelock: Authenticated, waking up...");
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "android.policy:BIOMETRIC");
        }
        if (z2) {
            this.mKeyguardViewMediator.onWakeAndUnlocking();
        }
        Trace.beginSection("release wake-and-unlock");
        releaseBiometricWakeLock();
        Trace.endSection();
    }

    private void onModeChanged(int i) {
        BiometricModeListener biometricModeListener = this.mBiometricModeListener;
        if (biometricModeListener != null) {
            biometricModeListener.onModeChanged(i);
        }
    }

    /* access modifiers changed from: private */
    public void showBouncer() {
        if (this.mMode == 3) {
            this.mKeyguardViewController.showBouncer(false);
        }
        this.mShadeController.animateCollapsePanels(0, true, false, 1.1f);
        this.mPendingShowBouncer = false;
    }

    public void onStartedGoingToSleep(int i) {
        resetMode();
        this.mFadedAwayAfterWakeAndUnlock = false;
        this.mPendingAuthenticated = null;
    }

    public void onFinishedGoingToSleep(int i) {
        Trace.beginSection("BiometricUnlockController#onFinishedGoingToSleep");
        PendingAuthenticated pendingAuthenticated = this.mPendingAuthenticated;
        if (pendingAuthenticated != null) {
            Log.v("BiometricUnlockCtrl", "onFinishedGoingToSleep onFingerprintAuthenticated");
            this.mHandler.post(new BiometricUnlockController$$ExternalSyntheticLambda0(this, pendingAuthenticated));
            this.mPendingAuthenticated = null;
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishedGoingToSleep$1(PendingAuthenticated pendingAuthenticated) {
        onBiometricAuthenticated(pendingAuthenticated.userId, pendingAuthenticated.biometricSourceType, pendingAuthenticated.isStrongBiometric);
    }

    public boolean hasPendingAuthentication() {
        PendingAuthenticated pendingAuthenticated = this.mPendingAuthenticated;
        return pendingAuthenticated != null && this.mUpdateMonitor.isUnlockingWithBiometricAllowed(pendingAuthenticated.isStrongBiometric) && this.mPendingAuthenticated.userId == KeyguardUpdateMonitor.getCurrentUser();
    }

    public int getMode() {
        return this.mMode;
    }

    private int calculateMode(BiometricSourceType biometricSourceType, boolean z) {
        if (biometricSourceType == BiometricSourceType.FACE || biometricSourceType == BiometricSourceType.IRIS) {
            return calculateModeForPassiveAuth(z);
        }
        return calculateModeForFingerprint(z);
    }

    private int calculateModeForFingerprint(boolean z) {
        boolean isUnlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z);
        boolean isDreaming = this.mUpdateMonitor.isDreaming();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mKeyguardViewController.isShowing()) {
                return 4;
            }
            if (this.mDozeScrimController.isPulsing() && isUnlockingWithBiometricAllowed) {
                return 2;
            }
            if (isUnlockingWithBiometricAllowed || !this.mKeyguardStateController.isMethodSecure()) {
                return 1;
            }
            return 3;
        } else if (isUnlockingWithBiometricAllowed && isDreaming) {
            return 6;
        } else {
            if (!this.mKeyguardViewController.isShowing()) {
                return 0;
            }
            if (this.mKeyguardViewController.bouncerIsOrWillBeShowing() && isUnlockingWithBiometricAllowed) {
                return 8;
            }
            if (isUnlockingWithBiometricAllowed) {
                return 5;
            }
            if (!this.mKeyguardViewController.isBouncerShowing()) {
                return 3;
            }
            return 0;
        }
    }

    private int calculateModeForPassiveAuth(boolean z) {
        boolean isUnlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z);
        boolean isDreaming = this.mUpdateMonitor.isDreaming();
        boolean z2 = this.mKeyguardBypassController.getBypassEnabled() || this.mKeyguardBypassController.getUserHasDeviceEntryIntent();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mKeyguardViewController.isShowing()) {
                if (z2) {
                    return 1;
                }
                return 4;
            } else if (!isUnlockingWithBiometricAllowed) {
                if (z2) {
                    return 3;
                }
                return 0;
            } else if (this.mDozeScrimController.isPulsing()) {
                if (z2) {
                    return 2;
                }
                return 4;
            } else if (z2) {
                return 2;
            } else {
                return 4;
            }
        } else if (!isUnlockingWithBiometricAllowed || !isDreaming) {
            if (isUnlockingWithBiometricAllowed && this.mKeyguardStateController.isOccluded()) {
                return 5;
            }
            if (!this.mKeyguardViewController.isShowing()) {
                return 0;
            }
            if ((this.mKeyguardViewController.bouncerIsOrWillBeShowing() || this.mKeyguardBypassController.getAltBouncerShowing()) && isUnlockingWithBiometricAllowed) {
                return (!z2 || !this.mKeyguardBypassController.canPlaySubtleWindowAnimations()) ? 8 : 7;
            }
            if (isUnlockingWithBiometricAllowed) {
                if (z2 || this.mAuthController.isUdfpsFingerDown()) {
                    return 5;
                }
                return 0;
            } else if (z2) {
                return 3;
            } else {
                return 0;
            }
        } else if (z2) {
            return 6;
        } else {
            return 4;
        }
    }

    public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(11).setSubtype(toSubtype(biometricSourceType)));
        Optional ofNullable = Optional.ofNullable(BiometricUiEvent.FAILURE_EVENT_BY_SOURCE_TYPE.get(biometricSourceType));
        UiEventLogger uiEventLogger = UI_EVENT_LOGGER;
        Objects.requireNonNull(uiEventLogger);
        ofNullable.ifPresent(new BiometricUnlockController$$ExternalSyntheticLambda2(uiEventLogger));
        this.mUpdateMonitor.reportFingerprintUnlock(false);
        this.mFpsLockedOut = false;
        cleanup();
    }

    public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(15).setSubtype(toSubtype(biometricSourceType)).addTaggedData(1741, Integer.valueOf(i)));
        Optional ofNullable = Optional.ofNullable(BiometricUiEvent.ERROR_EVENT_BY_SOURCE_TYPE.get(biometricSourceType));
        UiEventLogger uiEventLogger = UI_EVENT_LOGGER;
        Objects.requireNonNull(uiEventLogger);
        ofNullable.ifPresent(new BiometricUnlockController$$ExternalSyntheticLambda2(uiEventLogger));
        cleanup();
        if (i != 7) {
            handleBiometricError(i, false, biometricSourceType);
        } else if (!this.mFpsLockedOut) {
            if (MotoDisplayManager.isAospAD()) {
                Log.i("BiometricUnlockCtrl", "Transactional Failure, waking up...");
                this.mPowerManager.wakeUp(SystemClock.uptimeMillis());
            } else {
                handleBiometricError(i, true, biometricSourceType);
            }
            this.mFpsLockedOut = true;
        }
    }

    public void onBiometricHelp(int i, String str, BiometricSourceType biometricSourceType) {
        handleBiometricHelp(i, biometricSourceType);
        this.mFpsLockedOut = false;
    }

    private void cleanup() {
        releaseBiometricWakeLock();
    }

    public void startKeyguardFadingAway() {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BiometricUnlockController.this.mNotificationShadeWindowController.setForceDozeBrightness(false);
            }
        }, 96);
    }

    public void finishKeyguardFadingAway() {
        if (isWakeAndUnlock()) {
            this.mFadedAwayAfterWakeAndUnlock = true;
        }
        resetMode();
    }

    private void resetMode() {
        this.mMode = 0;
        this.mBiometricType = null;
        this.mNotificationShadeWindowController.setForceDozeBrightness(false);
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            this.mCliStatusBarWindowController.setBiometricUnlockCollapsing(false);
        }
        BiometricModeListener biometricModeListener = this.mBiometricModeListener;
        if (biometricModeListener != null) {
            biometricModeListener.onResetMode();
            this.mBiometricModeListener.notifyBiometricAuthModeChanged();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(" BiometricUnlockController:");
        printWriter.print("   mMode=");
        printWriter.println(this.mMode);
        printWriter.print("   mWakeLock=");
        printWriter.println(this.mWakeLock);
    }

    public boolean isWakeAndUnlock() {
        int i = this.mMode;
        return i == 1 || i == 2 || i == 6;
    }

    public boolean unlockedByWakeAndUnlock() {
        return isWakeAndUnlock() || this.mFadedAwayAfterWakeAndUnlock;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = r1.mMode;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isBiometricUnlock() {
        /*
            r1 = this;
            boolean r0 = r1.isWakeAndUnlock()
            if (r0 != 0) goto L_0x0011
            int r1 = r1.mMode
            r0 = 5
            if (r1 == r0) goto L_0x0011
            r0 = 7
            if (r1 != r0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r1 = 0
            goto L_0x0012
        L_0x0011:
            r1 = 1
        L_0x0012:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.BiometricUnlockController.isBiometricUnlock():boolean");
    }

    public BiometricSourceType getBiometricType() {
        return this.mBiometricType;
    }

    /* renamed from: com.android.systemui.statusbar.phone.BiometricUnlockController$5 */
    static /* synthetic */ class C17695 {
        static final /* synthetic */ int[] $SwitchMap$android$hardware$biometrics$BiometricSourceType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                android.hardware.biometrics.BiometricSourceType[] r0 = android.hardware.biometrics.BiometricSourceType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$android$hardware$biometrics$BiometricSourceType = r0
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.FINGERPRINT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$android$hardware$biometrics$BiometricSourceType     // Catch:{ NoSuchFieldError -> 0x001d }
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.FACE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$android$hardware$biometrics$BiometricSourceType     // Catch:{ NoSuchFieldError -> 0x0028 }
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.IRIS     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.BiometricUnlockController.C17695.<clinit>():void");
        }
    }

    private int toSubtype(BiometricSourceType biometricSourceType) {
        int i = C17695.$SwitchMap$android$hardware$biometrics$BiometricSourceType[biometricSourceType.ordinal()];
        if (i == 1) {
            return 0;
        }
        if (i != 2) {
            return i != 3 ? 3 : 2;
        }
        return 1;
    }

    public void setBiometricUnlockWithoutAnimListener(BiometricUnlockWithoutAnimListener biometricUnlockWithoutAnimListener) {
        this.mBiometricUnlockWithoutAnimListener = biometricUnlockWithoutAnimListener;
    }

    private void handleBiometricError(int i, boolean z, BiometricSourceType biometricSourceType) {
        Bundle bundle = new Bundle();
        bundle.putInt("FPS_ERROR_MESSAGE_ID", i);
        bundle.putSerializable("BIOMETRIC_ERROR_TYPE_ID", biometricSourceType);
        getMotoDisplayManagerIfNeed();
        this.mMotoDisplayManager.notifyEvent("FPS_ERROR_MESSAGE_ACTION", z, (String) null, (String) null, bundle);
    }

    private void handleBiometricHelp(int i, BiometricSourceType biometricSourceType) {
        Bundle bundle = new Bundle();
        bundle.putInt("FPS_HELP_MESSAGE_ID", i);
        bundle.putSerializable("BIOMETRIC_HELP_TYPE_ID", biometricSourceType);
        getMotoDisplayManagerIfNeed();
        this.mMotoDisplayManager.notifyEvent("FPS_HELP_MESSAGE_ACTION", false, (String) null, (String) null, bundle);
    }

    private void getMotoDisplayManagerIfNeed() {
        if (this.mMotoDisplayManager == null) {
            this.mMotoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
        }
    }
}
