package com.android.systemui.screenrecord;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import com.android.systemui.R$color;

public class RecordingBarBg extends View {
    private static final PorterDuffXfermode CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    public static final Property<RecordingBarBg, Float> STRETCH = new FloatProperty<RecordingBarBg>("drawHeight") {
        public void setValue(RecordingBarBg recordingBarBg, float f) {
            recordingBarBg.setDrawStretch(f);
        }

        public Float get(RecordingBarBg recordingBarBg) {
            return Float.valueOf(recordingBarBg.getDrawStretch());
        }
    };
    public static final int WHITE = R$color.screenrecord_outline_color;
    private boolean mAlignRight;
    private int mCameraPadding;
    private boolean mHasHole;
    private int mHeight;
    private Paint mPaint;
    private float mRadius;
    private float mStretchLen;
    private Paint mStrokePaint;
    private int mWidth;

    public RecordingBarBg(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecordingBarBg(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecordingBarBg(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RecordingBarBg(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mStretchLen = 0.0f;
        this.mCameraPadding = 0;
        this.mAlignRight = true;
        this.mHasHole = false;
        this.mPaint = new Paint(1);
        this.mStrokePaint = new Paint(1);
        this.mPaint.setColor(-872415232);
        this.mStrokePaint.setColor(context.getColor(WHITE));
        this.mStrokePaint.setStrokeWidth(2.0f);
        this.mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mHeight = i2;
        this.mWidth = i;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float min = ((float) Math.min(this.mWidth, this.mHeight)) / 2.0f;
        this.mRadius = min;
        boolean z = this.mAlignRight;
        float f = z ? this.mStretchLen : 0.0f;
        float f2 = z ? (float) this.mWidth : ((float) this.mWidth) - this.mStretchLen;
        canvas.drawRoundRect(f + 2.0f + 1.0f, 3.0f, (f2 - 2.0f) - 1.0f, (float) ((this.mHeight - 2) - 1), min, min, this.mPaint);
        float f3 = this.mRadius;
        canvas.drawRoundRect(f + 1.0f, 1.0f, f2 - 1.0f, (float) (this.mHeight - 1), f3, f3, this.mStrokePaint);
        if (this.mHasHole) {
            this.mPaint.setXfermode(CLEAR);
            canvas.drawCircle(this.mAlignRight ? ((float) this.mWidth) - this.mRadius : this.mRadius, (float) (this.mHeight / 2), this.mRadius - ((float) this.mCameraPadding), this.mPaint);
            this.mPaint.setXfermode((Xfermode) null);
        }
    }

    public void setAlignRight(boolean z) {
        this.mAlignRight = z;
    }

    public Animator createStretchAnimator(boolean z, boolean z2, int i) {
        this.mAlignRight = z2;
        Property<RecordingBarBg, Float> property = STRETCH;
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = z ? 0.0f : (float) i;
        if (z) {
            f = (float) i;
        }
        fArr[1] = f;
        return ObjectAnimator.ofFloat(this, property, fArr);
    }

    public void reset() {
        this.mStretchLen = 0.0f;
    }

    /* access modifiers changed from: private */
    public float getDrawStretch() {
        return this.mStretchLen;
    }

    /* access modifiers changed from: private */
    public void setDrawStretch(float f) {
        this.mStretchLen = f;
        invalidate();
    }

    public void setCameraPadding(int i) {
        this.mCameraPadding = i + 1;
    }
}
