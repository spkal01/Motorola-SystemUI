package com.android.systemui.biometrics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.widget.ImageView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.keyguard.KeyguardConstants;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.doze.util.BurnInHelperKt;

public class UdfpsKeyguardView extends UdfpsAnimationView {
    private int mAlpha;
    private LottieAnimationView mAodFp;
    private AnimatorSet mBackgroundInAnimator = new AnimatorSet();
    private DisplayManager.BacklightListener mBacklightListener = new UdfpsKeyguardView$$ExternalSyntheticLambda0(this);
    private ImageView mBgProtection;
    private float mBurnInOffsetX;
    private float mBurnInOffsetY;
    private float mBurnInProgress;
    private DisplayManager mDisplayManager;
    private float mFPIconAlphaCoefficient;
    private UdfpsDrawable mFingerprintDrawable;
    private float mInterpolatedDarkAmount;
    private LottieAnimationView mLockScreenFp;
    private final int mMaxBurnInOffsetX;
    private final int mMaxBurnInOffsetY;
    private int mStatusBarState;
    private int mTextColorPrimary;
    private int mUdfpsHbmNits;
    boolean mUdfpsRequested;

    /* access modifiers changed from: package-private */
    public void onIlluminationStarting() {
    }

    /* access modifiers changed from: package-private */
    public void onIlluminationStopped() {
    }

    public /* bridge */ /* synthetic */ void onExpansionChanged(float f, boolean z) {
        super.onExpansionChanged(f, z);
    }

    public UdfpsKeyguardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFingerprintDrawable = new UdfpsFpDrawable(context);
        this.mMaxBurnInOffsetX = context.getResources().getDimensionPixelSize(R$dimen.udfps_burn_in_offset_x);
        this.mMaxBurnInOffsetY = context.getResources().getDimensionPixelSize(R$dimen.udfps_burn_in_offset_y);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
        this.mUdfpsHbmNits = this.mContext.getResources().getInteger(R$integer.zz_moto_udfps_hbm_nits);
        updateFPIconAlphaCoefficient(getBrightnessFloat());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAodFp = (LottieAnimationView) findViewById(R$id.udfps_aod_fp);
        this.mLockScreenFp = (LottieAnimationView) findViewById(R$id.udfps_lockscreen_fp);
        this.mBgProtection = (ImageView) findViewById(R$id.udfps_keyguard_fp_bg);
        updateColor();
        this.mLockScreenFp.addValueCallback(new KeyPath("**"), LottieProperty.COLOR_FILTER, new UdfpsKeyguardView$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ ColorFilter lambda$onFinishInflate$0(LottieFrameInfo lottieFrameInfo) {
        return new PorterDuffColorFilter(this.mTextColorPrimary, PorterDuff.Mode.SRC_ATOP);
    }

    public UdfpsDrawable getDrawable() {
        return this.mFingerprintDrawable;
    }

    public boolean dozeTimeTick() {
        updateBurnInOffsets();
        return true;
    }

    private void updateBurnInOffsets() {
        this.mBurnInOffsetX = MathUtils.lerp(0.0f, (float) (BurnInHelperKt.getBurnInOffset(this.mMaxBurnInOffsetX * 2, true) - this.mMaxBurnInOffsetX), this.mInterpolatedDarkAmount);
        this.mBurnInOffsetY = MathUtils.lerp(0.0f, (float) (BurnInHelperKt.getBurnInOffset(this.mMaxBurnInOffsetY * 2, false) - this.mMaxBurnInOffsetY), this.mInterpolatedDarkAmount);
        this.mBurnInProgress = MathUtils.lerp(0.0f, BurnInHelperKt.getBurnInProgressOffset(), this.mInterpolatedDarkAmount);
        this.mAodFp.setTranslationX(this.mBurnInOffsetX);
        this.mAodFp.setTranslationY(this.mBurnInOffsetY);
        this.mAodFp.setProgress(this.mBurnInProgress);
        this.mAodFp.setAlpha(this.mInterpolatedDarkAmount * 255.0f);
        this.mLockScreenFp.setTranslationX(this.mBurnInOffsetX);
        this.mLockScreenFp.setTranslationY(this.mBurnInOffsetY);
        this.mLockScreenFp.setProgress(1.0f - this.mInterpolatedDarkAmount);
        this.mLockScreenFp.setAlpha((1.0f - this.mInterpolatedDarkAmount) * 255.0f);
    }

    /* access modifiers changed from: package-private */
    public void requestUdfps(boolean z, int i) {
        this.mUdfpsRequested = z;
    }

    /* access modifiers changed from: package-private */
    public void setStatusBarState(int i) {
        this.mStatusBarState = i;
    }

    /* access modifiers changed from: package-private */
    public void updateColor() {
        this.mTextColorPrimary = Utils.getColorAttrDefaultColor(this.mContext, 16842806);
        this.mBgProtection.setImageDrawable(getContext().getDrawable(R$drawable.fingerprint_bg));
        this.mLockScreenFp.invalidate();
    }

    /* access modifiers changed from: package-private */
    public void setUnpausedAlpha(int i) {
        this.mAlpha = i;
        updateAlpha();
    }

    /* access modifiers changed from: protected */
    public int updateAlpha() {
        int updateAlpha = super.updateAlpha();
        float f = ((float) updateAlpha) / 255.0f;
        this.mLockScreenFp.setAlpha(f);
        float f2 = this.mInterpolatedDarkAmount;
        if (f2 != 0.0f) {
            this.mBgProtection.setAlpha(1.0f - f2);
        } else {
            this.mBgProtection.setAlpha(f * this.mFPIconAlphaCoefficient);
        }
        return updateAlpha;
    }

    /* access modifiers changed from: package-private */
    public int calculateAlpha() {
        if (this.mPauseAuth) {
            return 0;
        }
        return this.mAlpha;
    }

    /* access modifiers changed from: package-private */
    public void onDozeAmountChanged(float f, float f2) {
        this.mInterpolatedDarkAmount = f2;
        updateAlpha();
        updateBurnInOffsets();
    }

    /* access modifiers changed from: package-private */
    public void animateInUdfpsBouncer(final Runnable runnable) {
        if (!this.mBackgroundInAnimator.isRunning()) {
            AnimatorSet animatorSet = new AnimatorSet();
            this.mBackgroundInAnimator = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mBgProtection, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.mBgProtection, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.mBgProtection, View.SCALE_Y, new float[]{0.0f, 1.0f})});
            this.mBackgroundInAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mBackgroundInAnimator.setDuration(500);
            this.mBackgroundInAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            this.mBackgroundInAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerBacklightChangeListener();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterBacklightChangeListener();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(float f) {
        updateFPIconAlphaCoefficient(f);
        updateAlpha();
    }

