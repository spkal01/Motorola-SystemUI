package com.android.systemui.biometrics;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.R$id;
import com.motorola.systemui.biometrics.MotoUdfpsAnimationViewController;
import java.util.Objects;

public class UdfpsEnrollView extends UdfpsAnimationView {
    private boolean mEnableEnrollProgress = getResources().getBoolean(17891840);
    private final UdfpsEnrollDrawable mFingerprintDrawable = new UdfpsEnrollDrawable(this.mContext, this.mEnableEnrollProgress);
    private ImageView mFingerprintView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private MotoUdfpsAnimationViewController mMotoUdfpsAnimViewController;

    public /* bridge */ /* synthetic */ void onExpansionChanged(float f, boolean z) {
        super.onExpansionChanged(f, z);
    }

    public UdfpsEnrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        ImageView imageView = (ImageView) findViewById(R$id.udfps_enroll_animation_fp_view);
        this.mFingerprintView = imageView;
        imageView.setImageDrawable(this.mFingerprintDrawable);
    }

    public UdfpsDrawable getDrawable() {
        return this.mFingerprintDrawable;
    }

    /* access modifiers changed from: package-private */
    public void setEnrollHelper(UdfpsEnrollHelper udfpsEnrollHelper) {
        this.mFingerprintDrawable.setEnrollHelper(udfpsEnrollHelper);
    }

    /* access modifiers changed from: package-private */
    public void onEnrollmentProgress(int i, int i2) {
        if (this.mEnableEnrollProgress) {
            this.mHandler.post(new UdfpsEnrollView$$ExternalSyntheticLambda1(this, i, i2));
        }
        MotoUdfpsAnimationViewController motoUdfpsAnimationViewController = this.mMotoUdfpsAnimViewController;
        if (motoUdfpsAnimationViewController != null) {
            motoUdfpsAnimationViewController.stopAllStyleAnimations();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEnrollmentProgress$0(int i, int i2) {
        this.mFingerprintDrawable.onEnrollmentProgress(i, i2);
    }

    /* access modifiers changed from: package-private */
    public void onLastStepAcquired() {
        if (this.mEnableEnrollProgress) {
            Handler handler = this.mHandler;
            UdfpsEnrollDrawable udfpsEnrollDrawable = this.mFingerprintDrawable;
            Objects.requireNonNull(udfpsEnrollDrawable);
            handler.post(new UdfpsEnrollView$$ExternalSyntheticLambda0(udfpsEnrollDrawable));
        }
    }

    public void setMotoUdfpsAnimViewController(MotoUdfpsAnimationViewController motoUdfpsAnimationViewController) {
        this.mMotoUdfpsAnimViewController = motoUdfpsAnimationViewController;
    }
}
