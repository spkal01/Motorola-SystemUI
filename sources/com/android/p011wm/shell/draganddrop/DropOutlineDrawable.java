package com.android.p011wm.shell.draganddrop;

import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.IntProperty;
import android.util.Property;
import android.view.animation.Interpolator;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;

/* renamed from: com.android.wm.shell.draganddrop.DropOutlineDrawable */
public class DropOutlineDrawable extends Drawable {
    private final IntProperty<DropOutlineDrawable> ALPHA;
    private final Property<DropOutlineDrawable, Rect> BOUNDS = new Property<DropOutlineDrawable, Rect>(Rect.class, "bounds") {
        public void set(DropOutlineDrawable dropOutlineDrawable, Rect rect) {
            dropOutlineDrawable.setRegionBounds(rect);
        }

        public Rect get(DropOutlineDrawable dropOutlineDrawable) {
            return dropOutlineDrawable.getRegionBounds();
        }
    };
    private ObjectAnimator mAlphaAnimator;
    private final Rect mBounds = new Rect();
    private ObjectAnimator mBoundsAnimator;
    private int mColor;
    private final float mCornerRadius;
    private final int mMaxAlpha;
    private final Paint mPaint = new Paint(1);
    private final RectEvaluator mRectEvaluator = new RectEvaluator(new Rect());

    public int getOpacity() {
        return -3;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public DropOutlineDrawable(Context context) {
        C23101 r0 = new IntProperty<DropOutlineDrawable>("alpha") {
            public void setValue(DropOutlineDrawable dropOutlineDrawable, int i) {
                dropOutlineDrawable.setAlpha(i);
            }

            public Integer get(DropOutlineDrawable dropOutlineDrawable) {
                return Integer.valueOf(dropOutlineDrawable.getAlpha());
            }
        };
        this.ALPHA = r0;
        this.mCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(context.getResources());
        int color = context.getColor(C2219R.C2220color.drop_outline_background);
        this.mColor = color;
        this.mMaxAlpha = Color.alpha(color);
        r0.set(this, 0);
    }

    public void setAlpha(int i) {
        int alphaComponent = ColorUtils.setAlphaComponent(this.mColor, i);
        this.mColor = alphaComponent;
        this.mPaint.setColor(alphaComponent);
        invalidateSelf();
    }

    public int getAlpha() {
        return Color.alpha(this.mColor);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        Rect rect = this.mBounds;
        float f = this.mCornerRadius;
        canvas.drawRoundRect((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, f, f, this.mPaint);
    }

    public void setRegionBounds(Rect rect) {
        this.mBounds.set(rect);
        invalidateSelf();
    }

    public Rect getRegionBounds() {
        return this.mBounds;
    }

    /* access modifiers changed from: package-private */
    public ObjectAnimator startBoundsAnimation(Rect rect, Interpolator interpolator) {
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            String valueOf = String.valueOf(this.mBounds);
            String valueOf2 = String.valueOf(rect);
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, -1000962629, 0, (String) null, valueOf, valueOf2);
        }
        ObjectAnimator objectAnimator = this.mBoundsAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator ofObject = ObjectAnimator.ofObject(this, this.BOUNDS, this.mRectEvaluator, new Rect[]{this.mBounds, rect});
        this.mBoundsAnimator = ofObject;
        ofObject.setDuration(200);
        this.mBoundsAnimator.setInterpolator(interpolator);
        this.mBoundsAnimator.start();
        return this.mBoundsAnimator;
    }

    /* access modifiers changed from: package-private */
    public ObjectAnimator startVisibilityAnimation(boolean z, Interpolator interpolator) {
        int i = 0;
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, 274140888, 5, (String) null, Long.valueOf((long) Color.alpha(this.mColor)), Long.valueOf(z ? (long) this.mMaxAlpha : 0));
        }
        ObjectAnimator objectAnimator = this.mAlphaAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        IntProperty<DropOutlineDrawable> intProperty = this.ALPHA;
        int[] iArr = new int[2];
        iArr[0] = Color.alpha(this.mColor);
        if (z) {
            i = this.mMaxAlpha;
        }
        iArr[1] = i;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this, intProperty, iArr);
        this.mAlphaAnimator = ofInt;
        ofInt.setDuration(135);
        this.mAlphaAnimator.setInterpolator(interpolator);
        this.mAlphaAnimator.start();
        return this.mAlphaAnimator;
    }
}
