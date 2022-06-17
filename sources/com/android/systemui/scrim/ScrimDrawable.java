package com.android.systemui.scrim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;

public class ScrimDrawable extends Drawable {
    private int mAlpha = 255;
    private int mBottomEdgePosition;
    /* access modifiers changed from: private */
    public ValueAnimator mColorAnimation;
    private ConcaveInfo mConcaveInfo;
    private float mCornerRadius;
    private boolean mCornerRadiusEnabled;
    private int mMainColor;
    private int mMainColorTo;
    private final Paint mPaint;

    public int getOpacity() {
        return -3;
    }

    public ScrimDrawable() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL);
    }

    public void setColor(int i, boolean z) {
        if (i != this.mMainColorTo) {
            ValueAnimator valueAnimator = this.mColorAnimation;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.mColorAnimation.cancel();
            }
            this.mMainColorTo = i;
            if (z) {
                int i2 = this.mMainColor;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.setDuration(2000);
                ofFloat.addUpdateListener(new ScrimDrawable$$ExternalSyntheticLambda0(this, i2, i));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator, boolean z) {
                        if (ScrimDrawable.this.mColorAnimation == animator) {
                            ValueAnimator unused = ScrimDrawable.this.mColorAnimation = null;
                        }
                    }
                });
                ofFloat.setInterpolator(new DecelerateInterpolator());
                ofFloat.start();
                this.mColorAnimation = ofFloat;
                return;
            }
            this.mMainColor = i;
            invalidateSelf();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setColor$0(int i, int i2, ValueAnimator valueAnimator) {
        this.mMainColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidateSelf();
    }

    public void setAlpha(int i) {
        if (i != this.mAlpha) {
            this.mAlpha = i;
            invalidateSelf();
        }
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void setXfermode(Xfermode xfermode) {
        this.mPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    public void setRoundedCorners(float f) {
        if (f != this.mCornerRadius) {
            this.mCornerRadius = f;
            ConcaveInfo concaveInfo = this.mConcaveInfo;
            if (concaveInfo != null) {
                concaveInfo.setCornerRadius(f);
                updatePath();
            }
            invalidateSelf();
        }
    }

    public void setRoundedCornersEnabled(boolean z) {
        if (this.mCornerRadiusEnabled != z) {
            this.mCornerRadiusEnabled = z;
            invalidateSelf();
        }
    }

    public void setBottomEdgeConcave(boolean z) {
        if (!z || this.mConcaveInfo == null) {
            if (!z) {
                this.mConcaveInfo = null;
            } else {
                ConcaveInfo concaveInfo = new ConcaveInfo();
                this.mConcaveInfo = concaveInfo;
                concaveInfo.setCornerRadius(this.mCornerRadius);
            }
            invalidateSelf();
        }
    }

    public void setBottomEdgePosition(int i) {
        if (this.mBottomEdgePosition != i) {
            this.mBottomEdgePosition = i;
            if (this.mConcaveInfo != null) {
                updatePath();
                invalidateSelf();
            }
        }
    }

    public void draw(Canvas canvas) {
        this.mPaint.setColor(this.mMainColor);
        this.mPaint.setAlpha(this.mAlpha);
        if (this.mConcaveInfo != null) {
            drawConcave(canvas);
            return;
        }
        if (this.mCornerRadiusEnabled) {
            float f = 0.0f;
            if (this.mCornerRadius > 0.0f) {
                if (getBounds().top != 0) {
                    f = this.mCornerRadius;
                }
                float f2 = f;
                canvas.drawRoundRect((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, ((float) getBounds().bottom) + this.mCornerRadius, f2, f2, this.mPaint);
                return;
            }
        }
        canvas.drawRect((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, (float) getBounds().bottom, this.mPaint);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        updatePath();
    }

    private void drawConcave(Canvas canvas) {
        float f;
        canvas.clipOutPath(this.mConcaveInfo.mPath);
        int i = this.mBottomEdgePosition;
        if (i == 0) {
            f = 0.0f;
        } else {
            f = ((float) i) + this.mConcaveInfo.mPathOverlap;
        }
        Canvas canvas2 = canvas;
        canvas2.drawRect((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, f, this.mPaint);
    }

    private void updatePath() {
        ConcaveInfo concaveInfo = this.mConcaveInfo;
        if (concaveInfo != null) {
            concaveInfo.mPath.reset();
            int i = this.mBottomEdgePosition;
            this.mConcaveInfo.mPath.addRoundRect((float) getBounds().left, (float) i, (float) getBounds().right, ((float) i) + this.mConcaveInfo.mPathOverlap, this.mConcaveInfo.mCornerRadii, Path.Direction.CW);
        }
    }

    @VisibleForTesting
    public int getMainColor() {
        return this.mMainColor;
    }

    private static class ConcaveInfo {
        /* access modifiers changed from: private */
        public final float[] mCornerRadii = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        /* access modifiers changed from: private */
        public final Path mPath = new Path();
        /* access modifiers changed from: private */
        public float mPathOverlap;

        ConcaveInfo() {
        }

        public void setCornerRadius(float f) {
            this.mPathOverlap = f;
            float[] fArr = this.mCornerRadii;
            fArr[0] = f;
            fArr[1] = f;
            fArr[2] = f;
            fArr[3] = f;
        }
    }
}