    private void updateFPIconAlphaCoefficient(float f) {
        this.mFPIconAlphaCoefficient = 1.0f - calculateBgProtectionAlpha(f);
        if (KeyguardConstants.DEBUG) {
            Log.d("UdfpsKeyguardView", "updateFPIconAlphaCoefficient: mFPIconAlphaCoefficient = " + this.mFPIconAlphaCoefficient + " brightness=" + f);
        }
    }

    private float getBrightnessFloat() {
        return this.mContext.getDisplay().getBrightnessInfo().brightness;
    }

    private float calculateBgProtectionAlpha(float f) {
        float defaultDisplayNits = this.mDisplayManager.getDefaultDisplayNits(f);
        float convertNits2Alpha = Utils.convertNits2Alpha(BrightnessSynchronizer.brightnessFloatToInt(f), defaultDisplayNits, (float) this.mUdfpsHbmNits, false, false);
        if (KeyguardConstants.DEBUG) {
            Log.d("UdfpsKeyguardView", "calculateBgProtectionAlpha: brightness=" + f + " nits=" + defaultDisplayNits + " mUdfpsHbmNits=" + this.mUdfpsHbmNits + " alpha=" + convertNits2Alpha);
        }
        return convertNits2Alpha;
    }

    private void registerBacklightChangeListener() {
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager != null) {
            displayManager.registerBacklightChangeListener(this.mBacklightListener);
        } else {
            Log.e("UdfpsKeyguardView", "registerBacklightChange: mDisplayManager == null");
        }
    }

    private void unRegisterBacklightChangeListener() {
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager != null) {
            displayManager.unRegisterBacklightChangeListener(this.mBacklightListener);
        } else {
            Log.e("UdfpsKeyguardView", "unRegisterBacklightChange: mDisplayManager == null");
        }
    }
}
