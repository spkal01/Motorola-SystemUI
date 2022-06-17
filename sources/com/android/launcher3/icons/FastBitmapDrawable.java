package com.android.launcher3.icons;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class FastBitmapDrawable extends Drawable {
    private static final Interpolator ACCEL = new AccelerateInterpolator();
    private static final Interpolator DEACCEL = new DecelerateInterpolator();
    private static final Property<FastBitmapDrawable, Float> SCALE = new Property<FastBitmapDrawable, Float>(Float.TYPE, "scale") {
        public Float get(FastBitmapDrawable fastBitmapDrawable) {
            return Float.valueOf(fastBitmapDrawable.mScale);
        }

        public void set(FastBitmapDrawable fastBitmapDrawable, Float f) {
            float unused = fastBitmapDrawable.mScale = f.floatValue();
            fastBitmapDrawable.invalidateSelf();
        }
    };
    private static ColorFilter sDisabledFColorFilter;
    private int mAlpha;
    protected Bitmap mBitmap;
    private boolean mChecked;
    private int mCheckedColor;
    private Drawable mCheckedIcon;
    private Path mCheckedShapePath;
    private ColorFilter mColorFilter;
    float mDisabledAlpha;
    protected final int mIconColor;
    protected boolean mIsDisabled;
    private boolean mIsPressed;
    protected final Paint mPaint;
    /* access modifiers changed from: private */
    public float mScale;
    private ObjectAnimator mScaleAnimation;
    private boolean mShowPressAnim;

    public int getOpacity() {
        return -3;
    }

    public boolean isStateful() {
        return true;
    }

    public FastBitmapDrawable(Bitmap bitmap) {
        this(bitmap, 0);
    }

    protected FastBitmapDrawable(Bitmap bitmap, int i) {
        this(bitmap, i, false);
    }

    protected FastBitmapDrawable(Bitmap bitmap, int i, boolean z) {
        this.mPaint = new Paint(3);
        this.mDisabledAlpha = 1.0f;
        this.mCheckedColor = 0;
        this.mScale = 1.0f;
        this.mAlpha = 255;
        this.mShowPressAnim = true;
        this.mBitmap = bitmap;
        this.mIconColor = i;
        setFilterBitmap(true);
        setIsDisabled(z);
    }

    public final void draw(Canvas canvas) {
        Rect rect;
        if (this.mScale != 1.0f) {
            int save = canvas.save();
            rect = getBounds();
            float f = this.mScale;
            canvas.scale(f, f, rect.exactCenterX(), rect.exactCenterY());
            drawInternal(canvas, rect);
            canvas.restoreToCount(save);
        } else {
            rect = getBounds();
            drawInternal(canvas, rect);
        }
        if (this.mChecked && this.mCheckedIcon != null && this.mCheckedShapePath != null && this.mCheckedColor != 0) {
            float f2 = ExtendedBitmapInfo.sScale;
            canvas.scale(f2, f2, rect.exactCenterX() + ((float) ExtendedBitmapInfo.sOffset), rect.exactCenterY() + ((float) ExtendedBitmapInfo.sOffset));
            drawCheckMark(canvas, rect);
        }
    }

    /* access modifiers changed from: protected */
    public void drawInternal(Canvas canvas, Rect rect) {
        try {
            canvas.drawBitmap(this.mBitmap, (Rect) null, rect, this.mPaint);
        } catch (IllegalArgumentException unused) {
            canvas.drawBitmap(this.mBitmap.copy(Bitmap.Config.ARGB_4444, false), (Rect) null, rect, this.mPaint);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        updateFilter();
    }

    public void setAlpha(int i) {
        if (this.mAlpha != i) {
            this.mAlpha = i;
            this.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    public void setFilterBitmap(boolean z) {
        this.mPaint.setFilterBitmap(z);
        this.mPaint.setAntiAlias(z);
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight();
    }

    public int getMinimumWidth() {
        return getBounds().width();
    }

    public int getMinimumHeight() {
        return getBounds().height();
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        boolean z;
        int length = iArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            } else if (iArr[i] == 16842919) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (this.mIsPressed == z || !this.mShowPressAnim) {
            return false;
        }
        this.mIsPressed = z;
        ObjectAnimator objectAnimator = this.mScaleAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mScaleAnimation = null;
        }
        if (this.mIsPressed) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SCALE, new float[]{1.1f});
            this.mScaleAnimation = ofFloat;
            ofFloat.setDuration(200);
            this.mScaleAnimation.setInterpolator(ACCEL);
            this.mScaleAnimation.start();
        } else if (isVisible()) {
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, SCALE, new float[]{1.0f});
            this.mScaleAnimation = ofFloat2;
            ofFloat2.setDuration(200);
            this.mScaleAnimation.setInterpolator(DEACCEL);
            this.mScaleAnimation.start();
        } else {
            this.mScale = 1.0f;
            invalidateSelf();
        }
        return true;
    }

    public void setIsDisabled(boolean z) {
        if (this.mIsDisabled != z) {
            this.mIsDisabled = z;
            updateFilter();
        }
    }

    private ColorFilter getDisabledColorFilter() {
        if (sDisabledFColorFilter == null) {
            sDisabledFColorFilter = getDisabledFColorFilter(this.mDisabledAlpha);
        }
        return sDisabledFColorFilter;
    }

    /* access modifiers changed from: protected */
    public void updateFilter() {
        this.mPaint.setColorFilter(this.mIsDisabled ? getDisabledColorFilter() : this.mColorFilter);
        invalidateSelf();
    }

    public Drawable.ConstantState getConstantState() {
        return new FastBitmapConstantState(this.mBitmap, this.mIconColor, this.mIsDisabled);
    }

    public static ColorFilter getDisabledFColorFilter(float f) {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorMatrix colorMatrix2 = new ColorMatrix();
        colorMatrix2.setSaturation(0.0f);
        float[] array = colorMatrix.getArray();
        array[0] = 0.5f;
        array[6] = 0.5f;
        array[12] = 0.5f;
        float f2 = (float) 127;
        array[4] = f2;
        array[9] = f2;
        array[14] = f2;
        array[18] = f;
        colorMatrix2.preConcat(colorMatrix);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    protected static class FastBitmapConstantState extends Drawable.ConstantState {
        protected final Bitmap mBitmap;
        protected final int mIconColor;
        protected final boolean mIsDisabled;

        public int getChangingConfigurations() {
            return 0;
        }

        public FastBitmapConstantState(Bitmap bitmap, int i, boolean z) {
            this.mBitmap = bitmap;
            this.mIconColor = i;
            this.mIsDisabled = z;
        }

        public FastBitmapDrawable newDrawable() {
            return new FastBitmapDrawable(this.mBitmap, this.mIconColor, this.mIsDisabled);
        }
    }

    public void drawCheckMark(Canvas canvas, Rect rect) {
        int save = canvas.save();
        canvas.translate((float) rect.left, (float) rect.top);
        canvas.scale(((float) rect.width()) / 100.0f, ((float) rect.height()) / 100.0f);
        Paint paint = new Paint(3);
        paint.setColor(this.mCheckedColor);
        canvas.drawPath(this.mCheckedShapePath, paint);
        canvas.restoreToCount(save);
        int min = (int) (((float) Math.min(rect.width(), rect.height())) / 2.0f);
        int width = (rect.width() - min) / 2;
        int height = (rect.height() - min) / 2;
        this.mCheckedIcon.setBounds(width, height, width + min, min + height);
        this.mCheckedIcon.draw(canvas);
    }
}
