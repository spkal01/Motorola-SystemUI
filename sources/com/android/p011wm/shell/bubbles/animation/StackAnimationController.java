package com.android.p011wm.shell.bubbles.animation;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import com.android.p011wm.shell.bubbles.BadgedImageView;
import com.android.p011wm.shell.bubbles.BubblePositioner;
import com.android.p011wm.shell.bubbles.BubbleStackView;
import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import com.android.p011wm.shell.common.magnetictarget.MagnetizedObject;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.IntSupplier;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController */
public class StackAnimationController extends PhysicsAnimationLayout.PhysicsAnimationController {
    private final PhysicsAnimator.SpringConfig mAnimateOutSpringConfig = new PhysicsAnimator.SpringConfig(700.0f, 1.0f);
    /* access modifiers changed from: private */
    public Rect mAnimatingToBounds = new Rect();
    private IntSupplier mBubbleCountSupplier;
    private int mBubbleOffscreen;
    /* access modifiers changed from: private */
    public int mBubblePaddingTop;
    /* access modifiers changed from: private */
    public int mBubbleSize;
    private int mElevation;
    private boolean mFirstBubbleSpringingToTouch = false;
    private FloatingContentCoordinator mFloatingContentCoordinator;
    private float mImeHeight = 0.0f;
    private boolean mIsMovingFromFlinging = false;
    private MagnetizedObject<StackAnimationController> mMagnetizedStack;
    private int mMaxBubbles;
    private Runnable mOnBubbleAnimatedOutAction;
    private Runnable mOnStackAnimationFinished;
    private BubblePositioner mPositioner;
    private float mPreImeY = -1.4E-45f;
    private boolean mSpringToTouchOnNextMotionEvent = false;
    private final FloatingContentCoordinator.FloatingContent mStackFloatingContent = new FloatingContentCoordinator.FloatingContent() {
        private final Rect mFloatingBoundsOnScreen = new Rect();

        public void moveToBounds(Rect rect) {
            StackAnimationController.this.springStack((float) rect.left, (float) rect.top, 700.0f);
        }

        public Rect getAllowedFloatingBoundsRegion() {
            Rect floatingBoundsOnScreen = getFloatingBoundsOnScreen();
            Rect rect = new Rect();
            StackAnimationController.this.getAllowableStackPositionRegion().roundOut(rect);
            rect.right += floatingBoundsOnScreen.width();
            rect.bottom += floatingBoundsOnScreen.height();
            return rect;
        }

        public Rect getFloatingBoundsOnScreen() {
            if (!StackAnimationController.this.mAnimatingToBounds.isEmpty()) {
                return StackAnimationController.this.mAnimatingToBounds;
            }
            if (StackAnimationController.this.mLayout.getChildCount() > 0) {
                this.mFloatingBoundsOnScreen.set((int) StackAnimationController.this.mStackPosition.x, (int) StackAnimationController.this.mStackPosition.y, ((int) StackAnimationController.this.mStackPosition.x) + StackAnimationController.this.mBubbleSize, ((int) StackAnimationController.this.mStackPosition.y) + StackAnimationController.this.mBubbleSize + StackAnimationController.this.mBubblePaddingTop);
            } else {
                this.mFloatingBoundsOnScreen.setEmpty();
            }
            return this.mFloatingBoundsOnScreen;
        }
    };
    private boolean mStackMovedToStartPosition = false;
    private float mStackOffset;
    /* access modifiers changed from: private */
    public PointF mStackPosition = new PointF(-1.0f, -1.0f);
    private HashMap<DynamicAnimation.ViewProperty, DynamicAnimation> mStackPositionAnimations = new HashMap<>();
    private float mSwapAnimationOffset;

    /* access modifiers changed from: package-private */
    public void onChildReordered(View view, int i, int i2) {
    }

