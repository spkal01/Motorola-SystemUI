package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.systemui.R$color;

public class KeyguardUserSwitcherScrim extends Drawable implements View.OnLayoutChangeListener {
    private int mAlpha = 255;
    private int mCircleX;
    private int mCircleY;
    private int mDarkColor;
    private Paint mRadialGradientPaint = new Paint();
    private int mSize;

    public int getOpacity() {
        return -3;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public KeyguardUserSwitcherScrim(Context context) {
        this.mDarkColor = context.getColor(R$color.keyguard_user_switcher_background_gradient_color);
    }

    public void draw(Canvas canvas) {
        if (this.mAlpha != 0) {
            Rect bounds = getBounds();
            canvas.drawRect((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, this.mRadialGradientPaint);
        }
    }

    public void setAlpha(int i) {
        this.mAlpha = i;
        updatePaint();
        invalidateSelf();
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i != i5 || i2 != i6 || i3 != i7 || i4 != i8) {
            this.mSize = Math.max(i3 - i, i4 - i2);
            updatePaint();
        }
    }

    private void updatePaint() {
        int i = this.mSize;
        if (i != 0) {
            float f = ((float) i) * 2.5f;
            this.mRadialGradientPaint.setShader(new RadialGradient((float) this.mCircleX, (float) this.mCircleY, f, new int[]{Color.argb((int) (((float) (Color.alpha(this.mDarkColor) * this.mAlpha)) / 255.0f), 0, 0, 0), 0}, new float[]{Math.max(0.0f, 0.1f), 1.0f}, Shader.TileMode.CLAMP));
        }
    }

    public void setGradientCenter(int i, int i2) {
        this.mCircleX = i;
        this.mCircleY = i2;
        updatePaint();
    }
}
