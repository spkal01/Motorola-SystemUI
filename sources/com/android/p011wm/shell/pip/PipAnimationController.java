package com.android.p011wm.shell.pip;

import android.animation.AnimationHandler;
import android.animation.Animator;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.app.TaskInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.RotationUtils;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.SfVsyncFrameCallbackProvider;
import com.android.p011wm.shell.animation.Interpolators;
import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import java.util.Objects;

/* renamed from: com.android.wm.shell.pip.PipAnimationController */
public class PipAnimationController {
    private PipTransitionAnimator mCurrentAnimator;
    private final ThreadLocal<AnimationHandler> mSfAnimationHandlerThreadLocal = ThreadLocal.withInitial(PipAnimationController$$ExternalSyntheticLambda0.INSTANCE);
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;

    /* renamed from: com.android.wm.shell.pip.PipAnimationController$PipAnimationCallback */
    public static class PipAnimationCallback {
        public void onPipAnimationCancel(TaskInfo taskInfo, PipTransitionAnimator pipTransitionAnimator) {
            throw null;
        }

        public void onPipAnimationEnd(TaskInfo taskInfo, SurfaceControl.Transaction transaction, PipTransitionAnimator pipTransitionAnimator) {
            throw null;
        }

        public void onPipAnimationStart(TaskInfo taskInfo, PipTransitionAnimator pipTransitionAnimator) {
            throw null;
        }
    }

