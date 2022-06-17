package com.motorola.systemui.cli.navgesture.util;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import com.motorola.systemui.cli.navgesture.view.ISwipeDismissView;

public class SwipeDismissHelper<T extends View & ISwipeDismissView> implements ISwipeDismissView {
    /* access modifiers changed from: private */
    public static final TimeInterpolator DISMISS_INTERPOLATOR = new DecelerateInterpolator(1.5f);
    public static final Direction HORIZONTAL = new Direction() {
        /* access modifiers changed from: package-private */
        public boolean isNegative(float f) {
            return f < 0.0f;
        }

        /* access modifiers changed from: package-private */
        public boolean isPositive(float f) {
            return f > 0.0f;
        }

        /* access modifiers changed from: package-private */
        public float getDisplacement(MotionEvent motionEvent, int i, PointF pointF, boolean z) {
            float x = motionEvent.getX(i) - pointF.x;
            return z ? -x : x;
        }

        /* access modifiers changed from: package-private */
        public boolean isMatchTouchSlop(MotionEvent motionEvent, int i, PointF pointF, float f) {
            float rawX = motionEvent.getRawX(i) - pointF.x;
            return Math.abs(rawX) > f && Math.abs(rawX) > Math.abs(motionEvent.getRawY(i) - pointF.y);
        }

        /* access modifiers changed from: package-private */
        public boolean canScroll(View view, float f, float f2, float f3, float f4) {
            return SwipeDismissHelper.canScrollHorizontally(view, false, f, f3, f4);
        }

        /* access modifiers changed from: package-private */
        public int getMaxSwipeDisplacement(View view) {
            return view.getWidth();
        }

        /* access modifiers changed from: package-private */
        public float getVelocity(VelocityTracker velocityTracker, int i, boolean z) {
            float xVelocity = velocityTracker.getXVelocity(i);
            return z ? -xVelocity : xVelocity;
        }
    };
    public static final Direction VERTICAL = new Direction() {
        /* access modifiers changed from: package-private */
        public boolean isNegative(float f) {
            return f < 0.0f;
        }

        /* access modifiers changed from: package-private */
        public boolean isPositive(float f) {
            return f > 0.0f;
        }

        /* access modifiers changed from: package-private */
        public float getDisplacement(MotionEvent motionEvent, int i, PointF pointF, boolean z) {
            return motionEvent.getY(i) - pointF.y;
        }

        /* access modifiers changed from: package-private */
        public boolean isMatchTouchSlop(MotionEvent motionEvent, int i, PointF pointF, float f) {
            float rawX = motionEvent.getRawX(i) - pointF.x;
            float rawY = motionEvent.getRawY(i) - pointF.y;
            return Math.abs(rawY) > f && Math.abs(rawY) > Math.abs(rawX);
        }

        /* access modifiers changed from: package-private */
        public boolean canScroll(View view, float f, float f2, float f3, float f4) {
            return SwipeDismissHelper.canScrollVertically(view, false, f2, f3, f4);
        }

        /* access modifiers changed from: package-private */
        public int getMaxSwipeDisplacement(View view) {
            return view.getHeight();
        }

        /* access modifiers changed from: package-private */
        public float getVelocity(VelocityTracker velocityTracker, int i, boolean z) {
            return velocityTracker.getYVelocity(i);
        }
    };
    private int mActivePointerId;
    private boolean mBlockGesture = false;
    private boolean mCancelClick = false;
    /* access modifiers changed from: private */
    public T mDelegateView;
    private int mDirectionFlag;
    private boolean mDisableFlingDetect = true;
    private boolean mDiscardIntercept;
    private final SwipeDismissHelper<T>.DismissAnimator mDismissAnimator = new DismissAnimator();
    private boolean mDismissed;
    private ISwipeDismissView.OnDismissedListener mDismissedListener;
    private boolean mDismissible = true;
    private final PointF mDownPos = new PointF();
    private boolean mIsRtl;
    private final PointF mLastPos = new PointF();
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private ISwipeDismissView.OnSwipeProgressChangedListener mProgressListener;
    private boolean mRequestInterceptTouchEventOnNextMove;
    private float mStartDisplacement;
    /* access modifiers changed from: private */
    public Direction mSwipeDirection;
    private boolean mSwiping;
    private int mTouchSlop;
    private float mUserSetMinDismissThreshold;
    private VelocityTracker mVelocityTracker;
    private boolean mWillInterceptOnNextMoveEvent;

    public static abstract class Direction {
        /* access modifiers changed from: package-private */
        public abstract boolean canScroll(View view, float f, float f2, float f3, float f4);

        /* access modifiers changed from: package-private */
        public abstract float getDisplacement(MotionEvent motionEvent, int i, PointF pointF, boolean z);

