package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.p011wm.shell.animation.FlingAnimationUtils;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class SwipeHelper implements Gefingerpoken {
    /* access modifiers changed from: private */
    public final Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private float mDensityScale;
    /* access modifiers changed from: private */
    public boolean mDisableHwLayers;
    /* access modifiers changed from: private */
    public final ArrayMap<View, Animator> mDismissPendingMap = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final float[] mDownLocation = new float[2];
    private final boolean mFadeDependingOnAmountSwiped;
    private final FalsingManager mFalsingManager;
    private final int mFalsingThreshold;
    private final FlingAnimationUtils mFlingAnimationUtils;
    protected final Handler mHandler;
    private float mInitialTouchPos;
    private boolean mIsSwiping;
    /* access modifiers changed from: private */
    public boolean mLongPressSent;
    private final long mLongPressTimeout;
    private float mMaxSwipeProgress = 1.0f;
    private boolean mMenuRowIntercepting;
    private float mMinSwipeProgress = 0.0f;
    private float mPagingTouchSlop;
    private final Runnable mPerformLongPress = new Runnable() {
        private final int[] mViewOffset = new int[2];

        public void run() {
            if (SwipeHelper.this.mTouchedView != null && !SwipeHelper.this.mLongPressSent) {
                boolean unused = SwipeHelper.this.mLongPressSent = true;
                if (SwipeHelper.this.mTouchedView instanceof ExpandableNotificationRow) {
                    SwipeHelper.this.mTouchedView.getLocationOnScreen(this.mViewOffset);
                    int i = ((int) SwipeHelper.this.mDownLocation[0]) - this.mViewOffset[0];
                    int i2 = ((int) SwipeHelper.this.mDownLocation[1]) - this.mViewOffset[1];
                    SwipeHelper.this.mTouchedView.sendAccessibilityEvent(2);
                    ((ExpandableNotificationRow) SwipeHelper.this.mTouchedView).doLongClickCallback(i, i2);
                }
            }
        }
    };
    private float mPerpendicularInitialTouchPos;
    private final float mSlopMultiplier;
    /* access modifiers changed from: private */
    public boolean mSnappingChild;
    private final int mSwipeDirection;
    private boolean mTouchAboveFalsingThreshold;
    /* access modifiers changed from: private */
    public View mTouchedView;
    private float mTranslation = 0.0f;
    private final VelocityTracker mVelocityTracker;

    /* access modifiers changed from: protected */
    public long getMaxEscapeAnimDuration() {
        return 400;
    }

    /* access modifiers changed from: protected */
    public float getUnscaledEscapeVelocity() {
        return 500.0f;
    }

    /* access modifiers changed from: protected */
    public boolean handleUpEvent(MotionEvent motionEvent, View view, float f, float f2) {
        return false;
    }

    public void onDownUpdate(View view, MotionEvent motionEvent) {
    }

    /* access modifiers changed from: protected */
    public void onMoveUpdate(View view, MotionEvent motionEvent, float f, float f2) {
    }

    /* access modifiers changed from: protected */
    public void prepareDismissAnimation(View view, Animator animator) {
    }

    /* access modifiers changed from: protected */
    public void prepareSnapBackAnimation(View view, Animator animator) {
    }

    public SwipeHelper(int i, Callback callback, Resources resources, ViewConfiguration viewConfiguration, FalsingManager falsingManager) {
        this.mCallback = callback;
        this.mHandler = new Handler();
        this.mSwipeDirection = i;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mPagingTouchSlop = (float) viewConfiguration.getScaledPagingTouchSlop();
        this.mSlopMultiplier = viewConfiguration.getScaledAmbiguousGestureMultiplier();
        this.mLongPressTimeout = (long) (((float) ViewConfiguration.getLongPressTimeout()) * 1.5f);
        this.mDensityScale = resources.getDisplayMetrics().density;
        this.mFalsingThreshold = resources.getDimensionPixelSize(R$dimen.swipe_helper_falsing_threshold);
        this.mFadeDependingOnAmountSwiped = resources.getBoolean(R$bool.config_fadeDependingOnAmountSwiped);
        this.mFalsingManager = falsingManager;
        this.mFlingAnimationUtils = new FlingAnimationUtils(resources.getDisplayMetrics(), ((float) getMaxEscapeAnimDuration()) / 1000.0f);
    }

    public void setDensityScale(float f) {
        this.mDensityScale = f;
    }

    public void setPagingTouchSlop(float f) {
        this.mPagingTouchSlop = f;
    }

    private float getPos(MotionEvent motionEvent) {
        return this.mSwipeDirection == 0 ? motionEvent.getX() : motionEvent.getY();
    }

    private float getPerpendicularPos(MotionEvent motionEvent) {
        return this.mSwipeDirection == 0 ? motionEvent.getY() : motionEvent.getX();
    }

    /* access modifiers changed from: protected */
    public float getTranslation(View view) {
        return this.mSwipeDirection == 0 ? view.getTranslationX() : view.getTranslationY();
    }

    private float getVelocity(VelocityTracker velocityTracker) {
        if (this.mSwipeDirection == 0) {
            return velocityTracker.getXVelocity();
        }
        return velocityTracker.getYVelocity();
    }

    /* access modifiers changed from: protected */
    public ObjectAnimator createTranslationAnimation(View view, float f) {
        return ObjectAnimator.ofFloat(view, this.mSwipeDirection == 0 ? View.TRANSLATION_X : View.TRANSLATION_Y, new float[]{f});
    }

    /* access modifiers changed from: protected */
    public Animator getViewTranslationAnimator(View view, float f, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ObjectAnimator createTranslationAnimation = createTranslationAnimation(view, f);
        if (animatorUpdateListener != null) {
            createTranslationAnimation.addUpdateListener(animatorUpdateListener);
        }
        return createTranslationAnimation;
    }

    /* access modifiers changed from: protected */
    public void setTranslation(View view, float f) {
        if (view != null) {
            if (this.mSwipeDirection == 0) {
                view.setTranslationX(f);
            } else {
                view.setTranslationY(f);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getSize(View view) {
        return (float) (this.mSwipeDirection == 0 ? view.getMeasuredWidth() : view.getMeasuredHeight());
    }

    private float getSwipeProgressForOffset(View view, float f) {
        return Math.min(Math.max(this.mMinSwipeProgress, Math.abs(f / getSize(view))), this.mMaxSwipeProgress);
    }

    private float getSwipeAlpha(float f) {
        if (this.mFadeDependingOnAmountSwiped) {
            return Math.max(1.0f - f, 0.0f);
        }
        return 1.0f - Math.max(0.0f, Math.min(1.0f, f / 0.5f));
    }

    /* access modifiers changed from: private */
    public void updateSwipeProgressFromOffset(View view, boolean z) {
        updateSwipeProgressFromOffset(view, z, getTranslation(view));
    }

    private void updateSwipeProgressFromOffset(View view, boolean z, float f) {
        float swipeProgressForOffset = getSwipeProgressForOffset(view, f);
        if (!this.mCallback.updateSwipeProgress(view, z, swipeProgressForOffset) && z) {
            if (!this.mDisableHwLayers) {
                if (swipeProgressForOffset == 0.0f || swipeProgressForOffset == 1.0f) {
                    view.setLayerType(0, (Paint) null);
                } else {
                    view.setLayerType(2, (Paint) null);
                }
            }
            view.setAlpha(getSwipeAlpha(swipeProgressForOffset));
        }
        invalidateGlobalRegion(view);
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom()));
    }

    public static void invalidateGlobalRegion(View view, RectF rectF) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(rectF);
            view.invalidate((int) Math.floor((double) rectF.left), (int) Math.floor((double) rectF.top), (int) Math.ceil((double) rectF.right), (int) Math.ceil((double) rectF.bottom));
        }
    }

    public void cancelLongPress() {
        this.mHandler.removeCallbacks(this.mPerformLongPress);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0024, code lost:
        if (r0 != 3) goto L_0x011a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            android.view.View r0 = r7.mTouchedView
            boolean r1 = r0 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r1 == 0) goto L_0x0016
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r0
            com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin r0 = r0.getProvider()
            if (r0 == 0) goto L_0x0016
            android.view.View r1 = r7.mTouchedView
            boolean r0 = r0.onInterceptTouchEvent(r1, r8)
            r7.mMenuRowIntercepting = r0
        L_0x0016:
            int r0 = r8.getAction()
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x00c4
            if (r0 == r1) goto L_0x00a5
            r3 = 2
            if (r0 == r3) goto L_0x0028
            r8 = 3
            if (r0 == r8) goto L_0x00a5
            goto L_0x011a
        L_0x0028:
            android.view.View r0 = r7.mTouchedView
            if (r0 == 0) goto L_0x011a
            boolean r0 = r7.mLongPressSent
            if (r0 != 0) goto L_0x011a
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r0.addMovement(r8)
            float r0 = r7.getPos(r8)
            float r4 = r7.getPerpendicularPos(r8)
            float r5 = r7.mInitialTouchPos
            float r0 = r0 - r5
            float r5 = r7.mPerpendicularInitialTouchPos
            float r4 = r4 - r5
            int r5 = r8.getClassification()
            if (r5 != r1) goto L_0x004f
            float r5 = r7.mPagingTouchSlop
            float r6 = r7.mSlopMultiplier
            float r5 = r5 * r6
            goto L_0x0051
        L_0x004f:
            float r5 = r7.mPagingTouchSlop
        L_0x0051:
            float r6 = java.lang.Math.abs(r0)
            int r5 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x008b
            float r0 = java.lang.Math.abs(r0)
            float r4 = java.lang.Math.abs(r4)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x008b
            com.android.systemui.SwipeHelper$Callback r0 = r7.mCallback
            android.view.View r3 = r7.mTouchedView
            boolean r0 = r0.canChildBeDragged(r3)
            if (r0 == 0) goto L_0x0086
            r7.mIsSwiping = r1
            com.android.systemui.SwipeHelper$Callback r0 = r7.mCallback
            android.view.View r3 = r7.mTouchedView
            r0.onBeginDrag(r3)
            float r8 = r7.getPos(r8)
            r7.mInitialTouchPos = r8
            android.view.View r8 = r7.mTouchedView
            float r8 = r7.getTranslation(r8)
            r7.mTranslation = r8
        L_0x0086:
            r7.cancelLongPress()
            goto L_0x011a
        L_0x008b:
            int r8 = r8.getClassification()
            if (r8 != r3) goto L_0x011a
            android.os.Handler r8 = r7.mHandler
            java.lang.Runnable r0 = r7.mPerformLongPress
            boolean r8 = r8.hasCallbacks(r0)
            if (r8 == 0) goto L_0x011a
            r7.cancelLongPress()
            java.lang.Runnable r8 = r7.mPerformLongPress
            r8.run()
            goto L_0x011a
        L_0x00a5:
            boolean r8 = r7.mIsSwiping
            if (r8 != 0) goto L_0x00b4
            boolean r8 = r7.mLongPressSent
            if (r8 != 0) goto L_0x00b4
            boolean r8 = r7.mMenuRowIntercepting
            if (r8 == 0) goto L_0x00b2
            goto L_0x00b4
        L_0x00b2:
            r8 = r2
            goto L_0x00b5
        L_0x00b4:
            r8 = r1
        L_0x00b5:
            r7.mIsSwiping = r2
            r0 = 0
            r7.mTouchedView = r0
            r7.mLongPressSent = r2
            r7.mMenuRowIntercepting = r2
            r7.cancelLongPress()
            if (r8 == 0) goto L_0x011a
            return r1
        L_0x00c4:
            r7.mTouchAboveFalsingThreshold = r2
            r7.mIsSwiping = r2
            r7.mSnappingChild = r2
            r7.mLongPressSent = r2
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r0.clear()
            com.android.systemui.SwipeHelper$Callback r0 = r7.mCallback
            android.view.View r0 = r0.getChildAtPosition(r8)
            r7.mTouchedView = r0
            if (r0 == 0) goto L_0x011a
            r7.onDownUpdate(r0, r8)
            com.android.systemui.SwipeHelper$Callback r0 = r7.mCallback
            android.view.View r3 = r7.mTouchedView
            boolean r0 = r0.canChildBeDismissed(r3)
            r7.mCanCurrViewBeDimissed = r0
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r0.addMovement(r8)
            float r0 = r7.getPos(r8)
            r7.mInitialTouchPos = r0
            float r0 = r7.getPerpendicularPos(r8)
            r7.mPerpendicularInitialTouchPos = r0
            android.view.View r0 = r7.mTouchedView
            float r0 = r7.getTranslation(r0)
            r7.mTranslation = r0
            float[] r0 = r7.mDownLocation
            float r3 = r8.getRawX()
            r0[r2] = r3
            float[] r0 = r7.mDownLocation
            float r8 = r8.getRawY()
            r0[r1] = r8
            android.os.Handler r8 = r7.mHandler
            java.lang.Runnable r0 = r7.mPerformLongPress
            long r3 = r7.mLongPressTimeout
            r8.postDelayed(r0, r3)
        L_0x011a:
            boolean r8 = r7.mIsSwiping
            if (r8 != 0) goto L_0x0128
            boolean r8 = r7.mLongPressSent
            if (r8 != 0) goto L_0x0128
            boolean r7 = r7.mMenuRowIntercepting
            if (r7 == 0) goto L_0x0127
            goto L_0x0128
        L_0x0127:
            r1 = r2
        L_0x0128:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public void dismissChild(View view, float f, boolean z) {
        dismissChild(view, f, (Runnable) null, 0, z, 0, false);
    }

    public void dismissChild(final View view, float f, Runnable runnable, long j, boolean z, long j2, boolean z2) {
        float f2;
        long j3;
        View view2 = view;
        long j4 = j;
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        boolean z3 = false;
        boolean z4 = view.getLayoutDirection() == 1;
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        boolean z5 = i == 0 && (getTranslation(view) == 0.0f || z2) && this.mSwipeDirection == 1;
        boolean z6 = i == 0 && (getTranslation(view) == 0.0f || z2) && z4;
        if ((Math.abs(f) > getEscapeVelocity() && f < 0.0f) || (getTranslation(view) < 0.0f && !z2)) {
            z3 = true;
        }
        if (z3 || z6 || z5) {
            f2 = -getTotalTranslationLength(view);
        } else {
            f2 = getTotalTranslationLength(view);
        }
        float f3 = f2;
        if (j2 == 0) {
            j3 = i != 0 ? Math.min(400, (long) ((int) ((Math.abs(f3 - getTranslation(view)) * 1000.0f) / Math.abs(f)))) : 200;
        } else {
            j3 = j2;
        }
        if (!this.mDisableHwLayers) {
            view.setLayerType(2, (Paint) null);
        }
        Animator viewTranslationAnimator = getViewTranslationAnimator(view, f3, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SwipeHelper.this.onTranslationUpdate(view, ((Float) valueAnimator.getAnimatedValue()).floatValue(), canChildBeDismissed);
            }
        });
        if (viewTranslationAnimator != null) {
            if (z) {
                viewTranslationAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
                viewTranslationAnimator.setDuration(j3);
            } else {
                this.mFlingAnimationUtils.applyDismissing(viewTranslationAnimator, getTranslation(view), f3, f, getSize(view));
            }
            if (j4 > 0) {
                viewTranslationAnimator.setStartDelay(j4);
            }
            final Runnable runnable2 = runnable;
            viewTranslationAnimator.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled;

                public void onAnimationCancel(Animator animator) {
                    this.mCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    SwipeHelper.this.updateSwipeProgressFromOffset(view, canChildBeDismissed);
                    SwipeHelper.this.mDismissPendingMap.remove(view);
                    View view = view;
                    boolean isRemoved = view instanceof ExpandableNotificationRow ? ((ExpandableNotificationRow) view).isRemoved() : false;
                    if (!this.mCancelled || isRemoved) {
                        SwipeHelper.this.mCallback.onChildDismissed(view);
                        SwipeHelper.this.resetSwipeState();
                    }
                    Runnable runnable = runnable2;
                    if (runnable != null) {
                        runnable.run();
                    }
                    if (!SwipeHelper.this.mDisableHwLayers) {
                        view.setLayerType(0, (Paint) null);
                    }
                }
            });
            prepareDismissAnimation(view, viewTranslationAnimator);
            this.mDismissPendingMap.put(view, viewTranslationAnimator);
            viewTranslationAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public float getTotalTranslationLength(View view) {
        return getSize(view);
    }

    public void snapChild(final View view, float f, float f2) {
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        Animator viewTranslationAnimator = getViewTranslationAnimator(view, f, new SwipeHelper$$ExternalSyntheticLambda0(this, view, canChildBeDismissed));
        if (viewTranslationAnimator != null) {
            viewTranslationAnimator.addListener(new AnimatorListenerAdapter() {
                boolean wasCancelled = false;

                public void onAnimationCancel(Animator animator) {
                    this.wasCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    boolean unused = SwipeHelper.this.mSnappingChild = false;
                    if (!this.wasCancelled) {
                        SwipeHelper.this.updateSwipeProgressFromOffset(view, canChildBeDismissed);
                        SwipeHelper.this.resetSwipeState();
                    }
                }
            });
            prepareSnapBackAnimation(view, viewTranslationAnimator);
            this.mSnappingChild = true;
            Animator animator = viewTranslationAnimator;
            this.mFlingAnimationUtils.apply(animator, getTranslation(view), f, f2, Math.abs(f - getTranslation(view)));
            viewTranslationAnimator.start();
            this.mCallback.onChildSnappedBack(view, f);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$snapChild$0(View view, boolean z, ValueAnimator valueAnimator) {
        onTranslationUpdate(view, ((Float) valueAnimator.getAnimatedValue()).floatValue(), z);
    }

    public void onTranslationUpdate(View view, float f, boolean z) {
        updateSwipeProgressFromOffset(view, z, f);
    }

    private void snapChildInstantly(View view) {
        boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        setTranslation(view, 0.0f);
        updateSwipeProgressFromOffset(view, canChildBeDismissed);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        if (getTranslation(r5) != 0.0f) goto L_0x001d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void snapChildIfNeeded(android.view.View r5, boolean r6, float r7) {
        /*
            r4 = this;
            boolean r0 = r4.mIsSwiping
            if (r0 == 0) goto L_0x0008
            android.view.View r0 = r4.mTouchedView
            if (r0 == r5) goto L_0x000c
        L_0x0008:
            boolean r0 = r4.mSnappingChild
            if (r0 == 0) goto L_0x000d
        L_0x000c:
            return
        L_0x000d:
            r0 = 0
            android.util.ArrayMap<android.view.View, android.animation.Animator> r1 = r4.mDismissPendingMap
            java.lang.Object r1 = r1.get(r5)
            android.animation.Animator r1 = (android.animation.Animator) r1
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x001f
            r1.cancel()
        L_0x001d:
            r0 = r2
            goto L_0x0028
        L_0x001f:
            float r1 = r4.getTranslation(r5)
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0028
            goto L_0x001d
        L_0x0028:
            if (r0 == 0) goto L_0x0033
            if (r6 == 0) goto L_0x0030
            r4.snapChild(r5, r7, r3)
            goto L_0x0033
        L_0x0030:
            r4.snapChildInstantly(r5)
        L_0x0033:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.snapChildIfNeeded(android.view.View, boolean, float):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003e, code lost:
        if (r0 != 4) goto L_0x00fe;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r11) {
        /*
            r10 = this;
            boolean r0 = r10.mLongPressSent
            r1 = 1
            if (r0 == 0) goto L_0x000a
            boolean r0 = r10.mMenuRowIntercepting
            if (r0 != 0) goto L_0x000a
            return r1
        L_0x000a:
            boolean r0 = r10.mIsSwiping
            r2 = 0
            if (r0 != 0) goto L_0x002b
            boolean r0 = r10.mMenuRowIntercepting
            if (r0 != 0) goto L_0x002b
            com.android.systemui.SwipeHelper$Callback r0 = r10.mCallback
            android.view.View r0 = r0.getChildAtPosition(r11)
            if (r0 == 0) goto L_0x0027
            com.android.systemui.SwipeHelper$Callback r0 = r10.mCallback
            android.view.View r0 = r0.getChildAtPosition(r11)
            r10.mTouchedView = r0
            r10.onInterceptTouchEvent(r11)
            return r1
        L_0x0027:
            r10.cancelLongPress()
            return r2
        L_0x002b:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.addMovement(r11)
            int r0 = r11.getAction()
            r3 = 0
            if (r0 == r1) goto L_0x00ba
            r4 = 2
            if (r0 == r4) goto L_0x0042
            r4 = 3
            if (r0 == r4) goto L_0x00ba
            r4 = 4
            if (r0 == r4) goto L_0x0042
            goto L_0x00fe
        L_0x0042:
            android.view.View r0 = r10.mTouchedView
            if (r0 == 0) goto L_0x00fe
            float r0 = r10.getPos(r11)
            float r4 = r10.mInitialTouchPos
            float r0 = r0 - r4
            float r4 = java.lang.Math.abs(r0)
            int r5 = r10.getFalsingThreshold()
            float r5 = (float) r5
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x005c
            r10.mTouchAboveFalsingThreshold = r1
        L_0x005c:
            com.android.systemui.SwipeHelper$Callback r5 = r10.mCallback
            android.view.View r6 = r10.mTouchedView
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0065
            r2 = r1
        L_0x0065:
            boolean r2 = r5.canChildBeDismissedInDirection(r6, r2)
            if (r2 != 0) goto L_0x00a2
            android.view.View r2 = r10.mTouchedView
            float r2 = r10.getSize(r2)
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r5 = r5 * r2
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 < 0) goto L_0x007f
            if (r3 <= 0) goto L_0x007d
            r0 = r5
            goto L_0x00a2
        L_0x007d:
            float r0 = -r5
            goto L_0x00a2
        L_0x007f:
            com.android.systemui.SwipeHelper$Callback r3 = r10.mCallback
            int r3 = r3.getConstrainSwipeStartPosition()
            float r3 = (float) r3
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x00a2
            float r4 = java.lang.Math.signum(r0)
            float r3 = r3 * r4
            int r3 = (int) r3
            float r3 = (float) r3
            float r0 = r0 - r3
            float r0 = r0 / r2
            double r6 = (double) r0
            r8 = 4609753056924675352(0x3ff921fb54442d18, double:1.5707963267948966)
            double r6 = r6 * r8
            double r6 = java.lang.Math.sin(r6)
            float r0 = (float) r6
            float r5 = r5 * r0
            float r0 = r3 + r5
        L_0x00a2:
            android.view.View r2 = r10.mTouchedView
            float r3 = r10.mTranslation
            float r3 = r3 + r0
            r10.setTranslation(r2, r3)
            android.view.View r2 = r10.mTouchedView
            boolean r3 = r10.mCanCurrViewBeDimissed
            r10.updateSwipeProgressFromOffset(r2, r3)
            android.view.View r2 = r10.mTouchedView
            float r3 = r10.mTranslation
            float r3 = r3 + r0
            r10.onMoveUpdate(r2, r11, r3, r0)
            goto L_0x00fe
        L_0x00ba:
            android.view.View r0 = r10.mTouchedView
            if (r0 != 0) goto L_0x00bf
            goto L_0x00fe
        L_0x00bf:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r4 = 1000(0x3e8, float:1.401E-42)
            float r5 = r10.getMaxVelocity()
            r0.computeCurrentVelocity(r4, r5)
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            float r0 = r10.getVelocity(r0)
            android.view.View r4 = r10.mTouchedView
            float r5 = r10.getTranslation(r4)
            boolean r4 = r10.handleUpEvent(r11, r4, r0, r5)
            if (r4 != 0) goto L_0x00fc
            boolean r11 = r10.isDismissGesture(r11)
            if (r11 == 0) goto L_0x00ed
            android.view.View r11 = r10.mTouchedView
            boolean r3 = r10.swipedFastEnough()
            r3 = r3 ^ r1
            r10.dismissChild(r11, r0, r3)
            goto L_0x00f9
        L_0x00ed:
            com.android.systemui.SwipeHelper$Callback r11 = r10.mCallback
            android.view.View r4 = r10.mTouchedView
            r11.onDragCancelled(r4)
            android.view.View r11 = r10.mTouchedView
            r10.snapChild(r11, r3, r0)
        L_0x00f9:
            r11 = 0
            r10.mTouchedView = r11
        L_0x00fc:
            r10.mIsSwiping = r2
        L_0x00fe:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private int getFalsingThreshold() {
        return (int) (((float) this.mFalsingThreshold) * this.mCallback.getFalsingThresholdFactor());
    }

    private float getMaxVelocity() {
        return this.mDensityScale * 4000.0f;
    }

    /* access modifiers changed from: protected */
    public float getEscapeVelocity() {
        return getUnscaledEscapeVelocity() * this.mDensityScale;
    }

    /* access modifiers changed from: protected */
    public boolean swipedFarEnough() {
        return Math.abs(getTranslation(this.mTouchedView)) > getSize(this.mTouchedView) * 0.6f;
    }

    public boolean isDismissGesture(MotionEvent motionEvent) {
        float translation = getTranslation(this.mTouchedView);
        if (motionEvent.getActionMasked() != 1 || this.mFalsingManager.isUnlockingDisabled() || isFalseGesture()) {
            return false;
        }
        if (!swipedFastEnough() && !swipedFarEnough()) {
            return false;
        }
        if (this.mCallback.canChildBeDismissedInDirection(this.mTouchedView, translation > 0.0f)) {
            return true;
        }
        return false;
    }

    public boolean isFalseGesture() {
        boolean isAntiFalsingNeeded = this.mCallback.isAntiFalsingNeeded();
        if (this.mFalsingManager.isClassifierEnabled()) {
            if (!isAntiFalsingNeeded || !this.mFalsingManager.isFalseTouch(1)) {
                return false;
            }
        } else if (!isAntiFalsingNeeded || this.mTouchAboveFalsingThreshold) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean swipedFastEnough() {
        float velocity = getVelocity(this.mVelocityTracker);
        float translation = getTranslation(this.mTouchedView);
        if (Math.abs(velocity) > getEscapeVelocity()) {
            if ((velocity > 0.0f) == (translation > 0.0f)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSwiping() {
        return this.mIsSwiping;
    }

    public View getSwipedView() {
        if (this.mIsSwiping) {
            return this.mTouchedView;
        }
        return null;
    }

    public void resetSwipeState() {
        this.mTouchedView = null;
        this.mIsSwiping = false;
    }

    public interface Callback {
        boolean canChildBeDismissed(View view);

        boolean canChildBeDragged(View view) {
            return true;
        }

        View getChildAtPosition(MotionEvent motionEvent);

        int getConstrainSwipeStartPosition() {
            return 0;
        }

        float getFalsingThresholdFactor();

        boolean isAntiFalsingNeeded();

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onChildSnappedBack(View view, float f);

        void onDragCancelled(View view);

        boolean updateSwipeProgress(View view, boolean z, float f);

        boolean canChildBeDismissedInDirection(View view, boolean z) {
            return canChildBeDismissed(view);
        }
    }

    public boolean isMoving() {
        return this.mIsSwiping || this.mSnappingChild;
    }
}
