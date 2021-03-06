package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.widget.FrameLayout;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.biometrics.UdfpsSurfaceView;
import com.android.systemui.doze.DozeReceiver;

public class UdfpsView extends FrameLayout implements DozeReceiver {
    private static final boolean DEBUG = (!Build.IS_USER);
    private UdfpsAnimationViewController mAnimationViewController;
    private String mDebugMessage;
    private final Paint mDebugTextPaint;
    private Runnable mFingerDownRunnable;
    private UdfpsSurfaceView mGhbmView;
    private UdfpsHbmProvider mHbmProvider;
    private final int mHbmType;
    private boolean mIlluminationRequested;
    private MotoUdfpsMaskViewController mMotoUdfpsMaskViewController;
    private final int mOnIlluminatedDelayMs;
    private FingerprintSensorPropertiesInternal mSensorProps;
    private final RectF mSensorRect = new RectF();
    private float mSensorTouchAreaCoefficient;

    public interface Callback {
        void onAcquired(int i);

        void onFailed();

        void onSuccess();
    }

    public UdfpsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initSensorTouchAreaCoefficient();
        Paint paint = new Paint();
        this.mDebugTextPaint = paint;
        paint.setAntiAlias(true);
        paint.setColor(-16776961);
        paint.setTextSize(32.0f);
        this.mOnIlluminatedDelayMs = this.mContext.getResources().getInteger(17694970);
        if (Build.IS_ENG || Build.IS_USERDEBUG) {
            this.mHbmType = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "com.android.systemui.biometrics.UdfpsSurfaceView.hbmType", 0, -2);
        } else {
            this.mHbmType = 0;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        return udfpsAnimationViewController == null || !udfpsAnimationViewController.shouldPauseAuth();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        if (this.mHbmType == 0) {
            this.mGhbmView = (UdfpsSurfaceView) findViewById(R$id.hbm_view);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSensorProperties(FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal) {
        this.mSensorProps = fingerprintSensorPropertiesInternal;
    }

    public void setHbmProvider(UdfpsHbmProvider udfpsHbmProvider) {
        this.mHbmProvider = udfpsHbmProvider;
    }

    public void dozeTimeTick() {
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        if (udfpsAnimationViewController != null) {
            udfpsAnimationViewController.dozeTimeTick();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        int i6 = 0;
        if (udfpsAnimationViewController == null) {
            i5 = 0;
        } else {
            i5 = udfpsAnimationViewController.getPaddingX();
        }
        UdfpsAnimationViewController udfpsAnimationViewController2 = this.mAnimationViewController;
        if (udfpsAnimationViewController2 != null) {
            i6 = udfpsAnimationViewController2.getPaddingY();
        }
        int i7 = this.mSensorProps.sensorRadius;
        this.mSensorRect.set((float) i5, (float) i6, (float) ((i7 * 2) + i5), (float) ((i7 * 2) + i6));
        UdfpsAnimationViewController udfpsAnimationViewController3 = this.mAnimationViewController;
        if (udfpsAnimationViewController3 != null) {
            udfpsAnimationViewController3.onSensorRectUpdated(new RectF(this.mSensorRect));
        }
    }

    /* access modifiers changed from: package-private */
    public void onTouchOutsideView() {
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        if (udfpsAnimationViewController != null) {
            udfpsAnimationViewController.onTouchOutsideView();
        }
    }

    /* access modifiers changed from: package-private */
    public void setAnimationViewController(UdfpsAnimationViewController udfpsAnimationViewController) {
        this.mAnimationViewController = udfpsAnimationViewController;
    }

    /* access modifiers changed from: package-private */
    public UdfpsAnimationViewController getAnimationViewController() {
        return this.mAnimationViewController;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v("UdfpsView", "onAttachedToWindow");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v("UdfpsView", "onDetachedFromWindow");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.mIlluminationRequested && !TextUtils.isEmpty(this.mDebugMessage)) {
            canvas.drawText(this.mDebugMessage, 0.0f, 160.0f, this.mDebugTextPaint);
        }
    }

    /* access modifiers changed from: package-private */
    public void setDebugMessage(String str) {
        this.mDebugMessage = str;
        postInvalidate();
    }

    /* access modifiers changed from: package-private */
    public boolean isWithinSensorArea(float f, float f2) {
        PointF pointF;
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        if (udfpsAnimationViewController == null) {
            pointF = new PointF(0.0f, 0.0f);
        } else {
            pointF = udfpsAnimationViewController.getTouchTranslation();
        }
        float centerX = this.mSensorRect.centerX() + pointF.x;
        float centerY = this.mSensorRect.centerY() + pointF.y;
        RectF rectF = this.mSensorRect;
        float f3 = (rectF.right - rectF.left) / 2.0f;
        float f4 = (rectF.bottom - rectF.top) / 2.0f;
        float f5 = this.mSensorTouchAreaCoefficient;
        return f > centerX - (f3 * f5) && f < centerX + (f3 * f5) && f2 > centerY - (f4 * f5) && f2 < centerY + (f4 * f5) && !this.mAnimationViewController.shouldPauseAuth();
    }

    /* access modifiers changed from: package-private */
    public boolean isIlluminationRequested() {
        return this.mIlluminationRequested;
    }

    public void startIllumination(Runnable runnable) {
        startIllumination(runnable, (Runnable) null);
    }

    public void startIllumination(Runnable runnable, Runnable runnable2) {
        this.mIlluminationRequested = true;
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        if (udfpsAnimationViewController != null) {
            udfpsAnimationViewController.onIlluminationStarting();
        }
        UdfpsSurfaceView udfpsSurfaceView = this.mGhbmView;
        if (udfpsSurfaceView != null) {
            this.mFingerDownRunnable = runnable2;
            udfpsSurfaceView.setGhbmIlluminationListener(new UdfpsView$$ExternalSyntheticLambda0(this));
            this.mGhbmView.setVisibility(0);
            this.mGhbmView.startGhbmIllumination(runnable);
            return;
        }
        doIlluminate((Surface) null, runnable);
    }

    private void doIlluminate(Surface surface, Runnable runnable) {
        if (this.mGhbmView != null && surface == null) {
            Log.e("UdfpsView", "doIlluminate | surface must be non-null for GHBM");
        }
        this.mHbmProvider.enableHbm(this.mHbmType, surface, new UdfpsView$$ExternalSyntheticLambda1(this, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$doIlluminate$0(Runnable runnable) {
        UdfpsSurfaceView udfpsSurfaceView = this.mGhbmView;
        if (udfpsSurfaceView != null) {
            udfpsSurfaceView.drawIlluminationDot(this.mSensorRect);
        }
        if (runnable != null) {
            postDelayed(runnable, (long) this.mOnIlluminatedDelayMs);
        } else {
            Log.w("UdfpsView", "doIlluminate | onIlluminatedRunnable is null");
        }
    }

    public void stopIllumination() {
        this.mIlluminationRequested = false;
        UdfpsAnimationViewController udfpsAnimationViewController = this.mAnimationViewController;
        if (udfpsAnimationViewController != null) {
            udfpsAnimationViewController.onIlluminationStopped();
        }
        UdfpsSurfaceView udfpsSurfaceView = this.mGhbmView;
        if (udfpsSurfaceView != null) {
            udfpsSurfaceView.setGhbmIlluminationListener((UdfpsSurfaceView.GhbmIlluminationListener) null);
            this.mGhbmView.setVisibility(4);
        }
        this.mFingerDownRunnable = null;
        MotoUdfpsMaskViewController motoUdfpsMaskViewController = this.mMotoUdfpsMaskViewController;
        if (motoUdfpsMaskViewController == null) {
            Log.e("UdfpsView", "stopIllumination | mMotoUdfpsMaskViewController is null.");
        } else if (motoUdfpsMaskViewController.isDozing()) {
            this.mMotoUdfpsMaskViewController.disableUdfpsHbm();
            return;
        }
        UdfpsHbmProvider udfpsHbmProvider = this.mHbmProvider;
        if (udfpsHbmProvider != null) {
            udfpsHbmProvider.disableHbm((Runnable) null);
        }
    }

    public void setMotoUdfpsMaskViewController(MotoUdfpsMaskViewController motoUdfpsMaskViewController) {
        this.mMotoUdfpsMaskViewController = motoUdfpsMaskViewController;
    }

    /* access modifiers changed from: private */
    public void doMotoIlluminate(Surface surface, Runnable runnable) {
        UdfpsSurfaceView udfpsSurfaceView = this.mGhbmView;
        if (udfpsSurfaceView != null) {
            udfpsSurfaceView.drawIlluminationDot(this.mSensorRect);
        }
        MotoUdfpsMaskViewController motoUdfpsMaskViewController = this.mMotoUdfpsMaskViewController;
        if (motoUdfpsMaskViewController != null) {
            motoUdfpsMaskViewController.enableUdfpsHbm(this.mFingerDownRunnable);
        } else {
            Log.e("UdfpsView", "doMotoIlluminate: mMotoUdfpsMaskViewController is null");
        }
        if (runnable != null) {
            postDelayed(runnable, (long) this.mOnIlluminatedDelayMs);
        } else {
            Log.w("UdfpsView", "doMotoIlluminate | onIlluminatedRunnable is null");
        }
    }

    private void initSensorTouchAreaCoefficient() {
        this.mSensorTouchAreaCoefficient = getResources().getFloat(R$dimen.udfps_sensor_touch_area_coefficient);
        if (DEBUG) {
            String str = SystemProperties.get("persist.udfps.sensor.touch.coefficient");
            if (!TextUtils.isEmpty(str)) {
                this.mSensorTouchAreaCoefficient = Float.parseFloat(str);
            }
            Log.i("UdfpsView", "init mSensorTouchAreaCoefficient: " + this.mSensorTouchAreaCoefficient);
        }
    }
}
