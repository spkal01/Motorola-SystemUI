package com.motorola.systemui.biometrics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.WindowManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.biometrics.UdfpsView;
import com.motorola.android.provider.MotorolaSettings;

public class MotoUdfpsAnimationViewController {
    private static final int[] ALL_ANIM_STYLES = {17236079, 17236080, 17236078};
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private AnimatedVectorDrawable mAuthErrorDrawable;
    /* access modifiers changed from: private */
    public AnimatedVectorDrawable mAuthScaningDrawable;
    private AnimatedVectorDrawable mAuthSuccessDrawable;
    private UdfpsView.Callback mCallback = new UdfpsView.Callback() {
        public void onSuccess() {
            if (MotoUdfpsAnimationViewController.DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "Fingerprint unlock: onSuccess");
            }
            MotoUdfpsAnimationViewController.this.startSuccessAnimation();
        }

        public void onFailed() {
            if (MotoUdfpsAnimationViewController.DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "Fingerprint unlock: onFailed");
            }
            MotoUdfpsAnimationViewController.this.startErrorAnimation();
        }

        public void onAcquired(int i) {
            if (MotoUdfpsAnimationViewController.DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "Fingerprint unlock: onAcquired(acquireInfo=" + i + ")");
            }
            if (i != 6 && i != 0) {
                MotoUdfpsAnimationViewController.this.startErrorAnimation();
            }
        }
    };
    private Context mContext;
    private Animatable2.AnimationCallback mErrorAnimationCallback = new Animatable2.AnimationCallback() {
        public void onAnimationStart(Drawable drawable) {
            super.onAnimationStart(drawable);
        }

        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            MotoUdfpsAnimationViewController.this.mView.setVisibility(8);
        }
    };
    private int mMotoUdfpsAnimationViewSize;
    private Animatable2.AnimationCallback mScaningAnimationCallback = new Animatable2.AnimationCallback() {
        public void onAnimationStart(Drawable drawable) {
            super.onAnimationStart(drawable);
        }

        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            if (MotoUdfpsAnimationViewController.this.mView != null && MotoUdfpsAnimationViewController.this.mAuthScaningDrawable != null) {
                MotoUdfpsAnimationViewController.this.mView.post(MotoUdfpsAnimationViewController.this.mScaningLoopRunnable);
            }
        }
    };
    /* access modifiers changed from: private */
    public Runnable mScaningLoopRunnable = new MotoUdfpsAnimationViewController$$ExternalSyntheticLambda0(this);
    private int mStyle = 0;
    private Animatable2.AnimationCallback mSuccessAnimationCallback = new Animatable2.AnimationCallback() {
        public void onAnimationStart(Drawable drawable) {
            super.onAnimationStart(drawable);
        }

        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            MotoUdfpsAnimationViewController.this.mView.setVisibility(8);
        }
    };
    private KeyguardUpdateMonitor mUpdateMonitor;
    /* access modifiers changed from: private */
    public MotoUdfpsAnimationView mView;
    private WindowManager mWindowManager;

    public MotoUdfpsAnimationViewController(MotoUdfpsAnimationView motoUdfpsAnimationView) {
        this.mView = motoUdfpsAnimationView;
        Context context = motoUdfpsAnimationView.getContext();
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mMotoUdfpsAnimationViewSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_udfps_animation_view_size);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        getCurrentStyle();
        loadCurrentStyleAnimation();
    }

    private void getCurrentStyle() {
        this.mStyle = MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), "fod_animation_style", 0, -2);
    }

    private void loadCurrentStyleAnimation() {
        int i = this.mStyle;
        if (i >= 0) {
            int[] iArr = ALL_ANIM_STYLES;
            if (i < iArr.length) {
                updateStyleAnims(parseDrawableArray(iArr[i]));
                return;
            }
        }
        Log.e("MotoUdfpsAnimationViewController", "loadCurrentStyleAnimation: mStyle =" + this.mStyle + ". It is invalid.");
    }

    private SparseArray<Integer> parseDrawableArray(int i) {
        SparseArray<Integer> sparseArray;
        TypedArray obtainTypedArray = this.mContext.getResources().obtainTypedArray(i);
        int length = obtainTypedArray.length();
        if (length > 0) {
            sparseArray = new SparseArray<>(length);
            for (int i2 = 0; i2 < length; i2++) {
                sparseArray.put(i2, Integer.valueOf(obtainTypedArray.getResourceId(i2, 0)));
            }
        } else {
            sparseArray = null;
        }
        obtainTypedArray.recycle();
        return sparseArray;
    }

    private void updateStyleAnims(SparseArray<Integer> sparseArray) {
        if (sparseArray == null || sparseArray.size() != 3) {
            Log.e("MotoUdfpsAnimationViewController", "mStyle =" + this.mStyle + ". It is invalid.");
            return;
        }
        Drawable drawable = this.mContext.getDrawable(sparseArray.get(0).intValue());
        Drawable drawable2 = this.mContext.getDrawable(sparseArray.get(1).intValue());
        Drawable drawable3 = this.mContext.getDrawable(sparseArray.get(2).intValue());
        if (drawable != null && (drawable instanceof AnimatedVectorDrawable)) {
            this.mAuthScaningDrawable = (AnimatedVectorDrawable) drawable;
        }
        if (drawable2 != null && (drawable2 instanceof AnimatedVectorDrawable)) {
            this.mAuthSuccessDrawable = (AnimatedVectorDrawable) drawable2;
        }
        if (drawable3 != null && (drawable3 instanceof AnimatedVectorDrawable)) {
            this.mAuthErrorDrawable = (AnimatedVectorDrawable) drawable3;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthScaningDrawable;
        if (animatedVectorDrawable != null && !animatedVectorDrawable.isRunning()) {
            this.mAuthScaningDrawable.start();
        }
    }

    public void startScaningAnimation() {
        if (DEBUG) {
            Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: startScaningAnimation");
        }
        stopAllStyleAnimations();
        this.mView.setVisibility(0);
        this.mView.setImageDrawable(this.mAuthScaningDrawable);
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthScaningDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.registerAnimationCallback(this.mScaningAnimationCallback);
            this.mAuthScaningDrawable.start();
            return;
        }
        Log.w("MotoUdfpsAnimationViewController", "StartScaningAnimation: mAuthScaningDrawable is null");
    }

    private void stopScaningAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthScaningDrawable;
        if (animatedVectorDrawable == null) {
            Log.w("MotoUdfpsAnimationViewController", "StopScaningAnimation: mAuthScaningDrawable is null");
        } else if (animatedVectorDrawable.isRunning()) {
            this.mAuthScaningDrawable.unregisterAnimationCallback(this.mScaningAnimationCallback);
            this.mAuthScaningDrawable.stop();
            if (DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: stopScaningAnimation");
            }
        }
    }

    public void startSuccessAnimation() {
        if (DEBUG) {
            Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: startSuccessAnimation");
        }
        stopAllStyleAnimations();
        this.mView.setVisibility(0);
        this.mView.setImageDrawable(this.mAuthSuccessDrawable);
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthSuccessDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.registerAnimationCallback(this.mSuccessAnimationCallback);
            this.mAuthSuccessDrawable.start();
            return;
        }
        Log.w("MotoUdfpsAnimationViewController", "StartSuccessAnimation: mAuthSuccessDrawable is null");
    }

    private void stopSuccessAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthSuccessDrawable;
        if (animatedVectorDrawable == null) {
            Log.w("MotoUdfpsAnimationViewController", "StopSuccessAnimation: mAuthSuccessDrawable is null");
        } else if (animatedVectorDrawable.isRunning()) {
            this.mAuthSuccessDrawable.unregisterAnimationCallback(this.mSuccessAnimationCallback);
            this.mAuthSuccessDrawable.stop();
            if (DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: stopSuccessAnimation");
            }
        }
    }

    public void startErrorAnimation() {
        if (DEBUG) {
            Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: startErrorAnimation");
        }
        stopAllStyleAnimations();
        this.mView.setVisibility(0);
        this.mView.setImageDrawable(this.mAuthErrorDrawable);
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthErrorDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.registerAnimationCallback(this.mErrorAnimationCallback);
            this.mAuthErrorDrawable.start();
            return;
        }
        Log.w("MotoUdfpsAnimationViewController", "StartErrorAnimation: mAuthErrorDrawable is null");
    }

    private void stopErrorAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAuthErrorDrawable;
        if (animatedVectorDrawable == null) {
            Log.w("MotoUdfpsAnimationViewController", "StopErrorAnimation: mAuthErrorDrawable is null");
        } else if (animatedVectorDrawable.isRunning()) {
            this.mAuthErrorDrawable.unregisterAnimationCallback(this.mErrorAnimationCallback);
            this.mAuthErrorDrawable.stop();
            if (DEBUG) {
                Log.d("MotoUdfpsAnimationViewController", "MotoUdfpsAnimation: stopErrorAnimation");
            }
        }
    }

    public void stopAllStyleAnimations() {
        this.mView.setVisibility(8);
        stopScaningAnimation();
        stopSuccessAnimation();
        stopErrorAnimation();
    }

    public void addToWindow(FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal) {
        Log.d("MotoUdfpsAnimationViewController", "Add Moto Udfps Animation view.");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(2946);
        layoutParams.flags = 66360;
        int i = layoutParams.privateFlags | 16;
        layoutParams.privateFlags = i;
        layoutParams.privateFlags = i | 536870912;
        layoutParams.setTitle("Moto Udfps Animation");
        layoutParams.gravity = 51;
        layoutParams.windowAnimations = 0;
        layoutParams.format = -3;
        layoutParams.layoutInDisplayCutoutMode = 3;
        int i2 = fingerprintSensorPropertiesInternal.sensorLocationX;
        int i3 = this.mMotoUdfpsAnimationViewSize;
        layoutParams.x = i2 - (i3 / 2);
        layoutParams.y = fingerprintSensorPropertiesInternal.sensorLocationY - (i3 / 2);
        layoutParams.width = i3;
        layoutParams.height = i3;
        this.mWindowManager.addView(this.mView, layoutParams);
        this.mUpdateMonitor.setFingerprintStateCallback(this.mCallback);
    }

    public void removeFromWindow() {
        if (this.mView != null) {
            Log.d("MotoUdfpsAnimationViewController", "Remove Moto Udfps Animation view.");
            this.mWindowManager.removeViewImmediate(this.mView);
        } else {
            Log.d("MotoUdfpsAnimationViewController", "removeUdfpsMask: Mask view had been removed.");
        }
        this.mUpdateMonitor.setFingerprintStateCallback((UdfpsView.Callback) null);
    }
}
