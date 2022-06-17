package com.android.p011wm.shell.bubbles;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.animation.Interpolators;
import com.android.p011wm.shell.bubbles.Bubble;
import com.android.p011wm.shell.common.TriangleShape;

/* renamed from: com.android.wm.shell.bubbles.BubbleFlyoutView */
public class BubbleFlyoutView extends FrameLayout {
    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private boolean mArrowPointingLeft = true;
    private final Paint mBgPaint;
    private final RectF mBgRect = new RectF();
    private float mBgTranslationX;
    private float mBgTranslationY;
    private final int mBubbleElevation;
    private int mBubbleSize;
    private final float mCornerRadius;
    private float[] mDotCenter;
    private int mDotColor;
    private final int mFloatingBackgroundColor;
    private final int mFlyoutElevation;
    private final int mFlyoutPadding;
    private final int mFlyoutSpaceFromBubble;
    private final ViewGroup mFlyoutTextContainer;
    private float mFlyoutToDotHeightDelta = 0.0f;
    private float mFlyoutToDotWidthDelta = 0.0f;
    private float mFlyoutY = 0.0f;
    private final ShapeDrawable mLeftTriangleShape;
    private final TextView mMessageText;
    private float mNewDotRadius;
    private float mNewDotSize;
    private Runnable mOnHide;
    private float mOriginalDotSize;
    private float mPercentStillFlyout = 0.0f;
    private float mPercentTransitionedToDot = 1.0f;
    private final int mPointerSize;
    private float mRestingTranslationX = 0.0f;
    private final ShapeDrawable mRightTriangleShape;
    private final ImageView mSenderAvatar;
    private final TextView mSenderText;
    private float mTranslationXWhenDot = 0.0f;
    private float mTranslationYWhenDot = 0.0f;
    private final Outline mTriangleOutline = new Outline();

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$animateUpdate$0() {
    }

    private void renderPointerTriangle(Canvas canvas, float f, float f2) {
    }

