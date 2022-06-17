package com.android.systemui.biometrics;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IUdfpsHbmListener;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.hardware.fingerprint.IUdfpsOverlayControllerCallback;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.concurrency.Execution;
import com.motorola.systemui.biometrics.MotoUdfpsAnimationView;
import com.motorola.systemui.biometrics.MotoUdfpsAnimationViewController;
import java.util.Optional;
import kotlin.Unit;

public class UdfpsController implements DozeReceiver {
    public static final VibrationEffect EFFECT_CLICK = VibrationEffect.get(0);
    private static final boolean UDFPS_DEBUG = Build.IS_DEBUGGABLE;
    @VisibleForTesting
    public static final AudioAttributes VIBRATION_SONIFICATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private final AccessibilityManager mAccessibilityManager;
    private int mActivePointerId = -1;
    /* access modifiers changed from: private */
    public Runnable mAodInterruptRunnable;
    private int mAodPointerDownDelay;
    private boolean mAttemptedToDismissKeyguard;
    private HandlerThread mBgHandlerThread;
    private final BroadcastReceiver mBroadcastReceiver;
    private Runnable mCancelAodTimeoutAction;
    private final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final WindowManager.LayoutParams mCoreLayoutParams;
    private final DumpManager mDumpManager;
    private final Execution mExecution;
    private final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public final DelayableExecutor mFgExecutor;
    /* access modifiers changed from: private */
    public final FingerprintManager mFingerprintManager;
    /* access modifiers changed from: private */
    public boolean mGoodCaptureReceived;
    private final UdfpsHbmProvider mHbmProvider;
    private final LayoutInflater mInflater;
    private boolean mIsAodInterruptActive;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final StatusBarKeyguardViewManager mKeyguardViewManager;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final LockscreenShadeTransitionController mLockscreenShadeTransitionController;
    private final Handler mMainHandler;
    private MotoDisplayManager mMotoDisplayManager;
    private MotoUdfpsAnimationViewController mMotoUdfpsAnimationViewController;
    /* access modifiers changed from: private */
    public MotoUdfpsMaskViewController mMotoUdfpsMaskViewController;
    private boolean mOnFingerDown;
    @SuppressLint({"ClickableViewAccessibility"})
    private final View.OnHoverListener mOnHoverListener;
    @SuppressLint({"ClickableViewAccessibility"})
    private final View.OnTouchListener mOnTouchListener;
    @VisibleForTesting
    final BiometricOrientationEventListener mOrientationListener;
    private int mPointerDownDelay;
    private final PowerManager mPowerManager;
    private boolean mPreUpIfNeeded;
    private int mReason = -1;
    private final ScreenLifecycle.Observer mScreenObserver;
    /* access modifiers changed from: private */
    public boolean mScreenOn;
    @VisibleForTesting
    final FingerprintSensorPropertiesInternal mSensorProps;
    ServerRequest mServerRequest;
    private final StatusBar mStatusBar;
    private final StatusBarStateController mStatusBarStateController;
    private final AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    private long mTouchLogTime;
    /* access modifiers changed from: private */
    public Handler mUdfpsBgHandler;
    private VelocityTracker mVelocityTracker;
    private final Vibrator mVibrator;
    /* access modifiers changed from: private */
    public UdfpsView mView;
    private final WindowManager mWindowManager;

    private int getCoreLayoutParamFlags() {
        return 16777512;
    }

    @VisibleForTesting
    public void playStartHaptic() {
    }

    private static class ServerRequest {
        final IUdfpsOverlayControllerCallback mCallback;
        final UdfpsEnrollHelper mEnrollHelper;
        final int mRequestReason;

        ServerRequest(int i, IUdfpsOverlayControllerCallback iUdfpsOverlayControllerCallback, UdfpsEnrollHelper udfpsEnrollHelper) {
            this.mRequestReason = i;
            this.mCallback = iUdfpsOverlayControllerCallback;
            this.mEnrollHelper = udfpsEnrollHelper;
        }

        /* access modifiers changed from: package-private */
        public void onEnrollmentProgress(int i) {
            UdfpsEnrollHelper udfpsEnrollHelper = this.mEnrollHelper;
            if (udfpsEnrollHelper != null) {
                udfpsEnrollHelper.onEnrollmentProgress(i);
            }
        }

        /* access modifiers changed from: package-private */
        public void onAcquiredGood() {
            UdfpsEnrollHelper udfpsEnrollHelper = this.mEnrollHelper;
            if (udfpsEnrollHelper != null) {
                udfpsEnrollHelper.animateIfLastStep();
            }
        }

        /* access modifiers changed from: package-private */
        public void onEnrollmentHelp() {
            UdfpsEnrollHelper udfpsEnrollHelper = this.mEnrollHelper;
            if (udfpsEnrollHelper != null) {
                udfpsEnrollHelper.onEnrollmentHelp();
            }
        }

        /* access modifiers changed from: package-private */
        public void onUserCanceled() {
            try {
                this.mCallback.onUserCanceled();
            } catch (RemoteException e) {
                Log.e("UdfpsController", "Remote exception", e);
            }
        }
    }

    public class UdfpsOverlayController extends IUdfpsOverlayController.Stub {
        public UdfpsOverlayController() {
        }

