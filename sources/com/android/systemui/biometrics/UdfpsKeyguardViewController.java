package com.android.systemui.biometrics;

import android.content.res.Configuration;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.StatusBarState;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class UdfpsKeyguardViewController extends UdfpsAnimationViewController<UdfpsKeyguardView> {
    private final StatusBarKeyguardViewManager.AlternateAuthInterceptor mAlternateAuthInterceptor = new StatusBarKeyguardViewManager.AlternateAuthInterceptor() {
        public boolean isAnimating() {
            return false;
        }

        public boolean showAlternateAuthBouncer() {
            boolean access$300 = UdfpsKeyguardViewController.this.showUdfpsBouncer(true);
            if (access$300) {
                UdfpsKeyguardViewController.this.updateAlpha();
            }
            return access$300;
        }

        public boolean hideAlternateAuthBouncer() {
            return UdfpsKeyguardViewController.this.showUdfpsBouncer(false);
        }

        public boolean isShowingAlternateAuthBouncer() {
            return UdfpsKeyguardViewController.this.mShowingUdfpsBouncer;
        }

        public void requestUdfps(boolean z, int i) {
            boolean unused = UdfpsKeyguardViewController.this.mUdfpsRequested = z;
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).requestUdfps(z, i);
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public void setQsExpanded(boolean z) {
            boolean unused = UdfpsKeyguardViewController.this.mQsExpanded = z;
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public boolean onTouch(MotionEvent motionEvent) {
            if (UdfpsKeyguardViewController.this.mTransitionToFullShadeProgress != 0.0f) {
                return false;
            }
            return UdfpsKeyguardViewController.this.mUdfpsController.onTouch(motionEvent);
        }

        public void setBouncerExpansionChanged(float f) {
            float unused = UdfpsKeyguardViewController.this.mInputBouncerHiddenAmount = f;
            UdfpsKeyguardViewController.this.updateAlpha();
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public void onBouncerVisibilityChanged() {
            UdfpsKeyguardViewController udfpsKeyguardViewController = UdfpsKeyguardViewController.this;
            boolean unused = udfpsKeyguardViewController.mIsBouncerVisible = udfpsKeyguardViewController.mKeyguardViewManager.isBouncerShowing();
            if (!UdfpsKeyguardViewController.this.mIsBouncerVisible) {
                float unused2 = UdfpsKeyguardViewController.this.mInputBouncerHiddenAmount = 1.0f;
            } else if (UdfpsKeyguardViewController.this.mKeyguardViewManager.isBouncerShowing()) {
                float unused3 = UdfpsKeyguardViewController.this.mInputBouncerHiddenAmount = 0.0f;
            }
            UdfpsKeyguardViewController.this.updateAlpha();
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println(UdfpsKeyguardViewController.this.getTag());
        }
    };
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onUiModeChanged() {
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).updateColor();
        }

        public void onThemeChanged() {
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).updateColor();
        }

        public void onOverlayChanged() {
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).updateColor();
        }

        public void onConfigChanged(Configuration configuration) {
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).updateColor();
            if (configuration.orientation != UdfpsKeyguardViewController.this.mOrientation) {
                Log.i("UdfpsKeyguardViewController", "Orientation changed. Update Udfps view layout params.");
                UdfpsKeyguardViewController.this.mUdfpsController.updateCoreLayoutParams(UdfpsKeyguardViewController.this.getPaddingX(), UdfpsKeyguardViewController.this.getPaddingY());
                int unused = UdfpsKeyguardViewController.this.mOrientation = configuration.orientation;
            }
        }
    };
    private final DelayableExecutor mExecutor;
    private boolean mFaceDetectRunning;
    /* access modifiers changed from: private */
    public boolean mGoingToSleep;
    /* access modifiers changed from: private */
    public float mInputBouncerHiddenAmount;
    /* access modifiers changed from: private */
    public boolean mIsBouncerVisible;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public final StatusBarKeyguardViewManager mKeyguardViewManager;
    private final KeyguardViewMediator mKeyguardViewMediator;
    /* access modifiers changed from: private */
    public float mLastDozeAmount;
    private final LockscreenShadeTransitionController mLockScreenShadeTransitionController;
    private MotoDisplayManager mMotoDisplayManager;
    /* access modifiers changed from: private */
    public int mOrientation;
    /* access modifiers changed from: private */
    public boolean mQsExpanded;
    /* access modifiers changed from: private */
    public boolean mShowingUdfpsBouncer;
    private final StatusBarStateController.StateListener mStateListener = new StatusBarStateController.StateListener() {
        public void onDozeAmountChanged(float f, float f2) {
            if (UdfpsKeyguardViewController.this.mLastDozeAmount < f) {
                boolean unused = UdfpsKeyguardViewController.this.showUdfpsBouncer(false);
            }
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).onDozeAmountChanged(f, f2);
            float unused2 = UdfpsKeyguardViewController.this.mLastDozeAmount = f;
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public void onStateChanged(int i) {
            int unused = UdfpsKeyguardViewController.this.mStatusBarState = i;
            ((UdfpsKeyguardView) UdfpsKeyguardViewController.this.mView).setStatusBarState(i);
            UdfpsKeyguardViewController.this.updatePauseAuth();
        }

        public void onDozingChanged(boolean z) {
            if (!z && UdfpsKeyguardViewController.this.mUdfpsHideByExternal) {
                Log.i("UdfpsKeyguardViewController", "Exit doze state, restore the hidden Udfps icon.");
                boolean unused = UdfpsKeyguardViewController.this.mUdfpsHideByExternal = false;
                UdfpsKeyguardViewController.this.updatePauseAuth();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mStatusBarState;
    /* access modifiers changed from: private */
    public float mTransitionToFullShadeProgress;
    private final MotoDisplayManager.UdfpsControlByExternal mUdfpsControlByExternal;
    /* access modifiers changed from: private */
    public final UdfpsController mUdfpsController;
    /* access modifiers changed from: private */
    public boolean mUdfpsHideByExternal;
    /* access modifiers changed from: private */
    public boolean mUdfpsRequested;
    private WakefulnessLifecycle.Observer mWakefulObserver = new WakefulnessLifecycle.Observer() {
        public void onStartedWakingUp() {
            boolean unused = UdfpsKeyguardViewController.this.mGoingToSleep = false;
            UdfpsKeyguardViewController.this.updateAlpha();
        }

        public void onStartedGoingToSleep() {
            boolean unused = UdfpsKeyguardViewController.this.mGoingToSleep = true;
            UdfpsKeyguardViewController.this.updateAlpha();
        }
    };
    private WakefulnessLifecycle mWakefulnessLifecycle = ((WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class));

    /* access modifiers changed from: package-private */
    public String getTag() {
        return "UdfpsKeyguardViewController";
    }

    public boolean listenForTouchesOutsideView() {
        return true;
    }

    protected UdfpsKeyguardViewController(UdfpsKeyguardView udfpsKeyguardView, StatusBarStateController statusBarStateController, StatusBar statusBar, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardUpdateMonitor keyguardUpdateMonitor, DelayableExecutor delayableExecutor, DumpManager dumpManager, KeyguardViewMediator keyguardViewMediator, LockscreenShadeTransitionController lockscreenShadeTransitionController, ConfigurationController configurationController, UdfpsController udfpsController) {
        super(udfpsKeyguardView, statusBarStateController, statusBar, dumpManager);
        C08745 r1 = new MotoDisplayManager.UdfpsControlByExternal() {
            public void showUdfps() {
                if (KeyguardConstants.DEBUG) {
                    Log.d("UdfpsKeyguardViewController", "--- AOD trigger showUdfps ---");
                }
                UdfpsKeyguardViewController.this.updateUdfpsIconStateByExternal(false);
            }

            public void hideUdfps() {
                if (KeyguardConstants.DEBUG) {
                    Log.d("UdfpsKeyguardViewController", "--- AOD trigger hideUdfps ---");
                }
                UdfpsKeyguardViewController.this.updateUdfpsIconStateByExternal(true);
            }
        };
        this.mUdfpsControlByExternal = r1;
        this.mKeyguardViewManager = statusBarKeyguardViewManager;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mExecutor = delayableExecutor;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mLockScreenShadeTransitionController = lockscreenShadeTransitionController;
        this.mConfigurationController = configurationController;
        this.mUdfpsController = udfpsController;
        MotoDisplayManager motoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
        this.mMotoDisplayManager = motoDisplayManager;
        motoDisplayManager.setUdfpsControlByExternal(r1);
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        float dozeAmount = this.mStatusBarStateController.getDozeAmount();
        this.mLastDozeAmount = dozeAmount;
        this.mStateListener.onDozeAmountChanged(dozeAmount, dozeAmount);
        this.mStatusBarStateController.addCallback(this.mStateListener);
        this.mUdfpsRequested = false;
        this.mStatusBarState = this.mStatusBarStateController.getState();
        this.mQsExpanded = this.mKeyguardViewManager.isQsExpanded();
        this.mInputBouncerHiddenAmount = 1.0f;
        this.mIsBouncerVisible = this.mKeyguardViewManager.bouncerIsOrWillBeShowing();
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        updateAlpha();
        updatePauseAuth();
        this.mKeyguardViewManager.setAlternateAuthInterceptor(this.mAlternateAuthInterceptor);
        this.mLockScreenShadeTransitionController.setUdfpsKeyguardViewController(this);
        this.mWakefulnessLifecycle.addObserver(this.mWakefulObserver);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        super.onViewDetached();
        this.mFaceDetectRunning = false;
        this.mStatusBarStateController.removeCallback(this.mStateListener);
        this.mKeyguardViewManager.removeAlternateAuthInterceptor(this.mAlternateAuthInterceptor);
        this.mKeyguardUpdateMonitor.requestFaceAuthOnOccludingApp(false);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
        if (this.mLockScreenShadeTransitionController.getUdfpsKeyguardViewController() == this) {
            this.mLockScreenShadeTransitionController.setUdfpsKeyguardViewController((UdfpsKeyguardViewController) null);
        }
        this.mWakefulnessLifecycle.removeObserver(this.mWakefulObserver);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mShowingUdfpsBouncer=" + this.mShowingUdfpsBouncer);
        printWriter.println("mFaceDetectRunning=" + this.mFaceDetectRunning);
        printWriter.println("mStatusBarState=" + StatusBarState.toShortString(this.mStatusBarState));
        printWriter.println("mQsExpanded=" + this.mQsExpanded);
        printWriter.println("mIsBouncerVisible=" + this.mIsBouncerVisible);
        printWriter.println("mInputBouncerHiddenAmount=" + this.mInputBouncerHiddenAmount);
        printWriter.println("mAlpha=" + ((UdfpsKeyguardView) this.mView).getAlpha());
        printWriter.println("mUdfpsRequested=" + this.mUdfpsRequested);
        printWriter.println("mView.mUdfpsRequested=" + ((UdfpsKeyguardView) this.mView).mUdfpsRequested);
    }

    /* access modifiers changed from: private */
    public boolean showUdfpsBouncer(boolean z) {
        if (this.mShowingUdfpsBouncer == z) {
            return false;
        }
        this.mShowingUdfpsBouncer = z;
        updatePauseAuth();
        if (this.mShowingUdfpsBouncer) {
            if (this.mStatusBarState == 2) {
                ((UdfpsKeyguardView) this.mView).animateInUdfpsBouncer((Runnable) null);
            }
            if (this.mKeyguardViewManager.isOccluded()) {
                this.mKeyguardUpdateMonitor.requestFaceAuthOnOccludingApp(true);
            }
            T t = this.mView;
            ((UdfpsKeyguardView) t).announceForAccessibility(((UdfpsKeyguardView) t).getContext().getString(R$string.accessibility_fingerprint_bouncer));
        } else {
            this.mKeyguardUpdateMonitor.requestFaceAuthOnOccludingApp(false);
        }
        return true;
    }

    public boolean shouldPauseAuth() {
        if (this.mUdfpsHideByExternal) {
            if (KeyguardConstants.DEBUG) {
                Log.d("UdfpsKeyguardViewController", "Udfps icon is hiden by AOD.");
            }
            return true;
        } else if (this.mShowingUdfpsBouncer) {
            return false;
        } else {
            if (this.mUdfpsRequested && !this.mNotificationShadeExpanded && (!this.mIsBouncerVisible || this.mInputBouncerHiddenAmount != 0.0f)) {
                return false;
            }
            if (this.mStatusBarState == 1 && !this.mQsExpanded && this.mInputBouncerHiddenAmount >= 0.5f && !this.mIsBouncerVisible) {
                return false;
            }
            return true;
        }
    }

    public void onTouchOutsideView() {
        maybeShowInputBouncer();
    }

    private void maybeShowInputBouncer() {
        if (this.mShowingUdfpsBouncer) {
            this.mKeyguardViewManager.showBouncer(true);
            this.mKeyguardViewManager.resetAlternateAuth(false);
        }
    }

    public void setTransitionToFullShadeProgress(float f) {
        this.mTransitionToFullShadeProgress = f;
        updateAlpha();
    }

    /* access modifiers changed from: private */
    public void updateAlpha() {
        int i;
        if (this.mShowingUdfpsBouncer) {
            i = 255;
        } else {
            i = (int) MathUtils.constrain(MathUtils.map(0.5f, 0.9f, 0.0f, 255.0f, this.mInputBouncerHiddenAmount), 0.0f, 255.0f);
        }
        int i2 = (int) (((float) i) * (1.0f - this.mTransitionToFullShadeProgress));
        if (this.mGoingToSleep) {
            i2 = 0;
        }
        ((UdfpsKeyguardView) this.mView).setUnpausedAlpha(i2);
    }

    /* access modifiers changed from: private */
    public void updateUdfpsIconStateByExternal(boolean z) {
        if (KeyguardConstants.DEBUG) {
            Log.d("UdfpsKeyguardViewController", "updateUdfpsIconStateByExternal: oldHide=" + this.mUdfpsHideByExternal + "  newHide=" + z);
        }
        if (this.mUdfpsHideByExternal != z) {
            this.mUdfpsHideByExternal = z;
            updatePauseAuth();
        }
    }
}
