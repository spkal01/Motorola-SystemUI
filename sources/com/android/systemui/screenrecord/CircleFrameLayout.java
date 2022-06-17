package com.android.systemui.screenrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CircleFrameLayout extends FrameLayout {
    private Path mCirclePath = new Path();

    public CircleFrameLayout(Context context) {
        super(context);
    }

    public CircleFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        canvas.clipPath(this.mCirclePath);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        float min = Math.min(((float) ((i - getPaddingStart()) - getPaddingEnd())) / 2.0f, ((float) ((i2 - getPaddingTop()) - getPaddingBottom())) / 2.0f);
        this.mCirclePath.reset();
        this.mCirclePath.addCircle(((float) i) / 2.0f, ((float) i2) / 2.0f, min, Path.Direction.CW);
    }
}
