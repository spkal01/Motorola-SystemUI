package com.android.systemui.biometrics;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.IUdfpsHbmListener;
import android.hardware.fingerprint.IUdfpsOverlayControllerCallback;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R$integer;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class MotoUdfpsMaskViewController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    DisplayManager.BacklightListener mBacklightListener = new MotoUdfpsMaskViewController$$ExternalSyntheticLambda0(this);
    final ContentObserver mColorInversionObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            MotoUdfpsMaskViewController.this.updateBackgroundColor();
        }
    };
    private Context mContext;
    private DisplayManager mDisplayManager;
    /* access modifiers changed from: private */
    public Runnable mFingerDownRunnable;
    /* access modifiers changed from: private */
    public C0861H mHandler = new C0861H(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public boolean mHbmEnabled;
    /* access modifiers changed from: private */
    public int mInteractive = 3;
    /* access modifiers changed from: private */
    public KeyguardStateController mKeyguardStateController;
    private float mMaskAlpha;
    private View mMaskingView;
    private int mReason = -1;
    private ScreenLifecycle mScreenLifecycle = ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class));
    private ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurningOn() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onScreenTurningOn");
            }
            int unused = MotoUdfpsMaskViewController.this.mScreenState = 1;
        }

        public void onScreenTurnedOn() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onScreenTurnedOn");
            }
            int unused = MotoUdfpsMaskViewController.this.mScreenState = 2;
            MotoUdfpsMaskViewController.this.updateMaskViewAlpha();
        }

        public void onScreenTurningOff() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onScreenTurningOff");
            }
            int unused = MotoUdfpsMaskViewController.this.mScreenState = 3;
        }

        public void onScreenTurnedOff() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onScreenTurnedOff");
            }
            int unused = MotoUdfpsMaskViewController.this.mScreenState = 0;
            MotoUdfpsMaskViewController.this.updateMaskViewAlpha();
        }
    };
    /* access modifiers changed from: private */
    public int mScreenState = -1;
    private StatusBarStateController mStatusBarStateController;
    private IUdfpsHbmListener mSystemUdfpsHbmListener;
    private final IUdfpsHbmListener mUdfpsHbmListener = new IUdfpsHbmListener.Stub() {
        public void onHbmEnabled(int i, int i2) {
            Log.d("MotoUdfpsMaskView", "onHbmEnabled hbmType=" + i + " displayId=" + i2);
            boolean unused = MotoUdfpsMaskViewController.this.mHbmEnabled = true;
            if (MotoUdfpsMaskViewController.this.mFingerDownRunnable != null) {
                MotoUdfpsMaskViewController.this.mFingerDownRunnable.run();
                Runnable unused2 = MotoUdfpsMaskViewController.this.mFingerDownRunnable = null;
            }
            MotoUdfpsMaskViewController.this.mHandler.post(new MotoUdfpsMaskViewController$1$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onHbmEnabled$0() {
            MotoUdfpsMaskViewController.this.updateMaskViewAlpha();
        }

        public void onHbmDisabled(int i, int i2) {
            Log.d("MotoUdfpsMaskView", "onHbmDisabled hbmType=" + i + " displayId=" + i2);
            MotoUdfpsMaskViewController.this.onUdfpsHbmDisabled();
            boolean unused = MotoUdfpsMaskViewController.this.mHbmEnabled = false;
            MotoUdfpsMaskViewController.this.mHandler.post(new MotoUdfpsMaskViewController$1$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onHbmDisabled$1() {
            MotoUdfpsMaskViewController.this.updateMaskViewAlpha();
        }
    };
    private int mUdfpsHbmNits;
    private IUdfpsOverlayControllerCallback mUdfpsOverlayControllerCallback;
    private boolean mUdfpsViewShow = false;
    /* access modifiers changed from: private */
    public KeyguardUpdateMonitor mUpdateMonitor;
    private WakefulnessLifecycle.Observer mWakefulObserver = new WakefulnessLifecycle.Observer() {
        public void onStartedWakingUp() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onStartedWakingUp");
            }
            int unused = MotoUdfpsMaskViewController.this.mInteractive = 0;
            boolean isTrusted = MotoUdfpsMaskViewController.this.mKeyguardStateController.isTrusted();
            boolean isUnlocked = MotoUdfpsMaskViewController.this.mKeyguardStateController.isUnlocked();
            boolean isKeyguardFadingAway = MotoUdfpsMaskViewController.this.mKeyguardStateController.isKeyguardFadingAway();
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onStartedWakingUp: isTrusted=" + isTrusted + " isUnlocked=" + isUnlocked + " isKeyguardFadingAway=" + isKeyguardFadingAway);
            }
            if (MotoUdfpsMaskViewController.this.mUpdateMonitor.isUdfpsEnrolled() && !isTrusted && !isUnlocked && !isKeyguardFadingAway) {
                MotoUdfpsMaskViewController.this.enableUdfpsHbm((Runnable) null, 150);
            }
        }

        public void onFinishedWakingUp() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onFinishedWakingUp");
            }
            int unused = MotoUdfpsMaskViewController.this.mInteractive = 1;
        }

        public void onStartedGoingToSleep() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onStartedGoingToSleep");
            }
            int unused = MotoUdfpsMaskViewController.this.mInteractive = 2;
        }

        public void onFinishedGoingToSleep() {
            if (MotoUdfpsMaskViewController.DEBUG) {
                Log.d("MotoUdfpsMaskView", "onFinishedGoingToSleep");
            }
            int unused = MotoUdfpsMaskViewController.this.mInteractive = 3;
            MotoUdfpsMaskViewController.this.disableUdfpsHbm();
        }
    };
    private WakefulnessLifecycle mWakefulnessLifecycle = ((WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class));
    private WindowManager mWindowManager;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(float f) {
        this.mMaskAlpha = calculateAlpha(f);
        if (this.mHbmEnabled) {
            this.mHandler.obtainMessage(3).sendToTarget();
        }
    }

    public MotoUdfpsMaskViewController(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
        this.mKeyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mUdfpsHbmNits = this.mContext.getResources().getInteger(R$integer.zz_moto_udfps_hbm_nits);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
    }

    public void addScreenAndWakefulObserver() {
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        this.mWakefulnessLifecycle.addObserver(this.mWakefulObserver);
    }

    public void removeScreenAndWakefulObserver() {
        this.mScreenLifecycle.removeObserver(this.mScreenObserver);
        this.mWakefulnessLifecycle.removeObserver(this.mWakefulObserver);
    }

    public void setUdfpsOverlayControllerCallback(IUdfpsOverlayControllerCallback iUdfpsOverlayControllerCallback) {
        this.mUdfpsOverlayControllerCallback = iUdfpsOverlayControllerCallback;
    }

    public void setUdfpsHbmListener(IUdfpsHbmListener iUdfpsHbmListener) {
        this.mSystemUdfpsHbmListener = iUdfpsHbmListener;
    }

    private void onUdfpsHbmEnabled() {
        try {
            Log.d("MotoUdfpsMaskView", "onUdfpsHbmEnabled.");
            if (this.mSystemUdfpsHbmListener != null && isDozing()) {
                this.mSystemUdfpsHbmListener.onHbmEnabled(0, 0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void onUdfpsHbmDisabled() {
        try {
            Log.d("MotoUdfpsMaskView", "onUdfpsHbmDisabled.");
            IUdfpsHbmListener iUdfpsHbmListener = this.mSystemUdfpsHbmListener;
            if (iUdfpsHbmListener != null) {
                iUdfpsHbmListener.onHbmDisabled(0, 0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUdfpsReason(int i) {
        this.mReason = i;
    }

    public void addUdfpsMask() {
        if (this.mMaskingView != null) {
            Log.i("MotoUdfpsMaskView", "The udfps mask had been added.");
            updateBackgroundColor();
            return;
        }
        if (DEBUG) {
            Log.d("MotoUdfpsMaskView", "Add Moto Udfps Mask");
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(2945);
        layoutParams.flags = 66360;
        int i = layoutParams.privateFlags | 16;
        layoutParams.privateFlags = i;
        int i2 = i | 536870912;
        layoutParams.privateFlags = i2;
        layoutParams.privateFlags = i2 | 2097152;
        layoutParams.setTitle("UDFPS_MASK");
        layoutParams.windowAnimations = 0;
        layoutParams.format = -3;
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.x = 0;
        layoutParams.y = 0;
        this.mMaskingView = new View(this.mContext);
        updateBackgroundColor();
        this.mMaskAlpha = calculateAlpha();
        updateMaskViewAlpha();
        this.mWindowManager.addView(this.mMaskingView, layoutParams);
        if (this.mWakefulnessLifecycle.getWakefulness() == 2) {
            enableUdfpsHbm((Runnable) null);
        }
        registerBacklightChangeListener();
        registerColorInversionSetting();
    }

    /* access modifiers changed from: private */
    public void updateMaskViewAlpha() {
        updateMaskViewAlpha(false);
    }

    private void updateMaskViewAlpha(boolean z) {
        float f;
        if (this.mMaskingView != null) {
            boolean z2 = DEBUG;
            if (z2) {
                Log.d("MotoUdfpsMaskView", "updateMaskViewAlpha: mHbmEnabled=" + this.mHbmEnabled + "  force=" + z + "  isScreenTurningOrTurnedOff=" + isScreenTurningOrTurnedOff() + "  isScreenTurningOrTurnedOn=" + isScreenTurningOrTurnedOn() + "  isPulsing=" + this.mStatusBarStateController.isPulsing());
            }
            if (this.mHbmEnabled || z || isScreenTurningOrTurnedOff() || (this.mHbmEnabled && isScreenTurningOrTurnedOn() && !this.mStatusBarStateController.isPulsing())) {
                f = this.mMaskAlpha;
            } else {
                f = 0.0f;
            }
            if (z2) {
                Log.d("MotoUdfpsMaskView", "updateMaskViewAlpha: set alpha to " + f);
            }
            this.mMaskingView.setAlpha(f);
        }
    }

    public void removeUdfpsMask() {
        if (this.mMaskingView != null) {
            Log.d("MotoUdfpsMaskView", "remove UdfpsMask");
            disableUdfpsHbm();
            this.mWindowManager.removeViewImmediate(this.mMaskingView);
            this.mMaskingView = null;
            unRegisterBacklightChangeListener();
            unRegisterColorInversionSetting();
            return;
        }
        Log.d("MotoUdfpsMaskView", "removeUdfpsMask: Mask view had been removed.");
    }

    private void registerBacklightChangeListener() {
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager != null) {
            displayManager.registerBacklightChangeListener(this.mBacklightListener);
        } else {
            Log.e("MotoUdfpsMaskView", "registerBacklightChange: mDisplayManager == null");
        }
    }

    private void unRegisterBacklightChangeListener() {
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager != null) {
            displayManager.unRegisterBacklightChangeListener(this.mBacklightListener);
        } else {
            Log.e("MotoUdfpsMaskView", "unRegisterBacklightChange: mDisplayManager == null");
        }
    }

    private float getBrightnessFloat() {
        return this.mContext.getDisplay().getBrightnessInfo().brightnessTarget;
    }

    private float calculateAlpha() {
        return calculateAlpha(getBrightnessFloat());
    }

    private float calculateAlpha(float f) {
        if (f == -1.0f) {
            if (DEBUG) {
                Log.d("MotoUdfpsMaskView", "calculateAlpha: brightness == -1.0, return.");
            }
            return this.mMaskAlpha;
        }
        float defaultDisplayNits = this.mDisplayManager.getDefaultDisplayNits(f);
        float convertNits2Alpha = Utils.convertNits2Alpha(BrightnessSynchronizer.brightnessFloatToInt(f), defaultDisplayNits, (float) this.mUdfpsHbmNits, false, false);
        if (DEBUG) {
            Log.d("MotoUdfpsMaskView", "calculateAlpha: brightness=" + f + " nits=" + defaultDisplayNits + " mUdfpsHbmNits=" + this.mUdfpsHbmNits + " alpha=" + convertNits2Alpha);
        }
        return convertNits2Alpha;
    }

    public void enableUdfpsHbm(Runnable runnable) {
        if (this.mReason == 4) {
            enableUdfpsHbm(runnable, 20);
        } else {
            enableUdfpsHbm(runnable, 85);
        }
    }

    public void enableUdfpsHbm(Runnable runnable, int i) {
        boolean isUnlocked = this.mKeyguardStateController.isUnlocked();
        boolean isKeyguardGoingAway = this.mKeyguardStateController.isKeyguardGoingAway();
        boolean isPulsing = this.mStatusBarStateController.isPulsing();
        this.mFingerDownRunnable = runnable;
        if (!this.mUdfpsViewShow) {
            updateMaskViewAlpha();
            Log.i("MotoUdfpsMaskView", "enableUdfpsHbm: Udfps view is not show, don't enable HBM.");
        } else if (this.mReason != 4 || isPulsing || (!isKeyguardGoingAway && !isUnlocked)) {
            onUdfpsHbmEnabled();
            this.mHbmEnabled = true;
            updateMaskViewAlpha(true);
            this.mHandler.sendEmptyMessageDelayed(1, (long) i);
        } else {
            Log.d("MotoUdfpsMaskView", "enableUdfpsHbm: The keyguard is going away, don't enable HBM.");
        }
    }

    public void disableUdfpsHbm() {
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
        if (this.mUdfpsOverlayControllerCallback != null) {
            try {
                if (DEBUG) {
                    Log.d("MotoUdfpsMaskView", "disableUdfpsHbm");
                }
                this.mUdfpsOverlayControllerCallback.disableUdfpsHbm(0, this.mUdfpsHbmListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MotoUdfpsMaskView", "disableUdfpsHbm: mUdfpsOverlayControllerCallback is null.");
        }
        this.mFingerDownRunnable = null;
    }

    public void setUdfpsViewShowState(boolean z) {
        this.mUdfpsViewShow = z;
        if (DEBUG) {
            Log.i("MotoUdfpsMaskView", "setUdfpsViewShowState: isGoingToSleep: " + this.mUpdateMonitor.isGoingToSleep());
        }
        if (!z || this.mUpdateMonitor.isGoingToSleep()) {
            disableUdfpsHbm();
        } else {
            enableUdfpsHbm((Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public void handleEnableUdfpsHbmInternal() {
        boolean z = DEBUG;
        if (z) {
            Log.d("MotoUdfpsMaskView", "handleEnableUdfpsHbmInternal");
        }
        if (this.mUdfpsOverlayControllerCallback != null) {
            if (z) {
                try {
                    Log.d("MotoUdfpsMaskView", "enableUdfpsHbm");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.mUdfpsOverlayControllerCallback.enableUdfpsHbm(0, this.mUdfpsHbmListener);
            return;
        }
        Log.e("MotoUdfpsMaskView", "enableUdfpsHbm: mUdfpsOverlayControllerCallback is null.");
    }

    private boolean isScreenTurningOrTurnedOff() {
        int i = this.mScreenState;
        return i == 3 || i == 0;
    }

    private boolean isScreenTurningOrTurnedOn() {
        int i = this.mScreenState;
        return i == 1 || i == 2;
    }

    /* access modifiers changed from: private */
    public void handleDisplayBrightnessChange() {
        if (DEBUG) {
            Log.d("MotoUdfpsMaskView", "handleDisplayBrightnessChange");
        }
        updateMaskViewAlpha();
    }

    public boolean isDozing() {
        return this.mStatusBarStateController.isDozing();
    }

    /* renamed from: com.android.systemui.biometrics.MotoUdfpsMaskViewController$H */
    private class C0861H extends Handler {
        public C0861H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MotoUdfpsMaskViewController.this.handleEnableUdfpsHbmInternal();
            } else if (i == 3) {
                MotoUdfpsMaskViewController.this.handleDisplayBrightnessChange();
            }
        }
    }

    private void registerColorInversionSetting() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), false, this.mColorInversionObserver, -1);
    }

    private void unRegisterColorInversionSetting() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mColorInversionObserver);
    }

    private boolean isColorInversionEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, -2) == 1;
    }

    /* access modifiers changed from: private */
    public void updateBackgroundColor() {
        if (isColorInversionEnabled()) {
            Log.d("MotoUdfpsMaskView", "Color inversion is enabled, set udfps mask to white.");
            this.mMaskingView.setBackgroundColor(-1);
            return;
        }
        Log.d("MotoUdfpsMaskView", "Color inversion is disabled, set udfps mask to black.");
        this.mMaskingView.setBackgroundColor(-16777216);
    }
}
