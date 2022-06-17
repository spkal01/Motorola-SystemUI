package com.android.systemui.biometrics;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.android.systemui.R$dimen;

public class UdfpsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    boolean mAwaitingSurfaceToStartIllumination;
    final ContentObserver mColorInversionObserver = new ContentObserver(getHandler()) {
        public void onChange(boolean z) {
            UdfpsSurfaceView.this.updatePaintColor();
        }
    };
    private GhbmIlluminationListener mGhbmIlluminationListener;
    boolean mHasValidSurface;
    private final SurfaceHolder mHolder;
    private Runnable mOnIlluminatedRunnable;
    private final Paint mSensorPaint;
    private int mSurfaceOvalSize;

    interface GhbmIlluminationListener {
        void enableGhbm(Surface surface, Runnable runnable);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    public UdfpsSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        this.mHolder = holder;
        holder.addCallback(this);
        holder.setFormat(1);
        Paint paint = new Paint(0);
        this.mSensorPaint = paint;
        paint.setAntiAlias(true);
        updatePaintColor();
        paint.setStyle(Paint.Style.FILL);
        this.mSurfaceOvalSize = context.getResources().getDimensionPixelOffset(R$dimen.zz_moto_udfps_surface_oval_size);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mHasValidSurface = true;
        if (this.mAwaitingSurfaceToStartIllumination) {
            doIlluminate(this.mOnIlluminatedRunnable);
            this.mOnIlluminatedRunnable = null;
            this.mAwaitingSurfaceToStartIllumination = false;
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mHasValidSurface = false;
    }

    /* access modifiers changed from: package-private */
    public void setGhbmIlluminationListener(GhbmIlluminationListener ghbmIlluminationListener) {
        this.mGhbmIlluminationListener = ghbmIlluminationListener;
    }

    /* access modifiers changed from: package-private */
    public void startGhbmIllumination(Runnable runnable) {
        if (this.mGhbmIlluminationListener == null) {
            Log.e("UdfpsSurfaceView", "startIllumination | mGhbmIlluminationListener is null");
        } else if (this.mHasValidSurface) {
            doIlluminate(runnable);
        } else {
            this.mAwaitingSurfaceToStartIllumination = true;
            this.mOnIlluminatedRunnable = runnable;
        }
    }

    private void doIlluminate(Runnable runnable) {
        GhbmIlluminationListener ghbmIlluminationListener = this.mGhbmIlluminationListener;
        if (ghbmIlluminationListener == null) {
            Log.e("UdfpsSurfaceView", "doIlluminate | mGhbmIlluminationListener is null");
        } else {
            ghbmIlluminationListener.enableGhbm(this.mHolder.getSurface(), runnable);
        }
    }

    /* access modifiers changed from: package-private */
    public void drawIlluminationDot(RectF rectF) {
        if (!this.mHasValidSurface) {
            Log.e("UdfpsSurfaceView", "drawIlluminationDot | the surface is destroyed or was never created.");
            return;
        }
        Canvas canvas = null;
        try {
            canvas = this.mHolder.lockCanvas();
            if (((float) this.mSurfaceOvalSize) > rectF.width()) {
                float width = (((float) this.mSurfaceOvalSize) - rectF.width()) / 2.0f;
                canvas.drawOval(rectF.left - width, rectF.top - width, rectF.right + width, rectF.bottom + width, this.mSensorPaint);
            } else {
                canvas.drawOval(rectF, this.mSensorPaint);
            }
        } finally {
            if (canvas != null) {
                this.mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerColorInversionSetting();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterColorInversionSetting();
    }

    /* access modifiers changed from: private */
    public void updatePaintColor() {
        if (isColorInversionEnabled()) {
            Log.d("UdfpsSurfaceView", "Color inversion is enabled, set udfps surface to black.");
            this.mSensorPaint.setARGB(255, 0, 0, 0);
        } else {
            Log.d("UdfpsSurfaceView", "Color inversion is disabled, set udfps surface to white.");
            this.mSensorPaint.setARGB(255, 255, 255, 255);
        }
        invalidate();
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
}