        /* access modifiers changed from: package-private */
        public abstract int getMaxSwipeDisplacement(View view);

        /* access modifiers changed from: package-private */
        public abstract float getVelocity(VelocityTracker velocityTracker, int i, boolean z);

        /* access modifiers changed from: package-private */
        public abstract boolean isMatchTouchSlop(MotionEvent motionEvent, int i, PointF pointF, float f);

        /* access modifiers changed from: package-private */
        public abstract boolean isNegative(float f);

        /* access modifiers changed from: package-private */
        public abstract boolean isPositive(float f);
    }

    private float progressToAlpha(float f) {
        float f2 = f * f * f;
        return f2 < 0.0f ? f2 + 1.0f : 1.0f - f2;
    }

    public final boolean superOnInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public final boolean superOnTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean superPerformClick() {
        return false;
    }

    public SwipeDismissHelper(Context context, T t, Direction direction) {
        this.mDelegateView = t;
        this.mSwipeDirection = direction;
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16;
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mIsRtl = isRtl();
    }

    public void setSwipeDirection(int i) {
        this.mDirectionFlag = i;
    }

    public void setMinDismissThreshold(float f) {
        this.mUserSetMinDismissThreshold = Utilities.boundToRange(f, 0.3f, 1.0f);
    }

    public void setOnDismissedListener(ISwipeDismissView.OnDismissedListener onDismissedListener) {
        this.mDismissedListener = onDismissedListener;
    }

    public void setOnSwipeProgressChangedListener(ISwipeDismissView.OnSwipeProgressChangedListener onSwipeProgressChangedListener) {
        this.mProgressListener = onSwipeProgressChangedListener;
    }