    /* renamed from: com.android.wm.shell.pip.PipAnimationController$PipTransactionHandler */
    public static class PipTransactionHandler {
        public boolean handlePipTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, Rect rect) {
            throw null;
        }
    }

    public static boolean isInPipDirection(int i) {
        return i == 2;
    }

    public static boolean isOutPipDirection(int i) {
        return i == 3 || i == 4;
    }

    public static boolean isRemovePipDirection(int i) {
        return i == 5;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ AnimationHandler lambda$new$0() {
        AnimationHandler animationHandler = new AnimationHandler();
        animationHandler.setProvider(new SfVsyncFrameCallbackProvider());
        return animationHandler;
    }

    public PipAnimationController(PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        this.mSurfaceTransactionHelper = pipSurfaceTransactionHelper;
    }

    @VisibleForTesting
    public PipTransitionAnimator getAnimator(TaskInfo taskInfo, SurfaceControl surfaceControl, Rect rect, float f, float f2) {
        PipTransitionAnimator pipTransitionAnimator = this.mCurrentAnimator;
        if (pipTransitionAnimator == null) {
            this.mCurrentAnimator = setupPipTransitionAnimator(PipTransitionAnimator.ofAlpha(taskInfo, surfaceControl, rect, f, f2));
        } else if (pipTransitionAnimator.getAnimationType() != 1 || !Objects.equals(rect, this.mCurrentAnimator.getDestinationBounds()) || !this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
            this.mCurrentAnimator = setupPipTransitionAnimator(PipTransitionAnimator.ofAlpha(taskInfo, surfaceControl, rect, f, f2));
        } else {
            this.mCurrentAnimator.updateEndValue(Float.valueOf(f2));
        }
        return this.mCurrentAnimator;
    }

    @VisibleForTesting
    public PipTransitionAnimator getAnimator(TaskInfo taskInfo, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, Rect rect4, int i, float f, int i2) {
        Rect rect5 = rect3;
        PipTransitionAnimator pipTransitionAnimator = this.mCurrentAnimator;
        if (pipTransitionAnimator == null) {
            this.mCurrentAnimator = setupPipTransitionAnimator(PipTransitionAnimator.ofBounds(taskInfo, surfaceControl, rect2, rect2, rect3, rect4, i, 0.0f, i2));
        } else if (pipTransitionAnimator.getAnimationType() == 1 && this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.setDestinationBounds(rect3);
        } else if (this.mCurrentAnimator.getAnimationType() != 0 || !this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
            this.mCurrentAnimator = setupPipTransitionAnimator(PipTransitionAnimator.ofBounds(taskInfo, surfaceControl, rect, rect2, rect3, rect4, i, f, i2));
        } else {
            this.mCurrentAnimator.setDestinationBounds(rect3);
            this.mCurrentAnimator.updateEndValue(new Rect(rect3));
        }
        return this.mCurrentAnimator;
    }

    /* access modifiers changed from: package-private */
    public PipTransitionAnimator getCurrentAnimator() {
        return this.mCurrentAnimator;
    }

    private PipTransitionAnimator setupPipTransitionAnimator(PipTransitionAnimator pipTransitionAnimator) {
        pipTransitionAnimator.setSurfaceTransactionHelper(this.mSurfaceTransactionHelper);
        pipTransitionAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        pipTransitionAnimator.setFloatValues(new float[]{0.0f, 1.0f});
        pipTransitionAnimator.setAnimationHandler(this.mSfAnimationHandlerThreadLocal.get());
        return pipTransitionAnimator;
    }

    /* renamed from: com.android.wm.shell.pip.PipAnimationController$PipTransitionAnimator */
    public static abstract class PipTransitionAnimator<T> extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private final int mAnimationType;
        private T mBaseValue;
        protected SurfaceControl mContentOverlay;
        protected T mCurrentValue;
        private final Rect mDestinationBounds;
        private T mEndValue;
        private final SurfaceControl mLeash;
        private PipAnimationCallback mPipAnimationCallback;
        private PipTransactionHandler mPipTransactionHandler;
        protected T mStartValue;
        private float mStartingAngle;
        private PipSurfaceTransactionHelper.SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
        private PipSurfaceTransactionHelper mSurfaceTransactionHelper;
        private final TaskInfo mTaskInfo;
        private int mTransitionDirection;

        /* access modifiers changed from: package-private */
        public abstract void applySurfaceControlTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, float f);

        public void onAnimationRepeat(Animator animator) {
        }

        /* access modifiers changed from: package-private */
        public void onEndTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i) {
        }

        /* access modifiers changed from: package-private */
        public void onStartTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction) {
        }

        private PipTransitionAnimator(TaskInfo taskInfo, SurfaceControl surfaceControl, int i, Rect rect, T t, T t2, T t3, float f) {
            Rect rect2 = new Rect();
            this.mDestinationBounds = rect2;
            this.mTaskInfo = taskInfo;
            this.mLeash = surfaceControl;
            this.mAnimationType = i;
            rect2.set(rect);
            this.mBaseValue = t;
            this.mStartValue = t2;
            this.mEndValue = t3;
            this.mStartingAngle = f;
            addListener(this);
            addUpdateListener(this);
            this.mSurfaceControlTransactionFactory = C2353xe6db84e7.INSTANCE;
            this.mTransitionDirection = 0;
        }

        public void onAnimationStart(Animator animator) {
            this.mCurrentValue = this.mStartValue;
            onStartTransaction(this.mLeash, newSurfaceControlTransaction());
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationStart(this.mTaskInfo, this);
            }
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            applySurfaceControlTransaction(this.mLeash, newSurfaceControlTransaction(), valueAnimator.getAnimatedFraction());
        }

        public void onAnimationEnd(Animator animator) {
            this.mCurrentValue = this.mEndValue;
            SurfaceControl.Transaction newSurfaceControlTransaction = newSurfaceControlTransaction();
            onEndTransaction(this.mLeash, newSurfaceControlTransaction, this.mTransitionDirection);
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationEnd(this.mTaskInfo, newSurfaceControlTransaction, this);
            }
            this.mTransitionDirection = 0;
        }

        public void onAnimationCancel(Animator animator) {
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationCancel(this.mTaskInfo, this);
            }
            this.mTransitionDirection = 0;
        }

        @VisibleForTesting
        public int getAnimationType() {
            return this.mAnimationType;
        }

        @VisibleForTesting
        public PipTransitionAnimator<T> setPipAnimationCallback(PipAnimationCallback pipAnimationCallback) {
            this.mPipAnimationCallback = pipAnimationCallback;
            return this;
        }

        /* access modifiers changed from: package-private */
        public PipTransitionAnimator<T> setPipTransactionHandler(PipTransactionHandler pipTransactionHandler) {
            this.mPipTransactionHandler = pipTransactionHandler;
            return this;
        }

        /* access modifiers changed from: package-private */
        public boolean handlePipTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, Rect rect) {
            PipTransactionHandler pipTransactionHandler = this.mPipTransactionHandler;
            if (pipTransactionHandler != null) {
                return pipTransactionHandler.handlePipTransaction(surfaceControl, transaction, rect);
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public SurfaceControl getContentOverlay() {
            return this.mContentOverlay;
        }

        /* access modifiers changed from: package-private */
        public PipTransitionAnimator<T> setUseContentOverlay(Context context) {
            SurfaceControl.Transaction newSurfaceControlTransaction = newSurfaceControlTransaction();
            SurfaceControl surfaceControl = this.mContentOverlay;
            if (surfaceControl != null) {
                newSurfaceControlTransaction.remove(surfaceControl);
                newSurfaceControlTransaction.apply();
            }
            SurfaceControl build = new SurfaceControl.Builder(new SurfaceSession()).setCallsite("PipAnimation").setName("PipContentOverlay").setColorLayer().build();
            this.mContentOverlay = build;
            newSurfaceControlTransaction.show(build);
            newSurfaceControlTransaction.setLayer(this.mContentOverlay, Integer.MAX_VALUE);
            newSurfaceControlTransaction.setColor(this.mContentOverlay, getContentOverlayColor(context));
            newSurfaceControlTransaction.setAlpha(this.mContentOverlay, 0.0f);
            newSurfaceControlTransaction.reparent(this.mContentOverlay, this.mLeash);
            newSurfaceControlTransaction.apply();
            return this;
        }

        private float[] getContentOverlayColor(Context context) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16842801});
            try {
                int color = obtainStyledAttributes.getColor(0, 0);
                return new float[]{((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f};
            } finally {
                obtainStyledAttributes.recycle();
            }
        }

        /* access modifiers changed from: package-private */
        public void clearContentOverlay() {
            this.mContentOverlay = null;
        }

        @VisibleForTesting
        public int getTransitionDirection() {
            return this.mTransitionDirection;
        }

        @VisibleForTesting
        public PipTransitionAnimator<T> setTransitionDirection(int i) {
            if (i != 1) {
                this.mTransitionDirection = i;
            }
            return this;
        }

        /* access modifiers changed from: package-private */
        public T getStartValue() {
            return this.mStartValue;
        }

        /* access modifiers changed from: package-private */
        public T getBaseValue() {
            return this.mBaseValue;
        }

        @VisibleForTesting
        public T getEndValue() {
            return this.mEndValue;
        }

        /* access modifiers changed from: package-private */
        public Rect getDestinationBounds() {
            return this.mDestinationBounds;
        }

        /* access modifiers changed from: package-private */
        public void setDestinationBounds(Rect rect) {
            this.mDestinationBounds.set(rect);
            if (this.mAnimationType == 1) {
                onStartTransaction(this.mLeash, newSurfaceControlTransaction());
            }
        }

        /* access modifiers changed from: package-private */
        public void setCurrentValue(T t) {
            this.mCurrentValue = t;
        }

        /* access modifiers changed from: package-private */
        public boolean shouldApplyCornerRadius() {
            return !PipAnimationController.isOutPipDirection(this.mTransitionDirection);
        }

        /* access modifiers changed from: package-private */
        public boolean inScaleTransition() {
            if (this.mAnimationType != 0) {
                return false;
            }
            int transitionDirection = getTransitionDirection();
            if (PipAnimationController.isInPipDirection(transitionDirection) || PipAnimationController.isOutPipDirection(transitionDirection)) {
                return false;
            }
            return true;
        }

        public void updateEndValue(T t) {
            this.mEndValue = t;
        }

        /* access modifiers changed from: protected */
        public SurfaceControl.Transaction newSurfaceControlTransaction() {
            SurfaceControl.Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
            transaction.setFrameTimelineVsync(Choreographer.getSfInstance().getVsyncId());
            return transaction;
        }

        @VisibleForTesting
        public void setSurfaceControlTransactionFactory(PipSurfaceTransactionHelper.SurfaceControlTransactionFactory surfaceControlTransactionFactory) {
            this.mSurfaceControlTransactionFactory = surfaceControlTransactionFactory;
        }

        /* access modifiers changed from: package-private */
        public PipSurfaceTransactionHelper getSurfaceTransactionHelper() {
            return this.mSurfaceTransactionHelper;
        }

        /* access modifiers changed from: package-private */
        public void setSurfaceTransactionHelper(PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
            this.mSurfaceTransactionHelper = pipSurfaceTransactionHelper;
        }

        static PipTransitionAnimator<Float> ofAlpha(TaskInfo taskInfo, SurfaceControl surfaceControl, Rect rect, float f, float f2) {
            return new PipTransitionAnimator<Float>(taskInfo, surfaceControl, 1, rect, Float.valueOf(f), Float.valueOf(f), Float.valueOf(f2), 0.0f) {
                /* access modifiers changed from: package-private */
                public void applySurfaceControlTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, float f) {
                    float floatValue = (((Float) getStartValue()).floatValue() * (1.0f - f)) + (((Float) getEndValue()).floatValue() * f);
                    setCurrentValue(Float.valueOf(floatValue));
                    getSurfaceTransactionHelper().alpha(transaction, surfaceControl, floatValue).round(transaction, surfaceControl, shouldApplyCornerRadius());
                    transaction.apply();
                }

                /* access modifiers changed from: package-private */
                public void onStartTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction) {
                    if (getTransitionDirection() != 5) {
                        getSurfaceTransactionHelper().resetScale(transaction, surfaceControl, getDestinationBounds()).crop(transaction, surfaceControl, getDestinationBounds()).round(transaction, surfaceControl, shouldApplyCornerRadius());
                        transaction.show(surfaceControl);
                        transaction.apply();
                    }
                }

                public void updateEndValue(Float f) {
                    super.updateEndValue(f);
                    this.mStartValue = this.mCurrentValue;
                }
            };
        }

        static PipTransitionAnimator<Rect> ofBounds(TaskInfo taskInfo, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, Rect rect4, int i, float f, int i2) {
            Rect rect5;
            final Rect rect6;
            final Rect rect7;
            Rect rect8;
            Rect rect9 = rect;
            Rect rect10 = rect3;
            Rect rect11 = rect4;
            int i3 = i2;
            final boolean isOutPipDirection = PipAnimationController.isOutPipDirection(i);
            if (isOutPipDirection) {
                rect5 = new Rect(rect10);
            } else {
                rect5 = new Rect(rect9);
            }
            Rect rect12 = rect5;
            if (i3 == 1 || i3 == 3) {
                Rect rect13 = new Rect(rect10);
                Rect rect14 = new Rect(rect10);
                RotationUtils.rotateBounds(rect14, rect12, i3);
                rect6 = rect13;
                rect7 = rect14;
                rect8 = isOutPipDirection ? rect14 : rect12;
            } else {
                rect7 = null;
                rect6 = null;
                rect8 = rect12;
            }
            final Rect rect15 = rect11 == null ? null : new Rect(rect11.left - rect8.left, rect11.top - rect8.top, rect8.right - rect11.right, rect8.bottom - rect11.bottom);
            final Rect rect16 = r1;
            Rect rect17 = new Rect(0, 0, 0, 0);
            Rect rect18 = r2;
            Rect rect19 = new Rect(rect9);
            Rect rect20 = r0;
            Rect rect21 = new Rect(rect2);
            Rect rect22 = r0;
            Rect rect23 = new Rect(rect10);
            Rect rect24 = rect12;
            final float f2 = f;
            final Rect rect25 = rect4;
            final Rect rect26 = rect24;
            final Rect rect27 = rect8;
            final Rect rect28 = rect3;
            final int i4 = i2;
            final int i5 = i;
            return new PipTransitionAnimator<Rect>(taskInfo, surfaceControl, 0, rect3, rect18, rect20, rect22, f) {
                private final RectEvaluator mInsetsEvaluator = new RectEvaluator(new Rect());
                private final RectEvaluator mRectEvaluator = new RectEvaluator(new Rect());

                /* access modifiers changed from: package-private */
                public void applySurfaceControlTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, float f) {
                    Rect rect = (Rect) getBaseValue();
                    Rect rect2 = (Rect) getStartValue();
                    Rect rect3 = (Rect) getEndValue();
                    SurfaceControl surfaceControl2 = this.mContentOverlay;
                    if (surfaceControl2 != null) {
                        transaction.setAlpha(surfaceControl2, f < 0.5f ? 0.0f : (f - 0.5f) * 2.0f);
                    }
                    if (rect7 != null) {
                        applyRotation(transaction, surfaceControl, f, rect2, rect3);
                        return;
                    }
                    Rect evaluate = this.mRectEvaluator.evaluate(f, rect2, rect3);
                    float f2 = (1.0f - f) * f2;
                    setCurrentValue(evaluate);
                    if (!inScaleTransition() && rect25 != null) {
                        Rect computeInsets = computeInsets(f);
                        getSurfaceTransactionHelper().scaleAndCrop(transaction, surfaceControl, rect26, evaluate, computeInsets);
                        if (shouldApplyCornerRadius()) {
                            Rect rect4 = new Rect(evaluate);
                            rect4.inset(computeInsets);
                            getSurfaceTransactionHelper().round(transaction, surfaceControl, rect27, rect4);
                        }
                    } else if (isOutPipDirection) {
                        getSurfaceTransactionHelper().scale(transaction, surfaceControl, rect3, evaluate);
                    } else {
                        getSurfaceTransactionHelper().scale(transaction, surfaceControl, rect, evaluate, f2).round(transaction, surfaceControl, rect, evaluate);
                    }
                    if (!handlePipTransaction(surfaceControl, transaction, evaluate)) {
                        transaction.apply();
                    }
                }

                private void applyRotation(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, float f, Rect rect, Rect rect2) {
                    float f2;
                    float f3;
                    int i;
                    int i2;
                    float f4 = f;
                    Rect rect3 = rect;
                    Rect rect4 = rect2;
                    if (!rect4.equals(rect6)) {
                        rect7.set(rect28);
                        RotationUtils.rotateBounds(rect7, rect26, i4);
                        rect6.set(rect4);
                    }
                    Rect evaluate = this.mRectEvaluator.evaluate(f4, rect3, rect7);
                    setCurrentValue(evaluate);
                    Rect computeInsets = computeInsets(f4);
                    if (i4 == 1) {
                        f3 = 90.0f * f4;
                        int i3 = rect4.right;
                        int i4 = rect3.left;
                        f2 = (((float) (i3 - i4)) * f4) + ((float) i4);
                        i = rect4.top;
                        i2 = rect3.top;
                    } else {
                        f3 = -90.0f * f4;
                        int i5 = rect4.left;
                        int i6 = rect3.left;
                        f2 = (((float) (i5 - i6)) * f4) + ((float) i6);
                        i = rect4.bottom;
                        i2 = rect3.top;
                    }
                    getSurfaceTransactionHelper().rotateAndScaleWithCrop(transaction, surfaceControl, rect27, evaluate, computeInsets, f3, f2, (f4 * ((float) (i - i2))) + ((float) i2), isOutPipDirection, i4 == 3).round(transaction, surfaceControl, rect27, evaluate);
                    transaction.apply();
                }

                private Rect computeInsets(float f) {
                    Rect rect = rect15;
                    if (rect == null) {
                        return rect16;
                    }
                    boolean z = isOutPipDirection;
                    Rect rect2 = z ? rect : rect16;
                    if (z) {
                        rect = rect16;
                    }
                    return this.mInsetsEvaluator.evaluate(f, rect2, rect);
                }

                /* access modifiers changed from: package-private */
                public void onStartTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction) {
                    getSurfaceTransactionHelper().alpha(transaction, surfaceControl, 1.0f).round(transaction, surfaceControl, shouldApplyCornerRadius());
                    if (PipAnimationController.isInPipDirection(i5)) {
                        transaction.setWindowCrop(surfaceControl, (Rect) getStartValue());
                    }
                    transaction.show(surfaceControl);
                    transaction.apply();
                }

                /* access modifiers changed from: package-private */
                public void onEndTransaction(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i) {
                    Rect destinationBounds = getDestinationBounds();
                    getSurfaceTransactionHelper().resetScale(transaction, surfaceControl, destinationBounds);
                    if (PipAnimationController.isOutPipDirection(i)) {
                        transaction.setMatrix(surfaceControl, 1.0f, 0.0f, 0.0f, 1.0f);
                        transaction.setPosition(surfaceControl, 0.0f, 0.0f);
                        transaction.setWindowCrop(surfaceControl, 0, 0);
                        return;
                    }
                    getSurfaceTransactionHelper().crop(transaction, surfaceControl, destinationBounds);
                }

                public void updateEndValue(Rect rect) {
                    T t;
                    super.updateEndValue(rect);
                    T t2 = this.mStartValue;
                    if (t2 != null && (t = this.mCurrentValue) != null) {
                        ((Rect) t2).set((Rect) t);
                    }
                }
            };
        }
    }
}
