package com.android.systemui.screenrecord;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;
import com.android.systemui.R$styleable;

public class RecordingRadioButton extends RadioButton {
    private int mBrushColor;
    private int mDotRadius;
    private int mOutlineColor;
    private int mOutlineWidth;
    private final Paint mPaint;
    private int mRingColor;
    private int mRingPadding;
    private int mRingStrokeWidth;

    public RecordingRadioButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecordingRadioButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPaint = new Paint(1);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RecordingRadioButton);
        this.mDotRadius = obtainStyledAttributes.getDimensionPixelSize(R$styleable.RecordingRadioButton_dotRadius, 0);
        this.mBrushColor = obtainStyledAttributes.getColor(R$styleable.RecordingRadioButton_circleColor, 1660944383);
        this.mRingColor = obtainStyledAttributes.getColor(R$styleable.RecordingRadioButton_ringColor, 1660944383);
        this.mRingPadding = obtainStyledAttributes.getDimensionPixelSize(R$styleable.RecordingRadioButton_ringPadding, 0);
        this.mRingStrokeWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.RecordingRadioButton_ringStroke, 0);
        this.mOutlineWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.RecordingRadioButton_outlineStroke, 0);
        this.mOutlineColor = obtainStyledAttributes.getColor(R$styleable.RecordingRadioButton_outlineColor, this.mRingColor);
        obtainStyledAttributes.recycle();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        float width = ((float) getWidth()) / 2.0f;
        float height = ((float) getHeight()) / 2.0f;
        canvas.save();
        this.mPaint.setColor(this.mBrushColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width, height, (float) this.mDotRadius, this.mPaint);
        if (this.mOutlineWidth > 0) {
            this.mPaint.setColor(this.mOutlineColor);
            this.mPaint.setStrokeWidth((float) this.mOutlineWidth);
            this.mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width, height, (float) this.mDotRadius, this.mPaint);
        }
        if (isChecked()) {
            this.mPaint.setColor(this.mRingColor);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth((float) this.mRingStrokeWidth);
            canvas.drawCircle(width, height, (float) (this.mDotRadius + this.mRingPadding), this.mPaint);
        }
        canvas.restore();
    }

    public void setChecked(boolean z) {
        boolean z2 = z != isChecked();
        super.setChecked(z);
        if (z2) {
            invalidate();
        }
    }

    public int getBrushColor() {
        return this.mBrushColor;
    }
}
