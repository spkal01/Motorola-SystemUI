package com.android.systemui.scrim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.systemui.screenshot.SaveImageInBackgroundTask$$ExternalSyntheticLambda0;
import java.util.concurrent.Executor;

public class ScrimView extends View {
    private Runnable mChangeRunnable;
    private Executor mChangeRunnableExecutor;
    private PorterDuffColorFilter mColorFilter;
    private final Object mColorLock;
    @GuardedBy({"mColorLock"})
    private final ColorExtractor.GradientColors mColors;
    private Drawable mDrawable;
    private Rect mDrawableBounds;
    private Executor mExecutor;
    private Looper mExecutorLooper;
    private int mTintColor;
    private final ColorExtractor.GradientColors mTmpColors;
    private float mViewAlpha;

    /* access modifiers changed from: protected */
    public boolean canReceivePointerEvents() {
        return false;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ScrimView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScrimView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScrimView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ScrimView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mColorLock = new Object();
        this.mTmpColors = new ColorExtractor.GradientColors();
        this.mViewAlpha = 1.0f;
        ScrimDrawable scrimDrawable = new ScrimDrawable();
        this.mDrawable = scrimDrawable;
        scrimDrawable.setCallback(this);
        this.mColors = new ColorExtractor.GradientColors();
        this.mExecutorLooper = Looper.myLooper();
        this.mExecutor = SaveImageInBackgroundTask$$ExternalSyntheticLambda0.INSTANCE;
        executeOnExecutor(new ScrimView$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateColorWithTint(false);
    }

    public void setExecutor(Executor executor, Looper looper) {
        this.mExecutor = executor;
        this.mExecutorLooper = looper;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDrawable.getAlpha() > 0) {
            this.mDrawable.draw(canvas);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setDrawable(Drawable drawable) {
        executeOnExecutor(new ScrimView$$ExternalSyntheticLambda3(this, drawable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDrawable$1(Drawable drawable) {
        this.mDrawable = drawable;
        drawable.setCallback(this);
        this.mDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
        this.mDrawable.setAlpha((int) (this.mViewAlpha * 255.0f));
        invalidate();
    }

    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable == this.mDrawable) {
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Rect rect = this.mDrawableBounds;
        if (rect != null) {
            this.mDrawable.setBounds(rect);
        } else if (z) {
            this.mDrawable.setBounds(i, i2, i3, i4);
            invalidate();
        }
    }

    public void setClickable(boolean z) {
        executeOnExecutor(new ScrimView$$ExternalSyntheticLambda5(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setClickable$2(boolean z) {
        super.setClickable(z);
    }

    public void setColors(ColorExtractor.GradientColors gradientColors, boolean z) {
        if (gradientColors != null) {
            executeOnExecutor(new ScrimView$$ExternalSyntheticLambda4(this, gradientColors, z));
            return;
        }
        throw new IllegalArgumentException("Colors cannot be null");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setColors$3(ColorExtractor.GradientColors gradientColors, boolean z) {
        synchronized (this.mColorLock) {
            if (!this.mColors.equals(gradientColors)) {
                this.mColors.set(gradientColors);
                updateColorWithTint(z);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Drawable getDrawable() {
        return this.mDrawable;
    }

    public ColorExtractor.GradientColors getColors() {
        synchronized (this.mColorLock) {
            this.mTmpColors.set(this.mColors);
        }
        return this.mTmpColors;
    }

    public void setTint(int i) {
        setTint(i, false);
    }

    public void setTint(int i, boolean z) {
        executeOnExecutor(new ScrimView$$ExternalSyntheticLambda2(this, i, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setTint$4(int i, boolean z) {
        if (this.mTintColor != i) {
            this.mTintColor = i;
            updateColorWithTint(z);
        }
    }

    private void updateColorWithTint(boolean z) {
        Drawable drawable = this.mDrawable;
        if (drawable instanceof ScrimDrawable) {
            ((ScrimDrawable) drawable).setColor(ColorUtils.blendARGB(this.mColors.getMainColor(), this.mTintColor, ((float) Color.alpha(this.mTintColor)) / 255.0f), z);
        } else {
            if (Color.alpha(this.mTintColor) != 0) {
                PorterDuffColorFilter porterDuffColorFilter = this.mColorFilter;
                PorterDuff.Mode mode = porterDuffColorFilter == null ? PorterDuff.Mode.SRC_OVER : porterDuffColorFilter.getMode();
                PorterDuffColorFilter porterDuffColorFilter2 = this.mColorFilter;
                if (porterDuffColorFilter2 == null || porterDuffColorFilter2.getColor() != this.mTintColor) {
                    this.mColorFilter = new PorterDuffColorFilter(this.mTintColor, mode);
                }
            } else {
                this.mColorFilter = null;
            }
            this.mDrawable.setColorFilter(this.mColorFilter);
            this.mDrawable.invalidateSelf();
        }
        Runnable runnable = this.mChangeRunnable;
        if (runnable != null) {
            this.mChangeRunnableExecutor.execute(runnable);
        }
    }

    public int getTint() {
        return this.mTintColor;
    }

    public void setViewAlpha(float f) {
        if (!Float.isNaN(f)) {
            executeOnExecutor(new ScrimView$$ExternalSyntheticLambda1(this, f));
            return;
        }
        throw new IllegalArgumentException("alpha cannot be NaN: " + f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setViewAlpha$5(float f) {
        if (f != this.mViewAlpha) {
            this.mViewAlpha = f;
            this.mDrawable.setAlpha((int) (f * 255.0f));
            Runnable runnable = this.mChangeRunnable;
            if (runnable != null) {
                this.mChangeRunnableExecutor.execute(runnable);
            }
        }
    }

    public float getViewAlpha() {
        return this.mViewAlpha;
    }

    public void setChangeRunnable(Runnable runnable, Executor executor) {
        this.mChangeRunnable = runnable;
        this.mChangeRunnableExecutor = executor;
    }

    private void executeOnExecutor(Runnable runnable) {
        if (this.mExecutor == null || Looper.myLooper() == this.mExecutorLooper) {
            runnable.run();
        } else {
            this.mExecutor.execute(runnable);
        }
    }

    public void enableBottomEdgeConcave(boolean z) {
        Drawable drawable = this.mDrawable;
        if (drawable instanceof ScrimDrawable) {
            ((ScrimDrawable) drawable).setBottomEdgeConcave(z);
        }
    }

    public void setBottomEdgePosition(int i) {
        Drawable drawable = this.mDrawable;
        if (drawable instanceof ScrimDrawable) {
            ((ScrimDrawable) drawable).setBottomEdgePosition(i);
        }
    }

    public void enableRoundedCorners(boolean z) {
        Drawable drawable = this.mDrawable;
        if (drawable instanceof ScrimDrawable) {
            ((ScrimDrawable) drawable).setRoundedCornersEnabled(z);
        }
    }

    public void setDrawableBounds(float f, float f2, float f3, float f4) {
        if (this.mDrawableBounds == null) {
            this.mDrawableBounds = new Rect();
        }
        this.mDrawableBounds.set((int) f, (int) f2, (int) f3, (int) f4);
        this.mDrawable.setBounds(this.mDrawableBounds);
    }

    public void setCornerRadius(int i) {
        Drawable drawable = this.mDrawable;
        if (drawable instanceof ScrimDrawable) {
            ((ScrimDrawable) drawable).setRoundedCorners((float) i);
        }
    }
}