    public BubbleFlyoutView(Context context) {
        super(context);
        Paint paint = new Paint(3);
        this.mBgPaint = paint;
        LayoutInflater.from(context).inflate(C2219R.layout.bubble_flyout, this, true);
        ViewGroup viewGroup = (ViewGroup) findViewById(C2219R.C2222id.bubble_flyout_text_container);
        this.mFlyoutTextContainer = viewGroup;
        this.mSenderText = (TextView) findViewById(C2219R.C2222id.bubble_flyout_name);
        this.mSenderAvatar = (ImageView) findViewById(C2219R.C2222id.bubble_flyout_avatar);
        this.mMessageText = (TextView) viewGroup.findViewById(C2219R.C2222id.bubble_flyout_text);
        Resources resources = getResources();
        this.mFlyoutPadding = resources.getDimensionPixelSize(C2219R.dimen.bubble_flyout_padding_x);
        this.mFlyoutSpaceFromBubble = resources.getDimensionPixelSize(C2219R.dimen.bubble_flyout_space_from_bubble);
        this.mPointerSize = 0;
        this.mBubbleElevation = resources.getDimensionPixelSize(C2219R.dimen.bubble_elevation);
        int dimensionPixelSize = resources.getDimensionPixelSize(C2219R.dimen.bubble_flyout_elevation);
        this.mFlyoutElevation = dimensionPixelSize;
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{17956910, 16844145});
        int color = obtainStyledAttributes.getColor(0, -1);
        this.mFloatingBackgroundColor = color;
        this.mCornerRadius = (float) obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
        setPadding(0, 0, 0, 0);
        setWillNotDraw(false);
        setClipChildren(true);
        setTranslationZ((float) dimensionPixelSize);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                BubbleFlyoutView.this.getOutline(outline);
            }
        });
        setLayoutDirection(3);
        paint.setColor(color);
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.createHorizontal((float) 0, (float) 0, true));
        this.mLeftTriangleShape = shapeDrawable;
        shapeDrawable.setBounds(0, 0, 0, 0);
        shapeDrawable.getPaint().setColor(color);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(TriangleShape.createHorizontal((float) 0, (float) 0, false));
        this.mRightTriangleShape = shapeDrawable2;
        shapeDrawable2.setBounds(0, 0, 0, 0);
        shapeDrawable2.getPaint().setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        renderBackground(canvas);
        invalidateOutline();
        super.onDraw(canvas);
    }

    /* access modifiers changed from: package-private */
    public void updateFontSize() {
        float dimensionPixelSize = (float) this.mContext.getResources().getDimensionPixelSize(17105550);
        this.mMessageText.setTextSize(0, dimensionPixelSize);
        this.mSenderText.setTextSize(0, dimensionPixelSize);
    }

    /* access modifiers changed from: package-private */
    public void animateUpdate(Bubble.FlyoutMessage flyoutMessage, float f, PointF pointF, boolean z, Runnable runnable) {
        this.mOnHide = runnable;
        fade(false, pointF, z, new BubbleFlyoutView$$ExternalSyntheticLambda2(this, flyoutMessage, f, pointF, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateUpdate$2(Bubble.FlyoutMessage flyoutMessage, float f, PointF pointF, boolean z) {
        updateFlyoutMessage(flyoutMessage, f);
        post(new BubbleFlyoutView$$ExternalSyntheticLambda0(this, pointF, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateUpdate$1(PointF pointF, boolean z) {
        fade(true, pointF, z, BubbleFlyoutView$$ExternalSyntheticLambda3.INSTANCE);
    }

    private void fade(boolean z, PointF pointF, boolean z2, Runnable runnable) {
        this.mFlyoutY = pointF.y + (((float) (this.mBubbleSize - this.mFlyoutTextContainer.getHeight())) / 2.0f);
        float f = 0.0f;
        setAlpha(z ? 0.0f : 1.0f);
        float f2 = this.mFlyoutY;
        if (z) {
            f2 += 40.0f;
        }
        setTranslationY(f2);
        updateFlyoutX(pointF.x);
        setTranslationX(this.mRestingTranslationX);
        updateDot(pointF, z2);
        ViewPropertyAnimator animate = animate();
        if (z) {
            f = 1.0f;
        }
        ViewPropertyAnimator alpha = animate.alpha(f);
        long j = 250;
        alpha.setDuration(z ? 250 : 150).setInterpolator(z ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT);
        ViewPropertyAnimator animate2 = animate();
        float f3 = this.mFlyoutY;
        if (!z) {
            f3 -= 40.0f;
        }
        ViewPropertyAnimator translationY = animate2.translationY(f3);
        if (!z) {
            j = 150;
        }
        translationY.setDuration(j).setInterpolator(z ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT).withEndAction(runnable);
    }

    private void updateFlyoutMessage(Bubble.FlyoutMessage flyoutMessage, float f) {
        Drawable drawable = flyoutMessage.senderAvatar;
        if (drawable == null || !flyoutMessage.isGroupChat) {
            this.mSenderAvatar.setVisibility(8);
            this.mSenderAvatar.setTranslationX(0.0f);
            this.mMessageText.setTranslationX(0.0f);
            this.mSenderText.setTranslationX(0.0f);
        } else {
            this.mSenderAvatar.setVisibility(0);
            this.mSenderAvatar.setImageDrawable(drawable);
        }
        int i = ((int) (f * 0.6f)) - (this.mFlyoutPadding * 2);
        if (!TextUtils.isEmpty(flyoutMessage.senderName)) {
            this.mSenderText.setMaxWidth(i);
            this.mSenderText.setText(flyoutMessage.senderName);
            this.mSenderText.setVisibility(0);
        } else {
            this.mSenderText.setVisibility(8);
        }
        this.mMessageText.setMaxWidth(i);
        this.mMessageText.setText(flyoutMessage.message);
    }

    /* access modifiers changed from: package-private */
    public void updateFlyoutX(float f) {
        float f2;
        if (this.mArrowPointingLeft) {
            f2 = f + ((float) this.mBubbleSize) + ((float) this.mFlyoutSpaceFromBubble);
        } else {
            f2 = (f - ((float) getWidth())) - ((float) this.mFlyoutSpaceFromBubble);
        }
        this.mRestingTranslationX = f2;
    }

    /* access modifiers changed from: package-private */
    public void updateDot(PointF pointF, boolean z) {
        float f = 0.0f;
        float f2 = z ? 0.0f : this.mNewDotSize;
        this.mFlyoutToDotWidthDelta = ((float) getWidth()) - f2;
        this.mFlyoutToDotHeightDelta = ((float) getHeight()) - f2;
        if (!z) {
            f = this.mOriginalDotSize / 2.0f;
        }
        float f3 = pointF.x;
        float[] fArr = this.mDotCenter;
        float f4 = (f3 + fArr[0]) - f;
        float f5 = (pointF.y + fArr[1]) - f;
        float f6 = this.mRestingTranslationX - f4;
        float f7 = this.mFlyoutY - f5;
        this.mTranslationXWhenDot = -f6;
        this.mTranslationYWhenDot = -f7;
    }

    /* access modifiers changed from: package-private */
    public void setupFlyoutStartingAsDot(Bubble.FlyoutMessage flyoutMessage, PointF pointF, float f, boolean z, int i, Runnable runnable, Runnable runnable2, float[] fArr, boolean z2, BubblePositioner bubblePositioner) {
        int bubbleSize = bubblePositioner.getBubbleSize();
        this.mBubbleSize = bubbleSize;
        float f2 = ((float) bubbleSize) * 0.228f;
        this.mOriginalDotSize = f2;
        float f3 = (f2 * 1.0f) / 2.0f;
        this.mNewDotRadius = f3;
        this.mNewDotSize = f3 * 2.0f;
        updateFlyoutMessage(flyoutMessage, f);
        this.mArrowPointingLeft = z;
        this.mDotColor = i;
        this.mOnHide = runnable2;
        this.mDotCenter = fArr;
        setCollapsePercent(1.0f);
        post(new BubbleFlyoutView$$ExternalSyntheticLambda1(this, pointF, z2, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupFlyoutStartingAsDot$3(PointF pointF, boolean z, Runnable runnable) {
        float height = pointF.y + (((float) (this.mBubbleSize - this.mFlyoutTextContainer.getHeight())) / 2.0f);
        this.mFlyoutY = height;
        setTranslationY(height);
        updateFlyoutX(pointF.x);
        updateDot(pointF, z);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public void hideFlyout() {
        Runnable runnable = this.mOnHide;
        if (runnable != null) {
            runnable.run();
            this.mOnHide = null;
        }
        setVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public void setCollapsePercent(float f) {
        if (!Float.isNaN(f)) {
            float max = Math.max(0.0f, Math.min(f, 1.0f));
            this.mPercentTransitionedToDot = max;
            this.mPercentStillFlyout = 1.0f - max;
            float width = max * ((float) (this.mArrowPointingLeft ? -getWidth() : getWidth()));
            float clampPercentage = clampPercentage((this.mPercentStillFlyout - 0.75f) / 0.25f);
            this.mMessageText.setTranslationX(width);
            this.mMessageText.setAlpha(clampPercentage);
            this.mSenderText.setTranslationX(width);
            this.mSenderText.setAlpha(clampPercentage);
            this.mSenderAvatar.setTranslationX(width);
            this.mSenderAvatar.setAlpha(clampPercentage);
            int i = this.mFlyoutElevation;
            setTranslationZ(((float) i) - (((float) (i - this.mBubbleElevation)) * this.mPercentTransitionedToDot));
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public float getRestingTranslationX() {
        return this.mRestingTranslationX;
    }

    private float clampPercentage(float f) {
        return Math.min(1.0f, Math.max(0.0f, f));
    }

    private void renderBackground(Canvas canvas) {
        float width = ((float) getWidth()) - (this.mFlyoutToDotWidthDelta * this.mPercentTransitionedToDot);
        float height = ((float) getHeight()) - (this.mFlyoutToDotHeightDelta * this.mPercentTransitionedToDot);
        float interpolatedRadius = getInterpolatedRadius();
        float f = this.mTranslationXWhenDot;
        float f2 = this.mPercentTransitionedToDot;
        this.mBgTranslationX = f * f2;
        this.mBgTranslationY = this.mTranslationYWhenDot * f2;
        RectF rectF = this.mBgRect;
        int i = this.mPointerSize;
        float f3 = this.mPercentStillFlyout;
        rectF.set(((float) i) * f3, 0.0f, width - (((float) i) * f3), height);
        this.mBgPaint.setColor(((Integer) this.mArgbEvaluator.evaluate(this.mPercentTransitionedToDot, Integer.valueOf(this.mFloatingBackgroundColor), Integer.valueOf(this.mDotColor))).intValue());
        canvas.save();
        canvas.translate(this.mBgTranslationX, this.mBgTranslationY);
        renderPointerTriangle(canvas, width, height);
        canvas.drawRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, this.mBgPaint);
        canvas.restore();
    }

    /* access modifiers changed from: private */
    public void getOutline(Outline outline) {
        this.mTriangleOutline.isEmpty();
        Path path = new Path();
        float interpolatedRadius = getInterpolatedRadius();
        path.addRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, Path.Direction.CW);
        outline.setPath(path);
        Matrix matrix = new Matrix();
        matrix.postTranslate(((float) getLeft()) + this.mBgTranslationX, ((float) getTop()) + this.mBgTranslationY);
        float f = this.mPercentTransitionedToDot;
        if (f > 0.98f) {
            float f2 = (f - 0.98f) / 0.02f;
            float f3 = 1.0f - f2;
            float f4 = this.mNewDotRadius;
            matrix.postTranslate(f4 * f2, f4 * f2);
            matrix.preScale(f3, f3);
        }
        outline.mPath.transform(matrix);
    }

    private float getInterpolatedRadius() {
        float f = this.mNewDotRadius;
        float f2 = this.mPercentTransitionedToDot;
        return (f * f2) + (this.mCornerRadius * (1.0f - f2));
    }
}
