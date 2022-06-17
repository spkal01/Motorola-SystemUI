package com.android.p011wm.shell.bubbles;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.PathParser;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import com.android.launcher3.icons.DotRenderer;
import com.android.launcher3.icons.IconNormalizer;
import com.android.p011wm.shell.animation.Interpolators;
import java.util.EnumSet;

/* renamed from: com.android.wm.shell.bubbles.BadgedImageView */
public class BadgedImageView extends ImageView {
    private float mAnimatingToDotScale;
    private BubbleViewProvider mBubble;
    private int mDotColor;
    private boolean mDotIsAnimating;
    private DotRenderer mDotRenderer;
    private float mDotScale;
    private final EnumSet<SuppressionFlag> mDotSuppressionFlags;
    private DotRenderer.DrawParams mDrawParams;
    private boolean mOnLeft;
    private Paint mPaint;
    private BubblePositioner mPositioner;
    private Rect mTempBounds;

    /* renamed from: com.android.wm.shell.bubbles.BadgedImageView$SuppressionFlag */
    enum SuppressionFlag {
        FLYOUT_VISIBLE,
        BEHIND_STACK
    }

    public BadgedImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mDotSuppressionFlags = EnumSet.of(SuppressionFlag.FLYOUT_VISIBLE);
        this.mDotScale = 0.0f;
        this.mAnimatingToDotScale = 0.0f;
        this.mDotIsAnimating = false;
        this.mPaint = new Paint(1);
        this.mTempBounds = new Rect();
        this.mDrawParams = new DotRenderer.DrawParams();
        setFocusable(true);
        setClickable(true);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                BadgedImageView.this.getOutline(outline);
            }
        });
    }

    /* access modifiers changed from: private */
    public void getOutline(Outline outline) {
        int bubbleSize = this.mPositioner.getBubbleSize();
        int normalizedCircleSize = IconNormalizer.getNormalizedCircleSize(bubbleSize);
        int i = (bubbleSize - normalizedCircleSize) / 2;
        int i2 = normalizedCircleSize + i;
        outline.setOval(i, i, i2, i2);
    }

    public void initialize(BubblePositioner bubblePositioner) {
        this.mPositioner = bubblePositioner;
        this.mDotRenderer = new DotRenderer(this.mPositioner.getBubbleSize(), PathParser.createPathFromPathData(getResources().getString(17039967)), 100);
    }

    public void showDotAndBadge(boolean z) {
        removeDotSuppressionFlag(SuppressionFlag.BEHIND_STACK);
        animateDotBadgePositions(z);
    }

    public void hideDotAndBadge(boolean z) {
        addDotSuppressionFlag(SuppressionFlag.BEHIND_STACK);
        this.mOnLeft = z;
        hideBadge();
    }

    public void setRenderedBubble(BubbleViewProvider bubbleViewProvider) {
        this.mBubble = bubbleViewProvider;
        if (this.mDotSuppressionFlags.contains(SuppressionFlag.BEHIND_STACK)) {
            hideBadge();
        } else {
            showBadge();
        }
        this.mDotColor = bubbleViewProvider.getDotColor();
        drawDot(bubbleViewProvider.getDotPath());
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldDrawDot()) {
            getDrawingRect(this.mTempBounds);
            DotRenderer.DrawParams drawParams = this.mDrawParams;
            drawParams.color = this.mDotColor;
            drawParams.iconBounds = this.mTempBounds;
            drawParams.leftAlign = this.mOnLeft;
            drawParams.scale = this.mDotScale;
            this.mDotRenderer.draw(canvas, drawParams);
        }
    }

    /* access modifiers changed from: package-private */
    public void addDotSuppressionFlag(SuppressionFlag suppressionFlag) {
        if (this.mDotSuppressionFlags.add(suppressionFlag)) {
            updateDotVisibility(suppressionFlag == SuppressionFlag.BEHIND_STACK);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeDotSuppressionFlag(SuppressionFlag suppressionFlag) {
        if (this.mDotSuppressionFlags.remove(suppressionFlag)) {
            updateDotVisibility(suppressionFlag == SuppressionFlag.BEHIND_STACK);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateDotVisibility(boolean z) {
        float f = shouldDrawDot() ? 1.0f : 0.0f;
        if (z) {
            animateDotScale(f, (Runnable) null);
            return;
        }
        this.mDotScale = f;
        this.mAnimatingToDotScale = f;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void drawDot(Path path) {
        this.mDotRenderer = new DotRenderer(this.mPositioner.getBubbleSize(), path, 100);
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void setDotScale(float f) {
        this.mDotScale = f;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public boolean getDotOnLeft() {
        return this.mOnLeft;
    }

    /* access modifiers changed from: package-private */
    public float[] getDotCenter() {
        float[] fArr;
        if (this.mOnLeft) {
            fArr = this.mDotRenderer.getLeftDotPosition();
        } else {
            fArr = this.mDotRenderer.getRightDotPosition();
        }
        getDrawingRect(this.mTempBounds);
        return new float[]{((float) this.mTempBounds.width()) * fArr[0], ((float) this.mTempBounds.height()) * fArr[1]};
    }

    public String getKey() {
        BubbleViewProvider bubbleViewProvider = this.mBubble;
        if (bubbleViewProvider != null) {
            return bubbleViewProvider.getKey();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getDotColor() {
        return this.mDotColor;
    }

    /* access modifiers changed from: package-private */
    public void animateDotBadgePositions(boolean z) {
        this.mOnLeft = z;
        if (z != getDotOnLeft() && shouldDrawDot()) {
            animateDotScale(0.0f, new BadgedImageView$$ExternalSyntheticLambda1(this));
        }
        showBadge();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateDotBadgePositions$0() {
        invalidate();
        animateDotScale(1.0f, (Runnable) null);
    }

    /* access modifiers changed from: package-private */
    public void setDotBadgeOnLeft(boolean z) {
        this.mOnLeft = z;
        invalidate();
        showBadge();
    }

    private boolean shouldDrawDot() {
        return this.mDotIsAnimating || (this.mBubble.showDot() && this.mDotSuppressionFlags.isEmpty());
    }

    private void animateDotScale(float f, Runnable runnable) {
        boolean z = true;
        this.mDotIsAnimating = true;
        if (this.mAnimatingToDotScale == f || !shouldDrawDot()) {
            this.mDotIsAnimating = false;
            return;
        }
        this.mAnimatingToDotScale = f;
        if (f <= 0.0f) {
            z = false;
        }
        clearAnimation();
        animate().setDuration(200).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setUpdateListener(new BadgedImageView$$ExternalSyntheticLambda0(this, z)).withEndAction(new BadgedImageView$$ExternalSyntheticLambda2(this, z, runnable)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateDotScale$1(boolean z, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (!z) {
            animatedFraction = 1.0f - animatedFraction;
        }
        setDotScale(animatedFraction);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateDotScale$2(boolean z, Runnable runnable) {
        setDotScale(z ? 1.0f : 0.0f);
        this.mDotIsAnimating = false;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public void showBadge() {
        Bitmap appBadge = this.mBubble.getAppBadge();
        if (appBadge == null) {
            setImageBitmap(this.mBubble.getBubbleIcon());
            return;
        }
        Canvas canvas = new Canvas();
        Bitmap bubbleIcon = this.mBubble.getBubbleIcon();
        Bitmap copy = bubbleIcon.copy(bubbleIcon.getConfig(), true);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        canvas.setBitmap(copy);
        int width = copy.getWidth();
        int i = (int) (((float) width) * 0.444f);
        Rect rect = new Rect();
        if (this.mOnLeft) {
            rect.set(0, width - i, i, width);
        } else {
            int i2 = width - i;
            rect.set(i2, i2, width, width);
        }
        canvas.drawBitmap(appBadge, (Rect) null, rect, this.mPaint);
        canvas.setBitmap((Bitmap) null);
        setImageBitmap(copy);
    }

    /* access modifiers changed from: package-private */
    public void hideBadge() {
        setImageBitmap(this.mBubble.getBubbleIcon());
    }
}
