package androidx.leanback.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$color;
import androidx.leanback.R$dimen;
import androidx.leanback.R$drawable;
import androidx.leanback.R$styleable;

public class PagingIndicator extends View {
    private static final TimeInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final Property<Dot, Float> DOT_ALPHA;
    private static final Property<Dot, Float> DOT_DIAMETER;
    private static final Property<Dot, Float> DOT_TRANSLATION_X;
    private final AnimatorSet mAnimator;
    Bitmap mArrow;
    final int mArrowDiameter;
    private final int mArrowGap;
    Paint mArrowPaint;
    final int mArrowRadius;
    final Rect mArrowRect;
    final float mArrowToBgRatio;
    final Paint mBgPaint;
    private int mCurrentPage;
    int mDotCenterY;
    final int mDotDiameter;
    int mDotFgSelectColor;
    private final int mDotGap;
    final int mDotRadius;
    private int[] mDotSelectedNextX;
    private int[] mDotSelectedPrevX;
    private int[] mDotSelectedX;
    private Dot[] mDots;
    final Paint mFgPaint;
    private final AnimatorSet mHideAnimator;
    boolean mIsLtr;
    private int mPageCount;
    private int mPreviousPage;
    private final int mShadowRadius;
    private final AnimatorSet mShowAnimator;

    static {
        Class<Float> cls = Float.class;
        DOT_ALPHA = new Property<Dot, Float>(cls, "alpha") {
            public Float get(Dot dot) {
                return Float.valueOf(dot.getAlpha());
            }

            public void set(Dot dot, Float f) {
                dot.setAlpha(f.floatValue());
            }
        };
        DOT_DIAMETER = new Property<Dot, Float>(cls, "diameter") {
            public Float get(Dot dot) {
                return Float.valueOf(dot.getDiameter());
            }

            public void set(Dot dot, Float f) {
                dot.setDiameter(f.floatValue());
            }
        };
        DOT_TRANSLATION_X = new Property<Dot, Float>(cls, "translation_x") {
            public Float get(Dot dot) {
                return Float.valueOf(dot.getTranslationX());
            }

            public void set(Dot dot, Float f) {
                dot.setTranslationX(f.floatValue());
            }
        };
    }