        public void showUdfpsOverlay(int i, int i2, IUdfpsOverlayControllerCallback iUdfpsOverlayControllerCallback) {
            UdfpsController.this.mMotoUdfpsMaskViewController.setUdfpsOverlayControllerCallback(iUdfpsOverlayControllerCallback);
            UdfpsController.this.mMotoUdfpsMaskViewController.setUdfpsReason(i2);
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda4(this, i2, iUdfpsOverlayControllerCallback));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showUdfpsOverlay$0(int i, IUdfpsOverlayControllerCallback iUdfpsOverlayControllerCallback) {
            UdfpsController.this.mServerRequest = new ServerRequest(i, iUdfpsOverlayControllerCallback, (i == 1 || i == 2) ? new UdfpsEnrollHelper(UdfpsController.this.mContext, i) : null);
            UdfpsController.this.updateOverlay();
        }

        public void hideUdfpsOverlay(int i) {
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$hideUdfpsOverlay$1() {
            UdfpsController udfpsController = UdfpsController.this;
            udfpsController.mServerRequest = null;
            udfpsController.updateOverlay();
        }

        public void onAcquiredGood(int i) {
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda2(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAcquiredGood$2(int i) {
            if (UdfpsController.this.mView == null) {
                Log.e("UdfpsController", "Null view when onAcquiredGood for sensorId: " + i);
                return;
            }
            boolean unused = UdfpsController.this.mGoodCaptureReceived = true;
            UdfpsController.this.mView.stopIllumination();
            ServerRequest serverRequest = UdfpsController.this.mServerRequest;
            if (serverRequest != null) {
                serverRequest.onAcquiredGood();
            } else {
                Log.e("UdfpsController", "Null serverRequest when onAcquiredGood");
            }
        }

        public void onEnrollmentProgress(int i, int i2) {
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda3(this, i2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollmentProgress$3(int i) {
            ServerRequest serverRequest = UdfpsController.this.mServerRequest;
            if (serverRequest == null) {
                Log.e("UdfpsController", "onEnrollProgress received but serverRequest is null");
            } else {
                serverRequest.onEnrollmentProgress(i);
            }
        }

        public void onEnrollmentHelp(int i) {
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollmentHelp$4() {
            ServerRequest serverRequest = UdfpsController.this.mServerRequest;
            if (serverRequest == null) {
                Log.e("UdfpsController", "onEnrollmentHelp received but serverRequest is null");
            } else {
                serverRequest.onEnrollmentHelp();
            }
        }

        public void setDebugMessage(int i, String str) {
            UdfpsController.this.mFgExecutor.execute(new UdfpsController$UdfpsOverlayController$$ExternalSyntheticLambda5(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setDebugMessage$5(String str) {
            if (UdfpsController.this.mView != null) {
                UdfpsController.this.mView.setDebugMessage(str);
            }
        }
    }

    private static float computePointerSpeed(VelocityTracker velocityTracker, int i) {
        return (float) Math.sqrt(Math.pow((double) velocityTracker.getXVelocity(i), 2.0d) + Math.pow((double) velocityTracker.getYVelocity(i), 2.0d));
    }

    public boolean onTouch(MotionEvent motionEvent) {
        UdfpsView udfpsView = this.mView;
        if (udfpsView == null) {
            return false;
        }
        return onTouch(udfpsView, motionEvent, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return onTouch(view, motionEvent, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return onTouch(view, motionEvent, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(boolean z) {
        updateTouchListener();
    }

    private boolean isWithinSensorArea(UdfpsView udfpsView, float f, float f2, boolean z) {
        if (z) {
            return udfpsView.isWithinSensorArea(f, f2);
        }
        UdfpsView udfpsView2 = this.mView;
        if (udfpsView2 == null || udfpsView2.getAnimationViewController() == null || this.mView.getAnimationViewController().shouldPauseAuth() || !getSensorLocation().contains(f, f2)) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00e6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onTouch(android.view.View r13, android.view.MotionEvent r14, boolean r15) {
        /*
            r12 = this;
            android.content.Context r0 = r12.mContext
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.udfpsUseAospTriggerFingerDown()
            r1 = 9
            r2 = 0
            if (r0 != 0) goto L_0x001e
            if (r15 == 0) goto L_0x001e
            int r0 = r14.getActionMasked()
            if (r0 == 0) goto L_0x001d
            int r0 = r14.getActionMasked()
            if (r0 != r1) goto L_0x001e
        L_0x001d:
            return r2
        L_0x001e:
            com.android.systemui.biometrics.UdfpsView r13 = (com.android.systemui.biometrics.UdfpsView) r13
            boolean r0 = r13.isIlluminationRequested()
            int r3 = r14.getActionMasked()
            r4 = -1
            r5 = 4
            r6 = 3
            r7 = 2
            java.lang.String r8 = "UdfpsController"
            r9 = 1
            if (r3 == 0) goto L_0x0075
            if (r3 == r9) goto L_0x004b
            if (r3 == r7) goto L_0x0048
            if (r3 == r6) goto L_0x004b
            if (r3 == r5) goto L_0x0044
            r10 = 7
            if (r3 == r10) goto L_0x0048
            if (r3 == r1) goto L_0x0075
            r13 = 10
            if (r3 == r13) goto L_0x004b
            goto L_0x01d3
        L_0x0044:
            r13.onTouchOutsideView()
            return r9
        L_0x0048:
            r3 = r2
            goto L_0x00ce
        L_0x004b:
            java.lang.String r13 = "UdfpsController.onTouch.ACTION_UP"
            android.os.Trace.beginSection(r13)
            r12.mActivePointerId = r4
            android.view.VelocityTracker r13 = r12.mVelocityTracker
            if (r13 == 0) goto L_0x005c
            r13.recycle()
            r13 = 0
            r12.mVelocityTracker = r13
        L_0x005c:
            java.lang.String r13 = "onTouch | finger up"
            android.util.Log.v(r8, r13)
            r12.mAttemptedToDismissKeyguard = r2
            r12.onFingerUp()
            r12.pointerPreUp()
            com.android.systemui.plugins.FalsingManager r12 = r12.mFalsingManager
            r13 = 13
            r12.isFalseTouch(r13)
            android.os.Trace.endSection()
            goto L_0x01d3
        L_0x0075:
            java.lang.String r1 = "UdfpsController.onTouch.ACTION_DOWN"
            android.os.Trace.beginSection(r1)
            android.view.VelocityTracker r1 = r12.mVelocityTracker
            if (r1 != 0) goto L_0x0085
            android.view.VelocityTracker r1 = android.view.VelocityTracker.obtain()
            r12.mVelocityTracker = r1
            goto L_0x0088
        L_0x0085:
            r1.clear()
        L_0x0088:
            float r1 = r14.getX()
            float r3 = r14.getY()
            boolean r1 = r12.isWithinSensorArea(r13, r1, r3, r15)
            if (r1 == 0) goto L_0x00ad
            java.lang.String r3 = "UdfpsController.e2e.onPointerDown"
            android.os.Trace.beginAsyncSection(r3, r2)
            java.lang.String r3 = "onTouch | action down"
            android.util.Log.v(r8, r3)
            int r3 = r14.getPointerId(r2)
            r12.mActivePointerId = r3
            android.view.VelocityTracker r3 = r12.mVelocityTracker
            r3.addMovement(r14)
            r3 = r9
            goto L_0x00ae
        L_0x00ad:
            r3 = r2
        L_0x00ae:
            if (r1 != 0) goto L_0x00b2
            if (r15 == 0) goto L_0x00cb
        L_0x00b2:
            boolean r1 = r12.shouldTryToDismissKeyguard()
            if (r1 == 0) goto L_0x00cb
            java.lang.String r1 = "onTouch | dismiss keyguard ACTION_DOWN"
            android.util.Log.v(r8, r1)
            boolean r1 = r12.mOnFingerDown
            if (r1 != 0) goto L_0x00c4
            r12.playStartHaptic()
        L_0x00c4:
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r1 = r12.mKeyguardViewManager
            r1.notifyKeyguardAuthenticated(r2)
            r12.mAttemptedToDismissKeyguard = r9
        L_0x00cb:
            android.os.Trace.endSection()
        L_0x00ce:
            java.lang.String r1 = "UdfpsController.onTouch.ACTION_MOVE"
            android.os.Trace.beginSection(r1)
            int r1 = r12.mActivePointerId
            if (r1 != r4) goto L_0x00dc
            int r1 = r14.getPointerId(r2)
            goto L_0x00e0
        L_0x00dc:
            int r1 = r14.findPointerIndex(r1)
        L_0x00e0:
            int r4 = r14.getActionIndex()
            if (r1 != r4) goto L_0x01cf
            float r4 = r14.getX(r1)
            float r10 = r14.getY(r1)
            boolean r13 = r12.isWithinSensorArea(r13, r4, r10, r15)
            if (r15 != 0) goto L_0x00f6
            if (r13 == 0) goto L_0x0112
        L_0x00f6:
            boolean r15 = r12.shouldTryToDismissKeyguard()
            if (r15 == 0) goto L_0x0112
            java.lang.String r13 = "onTouch | dismiss keyguard ACTION_MOVE"
            android.util.Log.v(r8, r13)
            boolean r13 = r12.mOnFingerDown
            if (r13 != 0) goto L_0x0108
            r12.playStartHaptic()
        L_0x0108:
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r13 = r12.mKeyguardViewManager
            r13.notifyKeyguardAuthenticated(r2)
            r12.mAttemptedToDismissKeyguard = r9
            r2 = r3
            goto L_0x01d3
        L_0x0112:
            if (r13 == 0) goto L_0x01c7
            android.view.VelocityTracker r13 = r12.mVelocityTracker
            if (r13 != 0) goto L_0x011e
            android.view.VelocityTracker r13 = android.view.VelocityTracker.obtain()
            r12.mVelocityTracker = r13
        L_0x011e:
            android.view.VelocityTracker r13 = r12.mVelocityTracker
            r13.addMovement(r14)
            android.view.VelocityTracker r13 = r12.mVelocityTracker
            r15 = 1000(0x3e8, float:1.401E-42)
            r13.computeCurrentVelocity(r15)
            android.view.VelocityTracker r13 = r12.mVelocityTracker
            int r15 = r12.mActivePointerId
            float r13 = computePointerSpeed(r13, r15)
            float r15 = r14.getTouchMinor(r1)
            float r1 = r14.getTouchMajor(r1)
            r4 = 1144750080(0x443b8000, float:750.0)
            int r4 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0143
            r4 = r9
            goto L_0x0144
        L_0x0143:
            r4 = r2
        L_0x0144:
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.Float r10 = java.lang.Float.valueOf(r15)
            r5[r2] = r10
            java.lang.Float r10 = java.lang.Float.valueOf(r1)
            r5[r9] = r10
            java.lang.Float r13 = java.lang.Float.valueOf(r13)
            r5[r7] = r13
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r4)
            r5[r6] = r13
            java.lang.String r13 = "minor: %.1f, major: %.1f, v: %.1f, exceedsVelocityThreshold: %b"
            java.lang.String r13 = java.lang.String.format(r13, r5)
            long r5 = android.os.SystemClock.elapsedRealtime()
            long r10 = r12.mTouchLogTime
            long r5 = r5 - r10
            if (r0 != 0) goto L_0x01a4
            boolean r0 = r12.mGoodCaptureReceived
            if (r0 != 0) goto L_0x01a4
            if (r4 != 0) goto L_0x01a4
            float r0 = r14.getRawX()
            int r0 = (int) r0
            float r14 = r14.getRawY()
            int r14 = (int) r14
            r12.onFingerDown(r0, r14, r15, r1)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "onTouch | finger down: "
            r14.append(r15)
            r14.append(r13)
            java.lang.String r13 = r14.toString()
            android.util.Log.v(r8, r13)
            long r13 = android.os.SystemClock.elapsedRealtime()
            r12.mTouchLogTime = r13
            android.os.PowerManager r12 = r12.mPowerManager
            long r13 = android.os.SystemClock.uptimeMillis()
            r12.userActivity(r13, r7, r2)
            goto L_0x01c5
        L_0x01a4:
            r14 = 50
            int r14 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r14 < 0) goto L_0x01c4
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "onTouch | finger move: "
            r14.append(r15)
            r14.append(r13)
            java.lang.String r13 = r14.toString()
            android.util.Log.v(r8, r13)
            long r13 = android.os.SystemClock.elapsedRealtime()
            r12.mTouchLogTime = r13
        L_0x01c4:
            r9 = r3
        L_0x01c5:
            r2 = r9
            goto L_0x01d0
        L_0x01c7:
            java.lang.String r13 = "onTouch | finger outside"
            android.util.Log.v(r8, r13)
            r12.onFingerUp()
        L_0x01cf:
            r2 = r3
        L_0x01d0:
            android.os.Trace.endSection()
        L_0x01d3:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.biometrics.UdfpsController.onTouch(android.view.View, android.view.MotionEvent, boolean):boolean");
    }

    private boolean shouldTryToDismissKeyguard() {
        return this.mView.getAnimationViewController() != null && (this.mView.getAnimationViewController() instanceof UdfpsKeyguardViewController) && this.mKeyguardStateController.canDismissLockScreen() && !this.mAttemptedToDismissKeyguard;
    }

    public UdfpsController(Context context, Execution execution, LayoutInflater layoutInflater, FingerprintManager fingerprintManager, WindowManager windowManager, StatusBarStateController statusBarStateController, DelayableExecutor delayableExecutor, StatusBar statusBar, StatusBarKeyguardViewManager statusBarKeyguardViewManager, DumpManager dumpManager, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardViewMediator keyguardViewMediator, FalsingManager falsingManager, PowerManager powerManager, AccessibilityManager accessibilityManager, LockscreenShadeTransitionController lockscreenShadeTransitionController, ScreenLifecycle screenLifecycle, Vibrator vibrator, UdfpsHapticsSimulator udfpsHapticsSimulator, Optional<UdfpsHbmProvider> optional, KeyguardStateController keyguardStateController, KeyguardBypassController keyguardBypassController, DisplayManager displayManager, Handler handler, MotoDisplayManager motoDisplayManager, ConfigurationController configurationController) {
        StatusBar statusBar2 = statusBar;
        C08641 r3 = new ScreenLifecycle.Observer() {
            public void onScreenTurnedOn() {
                boolean unused = UdfpsController.this.mScreenOn = true;
                if (UdfpsController.this.mAodInterruptRunnable != null) {
                    UdfpsController.this.mAodInterruptRunnable.run();
                    Runnable unused2 = UdfpsController.this.mAodInterruptRunnable = null;
                }
            }

            public void onScreenTurnedOff() {
                boolean unused = UdfpsController.this.mScreenOn = false;
            }
        };
        this.mScreenObserver = r3;
        C08652 r4 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ServerRequest serverRequest = UdfpsController.this.mServerRequest;
                if (serverRequest != null && serverRequest.mRequestReason != 4 && "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                    Log.d("UdfpsController", "ACTION_CLOSE_SYSTEM_DIALOGS received, mRequestReason: " + UdfpsController.this.mServerRequest.mRequestReason);
                    UdfpsController.this.mServerRequest.onUserCanceled();
                    UdfpsController udfpsController = UdfpsController.this;
                    udfpsController.mServerRequest = null;
                    udfpsController.updateOverlay();
                }
            }
        };
        this.mBroadcastReceiver = r4;
        this.mOnTouchListener = new UdfpsController$$ExternalSyntheticLambda1(this);
        this.mOnHoverListener = new UdfpsController$$ExternalSyntheticLambda0(this);
        this.mTouchExplorationStateChangeListener = new UdfpsController$$ExternalSyntheticLambda2(this);
        this.mContext = context;
        this.mExecution = execution;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mVibrator = vibrator;
        this.mInflater = layoutInflater;
        FingerprintManager fingerprintManager2 = (FingerprintManager) Preconditions.checkNotNull(fingerprintManager);
        this.mFingerprintManager = fingerprintManager2;
        this.mWindowManager = windowManager;
        this.mFgExecutor = delayableExecutor;
        this.mStatusBar = statusBar2;
        this.mStatusBarStateController = statusBarStateController;
        this.mKeyguardStateController = keyguardStateController;
        this.mKeyguardViewManager = statusBarKeyguardViewManager;
        this.mDumpManager = dumpManager;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mFalsingManager = falsingManager;
        this.mPowerManager = powerManager;
        this.mAccessibilityManager = accessibilityManager;
        this.mLockscreenShadeTransitionController = lockscreenShadeTransitionController;
        this.mHbmProvider = optional.orElse((Object) null);
        screenLifecycle.addObserver(r3);
        boolean z = true;
        this.mScreenOn = screenLifecycle.getScreenState() == 2;
        this.mOrientationListener = new BiometricOrientationEventListener(context, new UdfpsController$$ExternalSyntheticLambda8(this), displayManager, handler);
        this.mKeyguardBypassController = keyguardBypassController;
        this.mConfigurationController = configurationController;
        this.mMotoDisplayManager = motoDisplayManager;
        FingerprintSensorPropertiesInternal findFirstUdfps = findFirstUdfps();
        this.mSensorProps = findFirstUdfps;
        Preconditions.checkArgument(findFirstUdfps == null ? false : z);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(2946, getCoreLayoutParamFlags(), -3);
        this.mCoreLayoutParams = layoutParams;
        layoutParams.setTitle("UdfpsController");
        layoutParams.setFitInsetsTypes(0);
        layoutParams.gravity = 51;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.privateFlags = 536870912;
        layoutParams.privateFlags = 536870912 | 16;
        this.mMotoUdfpsMaskViewController = new MotoUdfpsMaskViewController(context);
        fingerprintManager2.setUdfpsOverlayController(new UdfpsOverlayController());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(r4, intentFilter);
        udfpsHapticsSimulator.setUdfpsController(this);
        this.mMotoDisplayManager.setUdfpsCOntroller(this);
        statusBar2.setUdfpsController(this);
        initUdfpsParams();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Unit lambda$new$3() {
        onOrientationChanged();
        return Unit.INSTANCE;
    }

    private FingerprintSensorPropertiesInternal findFirstUdfps() {
        for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : this.mFingerprintManager.getSensorPropertiesInternal()) {
            if (fingerprintSensorPropertiesInternal.isAnyUdfpsType()) {
                return fingerprintSensorPropertiesInternal;
            }
        }
        return null;
    }

    public void dozeTimeTick() {
        UdfpsView udfpsView = this.mView;
        if (udfpsView != null) {
            udfpsView.dozeTimeTick();
        }
    }

    public RectF getSensorLocation() {
        FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal = this.mSensorProps;
        int i = fingerprintSensorPropertiesInternal.sensorLocationX;
        int i2 = fingerprintSensorPropertiesInternal.sensorRadius;
        int i3 = fingerprintSensorPropertiesInternal.sensorLocationY;
        return new RectF((float) (i - i2), (float) (i3 - i2), (float) (i + i2), (float) (i3 + i2));
    }

    /* access modifiers changed from: private */
    public void updateOverlay() {
        this.mExecution.assertIsMainThread();
        if (this.mServerRequest != null) {
            startBgHandlerThread();
            showUdfpsOverlay(this.mServerRequest);
            this.mMotoUdfpsMaskViewController.addUdfpsMask();
            return;
        }
        hideUdfpsOverlay();
        stopBgHandlerThread();
        this.mMotoUdfpsMaskViewController.removeUdfpsMask();
    }

    private WindowManager.LayoutParams computeLayoutParams(UdfpsAnimationViewController udfpsAnimationViewController) {
        int i = 0;
        int paddingX = udfpsAnimationViewController != null ? udfpsAnimationViewController.getPaddingX() : 0;
        if (udfpsAnimationViewController != null) {
            i = udfpsAnimationViewController.getPaddingY();
        }
        this.mCoreLayoutParams.flags = getCoreLayoutParamFlags();
        if (udfpsAnimationViewController != null && udfpsAnimationViewController.listenForTouchesOutsideView()) {
            this.mCoreLayoutParams.flags |= 262144;
        }
        WindowManager.LayoutParams layoutParams = this.mCoreLayoutParams;
        FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal = this.mSensorProps;
        int i2 = fingerprintSensorPropertiesInternal.sensorLocationX;
        int i3 = fingerprintSensorPropertiesInternal.sensorRadius;
        layoutParams.x = (i2 - i3) - paddingX;
        layoutParams.y = (fingerprintSensorPropertiesInternal.sensorLocationY - i3) - i;
        layoutParams.height = (i3 * 2) + (paddingX * 2);
        layoutParams.width = (i3 * 2) + (i * 2);
        Point point = new Point();
        this.mContext.getDisplay().getRealSize(point);
        int rotation = this.mContext.getDisplay().getRotation();
        if (rotation != 1) {
            if (rotation == 3 && (!(udfpsAnimationViewController instanceof UdfpsKeyguardViewController) || !this.mKeyguardUpdateMonitor.isGoingToSleep())) {
                WindowManager.LayoutParams layoutParams2 = this.mCoreLayoutParams;
                int i4 = point.x;
                FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal2 = this.mSensorProps;
                int i5 = i4 - fingerprintSensorPropertiesInternal2.sensorLocationY;
                int i6 = fingerprintSensorPropertiesInternal2.sensorRadius;
                layoutParams2.x = (i5 - i6) - paddingX;
                layoutParams2.y = (fingerprintSensorPropertiesInternal2.sensorLocationX - i6) - i;
            }
        } else if (!(udfpsAnimationViewController instanceof UdfpsKeyguardViewController) || !this.mKeyguardUpdateMonitor.isGoingToSleep()) {
            WindowManager.LayoutParams layoutParams3 = this.mCoreLayoutParams;
            FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal3 = this.mSensorProps;
            int i7 = fingerprintSensorPropertiesInternal3.sensorLocationY;
            int i8 = fingerprintSensorPropertiesInternal3.sensorRadius;
            layoutParams3.x = (i7 - i8) - paddingX;
            layoutParams3.y = ((point.y - fingerprintSensorPropertiesInternal3.sensorLocationX) - i8) - i;
        }
        WindowManager.LayoutParams layoutParams4 = this.mCoreLayoutParams;
        layoutParams4.accessibilityTitle = " ";
        return layoutParams4;
    }

    public void updateCoreLayoutParams(int i, int i2) {
        UdfpsView udfpsView = this.mView;
        if (udfpsView != null && udfpsView.isAttachedToWindow()) {
            Point point = new Point();
            this.mContext.getDisplay().getRealSize(point);
            WindowManager.LayoutParams layoutParams = this.mCoreLayoutParams;
            FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal = this.mSensorProps;
            int i3 = fingerprintSensorPropertiesInternal.sensorLocationX;
            int i4 = fingerprintSensorPropertiesInternal.sensorRadius;
            layoutParams.x = (i3 - i4) - i;
            layoutParams.y = (fingerprintSensorPropertiesInternal.sensorLocationY - i4) - i2;
            int rotation = this.mContext.getDisplay().getRotation();
            if (rotation == 1) {
                WindowManager.LayoutParams layoutParams2 = this.mCoreLayoutParams;
                FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal2 = this.mSensorProps;
                int i5 = fingerprintSensorPropertiesInternal2.sensorLocationY;
                int i6 = fingerprintSensorPropertiesInternal2.sensorRadius;
                layoutParams2.x = (i5 - i6) - i;
                layoutParams2.y = ((point.y - fingerprintSensorPropertiesInternal2.sensorLocationX) - i6) - i2;
            } else if (rotation == 3) {
                WindowManager.LayoutParams layoutParams3 = this.mCoreLayoutParams;
                int i7 = point.x;
                FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal3 = this.mSensorProps;
                int i8 = i7 - fingerprintSensorPropertiesInternal3.sensorLocationY;
                int i9 = fingerprintSensorPropertiesInternal3.sensorRadius;
                layoutParams3.x = (i8 - i9) - i;
                layoutParams3.y = (fingerprintSensorPropertiesInternal3.sensorLocationX - i9) - i2;
            }
            this.mWindowManager.updateViewLayout(this.mView, this.mCoreLayoutParams);
        }
    }

    private void onOrientationChanged() {
        hideUdfpsOverlay();
        updateOverlay();
    }

    private void showUdfpsOverlay(ServerRequest serverRequest) {
        this.mExecution.assertIsMainThread();
        int i = serverRequest.mRequestReason;
        if (this.mView == null) {
            try {
                Log.v("UdfpsController", "showUdfpsOverlay | adding window reason=" + i);
                UdfpsView udfpsView = (UdfpsView) this.mInflater.inflate(R$layout.udfps_view, (ViewGroup) null, false);
                this.mView = udfpsView;
                this.mOnFingerDown = false;
                udfpsView.setSensorProperties(this.mSensorProps);
                this.mView.setHbmProvider(this.mHbmProvider);
                this.mView.setMotoUdfpsMaskViewController(this.mMotoUdfpsMaskViewController);
                MotoUdfpsAnimationViewController motoUdfpsAnimationViewController = new MotoUdfpsAnimationViewController((MotoUdfpsAnimationView) this.mInflater.inflate(R$layout.udfps_moto_animation_view, (ViewGroup) null, false));
                this.mMotoUdfpsAnimationViewController = motoUdfpsAnimationViewController;
                motoUdfpsAnimationViewController.addToWindow(this.mSensorProps);
                UdfpsAnimationViewController inflateUdfpsAnimation = inflateUdfpsAnimation(i);
                this.mAttemptedToDismissKeyguard = false;
                inflateUdfpsAnimation.init();
                this.mView.setAnimationViewController(inflateUdfpsAnimation);
                this.mOrientationListener.enable();
                if (i == 1 || i == 2 || i == 3) {
                    this.mView.setImportantForAccessibility(2);
                }
                this.mMotoUdfpsMaskViewController.addScreenAndWakefulObserver();
                this.mMotoUdfpsMaskViewController.setUdfpsViewShowState(true);
                this.mWindowManager.addView(this.mView, computeLayoutParams(inflateUdfpsAnimation));
                this.mAccessibilityManager.addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
                updateTouchListener();
            } catch (RuntimeException e) {
                Log.e("UdfpsController", "showUdfpsOverlay | failed to add window", e);
            }
        } else {
            Log.v("UdfpsController", "showUdfpsOverlay | the overlay is already showing");
        }
    }

    private UdfpsAnimationViewController inflateUdfpsAnimation(int i) {
        this.mReason = i;
        if (i == 1 || i == 2) {
            UdfpsEnrollView udfpsEnrollView = (UdfpsEnrollView) this.mInflater.inflate(R$layout.udfps_enroll_view, (ViewGroup) null);
            udfpsEnrollView.setMotoUdfpsAnimViewController(this.mMotoUdfpsAnimationViewController);
            this.mView.addView(udfpsEnrollView);
            return new UdfpsEnrollViewController(udfpsEnrollView, this.mServerRequest.mEnrollHelper, this.mStatusBarStateController, this.mStatusBar, this.mDumpManager);
        } else if (i == 3) {
            UdfpsBpView udfpsBpView = (UdfpsBpView) this.mInflater.inflate(R$layout.udfps_bp_view, (ViewGroup) null);
            this.mView.addView(udfpsBpView);
            return new UdfpsBpViewController(udfpsBpView, this.mStatusBarStateController, this.mStatusBar, this.mDumpManager);
        } else if (i == 4) {
            UdfpsKeyguardView udfpsKeyguardView = (UdfpsKeyguardView) this.mInflater.inflate(R$layout.udfps_keyguard_view, (ViewGroup) null);
            this.mView.addView(udfpsKeyguardView);
            return new UdfpsKeyguardViewController(udfpsKeyguardView, this.mStatusBarStateController, this.mStatusBar, this.mKeyguardViewManager, this.mKeyguardUpdateMonitor, this.mFgExecutor, this.mDumpManager, this.mKeyguardViewMediator, this.mLockscreenShadeTransitionController, this.mConfigurationController, this);
        } else if (i != 5) {
            Log.d("UdfpsController", "Animation for reason " + i + " not supported yet");
            return null;
        } else {
            UdfpsFpmOtherView udfpsFpmOtherView = (UdfpsFpmOtherView) this.mInflater.inflate(R$layout.udfps_fpm_other_view, (ViewGroup) null);
            this.mView.addView(udfpsFpmOtherView);
            return new UdfpsFpmOtherViewController(udfpsFpmOtherView, this.mStatusBarStateController, this.mStatusBar, this.mDumpManager);
        }
    }

    private void hideUdfpsOverlay() {
        this.mExecution.assertIsMainThread();
        if (this.mView != null) {
            Log.v("UdfpsController", "hideUdfpsOverlay | removing window");
            onFingerUp();
            this.mWindowManager.removeView(this.mView);
            this.mView.setOnTouchListener((View.OnTouchListener) null);
            this.mView.setOnHoverListener((View.OnHoverListener) null);
            this.mView.setAnimationViewController((UdfpsAnimationViewController) null);
            this.mAccessibilityManager.removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
            this.mView = null;
            this.mMotoUdfpsMaskViewController.removeScreenAndWakefulObserver();
            this.mMotoUdfpsMaskViewController.setUdfpsViewShowState(false);
            this.mReason = -1;
            this.mMotoUdfpsAnimationViewController.removeFromWindow();
        } else {
            Log.v("UdfpsController", "hideUdfpsOverlay | the overlay is already hidden");
        }
        this.mOrientationListener.disable();
    }

    /* access modifiers changed from: package-private */
    public void onAodInterrupt(int i, int i2, float f, float f2) {
        if (!this.mIsAodInterruptActive) {
            UdfpsController$$ExternalSyntheticLambda6 udfpsController$$ExternalSyntheticLambda6 = new UdfpsController$$ExternalSyntheticLambda6(this, i, i2, f2, f);
            this.mAodInterruptRunnable = udfpsController$$ExternalSyntheticLambda6;
            if (this.mScreenOn) {
                udfpsController$$ExternalSyntheticLambda6.run();
                this.mAodInterruptRunnable = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAodInterrupt$4(int i, int i2, float f, float f2) {
        this.mIsAodInterruptActive = true;
        this.mCancelAodTimeoutAction = this.mFgExecutor.executeDelayed(new UdfpsController$$ExternalSyntheticLambda3(this), 1000);
        onFingerDown(i, i2, f, f2);
    }

    /* access modifiers changed from: package-private */
    public void onCancelUdfps() {
        onFingerUp();
        if (this.mIsAodInterruptActive) {
            Runnable runnable = this.mCancelAodTimeoutAction;
            if (runnable != null) {
                runnable.run();
                this.mCancelAodTimeoutAction = null;
            }
            this.mIsAodInterruptActive = false;
        }
    }

    public boolean isFingerDown() {
        return this.mOnFingerDown;
    }

    public void onFingerDown(int i, int i2, float f, float f2) {
        this.mExecution.assertIsMainThread();
        UdfpsView udfpsView = this.mView;
        if (udfpsView == null) {
            Log.w("UdfpsController", "Null view in onFingerDown");
            return;
        }
        if ((udfpsView.getAnimationViewController() instanceof UdfpsKeyguardViewController) && !this.mStatusBarStateController.isDozing()) {
            this.mKeyguardBypassController.setUserHasDeviceEntryIntent(true);
        }
        if (!this.mOnFingerDown) {
            playStartHaptic();
            if (!this.mKeyguardUpdateMonitor.isFaceDetectionRunning() && this.mKeyguardUpdateMonitor.isFaceAuthEnabledForUser(KeyguardUpdateMonitor.getCurrentUser())) {
                this.mKeyguardUpdateMonitor.requestFaceAuth(false);
            }
        }
        this.mOnFingerDown = true;
        pointerPreDown();
        Trace.beginAsyncSection("UdfpsController.e2e.startIllumination", 0);
        PointerInfo pointerInfo = new PointerInfo(this.mSensorProps.sensorId, i, i2, f, f2);
        if (this.mStatusBarStateController.isDozing()) {
            this.mView.startIllumination(new UdfpsController$$ExternalSyntheticLambda5(this), new UdfpsController$$ExternalSyntheticLambda7(this, pointerInfo));
        } else {
            this.mView.startIllumination(new UdfpsController$$ExternalSyntheticLambda4(this));
            this.mPreUpIfNeeded = false;
            Log.i("UdfpsController", "Send message to call FingerprintManager#onPointerDown");
            this.mUdfpsBgHandler.sendMessageDelayed(this.mUdfpsBgHandler.obtainMessage(1, pointerInfo), (long) this.mPointerDownDelay);
        }
        Trace.endAsyncSection("UdfpsController.e2e.onPointerDown", 0);
        this.mMotoUdfpsAnimationViewController.startScaningAnimation();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFingerDown$5() {
        this.mFingerprintManager.onUiReady(this.mSensorProps.sensorId);
        Trace.endAsyncSection("UdfpsController.e2e.startIllumination", 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFingerDown$6(PointerInfo pointerInfo) {
        this.mPreUpIfNeeded = false;
        Log.i("UdfpsController", "Send message to call FingerprintManager#onPointerDown");
        this.mUdfpsBgHandler.sendMessageDelayed(this.mUdfpsBgHandler.obtainMessage(1, pointerInfo), (long) this.mAodPointerDownDelay);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFingerDown$7() {
        this.mFingerprintManager.onUiReady(this.mSensorProps.sensorId);
        Trace.endAsyncSection("UdfpsController.e2e.startIllumination", 0);
    }

    public void onFingerUp() {
        this.mExecution.assertIsMainThread();
        this.mActivePointerId = -1;
        this.mGoodCaptureReceived = false;
        if (this.mView == null) {
            Log.w("UdfpsController", "Null view in onFingerUp");
            return;
        }
        if (this.mOnFingerDown) {
            this.mFingerprintManager.onPointerUp(this.mSensorProps.sensorId);
        }
        this.mOnFingerDown = false;
        if (this.mView.isIlluminationRequested()) {
            this.mView.stopIllumination();
        }
        this.mMotoUdfpsAnimationViewController.stopAllStyleAnimations();
    }

    private void updateTouchListener() {
        if (this.mView != null) {
            if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
                this.mView.setOnHoverListener(this.mOnHoverListener);
                this.mView.setOnTouchListener((View.OnTouchListener) null);
                return;
            }
            this.mView.setOnHoverListener((View.OnHoverListener) null);
            this.mView.setOnTouchListener(this.mOnTouchListener);
        }
    }

    public boolean shouldPauseAuth() {
        UdfpsAnimationViewController animationViewController;
        UdfpsView udfpsView = this.mView;
        if (udfpsView == null || (animationViewController = udfpsView.getAnimationViewController()) == null) {
            return true;
        }
        return animationViewController.shouldPauseAuth();
    }

    public void setUdfpsHbmListener(IUdfpsHbmListener iUdfpsHbmListener) {
        MotoUdfpsMaskViewController motoUdfpsMaskViewController = this.mMotoUdfpsMaskViewController;
        if (motoUdfpsMaskViewController != null) {
            motoUdfpsMaskViewController.setUdfpsHbmListener(iUdfpsHbmListener);
        }
    }

    private void startBgHandlerThread() {
        if (this.mBgHandlerThread != null) {
            stopBgHandlerThread();
        }
        HandlerThread handlerThread = new HandlerThread("UdfpsBgThread");
        this.mBgHandlerThread = handlerThread;
        handlerThread.start();
        this.mUdfpsBgHandler = new Handler(this.mBgHandlerThread.getLooper()) {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Log.i("UdfpsController", "Calling FingerprintManager#onPointerDown");
                    PointerInfo pointerInfo = (PointerInfo) message.obj;
                    UdfpsController.this.mFingerprintManager.onPointerDown(pointerInfo.sensorId, pointerInfo.f76x, pointerInfo.f77y, pointerInfo.minor, pointerInfo.major);
                    UdfpsController.this.mUdfpsBgHandler.removeMessages(5);
                } else if (i != 2) {
                    if (i != 4) {
                        if (i == 5) {
                            Log.i("UdfpsController", "Pointer down failed. Call FingerprintManager#onPointerPreUp");
                        } else {
                            return;
                        }
                    }
                    Log.i("UdfpsController", "Calling FingerprintManager#onPointerPreUp");
                    UdfpsController.this.mFingerprintManager.onPointerPreUp(UdfpsController.this.mSensorProps.sensorId);
                } else {
                    Log.i("UdfpsController", "Calling FingerprintManager#onPointerPreDown");
                    UdfpsController.this.mFingerprintManager.onPointerPreDown(UdfpsController.this.mSensorProps.sensorId);
                }
            }
        };
    }

    private void stopBgHandlerThread() {
        HandlerThread handlerThread = this.mBgHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mBgHandlerThread = null;
        }
    }

    private void initUdfpsParams() {
        this.mPointerDownDelay = this.mContext.getResources().getInteger(R$integer.udfps_pointer_down_delay);
        this.mAodPointerDownDelay = this.mContext.getResources().getInteger(R$integer.udfps_aod_pointer_down_delay);
        if (UDFPS_DEBUG) {
            int i = SystemProperties.getInt("persist.udfps.pointer.down.delay", -1);
            int i2 = SystemProperties.getInt("persist.udfps.aod.pointer.down.delay", -1);
            if (i != -1) {
                this.mPointerDownDelay = i;
            }
            if (i2 != -1) {
                this.mAodPointerDownDelay = i2;
            }
        }
        Log.i("UdfpsController", "mPointerDownDelay = " + this.mPointerDownDelay + "  mAodPointerDownDelay = " + this.mAodPointerDownDelay);
    }

    class PointerInfo {
        float major;
        float minor;
        int sensorId;

        /* renamed from: x */
        int f76x;

        /* renamed from: y */
        int f77y;

        public PointerInfo(int i, int i2, int i3, float f, float f2) {
            this.sensorId = i;
            this.f76x = i2;
            this.f77y = i3;
            this.minor = f;
            this.major = f2;
        }
    }

    private void pointerPreDown() {
        this.mPreUpIfNeeded = true;
        if (this.mUdfpsBgHandler.hasMessages(4)) {
            this.mUdfpsBgHandler.removeMessages(4);
        }
        if (this.mUdfpsBgHandler.hasMessages(5)) {
            this.mUdfpsBgHandler.removeMessages(5);
        }
        Log.d("UdfpsController", "pointerPreDown: Send message to call pointer pre down.");
        this.mUdfpsBgHandler.sendEmptyMessage(2);
        this.mUdfpsBgHandler.sendEmptyMessageDelayed(5, 500);
    }

    private void pointerPreUp() {
        if (this.mUdfpsBgHandler.hasMessages(2)) {
            this.mUdfpsBgHandler.removeMessages(2);
        }
        if (this.mUdfpsBgHandler.hasMessages(5)) {
            this.mUdfpsBgHandler.removeMessages(5);
        }
        if (this.mPreUpIfNeeded) {
            Log.d("UdfpsController", "pointerPreUp: Send message to call pointer pre up.");
            this.mUdfpsBgHandler.sendEmptyMessage(4);
        }
    }
}