    public StackAnimationController(FloatingContentCoordinator floatingContentCoordinator, IntSupplier intSupplier, Runnable runnable, Runnable runnable2, BubblePositioner bubblePositioner) {
        this.mFloatingContentCoordinator = floatingContentCoordinator;
        this.mBubbleCountSupplier = intSupplier;
        this.mOnBubbleAnimatedOutAction = runnable;
        this.mOnStackAnimationFinished = runnable2;
        this.mPositioner = bubblePositioner;
    }

    public void moveFirstBubbleWithStackFollowing(float f, float f2) {
        this.mAnimatingToBounds.setEmpty();
        this.mPreImeY = -1.4E-45f;
        moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, f);
        moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, f2);
        this.mIsMovingFromFlinging = false;
    }

    public PointF getStackPosition() {
        return this.mStackPosition;
    }

    public boolean isStackOnLeftSide() {
        if (this.mLayout == null || !isStackPositionSet() || this.mStackPosition.x + ((float) (this.mBubbleSize / 2)) < ((float) (this.mLayout.getWidth() / 2))) {
            return true;
        }
        return false;
    }

    public void springStack(float f, float f2, float f3) {
        float f4 = f3;
        notifyFloatingCoordinatorStackAnimatingTo(f, f2);
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, new SpringForce().setStiffness(f4).setDampingRatio(0.85f), 0.0f, f, new Runnable[0]);
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, new SpringForce().setStiffness(f4).setDampingRatio(0.85f), 0.0f, f2, new Runnable[0]);
    }

    public void springStackAfterFling(float f, float f2) {
        springStack(f, f2, 700.0f);
    }

    public float flingStackThenSpringToEdge(float f, float f2, float f3) {
        float f4;
        float f5 = f2;
        boolean z = !(((f - ((float) (this.mBubbleSize / 2))) > ((float) (this.mLayout.getWidth() / 2)) ? 1 : ((f - ((float) (this.mBubbleSize / 2))) == ((float) (this.mLayout.getWidth() / 2)) ? 0 : -1)) < 0) ? f5 < -750.0f : f5 < 750.0f;
        RectF allowableStackPositionRegion = getAllowableStackPositionRegion();
        float f6 = z ? allowableStackPositionRegion.left : allowableStackPositionRegion.right;
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        if (!(physicsAnimationLayout == null || physicsAnimationLayout.getChildCount() == 0)) {
            ContentResolver contentResolver = this.mLayout.getContext().getContentResolver();
            float f7 = Settings.Secure.getFloat(contentResolver, "bubble_stiffness", 700.0f);
            float f8 = Settings.Secure.getFloat(contentResolver, "bubble_damping", 0.85f);
            float f9 = Settings.Secure.getFloat(contentResolver, "bubble_friction", 1.9f);
            float f10 = (f6 - f) * 4.2f * f9;
            notifyFloatingCoordinatorStackAnimatingTo(f6, PhysicsAnimator.estimateFlingEndValue(this.mStackPosition.y, f3, new PhysicsAnimator.FlingConfig(f9, allowableStackPositionRegion.top, allowableStackPositionRegion.bottom)));
            if (z) {
                f4 = Math.min(f10, f5);
            } else {
                f4 = Math.max(f10, f5);
            }
            float f11 = f9;
            flingThenSpringFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, f4, f11, new SpringForce().setStiffness(f7).setDampingRatio(f8), Float.valueOf(f6));
            flingThenSpringFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, f3, f11, new SpringForce().setStiffness(f7).setDampingRatio(f8), (Float) null);
            this.mFirstBubbleSpringingToTouch = false;
            this.mIsMovingFromFlinging = true;
        }
        return f6;
    }

    public PointF getStackPositionAlongNearestHorizontalEdge() {
        if (this.mPositioner.showingInTaskbar()) {
            return this.mPositioner.getRestingPosition();
        }
        PointF stackPosition = getStackPosition();
        boolean isFirstChildXLeftOfCenter = this.mLayout.isFirstChildXLeftOfCenter(stackPosition.x);
        RectF allowableStackPositionRegion = getAllowableStackPositionRegion();
        stackPosition.x = isFirstChildXLeftOfCenter ? allowableStackPositionRegion.left : allowableStackPositionRegion.right;
        return stackPosition;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StackAnimationController state:");
        printWriter.print("  isActive:             ");
        printWriter.println(isActiveController());
        printWriter.print("  restingStackPos:      ");
        printWriter.println(this.mPositioner.getRestingPosition().toString());
        printWriter.print("  currentStackPos:      ");
        printWriter.println(this.mStackPosition.toString());
        printWriter.print("  isMovingFromFlinging: ");
        printWriter.println(this.mIsMovingFromFlinging);
        printWriter.print("  withinDismiss:        ");
        printWriter.println(isStackStuckToTarget());
        printWriter.print("  firstBubbleSpringing: ");
        printWriter.println(this.mFirstBubbleSpringingToTouch);
    }

    /* access modifiers changed from: protected */
    public void flingThenSpringFirstBubbleWithStackFollowing(DynamicAnimation.ViewProperty viewProperty, float f, float f2, SpringForce springForce, Float f3) {
        float f4;
        float f5;
        DynamicAnimation.ViewProperty viewProperty2 = viewProperty;
        if (isActiveController()) {
            Log.d("Bubbs.StackCtrl", String.format("Flinging %s.", new Object[]{PhysicsAnimationLayout.getReadablePropertyName(viewProperty)}));
            StackPositionProperty stackPositionProperty = new StackPositionProperty(viewProperty);
            float value = stackPositionProperty.getValue(this);
            RectF allowableStackPositionRegion = getAllowableStackPositionRegion();
            DynamicAnimation.ViewProperty viewProperty3 = DynamicAnimation.TRANSLATION_X;
            if (viewProperty.equals(viewProperty3)) {
                f4 = allowableStackPositionRegion.left;
            } else {
                f4 = allowableStackPositionRegion.top;
            }
            float f6 = f4;
            if (viewProperty.equals(viewProperty3)) {
                f5 = allowableStackPositionRegion.right;
            } else {
                f5 = allowableStackPositionRegion.bottom;
            }
            float f7 = f5;
            FlingAnimation flingAnimation = new FlingAnimation(this, stackPositionProperty);
            float f8 = f2;
            float f9 = f;
            flingAnimation.setFriction(f2).setStartVelocity(f).setMinValue(Math.min(value, f6)).setMaxValue(Math.max(value, f7)).addEndListener(new StackAnimationController$$ExternalSyntheticLambda0(this, viewProperty, springForce, f3, f6, f7));
            cancelStackPositionAnimation(viewProperty);
            this.mStackPositionAnimations.put(viewProperty, flingAnimation);
            flingAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$flingThenSpringFirstBubbleWithStackFollowing$0(DynamicAnimation.ViewProperty viewProperty, SpringForce springForce, Float f, float f2, float f3, DynamicAnimation dynamicAnimation, boolean z, float f4, float f5) {
        float f6;
        if (!z) {
            this.mPositioner.setRestingPosition(this.mStackPosition);
            if (f != null) {
                f6 = f.floatValue();
            } else {
                f6 = Math.max(f2, Math.min(f3, f4));
            }
            springFirstBubbleWithStackFollowing(viewProperty, springForce, f5, f6, new Runnable[0]);
        }
    }

    public void cancelStackPositionAnimations() {
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
        cancelStackPositionAnimation(viewProperty);
        DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
        cancelStackPositionAnimation(viewProperty2);
        removeEndActionForProperty(viewProperty);
        removeEndActionForProperty(viewProperty2);
    }

    public void setImeHeight(int i) {
        this.mImeHeight = (float) i;
    }

    public float getImeHeight() {
        return this.mImeHeight;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float animateForImeVisibility(boolean r9) {
        /*
            r8 = this;
            android.graphics.RectF r0 = r8.getAllowableStackPositionRegion()
            float r0 = r0.bottom
            r1 = -2147483647(0xffffffff80000001, float:-1.4E-45)
            if (r9 == 0) goto L_0x001c
            android.graphics.PointF r9 = r8.mStackPosition
            float r9 = r9.y
            int r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r2 <= 0) goto L_0x0025
            float r2 = r8.mPreImeY
            int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r2 != 0) goto L_0x0025
            r8.mPreImeY = r9
            goto L_0x0026
        L_0x001c:
            float r0 = r8.mPreImeY
            int r9 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0025
            r8.mPreImeY = r1
            goto L_0x0026
        L_0x0025:
            r0 = r1
        L_0x0026:
            int r9 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0047
            androidx.dynamicanimation.animation.DynamicAnimation$ViewProperty r3 = androidx.dynamicanimation.animation.DynamicAnimation.TRANSLATION_Y
            r1 = 0
            androidx.dynamicanimation.animation.SpringForce r1 = r8.getSpringForce(r3, r1)
            r2 = 1128792064(0x43480000, float:200.0)
            androidx.dynamicanimation.animation.SpringForce r4 = r1.setStiffness(r2)
            r5 = 0
            r1 = 0
            java.lang.Runnable[] r7 = new java.lang.Runnable[r1]
            r2 = r8
            r6 = r0
            r2.springFirstBubbleWithStackFollowing(r3, r4, r5, r6, r7)
            android.graphics.PointF r1 = r8.mStackPosition
            float r1 = r1.x
            r8.notifyFloatingCoordinatorStackAnimatingTo(r1, r0)
        L_0x0047:
            if (r9 == 0) goto L_0x004a
            goto L_0x004e
        L_0x004a:
            android.graphics.PointF r8 = r8.mStackPosition
            float r0 = r8.y
        L_0x004e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.bubbles.animation.StackAnimationController.animateForImeVisibility(boolean):float");
    }

    private void notifyFloatingCoordinatorStackAnimatingTo(float f, float f2) {
        Rect floatingBoundsOnScreen = this.mStackFloatingContent.getFloatingBoundsOnScreen();
        floatingBoundsOnScreen.offsetTo((int) f, (int) f2);
        this.mAnimatingToBounds = floatingBoundsOnScreen;
        this.mFloatingContentCoordinator.onContentMoved(this.mStackFloatingContent);
    }

    public RectF getAllowableStackPositionRegion() {
        RectF rectF = new RectF(this.mPositioner.getAvailableRect());
        float f = rectF.left;
        int i = this.mBubbleOffscreen;
        rectF.left = f - ((float) i);
        float f2 = rectF.top;
        int i2 = this.mBubblePaddingTop;
        rectF.top = f2 + ((float) i2);
        float f3 = rectF.right;
        int i3 = this.mBubbleSize;
        rectF.right = f3 + ((float) (i - i3));
        float f4 = rectF.bottom;
        float f5 = (float) (i3 + i2);
        float f6 = this.mImeHeight;
        rectF.bottom = f4 - (f5 + (f6 != -1.4E-45f ? f6 + ((float) i2) : 0.0f));
        return rectF;
    }

    public void moveStackFromTouch(float f, float f2) {
        if (this.mSpringToTouchOnNextMotionEvent) {
            springStack(f, f2, 12000.0f);
            this.mSpringToTouchOnNextMotionEvent = false;
            this.mFirstBubbleSpringingToTouch = true;
        } else if (this.mFirstBubbleSpringingToTouch) {
            SpringAnimation springAnimation = (SpringAnimation) this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_X);
            SpringAnimation springAnimation2 = (SpringAnimation) this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_Y);
            if (springAnimation.isRunning() || springAnimation2.isRunning()) {
                springAnimation.animateToFinalPosition(f);
                springAnimation2.animateToFinalPosition(f2);
            } else {
                this.mFirstBubbleSpringingToTouch = false;
            }
        }
        if (!this.mFirstBubbleSpringingToTouch && !isStackStuckToTarget()) {
            moveFirstBubbleWithStackFollowing(f, f2);
        }
    }

    public void onUnstuckFromTarget() {
        this.mSpringToTouchOnNextMotionEvent = true;
    }

    public void animateStackDismissal(float f, Runnable runnable) {
        animationsForChildrenFromIndex(0, new StackAnimationController$$ExternalSyntheticLambda2(this, f)).startAll(runnable);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateStackDismissal$1(float f, int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
        physicsPropertyAnimator.scaleX(0.0f, new Runnable[0]).scaleY(0.0f, new Runnable[0]).alpha(0.0f, new Runnable[0]).translationY(this.mLayout.getChildAt(i).getTranslationY() + f, new Runnable[0]).withStiffness(10000.0f);
    }

    /* access modifiers changed from: protected */
    public void springFirstBubbleWithStackFollowing(DynamicAnimation.ViewProperty viewProperty, SpringForce springForce, float f, float f2, Runnable... runnableArr) {
        if (this.mLayout.getChildCount() != 0 && isActiveController()) {
            Log.d("Bubbs.StackCtrl", String.format("Springing %s to final position %f.", new Object[]{PhysicsAnimationLayout.getReadablePropertyName(viewProperty), Float.valueOf(f2)}));
            SpringAnimation springAnimation = (SpringAnimation) ((SpringAnimation) new SpringAnimation(this, new StackPositionProperty(viewProperty)).setSpring(springForce).addEndListener(new StackAnimationController$$ExternalSyntheticLambda1(this, this.mSpringToTouchOnNextMotionEvent, runnableArr))).setStartVelocity(f);
            cancelStackPositionAnimation(viewProperty);
            this.mStackPositionAnimations.put(viewProperty, springAnimation);
            springAnimation.animateToFinalPosition(f2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$springFirstBubbleWithStackFollowing$2(boolean z, Runnable[] runnableArr, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (!z) {
            this.mPositioner.setRestingPosition(this.mStackPosition);
        }
        Runnable runnable = this.mOnStackAnimationFinished;
        if (runnable != null) {
            runnable.run();
        }
        if (runnableArr != null) {
            for (Runnable run : runnableArr) {
                run.run();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
        return Sets.newHashSet(new DynamicAnimation.ViewProperty[]{DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.ALPHA, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y});
    }

    /* access modifiers changed from: package-private */
    public int getNextAnimationInChain(DynamicAnimation.ViewProperty viewProperty, int i) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X) || viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return i + 1;
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public float getOffsetForChainedPropertyAnimation(DynamicAnimation.ViewProperty viewProperty, int i) {
        if (!viewProperty.equals(DynamicAnimation.TRANSLATION_Y) || isStackStuckToTarget() || i > 1) {
            return 0.0f;
        }
        return this.mStackOffset;
    }

    /* access modifiers changed from: package-private */
    public SpringForce getSpringForce(DynamicAnimation.ViewProperty viewProperty, View view) {
        return new SpringForce().setDampingRatio(Settings.Secure.getFloat(this.mLayout.getContext().getContentResolver(), "bubble_damping", 0.9f)).setStiffness(800.0f);
    }

    /* access modifiers changed from: package-private */
    public void onChildAdded(View view, int i) {
        if (!isStackStuckToTarget()) {
            if (getBubbleCount() == 1) {
                moveStackToStartPosition();
            } else if (isStackPositionSet() && this.mLayout.indexOfChild(view) == 0) {
                animateInBubble(view, i);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onChildRemoved(View view, int i, Runnable runnable) {
        PhysicsAnimator.getInstance(view).spring(DynamicAnimation.ALPHA, 0.0f).spring(DynamicAnimation.SCALE_X, 0.0f, this.mAnimateOutSpringConfig).spring(DynamicAnimation.SCALE_Y, 0.0f, this.mAnimateOutSpringConfig).withEndActions(runnable, this.mOnBubbleAnimatedOutAction).start();
        if (getBubbleCount() > 0) {
            animationForChildAtIndex(0).translationX(this.mStackPosition.x, new Runnable[0]).start(new Runnable[0]);
            return;
        }
        BubblePositioner bubblePositioner = this.mPositioner;
        bubblePositioner.setRestingPosition(bubblePositioner.getRestingPosition());
        this.mFloatingContentCoordinator.onContentRemoved(this.mStackFloatingContent);
    }

    public void animateReorder(List<View> list, Runnable runnable) {
        StackAnimationController$$ExternalSyntheticLambda7 stackAnimationController$$ExternalSyntheticLambda7 = new StackAnimationController$$ExternalSyntheticLambda7(this, list);
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            animateSwap(view, this.mLayout.indexOfChild(view), i, stackAnimationController$$ExternalSyntheticLambda7, runnable);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateReorder$3(List list) {
        for (int i = 0; i < list.size(); i++) {
            updateBadgesAndZOrder((View) list.get(i), i);
        }
    }

    private void animateSwap(View view, int i, int i2, Runnable runnable, Runnable runnable2) {
        if (i2 == i) {
            updateBadgesAndZOrder(view, i2);
            if (i2 == 0) {
                animateInBubble(view, i2);
            } else {
                moveToFinalIndex(view, i2, runnable2);
            }
        } else if (i2 == 0) {
            animateToFrontThenUpdateIcons(view, runnable, runnable2);
        } else {
            moveToFinalIndex(view, i2, runnable2);
        }
    }

    private void animateToFrontThenUpdateIcons(View view, Runnable runnable, Runnable runnable2) {
        view.setTag(C2219R.C2222id.reorder_animator_tag, view.animate().translationY(getStackPosition().y - this.mSwapAnimationOffset).setDuration(300).withEndAction(new StackAnimationController$$ExternalSyntheticLambda6(this, runnable, view, runnable2)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToFrontThenUpdateIcons$4(Runnable runnable, View view, Runnable runnable2) {
        runnable.run();
        moveToFinalIndex(view, 0, runnable2);
    }

    private void moveToFinalIndex(View view, int i, Runnable runnable) {
        view.setTag(C2219R.C2222id.reorder_animator_tag, view.animate().translationY(getStackPosition().y + (((float) Math.min(i, 1)) * this.mStackOffset)).setDuration(300).withEndAction(new StackAnimationController$$ExternalSyntheticLambda4(view, runnable)));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$moveToFinalIndex$5(View view, Runnable runnable) {
        view.setTag(C2219R.C2222id.reorder_animator_tag, (Object) null);
        runnable.run();
    }

    private void updateBadgesAndZOrder(View view, int i) {
        view.setZ(i < 2 ? (float) ((this.mMaxBubbles * this.mElevation) - i) : 0.0f);
        BadgedImageView badgedImageView = (BadgedImageView) view;
        if (i == 0) {
            badgedImageView.showDotAndBadge(!isStackOnLeftSide());
        } else {
            badgedImageView.hideDotAndBadge(!isStackOnLeftSide());
        }
    }

    /* access modifiers changed from: package-private */
    public void onActiveControllerForLayout(PhysicsAnimationLayout physicsAnimationLayout) {
        Resources resources = physicsAnimationLayout.getResources();
        this.mStackOffset = (float) resources.getDimensionPixelSize(C2219R.dimen.bubble_stack_offset);
        this.mSwapAnimationOffset = (float) resources.getDimensionPixelSize(C2219R.dimen.bubble_swap_animation_offset);
        this.mMaxBubbles = resources.getInteger(C2219R.integer.bubbles_max_rendered);
        this.mElevation = resources.getDimensionPixelSize(C2219R.dimen.bubble_elevation);
        this.mBubbleSize = this.mPositioner.getBubbleSize();
        this.mBubblePaddingTop = resources.getDimensionPixelSize(C2219R.dimen.bubble_padding_top);
        this.mBubbleOffscreen = resources.getDimensionPixelSize(C2219R.dimen.bubble_stack_offscreen);
    }

    public void updateResources() {
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        if (physicsAnimationLayout != null) {
            this.mBubblePaddingTop = physicsAnimationLayout.getContext().getResources().getDimensionPixelSize(C2219R.dimen.bubble_padding_top);
        }
    }

    private boolean isStackStuckToTarget() {
        MagnetizedObject<StackAnimationController> magnetizedObject = this.mMagnetizedStack;
        return magnetizedObject != null && magnetizedObject.getObjectStuckToTarget();
    }

    private void moveStackToStartPosition() {
        this.mLayout.setVisibility(4);
        this.mLayout.post(new StackAnimationController$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$moveStackToStartPosition$6() {
        setStackPosition(this.mPositioner.getRestingPosition());
        this.mStackMovedToStartPosition = true;
        this.mLayout.setVisibility(0);
        if (this.mLayout.getChildCount() > 0) {
            this.mFloatingContentCoordinator.onContentAdded(this.mStackFloatingContent);
            animateInBubble(this.mLayout.getChildAt(0), 0);
        }
    }

    /* access modifiers changed from: private */
    public void moveFirstBubbleWithStackFollowing(DynamicAnimation.ViewProperty viewProperty, float f) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            this.mStackPosition.x = f;
        } else if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            this.mStackPosition.y = f;
        }
        if (this.mLayout.getChildCount() > 0) {
            viewProperty.setValue(this.mLayout.getChildAt(0), f);
            if (this.mLayout.getChildCount() > 1) {
                animationForChildAtIndex(1).property(viewProperty, f + getOffsetForChainedPropertyAnimation(viewProperty, 0), new Runnable[0]).start(new Runnable[0]);
            }
        }
    }

    public void setStackPosition(PointF pointF) {
        Log.d("Bubbs.StackCtrl", String.format("Setting position to (%f, %f).", new Object[]{Float.valueOf(pointF.x), Float.valueOf(pointF.y)}));
        this.mStackPosition.set(pointF.x, pointF.y);
        this.mPositioner.setRestingPosition(this.mStackPosition);
        if (isActiveController()) {
            PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
            DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
            DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
            physicsAnimationLayout.cancelAllAnimationsOfProperties(viewProperty, viewProperty2);
            cancelStackPositionAnimations();
            float offsetForChainedPropertyAnimation = getOffsetForChainedPropertyAnimation(viewProperty, 0);
            float offsetForChainedPropertyAnimation2 = getOffsetForChainedPropertyAnimation(viewProperty2, 0);
            for (int i = 0; i < this.mLayout.getChildCount(); i++) {
                float min = (float) Math.min(i, 1);
                this.mLayout.getChildAt(i).setTranslationX(pointF.x + (min * offsetForChainedPropertyAnimation));
                this.mLayout.getChildAt(i).setTranslationY(pointF.y + (min * offsetForChainedPropertyAnimation2));
            }
        }
    }

    public void setStackPosition(BubbleStackView.RelativeStackPosition relativeStackPosition) {
        setStackPosition(relativeStackPosition.getAbsolutePositionInRegion(getAllowableStackPositionRegion()));
    }

    private boolean isStackPositionSet() {
        return this.mStackMovedToStartPosition;
    }

    private void animateInBubble(View view, int i) {
        if (isActiveController()) {
            float offsetForChainedPropertyAnimation = getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_Y, 0);
            PointF pointF = this.mStackPosition;
            float f = pointF.y + (offsetForChainedPropertyAnimation * ((float) i));
            float f2 = pointF.x;
            if (this.mPositioner.showBubblesVertically()) {
                view.setTranslationY(f);
                view.setTranslationX(isStackOnLeftSide() ? f2 - 100.0f : f2 + 100.0f);
            } else {
                view.setTranslationX(this.mStackPosition.x);
                view.setTranslationY(100.0f + f);
            }
            view.setScaleX(0.5f);
            view.setScaleY(0.5f);
            view.setAlpha(0.0f);
            ViewPropertyAnimator withEndAction = view.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(300).withEndAction(new StackAnimationController$$ExternalSyntheticLambda3(view));
            view.setTag(C2219R.C2222id.reorder_animator_tag, withEndAction);
            if (this.mPositioner.showBubblesVertically()) {
                withEndAction.translationX(f2);
            } else {
                withEndAction.translationY(f);
            }
        }
    }

    private void cancelStackPositionAnimation(DynamicAnimation.ViewProperty viewProperty) {
        if (this.mStackPositionAnimations.containsKey(viewProperty)) {
            this.mStackPositionAnimations.get(viewProperty).cancel();
        }
    }

    public MagnetizedObject<StackAnimationController> getMagnetizedStack() {
        if (this.mMagnetizedStack == null) {
            C22812 r1 = new MagnetizedObject<StackAnimationController>(this.mLayout.getContext(), this, new StackPositionProperty(DynamicAnimation.TRANSLATION_X), new StackPositionProperty(DynamicAnimation.TRANSLATION_Y)) {
                public float getWidth(StackAnimationController stackAnimationController) {
                    return (float) StackAnimationController.this.mBubbleSize;
                }

                public float getHeight(StackAnimationController stackAnimationController) {
                    return (float) StackAnimationController.this.mBubbleSize;
                }

                public void getLocationOnScreen(StackAnimationController stackAnimationController, int[] iArr) {
                    iArr[0] = (int) StackAnimationController.this.mStackPosition.x;
                    iArr[1] = (int) StackAnimationController.this.mStackPosition.y;
                }
            };
            this.mMagnetizedStack = r1;
            r1.setHapticsEnabled(true);
            this.mMagnetizedStack.setFlingToTargetMinVelocity(4000.0f);
        }
        ContentResolver contentResolver = this.mLayout.getContext().getContentResolver();
        float f = Settings.Secure.getFloat(contentResolver, "bubble_dismiss_fling_min_velocity", this.mMagnetizedStack.getFlingToTargetMinVelocity());
        float f2 = Settings.Secure.getFloat(contentResolver, "bubble_dismiss_stick_max_velocity", this.mMagnetizedStack.getStickToTargetMaxXVelocity());
        float f3 = Settings.Secure.getFloat(contentResolver, "bubble_dismiss_target_width_percent", this.mMagnetizedStack.getFlingToTargetWidthPercent());
        this.mMagnetizedStack.setFlingToTargetMinVelocity(f);
        this.mMagnetizedStack.setStickToTargetMaxXVelocity(f2);
        this.mMagnetizedStack.setFlingToTargetWidthPercent(f3);
        return this.mMagnetizedStack;
    }

    private int getBubbleCount() {
        return this.mBubbleCountSupplier.getAsInt();
    }

    /* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$StackPositionProperty */
    private class StackPositionProperty extends FloatPropertyCompat<StackAnimationController> {
        private final DynamicAnimation.ViewProperty mProperty;

        private StackPositionProperty(DynamicAnimation.ViewProperty viewProperty) {
            super(viewProperty.toString());
            this.mProperty = viewProperty;
        }

        public float getValue(StackAnimationController stackAnimationController) {
            if (StackAnimationController.this.mLayout.getChildCount() > 0) {
                return this.mProperty.getValue(StackAnimationController.this.mLayout.getChildAt(0));
            }
            return 0.0f;
        }

        public void setValue(StackAnimationController stackAnimationController, float f) {
            StackAnimationController.this.moveFirstBubbleWithStackFollowing(this.mProperty, f);
        }
    }
}