    public PagingIndicator(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public PagingIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PagingIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        AnimatorSet animatorSet = new AnimatorSet();
        this.mAnimator = animatorSet;
        Resources resources = getResources();
        int[] iArr = R$styleable.PagingIndicator;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr, i, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, iArr, attributeSet, obtainStyledAttributes, i, 0);
        int dimensionFromTypedArray = getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_lbDotRadius, R$dimen.lb_page_indicator_dot_radius);
        this.mDotRadius = dimensionFromTypedArray;
        this.mDotDiameter = dimensionFromTypedArray * 2;
        int dimensionFromTypedArray2 = getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_arrowRadius, R$dimen.lb_page_indicator_arrow_radius);
        this.mArrowRadius = dimensionFromTypedArray2;
        int i2 = dimensionFromTypedArray2 * 2;
        this.mArrowDiameter = i2;
        this.mDotGap = getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotToDotGap, R$dimen.lb_page_indicator_dot_gap);
        this.mArrowGap = getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotToArrowGap, R$dimen.lb_page_indicator_arrow_gap);
        int colorFromTypedArray = getColorFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotBgColor, R$color.lb_page_indicator_dot);
        Paint paint = new Paint(1);
        this.mBgPaint = paint;
        paint.setColor(colorFromTypedArray);
        this.mDotFgSelectColor = getColorFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_arrowBgColor, R$color.lb_page_indicator_arrow_background);
        if (this.mArrowPaint == null) {
            int i3 = R$styleable.PagingIndicator_arrowColor;
            if (obtainStyledAttributes.hasValue(i3)) {
                setArrowColor(obtainStyledAttributes.getColor(i3, 0));
            }
        }
        obtainStyledAttributes.recycle();
        this.mIsLtr = resources.getConfiguration().getLayoutDirection() == 0;
        int color = resources.getColor(R$color.lb_page_indicator_arrow_shadow);
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.lb_page_indicator_arrow_shadow_radius);
        this.mShadowRadius = dimensionPixelSize;
        Paint paint2 = new Paint(1);
        this.mFgPaint = paint2;
        float dimensionPixelSize2 = (float) resources.getDimensionPixelSize(R$dimen.lb_page_indicator_arrow_shadow_offset);
        paint2.setShadowLayer((float) dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize2, color);
        this.mArrow = loadArrow();
        this.mArrowRect = new Rect(0, 0, this.mArrow.getWidth(), this.mArrow.getHeight());
        this.mArrowToBgRatio = ((float) this.mArrow.getWidth()) / ((float) i2);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mShowAnimator = animatorSet2;
        animatorSet2.playTogether(new Animator[]{createDotAlphaAnimator(0.0f, 1.0f), createDotDiameterAnimator((float) (dimensionFromTypedArray * 2), (float) (dimensionFromTypedArray2 * 2)), createDotTranslationXAnimator()});
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.mHideAnimator = animatorSet3;
        animatorSet3.playTogether(new Animator[]{createDotAlphaAnimator(1.0f, 0.0f), createDotDiameterAnimator((float) (dimensionFromTypedArray2 * 2), (float) (dimensionFromTypedArray * 2)), createDotTranslationXAnimator()});
        animatorSet.playTogether(new Animator[]{animatorSet2, animatorSet3});
        setLayerType(1, (Paint) null);
    }

    private int getDimensionFromTypedArray(TypedArray typedArray, int i, int i2) {
        return typedArray.getDimensionPixelOffset(i, getResources().getDimensionPixelOffset(i2));
    }

    private int getColorFromTypedArray(TypedArray typedArray, int i, int i2) {
        return typedArray.getColor(i, getResources().getColor(i2));
    }

    private Bitmap loadArrow() {
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R$drawable.lb_ic_nav_arrow);
        if (this.mIsLtr) {
            return decodeResource;
        }
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(decodeResource, 0, 0, decodeResource.getWidth(), decodeResource.getHeight(), matrix, false);
    }

    public void setArrowColor(int i) {
        if (this.mArrowPaint == null) {
            this.mArrowPaint = new Paint();
        }
        this.mArrowPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
    }

    private Animator createDotAlphaAnimator(float f, float f2) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) null, DOT_ALPHA, new float[]{f, f2});
        ofFloat.setDuration(167);
        ofFloat.setInterpolator(DECELERATE_INTERPOLATOR);
        return ofFloat;
    }

    private Animator createDotDiameterAnimator(float f, float f2) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) null, DOT_DIAMETER, new float[]{f, f2});
        ofFloat.setDuration(417);
        ofFloat.setInterpolator(DECELERATE_INTERPOLATOR);
        return ofFloat;
    }

    private Animator createDotTranslationXAnimator() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) null, DOT_TRANSLATION_X, new float[]{(float) ((-this.mArrowGap) + this.mDotGap), 0.0f});
        ofFloat.setDuration(417);
        ofFloat.setInterpolator(DECELERATE_INTERPOLATOR);
        return ofFloat;
    }

    private void calculateDotPositions() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = getWidth() - getPaddingRight();
        int requiredWidth = getRequiredWidth();
        int i = (paddingLeft + width) / 2;
        int i2 = this.mPageCount;
        int[] iArr = new int[i2];
        this.mDotSelectedX = iArr;
        int[] iArr2 = new int[i2];
        this.mDotSelectedPrevX = iArr2;
        int[] iArr3 = new int[i2];
        this.mDotSelectedNextX = iArr3;
        int i3 = 1;
        if (this.mIsLtr) {
            int i4 = i - (requiredWidth / 2);
            int i5 = this.mDotRadius;
            int i6 = this.mDotGap;
            int i7 = this.mArrowGap;
            iArr[0] = ((i4 + i5) - i6) + i7;
            iArr2[0] = i4 + i5;
            iArr3[0] = ((i4 + i5) - (i6 * 2)) + (i7 * 2);
            while (i3 < this.mPageCount) {
                int[] iArr4 = this.mDotSelectedX;
                int[] iArr5 = this.mDotSelectedPrevX;
                int i8 = i3 - 1;
                int i9 = iArr5[i8];
                int i10 = this.mArrowGap;
                iArr4[i3] = i9 + i10;
                iArr5[i3] = iArr5[i8] + this.mDotGap;
                this.mDotSelectedNextX[i3] = iArr4[i8] + i10;
                i3++;
            }
        } else {
            int i11 = i + (requiredWidth / 2);
            int i12 = this.mDotRadius;
            int i13 = this.mDotGap;
            int i14 = this.mArrowGap;
            iArr[0] = ((i11 - i12) + i13) - i14;
            iArr2[0] = i11 - i12;
            iArr3[0] = ((i11 - i12) + (i13 * 2)) - (i14 * 2);
            while (i3 < this.mPageCount) {
                int[] iArr6 = this.mDotSelectedX;
                int[] iArr7 = this.mDotSelectedPrevX;
                int i15 = i3 - 1;
                int i16 = iArr7[i15];
                int i17 = this.mArrowGap;
                iArr6[i3] = i16 - i17;
                iArr7[i3] = iArr7[i15] - this.mDotGap;
                this.mDotSelectedNextX[i3] = iArr6[i15] - i17;
                i3++;
            }
        }
        this.mDotCenterY = paddingTop + this.mArrowRadius;
        adjustDotPosition();
    }

    /* access modifiers changed from: package-private */
    public int getPageCount() {
        return this.mPageCount;
    }

    /* access modifiers changed from: package-private */
    public int[] getDotSelectedX() {
        return this.mDotSelectedX;
    }

    /* access modifiers changed from: package-private */
    public int[] getDotSelectedLeftX() {
        return this.mDotSelectedPrevX;
    }

    /* access modifiers changed from: package-private */
    public int[] getDotSelectedRightX() {
        return this.mDotSelectedNextX;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int desiredHeight = getDesiredHeight();
        int mode = View.MeasureSpec.getMode(i2);
        if (mode == Integer.MIN_VALUE) {
            desiredHeight = Math.min(desiredHeight, View.MeasureSpec.getSize(i2));
        } else if (mode == 1073741824) {
            desiredHeight = View.MeasureSpec.getSize(i2);
        }
        int desiredWidth = getDesiredWidth();
        int mode2 = View.MeasureSpec.getMode(i);
        if (mode2 == Integer.MIN_VALUE) {
            desiredWidth = Math.min(desiredWidth, View.MeasureSpec.getSize(i));
        } else if (mode2 == 1073741824) {
            desiredWidth = View.MeasureSpec.getSize(i);
        }
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        setMeasuredDimension(i, i2);
        calculateDotPositions();
    }

    private int getDesiredHeight() {
        return getPaddingTop() + this.mArrowDiameter + getPaddingBottom() + this.mShadowRadius;
    }

    private int getRequiredWidth() {
        return (this.mDotRadius * 2) + (this.mArrowGap * 2) + ((this.mPageCount - 3) * this.mDotGap);
    }

    private int getDesiredWidth() {
        return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        for (int i = 0; i < this.mPageCount; i++) {
            this.mDots[i].draw(canvas);
        }
    }

    private void adjustDotPosition() {
        int i;
        float f;
        int i2 = 0;
        while (true) {
            i = this.mCurrentPage;
            f = -1.0f;
            if (i2 >= i) {
                break;
            }
            this.mDots[i2].deselect();
            Dot[] dotArr = this.mDots;
            Dot dot = dotArr[i2];
            if (i2 != this.mPreviousPage) {
                f = 1.0f;
            }
            dot.mDirection = f;
            dotArr[i2].mCenterX = (float) this.mDotSelectedPrevX[i2];
            i2++;
        }
        this.mDots[i].select();
        Dot[] dotArr2 = this.mDots;
        int i3 = this.mCurrentPage;
        Dot dot2 = dotArr2[i3];
        if (this.mPreviousPage >= i3) {
            f = 1.0f;
        }
        dot2.mDirection = f;
        dotArr2[i3].mCenterX = (float) this.mDotSelectedX[i3];
        while (true) {
            i3++;
            if (i3 < this.mPageCount) {
                this.mDots[i3].deselect();
                Dot[] dotArr3 = this.mDots;
                dotArr3[i3].mDirection = 1.0f;
                dotArr3[i3].mCenterX = (float) this.mDotSelectedNextX[i3];
            } else {
                return;
            }
        }
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        boolean z = i == 0;
        if (this.mIsLtr != z) {
            this.mIsLtr = z;
            this.mArrow = loadArrow();
            Dot[] dotArr = this.mDots;
            if (dotArr != null) {
                for (Dot onRtlPropertiesChanged : dotArr) {
                    onRtlPropertiesChanged.onRtlPropertiesChanged();
                }
            }
            calculateDotPositions();
            invalidate();
        }
    }

    public class Dot {
        float mAlpha;
        float mArrowImageRadius;
        float mCenterX;
        float mDiameter;
        float mDirection;
        int mFgColor;
        float mLayoutDirection;
        float mRadius;
        float mTranslationX;
        final /* synthetic */ PagingIndicator this$0;

        /* access modifiers changed from: package-private */
        public void select() {
            this.mTranslationX = 0.0f;
            this.mCenterX = 0.0f;
            PagingIndicator pagingIndicator = this.this$0;
            this.mDiameter = (float) pagingIndicator.mArrowDiameter;
            float f = (float) pagingIndicator.mArrowRadius;
            this.mRadius = f;
            this.mArrowImageRadius = f * pagingIndicator.mArrowToBgRatio;
            this.mAlpha = 1.0f;
            adjustAlpha();
        }

        /* access modifiers changed from: package-private */
        public void deselect() {
            this.mTranslationX = 0.0f;
            this.mCenterX = 0.0f;
            PagingIndicator pagingIndicator = this.this$0;
            this.mDiameter = (float) pagingIndicator.mDotDiameter;
            float f = (float) pagingIndicator.mDotRadius;
            this.mRadius = f;
            this.mArrowImageRadius = f * pagingIndicator.mArrowToBgRatio;
            this.mAlpha = 0.0f;
            adjustAlpha();
        }

        public void adjustAlpha() {
            this.mFgColor = Color.argb(Math.round(this.mAlpha * 255.0f), Color.red(this.this$0.mDotFgSelectColor), Color.green(this.this$0.mDotFgSelectColor), Color.blue(this.this$0.mDotFgSelectColor));
        }

        public float getAlpha() {
            return this.mAlpha;
        }

        public void setAlpha(float f) {
            this.mAlpha = f;
            adjustAlpha();
            this.this$0.invalidate();
        }

        public float getTranslationX() {
            return this.mTranslationX;
        }

        public void setTranslationX(float f) {
            this.mTranslationX = f * this.mDirection * this.mLayoutDirection;
            this.this$0.invalidate();
        }

        public float getDiameter() {
            return this.mDiameter;
        }

        public void setDiameter(float f) {
            this.mDiameter = f;
            float f2 = f / 2.0f;
            this.mRadius = f2;
            PagingIndicator pagingIndicator = this.this$0;
            this.mArrowImageRadius = f2 * pagingIndicator.mArrowToBgRatio;
            pagingIndicator.invalidate();
        }

        /* access modifiers changed from: package-private */
        public void draw(Canvas canvas) {
            float f = this.mCenterX + this.mTranslationX;
            PagingIndicator pagingIndicator = this.this$0;
            canvas.drawCircle(f, (float) pagingIndicator.mDotCenterY, this.mRadius, pagingIndicator.mBgPaint);
            if (this.mAlpha > 0.0f) {
                this.this$0.mFgPaint.setColor(this.mFgColor);
                PagingIndicator pagingIndicator2 = this.this$0;
                canvas.drawCircle(f, (float) pagingIndicator2.mDotCenterY, this.mRadius, pagingIndicator2.mFgPaint);
                PagingIndicator pagingIndicator3 = this.this$0;
                Bitmap bitmap = pagingIndicator3.mArrow;
                Rect rect = pagingIndicator3.mArrowRect;
                float f2 = this.mArrowImageRadius;
                int i = this.this$0.mDotCenterY;
                canvas.drawBitmap(bitmap, rect, new Rect((int) (f - f2), (int) (((float) i) - f2), (int) (f + f2), (int) (((float) i) + f2)), this.this$0.mArrowPaint);
            }
        }

        /* access modifiers changed from: package-private */
        public void onRtlPropertiesChanged() {
            this.mLayoutDirection = this.this$0.mIsLtr ? 1.0f : -1.0f;
        }
    }
}
