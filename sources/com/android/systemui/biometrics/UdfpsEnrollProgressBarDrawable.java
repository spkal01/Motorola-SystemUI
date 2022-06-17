package com.android.systemui.biometrics;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import com.android.systemui.R$color;

public class UdfpsEnrollProgressBarDrawable extends Drawable {
    private final Paint mBackgroundCirclePaint;
    private final Context mContext;
    private boolean mLastStepAcquired;
    private final UdfpsEnrollDrawable mParent;
    private float mProgress;
    private ValueAnimator mProgressAnimator;
    private final Paint mProgressPaint;
    private int mRotation;

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public UdfpsEnrollProgressBarDrawable(Context context, UdfpsEnrollDrawable udfpsEnrollDrawable) {
        this.mContext = context;
        this.mParent = udfpsEnrollDrawable;
        Paint paint = new Paint();
        this.mBackgroundCirclePaint = paint;
        paint.setStrokeWidth(Utils.dpToPixels(context, 12.0f));
        paint.setColor(context.getColor(R$color.white_disabled));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843817});
        paint.setColor(obtainStyledAttributes.getColor(0, paint.getColor()));
        obtainStyledAttributes.recycle();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16842803, typedValue, true);
        paint.setAlpha((int) (typedValue.getFloat() * 255.0f));
        Paint paint2 = new Paint();
        this.mProgressPaint = paint2;
        paint2.setStrokeWidth(Utils.dpToPixels(context, 12.0f));
        paint2.setColor(context.getColor(R$color.udfps_enroll_progress));
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeCap(Paint.Cap.ROUND);
    }

    /* access modifiers changed from: package-private */
    public void setEnrollmentProgress(int i, int i2) {
        setEnrollmentProgress(((float) ((i2 - i) + 1)) / ((float) (i2 + 1)));
    }

    private void setEnrollmentProgress(float f) {
        if (!this.mLastStepAcquired) {
            long j = 150;
            if (f == 1.0f) {
                j = 400;
                ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, 400});
                ofInt.setDuration(400);
                ofInt.addUpdateListener(new UdfpsEnrollProgressBarDrawable$$ExternalSyntheticLambda0(this));
                ofInt.start();
            }
            ValueAnimator valueAnimator = this.mProgressAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.mProgressAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mProgress, f});
            this.mProgressAnimator = ofFloat;
            ofFloat.setDuration(j);
            this.mProgressAnimator.addUpdateListener(new UdfpsEnrollProgressBarDrawable$$ExternalSyntheticLambda1(this));
            this.mProgressAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setEnrollmentProgress$0(ValueAnimator valueAnimator) {
        Log.d("UdfpsEnrollProgressBarDrawable", "Rotation: " + this.mRotation);
        this.mRotation = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mParent.invalidateSelf();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setEnrollmentProgress$1(ValueAnimator valueAnimator) {
        this.mProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mParent.invalidateSelf();
    }

    /* access modifiers changed from: package-private */
    public void onLastStepAcquired() {
        setEnrollmentProgress(1.0f);
        this.mLastStepAcquired = true;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) (this.mRotation - 90), (float) getBounds().centerX(), (float) getBounds().centerY());
        float dpToPixels = Utils.dpToPixels(this.mContext, 12.0f) / 2.0f;
        float f = dpToPixels;
        float f2 = dpToPixels;
        canvas.drawArc(f, f2, ((float) getBounds().right) - dpToPixels, ((float) getBounds().bottom) - dpToPixels, 0.0f, 360.0f, false, this.mBackgroundCirclePaint);
        Canvas canvas2 = canvas;
        canvas2.drawArc(f, f2, ((float) getBounds().right) - dpToPixels, ((float) getBounds().bottom) - dpToPixels, 0.0f, this.mProgress * 360.0f, false, this.mProgressPaint);
        canvas.restore();
    }
}