    public View getView() {
        return this.mDelegateView;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        checkGesture(motionEvent);
        if (this.mBlockGesture) {
            return true;
        }
        if (!this.mDismissible) {
            return ((ISwipeDismissView) this.mDelegateView).superOnInterceptTouchEvent(motionEvent);
        }
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked == 6) {
                            int actionIndex = motionEvent.getActionIndex();
                            if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                                int i = actionIndex == 0 ? 1 : 0;
                                this.mDownPos.set(motionEvent.getRawX(i) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getRawY(i) - (this.mLastPos.y - this.mDownPos.y));
                                this.mLastPos.set(motionEvent.getRawX(i), motionEvent.getRawY(i));
                                this.mActivePointerId = motionEvent.getPointerId(i);
                            }
                        }
                    }
                } else if (this.mVelocityTracker != null && !this.mDiscardIntercept) {
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (findPointerIndex == -1) {
                        Log.e("SwipeDismiss", "Invalid pointer index: ignoring.");
                        this.mDiscardIntercept = true;
                    } else {
                        float rawX2 = motionEvent.getRawX() - this.mDownPos.x;
                        float rawY2 = motionEvent.getRawY() - this.mDownPos.y;
                        float rawX3 = motionEvent.getRawX(findPointerIndex);
                        float rawY3 = motionEvent.getRawY(findPointerIndex);
                        if (!(rawX2 == 0.0f && rawY2 == 0.0f) && this.mSwipeDirection.canScroll(this.mDelegateView, rawX2, rawY2, rawX3, rawY3)) {
                            this.mDiscardIntercept = true;
                        } else {
                            updateSwiping(motionEvent, findPointerIndex);
                            this.mLastPos.set(rawX3, rawY3);
                        }
                    }
                }
            }
            resetMembers();
        } else {
            resetMembers();
            this.mDownPos.set(motionEvent.getRawX(), motionEvent.getRawY());
            this.mLastPos.set(this.mDownPos);
            this.mActivePointerId = motionEvent.getPointerId(0);
            VelocityTracker obtain = VelocityTracker.obtain();
            this.mVelocityTracker = obtain;
            obtain.addMovement(motionEvent);
        }
        motionEvent.offsetLocation(-rawX, -rawY);
        if (this.mDiscardIntercept || !this.mSwiping) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        checkGesture(motionEvent);
        if (this.mBlockGesture) {
            return true;
        }
        if (this.mVelocityTracker == null || !this.mDismissible) {
            return ((ISwipeDismissView) this.mDelegateView).superOnTouchEvent(motionEvent);
        }
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        if (findPointerIndex == -1) {
            Log.e("SwipeDismiss", "Invalid pointer index: ignoring.");
            cancelSwipe();
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            updateDismiss(motionEvent, findPointerIndex);
            float displacement = this.mSwipeDirection.getDisplacement(motionEvent, findPointerIndex, this.mDownPos, this.mIsRtl) - this.mStartDisplacement;
            if (this.mDismissed) {
                this.mDismissAnimator.animateDismissal(displacement, this.mSwipeDirection.isPositive(displacement));
            } else if (this.mSwiping) {
                this.mDismissAnimator.animateRecovery(displacement);
            }
            resetMembers();
        } else if (actionMasked == 2) {
            this.mVelocityTracker.addMovement(motionEvent);
            updateSwiping(motionEvent, findPointerIndex);
            if (this.mSwiping) {
                setProgress(this.mSwipeDirection.getDisplacement(motionEvent, findPointerIndex, this.mDownPos, this.mIsRtl) - this.mStartDisplacement);
            }
            this.mLastPos.set(motionEvent.getRawX(findPointerIndex), motionEvent.getRawY(findPointerIndex));
        } else if (actionMasked == 3) {
            cancelSwipe();
            resetMembers();
        } else if (actionMasked == 6) {
            int actionIndex = motionEvent.getActionIndex();
            if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                int i = actionIndex == 0 ? 1 : 0;
                this.mDownPos.set(motionEvent.getRawX(i) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getRawY(i) - (this.mLastPos.y - this.mDownPos.y));
                this.mLastPos.set(motionEvent.getRawX(i), motionEvent.getRawY(i));
                this.mActivePointerId = motionEvent.getPointerId(i);
            }
        }
        motionEvent.offsetLocation(-rawX, -rawY);
        if (this.mSwiping || ((ISwipeDismissView) this.mDelegateView).superOnTouchEvent(motionEvent)) {
            return true;
        }
        return false;
    }

    public void requestParentDisallowInterceptTouchEvent(boolean z) {
        ViewParent parent = this.mDelegateView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    /* access modifiers changed from: private */
    public void setProgress(float f) {
        if (this.mProgressListener != null) {
            this.mProgressListener.onSwipeProgressChanged(this, progressToAlpha(f / ((float) this.mSwipeDirection.getMaxSwipeDisplacement(this.mDelegateView))), f);
        }
    }

    public void dismiss(boolean z) {
        if (z) {
            this.mDismissAnimator.animateDismissal(0.0f, true);
            return;
        }
        ISwipeDismissView.OnDismissedListener onDismissedListener = this.mDismissedListener;
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed(this);
        }
    }

    public void cancelSwipe() {
        ISwipeDismissView.OnSwipeProgressChangedListener onSwipeProgressChangedListener = this.mProgressListener;
        if (onSwipeProgressChangedListener != null) {
            onSwipeProgressChangedListener.onSwipeCancelled(this);
        }
    }

    private boolean isRtl() {
        return this.mDelegateView.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    private void resetMembers() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = null;
        this.mSwiping = false;
        this.mDismissed = false;
        this.mDiscardIntercept = false;
        this.mStartDisplacement = 0.0f;
        this.mWillInterceptOnNextMoveEvent = this.mRequestInterceptTouchEventOnNextMove;
        requestParentDisallowInterceptTouchEvent(false);
    }

    private void updateSwiping(MotionEvent motionEvent, int i) {
        if (!this.mSwiping) {
            float displacement = this.mSwipeDirection.getDisplacement(motionEvent, i, this.mDownPos, this.mIsRtl);
            boolean isMatchTouchSlop = this.mSwipeDirection.isMatchTouchSlop(motionEvent, i, this.mDownPos, (float) this.mTouchSlop);
            boolean z = ((this.mDirectionFlag & 4) != 0 && this.mSwipeDirection.isNegative(displacement)) || ((this.mDirectionFlag & 2) != 0 && this.mSwipeDirection.isPositive(displacement));
            if (isMatchTouchSlop && z) {
                this.mSwiping = true;
                if (this.mRequestInterceptTouchEventOnNextMove && this.mWillInterceptOnNextMoveEvent) {
                    this.mWillInterceptOnNextMoveEvent = false;
                    this.mSwiping = false;
                }
            }
            boolean z2 = this.mSwiping;
            this.mCancelClick = z2;
            if (z2) {
                if (displacement > 0.0f) {
                    this.mStartDisplacement = (float) this.mTouchSlop;
                } else {
                    this.mStartDisplacement = (float) (-this.mTouchSlop);
                }
                requestParentDisallowInterceptTouchEvent(true);
            }
        }
    }

    private void updateDismiss(MotionEvent motionEvent, int i) {
        if (!this.mDismissed) {
            float displacement = this.mSwipeDirection.getDisplacement(motionEvent, i, this.mDownPos, this.mIsRtl) - this.mStartDisplacement;
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
            float velocity = this.mSwipeDirection.getVelocity(this.mVelocityTracker, i, this.mIsRtl);
            float maxSwipeDisplacement = ((float) this.mSwipeDirection.getMaxSwipeDisplacement(this.mDelegateView)) * Math.max(0.3f, this.mUserSetMinDismissThreshold);
            boolean z = true;
            boolean z2 = Math.abs(velocity) >= ((float) this.mMinFlingVelocity);
            if (((this.mDirectionFlag & 4) == 0 || !this.mSwipeDirection.isNegative(displacement)) && ((this.mDirectionFlag & 2) == 0 || !this.mSwipeDirection.isPositive(displacement))) {
                z = false;
            }
            if (Math.abs(displacement) > maxSwipeDisplacement || (!this.mDisableFlingDetect && z2)) {
                this.mDismissed = z;
            }
        }
    }

    public boolean performClick() {
        return !this.mCancelClick && ((ISwipeDismissView) this.mDelegateView).superPerformClick();
    }

    public void setDismissible(boolean z) {
        if (!z && this.mDismissible) {
            cancelSwipe();
            resetMembers();
        }
        this.mDismissible = z;
    }

    private void checkGesture(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mBlockGesture = this.mDismissAnimator.isAnimating();
        }
    }

    private class DismissAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private final ValueAnimator mDismissAnimator;
        private boolean mDismissOnComplete = false;
        private boolean mWasCanceled = false;

        public void onAnimationRepeat(Animator animator) {
        }

        DismissAnimator() {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.mDismissAnimator = valueAnimator;
            valueAnimator.addUpdateListener(this);
            valueAnimator.addListener(this);
        }

        /* access modifiers changed from: package-private */
        public void animateDismissal(float f, boolean z) {
            animate(f / ((float) SwipeDismissHelper.this.mSwipeDirection.getMaxSwipeDisplacement(SwipeDismissHelper.this.mDelegateView)), z ? 1.0f : -1.0f, 250, SwipeDismissHelper.DISMISS_INTERPOLATOR, true);
        }

        /* access modifiers changed from: package-private */
        public void animateRecovery(float f) {
            animate(f / ((float) SwipeDismissHelper.this.mSwipeDirection.getMaxSwipeDisplacement(SwipeDismissHelper.this.mDelegateView)), 0.0f, 250, SwipeDismissHelper.DISMISS_INTERPOLATOR, false);
        }

        /* access modifiers changed from: package-private */
        public boolean isAnimating() {
            return this.mDismissAnimator.isStarted();
        }

        private void animate(float f, float f2, long j, TimeInterpolator timeInterpolator, boolean z) {
            this.mDismissAnimator.cancel();
            this.mDismissOnComplete = z;
            this.mDismissAnimator.setFloatValues(new float[]{f, f2});
            this.mDismissAnimator.setDuration(j);
            this.mDismissAnimator.setInterpolator(timeInterpolator);
            this.mDismissAnimator.start();
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            SwipeDismissHelper.this.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue() * ((float) SwipeDismissHelper.this.mSwipeDirection.getMaxSwipeDisplacement(SwipeDismissHelper.this.mDelegateView)));
        }

        public void onAnimationStart(Animator animator) {
            this.mWasCanceled = false;
        }

        public void onAnimationCancel(Animator animator) {
            this.mWasCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mWasCanceled) {
                return;
            }
            if (this.mDismissOnComplete) {
                SwipeDismissHelper.this.dismiss(false);
            } else {
                SwipeDismissHelper.this.cancelSwipe();
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean canScrollHorizontally(View view, boolean z, float f, float f2, float f3) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                float f4 = ((float) scrollX) + f2;
                if (f4 >= ((float) childAt.getLeft()) && f4 < ((float) childAt.getRight())) {
                    float f5 = ((float) scrollY) + f3;
                    if (f5 >= ((float) childAt.getTop()) && f5 < ((float) childAt.getBottom()) && canScrollHorizontally(childAt, true, f, f4 - ((float) childAt.getLeft()), f5 - ((float) childAt.getTop()))) {
                        return true;
                    }
                }
            }
        }
        if (!z || !view.canScrollHorizontally((int) (-f))) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean canScrollVertically(View view, boolean z, float f, float f2, float f3) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                float f4 = ((float) scrollX) + f2;
                if (f4 >= ((float) childAt.getLeft()) && f4 < ((float) childAt.getRight())) {
                    float f5 = ((float) scrollY) + f3;
                    if (f5 >= ((float) childAt.getTop()) && f5 < ((float) childAt.getBottom()) && canScrollVertically(childAt, true, f, f4 - ((float) childAt.getLeft()), f5 - ((float) childAt.getTop()))) {
                        return true;
                    }
                }
            }
        }
        if (!z || !view.canScrollVertically((int) (-f))) {
            return false;
        }
        return true;
    }
}
