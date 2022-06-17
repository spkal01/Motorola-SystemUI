package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.util.BoostFramework;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.util.LatencyTracker;
import com.android.p011wm.shell.animation.FlingAnimationUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.PanelView;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.motorola.perf.MotoPerfManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class PanelViewController {
    public static final boolean DEBUG_PANEL = Build.IS_DEBUGGABLE;
    public static final String TAG = PanelView.class.getSimpleName();
    protected final AmbientState mAmbientState;
    /* access modifiers changed from: private */
    public boolean mAnimateAfterExpanding;
    /* access modifiers changed from: private */
    public boolean mAnimatingOnDown;
    PanelBar mBar;
    private Interpolator mBounceInterpolator;
    /* access modifiers changed from: private */
    public boolean mClosing;
    /* access modifiers changed from: private */
    public boolean mCollapsedAndHeadsUpOnDown;
    protected long mDownTime;
    private final DozeLog mDozeLog;
    private boolean mExpandLatencyTracking;
    protected float mExpandedFraction = 0.0f;
    protected float mExpandedHeight = 0.0f;
    protected boolean mExpanding;
    protected ArrayList<PanelExpansionListener> mExpansionListeners = new ArrayList<>();
    private final FalsingManager mFalsingManager;
    private int mFixedDuration = -1;
    private FlingAnimationUtils mFlingAnimationUtils;
    private FlingAnimationUtils mFlingAnimationUtilsClosing;
    private FlingAnimationUtils mFlingAnimationUtilsDismissing;
    private final Runnable mFlingCollapseRunnable = new Runnable() {
        public void run() {
            PanelViewController panelViewController = PanelViewController.this;
            panelViewController.fling(0.0f, false, panelViewController.mNextCollapseSpeedUpFactor, false);
        }
    };
    /* access modifiers changed from: private */
    public boolean mGestureWaitForTouchSlop;
    /* access modifiers changed from: private */
    public boolean mHandlingPointerUp;
    /* access modifiers changed from: private */
    public boolean mHasLayoutedSinceDown;
    protected HeadsUpManagerPhone mHeadsUpManager;
    /* access modifiers changed from: private */
    public ValueAnimator mHeightAnimator;
    protected boolean mHintAnimationRunning;
    private float mHintDistance;
    /* access modifiers changed from: private */
    public boolean mIgnoreXTouchSlop;
    /* access modifiers changed from: private */
    public float mInitialOffsetOnTouch;
    /* access modifiers changed from: private */
    public float mInitialTouchX;
    /* access modifiers changed from: private */
    public float mInitialTouchY;
    /* access modifiers changed from: private */
    public boolean mInstantExpanding;
    private boolean mIsFlinging;
    protected boolean mIsLaunchAnimationRunning;
    protected boolean mIsPrcCustom;
    /* access modifiers changed from: private */
    public boolean mIsSpringBackAnimation;
    protected KeyguardBottomAreaView mKeyguardBottomArea;
    protected final KeyguardStateController mKeyguardStateController;
    private float mLastGesturedOverExpansion = -1.0f;
    private final LatencyTracker mLatencyTracker;
    private LockscreenGestureLogger mLockscreenGestureLogger = new LockscreenGestureLogger();
    /* access modifiers changed from: private */
    public float mMinExpandHeight;
    /* access modifiers changed from: private */
    public boolean mMotionAborted;
    /* access modifiers changed from: private */
    public float mNextCollapseSpeedUpFactor = 1.0f;
    /* access modifiers changed from: private */
    public boolean mNotificationsDragEnabled;
    protected float mOverExpansion;
    /* access modifiers changed from: private */
    public boolean mPanelClosedOnDown;
    private float mPanelFlingOvershootAmount;
    protected int mPanelState;
    private boolean mPanelUpdateWhenAnimatorEnds;
    /* access modifiers changed from: private */
    public BoostFramework mPerf = null;
    protected final Runnable mPostCollapseRunnable = new Runnable() {
        public void run() {
            PanelViewController.this.collapse(false, 1.0f);
        }
    };
    protected final Resources mResources;
    private float mSlopMultiplier;
    protected StatusBar mStatusBar;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    protected final SysuiStatusBarStateController mStatusBarStateController;
    protected final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    /* access modifiers changed from: private */
    public boolean mTouchAboveFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mTouchDisabled;
    private int mTouchSlop;
    /* access modifiers changed from: private */
    public boolean mTouchSlopExceeded;
    protected boolean mTouchSlopExceededBeforeDown;
    /* access modifiers changed from: private */
    public boolean mTouchStartedInEmptyArea;
    protected boolean mTracking;
    /* access modifiers changed from: private */
    public int mTrackingPointer;
    private int mUnlockFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mUpdateFlingOnLayout;
    /* access modifiers changed from: private */
    public float mUpdateFlingVelocity;
    /* access modifiers changed from: private */
    public boolean mUpwardsWhenThresholdReached;
    /* access modifiers changed from: private */
    public final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private boolean mVibrateOnOpening;
    private final VibratorHelper mVibratorHelper;
    /* access modifiers changed from: private */
    public final PanelView mView;
    /* access modifiers changed from: private */
    public String mViewName;

    /* access modifiers changed from: protected */
    public abstract boolean canCollapsePanelOnTouch();

    /* access modifiers changed from: protected */
    public abstract void cancelExpandImmediate();

    public abstract OnLayoutChangeListener createLayoutChangeListener();

    /* access modifiers changed from: protected */
    public abstract OnConfigurationChangedListener createOnConfigurationChangedListener();

    /* access modifiers changed from: protected */
    public abstract TouchHandler createTouchHandler();

    /* access modifiers changed from: protected */
    public abstract int getMaxPanelHeight();

    /* access modifiers changed from: protected */
    public abstract boolean isInContentBounds(float f, float f2);

    /* access modifiers changed from: protected */
    public abstract boolean isPanelVisibleBecauseOfHeadsUp();

    /* access modifiers changed from: protected */
    public abstract boolean isTrackingBlocked();

    /* access modifiers changed from: protected */
    public void onExpandingStarted() {
    }

    /* access modifiers changed from: protected */
    public abstract void onHeightUpdated(float f);

    /* access modifiers changed from: protected */
    public abstract boolean onMiddleClicked();

    public abstract void resetViewPrc();

    public abstract void resetViews(boolean z);

    /* access modifiers changed from: protected */
    public abstract void setHeaderState(int i);

    public abstract void setIsShadeOpening(boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean shouldGestureIgnoreXTouchSlop(float f, float f2);

    /* access modifiers changed from: protected */
    public abstract boolean shouldGestureWaitForTouchSlop();

    /* access modifiers changed from: protected */
    public abstract boolean shouldUseDismissingAnimation();

    public abstract void updatePanelStateAfterFling();

    /* access modifiers changed from: protected */
    public void onExpandingFinished() {
        this.mBar.onExpandingFinished();
    }

    /* access modifiers changed from: protected */
    public void notifyExpandingStarted() {
        if (!this.mExpanding) {
            this.mExpanding = true;
            onExpandingStarted();
        }
    }

    /* access modifiers changed from: protected */
    public final void notifyExpandingFinished() {
        endClosing();
        if (this.mExpanding) {
            this.mExpanding = false;
            onExpandingFinished();
        }
    }

    public PanelViewController(PanelView panelView, FalsingManager falsingManager, DozeLog dozeLog, KeyguardStateController keyguardStateController, SysuiStatusBarStateController sysuiStatusBarStateController, VibratorHelper vibratorHelper, StatusBarKeyguardViewManager statusBarKeyguardViewManager, LatencyTracker latencyTracker, FlingAnimationUtils.Builder builder, StatusBarTouchableRegionManager statusBarTouchableRegionManager, AmbientState ambientState) {
        this.mAmbientState = ambientState;
        this.mView = panelView;
        this.mIsPrcCustom = MotoFeature.getInstance(panelView.getContext()).isCustomPanelView();
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        panelView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewDetachedFromWindow(View view) {
            }

            public void onViewAttachedToWindow(View view) {
                PanelViewController panelViewController = PanelViewController.this;
                String unused = panelViewController.mViewName = panelViewController.mResources.getResourceName(panelViewController.mView.getId());
            }
        });
        panelView.addOnLayoutChangeListener(createLayoutChangeListener());
        panelView.setOnTouchListener(createTouchHandler());
        panelView.setOnConfigurationChangedListener(createOnConfigurationChangedListener());
        Resources resources = panelView.getResources();
        this.mResources = resources;
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mFlingAnimationUtils = builder.reset().setMaxLengthSeconds(0.6f).setSpeedUpFactor(0.6f).build();
        this.mFlingAnimationUtilsClosing = builder.reset().setMaxLengthSeconds(0.5f).setSpeedUpFactor(0.6f).build();
        this.mFlingAnimationUtilsDismissing = builder.reset().setMaxLengthSeconds(0.5f).setSpeedUpFactor(0.6f).setX2(0.6f).setY2(0.84f).build();
        this.mLatencyTracker = latencyTracker;
        this.mBounceInterpolator = new BounceInterpolator();
        this.mFalsingManager = falsingManager;
        this.mDozeLog = dozeLog;
        this.mNotificationsDragEnabled = resources.getBoolean(R$bool.config_enableNotificationShadeDrag);
        this.mVibratorHelper = vibratorHelper;
        this.mVibrateOnOpening = resources.getBoolean(R$bool.config_vibrateOnIconAnimation);
        this.mStatusBarTouchableRegionManager = statusBarTouchableRegionManager;
        if (MotoPerfManager.isQcomPerfEnabled()) {
            this.mPerf = new BoostFramework();
        }
    }

    /* access modifiers changed from: protected */
    public void loadDimens() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mView.getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mSlopMultiplier = viewConfiguration.getScaledAmbiguousGestureMultiplier();
        this.mHintDistance = this.mResources.getDimension(R$dimen.hint_move_distance);
        this.mPanelFlingOvershootAmount = this.mResources.getDimension(R$dimen.panel_overshoot_amount);
        this.mUnlockFalsingThreshold = this.mResources.getDimensionPixelSize(R$dimen.unlock_falsing_threshold);
    }

    /* access modifiers changed from: protected */
    public float getTouchSlop(MotionEvent motionEvent) {
        if (motionEvent.getClassification() == 1) {
            return ((float) this.mTouchSlop) * this.mSlopMultiplier;
        }
        return (float) this.mTouchSlop;
    }

    /* access modifiers changed from: private */
    public void addMovement(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        this.mVelocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-rawX, -rawY);
    }

    public void setTouchAndAnimationDisabled(boolean z) {
        this.mTouchDisabled = z;
        if (z) {
            cancelHeightAnimator();
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            notifyExpandingFinished();
        }
    }

    public void startExpandLatencyTracking() {
        if (this.mLatencyTracker.isEnabled()) {
            this.mLatencyTracker.onActionStart(0);
            this.mExpandLatencyTracking = true;
        }
    }

    /* access modifiers changed from: private */
    public void startOpening(MotionEvent motionEvent) {
        notifyBarPanelExpansionChanged();
        maybeVibrateOnOpening();
        float displayWidth = this.mStatusBar.getDisplayWidth();
        float displayHeight = this.mStatusBar.getDisplayHeight();
        this.mLockscreenGestureLogger.writeAtFractionalPosition(1328, (int) ((motionEvent.getX() / displayWidth) * 100.0f), (int) ((motionEvent.getY() / displayHeight) * 100.0f), this.mStatusBar.getRotation());
        this.mLockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_UNLOCKED_NOTIFICATION_PANEL_EXPAND);
    }

    /* access modifiers changed from: protected */
    public void maybeVibrateOnOpening() {
        if (this.mVibrateOnOpening) {
            this.mVibratorHelper.vibrate(2);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDirectionUpwards(float f, float f2) {
        float f3 = f - this.mInitialTouchX;
        float f4 = f2 - this.mInitialTouchY;
        if (f4 < 0.0f && Math.abs(f4) >= Math.abs(f3)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void startExpandMotion(float f, float f2, boolean z, float f3) {
        if (!this.mHandlingPointerUp) {
            beginJankMonitoring(0);
        }
        this.mInitialOffsetOnTouch = f3;
        this.mInitialTouchY = f2;
        this.mInitialTouchX = f;
        if (z) {
            this.mTouchSlopExceeded = true;
            setExpandedHeight(f3);
            onTrackingStarted();
        }
    }

    /* access modifiers changed from: private */
    public void endMotionEvent(MotionEvent motionEvent, float f, float f2, boolean z) {
        this.mTrackingPointer = -1;
        if ((this.mTracking && this.mTouchSlopExceeded) || Math.abs(f - this.mInitialTouchX) > ((float) this.mTouchSlop) || Math.abs(f2 - this.mInitialTouchY) > ((float) this.mTouchSlop) || motionEvent.getActionMasked() == 3 || z) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float yVelocity = this.mVelocityTracker.getYVelocity();
            float hypot = (float) Math.hypot((double) this.mVelocityTracker.getXVelocity(), (double) this.mVelocityTracker.getYVelocity());
            boolean z2 = false;
            boolean z3 = this.mStatusBarStateController.getState() == 1;
            boolean flingExpands = (motionEvent.getActionMasked() == 3 || z) ? z3 ? true : !this.mPanelClosedOnDown : flingExpands(yVelocity, hypot, f, f2);
            this.mDozeLog.traceFling(flingExpands, this.mTouchAboveFalsingThreshold, this.mStatusBar.isFalsingThresholdNeeded(), this.mStatusBar.isWakeUpComingFromTouch());
            if (!flingExpands && z3) {
                float displayDensity = this.mStatusBar.getDisplayDensity();
                this.mLockscreenGestureLogger.write(186, (int) Math.abs((f2 - this.mInitialTouchY) / displayDensity), (int) Math.abs(yVelocity / displayDensity));
                this.mLockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_UNLOCK);
            }
            int i = (yVelocity > 0.0f ? 1 : (yVelocity == 0.0f ? 0 : -1));
            fling(yVelocity, flingExpands, isFalseTouch(f, f2, i == 0 ? 7 : i > 0 ? 0 : this.mKeyguardStateController.canDismissLockScreen() ? 4 : 8));
            onTrackingStopped(flingExpands);
            if (flingExpands && this.mPanelClosedOnDown && !this.mHasLayoutedSinceDown) {
                z2 = true;
            }
            this.mUpdateFlingOnLayout = z2;
            if (z2) {
                this.mUpdateFlingVelocity = yVelocity;
            }
        } else if (!this.mStatusBar.isBouncerShowing() && !this.mStatusBarKeyguardViewManager.isShowingAlternateAuthOrAnimating()) {
            onTrackingStopped(onEmptySpaceClick(this.mInitialTouchX));
        }
        this.mVelocityTracker.clear();
    }

    /* access modifiers changed from: protected */
    public float getCurrentExpandVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return this.mVelocityTracker.getYVelocity();
    }

    /* access modifiers changed from: private */
    public int getFalsingThreshold() {
        return (int) (((float) this.mUnlockFalsingThreshold) * (this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f));
    }

    /* access modifiers changed from: protected */
    public void onTrackingStopped(boolean z) {
        this.mTracking = false;
        this.mBar.onTrackingStopped(z);
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: protected */
    public void onTrackingStarted() {
        endClosing();
        this.mTracking = true;
        this.mBar.onTrackingStarted();
        notifyExpandingStarted();
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: protected */
    public void cancelHeightAnimator() {
        ValueAnimator valueAnimator = this.mHeightAnimator;
        if (valueAnimator != null) {
            if (valueAnimator.isRunning()) {
                this.mPanelUpdateWhenAnimatorEnds = false;
            }
            this.mHeightAnimator.cancel();
        }
        endClosing();
    }

    private void endClosing() {
        if (this.mClosing) {
            this.mClosing = false;
            onClosingFinished();
        }
    }

    /* access modifiers changed from: protected */
    public boolean flingExpands(float f, float f2, float f3, float f4) {
        int i;
        if (this.mFalsingManager.isUnlockingDisabled()) {
            return true;
        }
        int i2 = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i2 > 0) {
            i = 0;
        } else {
            i = this.mKeyguardStateController.canDismissLockScreen() ? 4 : 8;
        }
        if (isFalseTouch(f3, f4, i)) {
            return true;
        }
        if (Math.abs(f2) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
            return shouldExpandWhenNotFlinging();
        }
        if (i2 > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean shouldExpandWhenNotFlinging() {
        return getExpandedFraction() > 0.5f;
    }

    private boolean isFalseTouch(float f, float f2, int i) {
        if (!this.mStatusBar.isFalsingThresholdNeeded()) {
            return false;
        }
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch(i);
        }
        if (!this.mTouchAboveFalsingThreshold) {
            return true;
        }
        if (this.mUpwardsWhenThresholdReached) {
            return false;
        }
        return !isDirectionUpwards(f, f2);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z) {
        fling(f, z, 1.0f, false);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z, boolean z2) {
        fling(f, z, 1.0f, z2);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z, float f2, boolean z2) {
        float maxPanelHeight = z ? (float) getMaxPanelHeight() : 0.0f;
        if (!z) {
            this.mClosing = true;
        }
        flingToHeight(f, z, maxPanelHeight, f2, z2);
    }

    /* access modifiers changed from: protected */
    public void flingToHeight(float f, boolean z, float f2, float f3, boolean z2) {
        int i;
        float f4 = f2;
        if (f4 == this.mExpandedHeight && this.mOverExpansion == 0.0f) {
            endJankMonitoring(0);
            this.mKeyguardStateController.notifyPanelFlingEnd();
            notifyExpandingFinished();
            return;
        }
        this.mIsFlinging = true;
        boolean z3 = z && this.mStatusBarStateController.getState() != 1 && this.mOverExpansion == 0.0f && f >= 0.0f;
        final boolean z4 = z3 || (this.mOverExpansion != 0.0f && z);
        float lerp = z3 ? MathUtils.lerp(0.2f, 1.0f, MathUtils.saturate(f / (this.mFlingAnimationUtils.getHighVelocityPxPerSecond() * 0.5f))) + (this.mOverExpansion / this.mPanelFlingOvershootAmount) : 0.0f;
        ValueAnimator createHeightAnimator = createHeightAnimator(f4, lerp);
        if (z) {
            float f5 = (!z2 || f >= 0.0f) ? f : 0.0f;
            this.mFlingAnimationUtils.apply(createHeightAnimator, this.mExpandedHeight, f4 + (lerp * this.mPanelFlingOvershootAmount), f5, (float) this.mView.getHeight());
            if (f5 == 0.0f) {
                createHeightAnimator.setDuration(350);
            }
            i = -1;
        } else {
            if (!shouldUseDismissingAnimation()) {
                i = -1;
                this.mFlingAnimationUtilsClosing.apply(createHeightAnimator, this.mExpandedHeight, f2, f, (float) this.mView.getHeight());
            } else if (f == 0.0f) {
                createHeightAnimator.setInterpolator(Interpolators.PANEL_CLOSE_ACCELERATED);
                createHeightAnimator.setDuration((long) (((this.mExpandedHeight / ((float) this.mView.getHeight())) * 100.0f) + 200.0f));
                i = -1;
            } else {
                i = -1;
                this.mFlingAnimationUtilsDismissing.apply(createHeightAnimator, this.mExpandedHeight, f2, f, (float) this.mView.getHeight());
            }
            if (f == 0.0f) {
                createHeightAnimator.setDuration((long) (((float) createHeightAnimator.getDuration()) / f3));
            }
            int i2 = this.mFixedDuration;
            if (i2 != i) {
                createHeightAnimator.setDuration((long) i2);
            }
        }
        if (this.mPerf != null) {
            this.mPerf.perfHint(4224, this.mView.getContext().getPackageName(), i, 3);
        }
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationStart(Animator animator) {
                PanelViewController.this.beginJankMonitoring(0);
            }

            public void onAnimationCancel(Animator animator) {
                if (PanelViewController.this.mPerf != null) {
                    PanelViewController.this.mPerf.perfLockRelease();
                }
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (PanelViewController.this.mPerf != null) {
                    PanelViewController.this.mPerf.perfLockRelease();
                }
                if (!z4 || this.mCancelled) {
                    PanelViewController.this.onFlingEnd(this.mCancelled);
                } else {
                    PanelViewController.this.springBack();
                }
                PanelViewController panelViewController = PanelViewController.this;
                if (panelViewController.mIsPrcCustom) {
                    panelViewController.updatePanelStateAfterFling();
                }
            }
        });
        setAnimator(createHeightAnimator);
        createHeightAnimator.start();
    }

    /* access modifiers changed from: private */
    public void springBack() {
        float f = this.mOverExpansion;
        if (f == 0.0f) {
            onFlingEnd(false);
            return;
        }
        this.mIsSpringBackAnimation = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, 0.0f});
        ofFloat.addUpdateListener(new PanelViewController$$ExternalSyntheticLambda0(this));
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                boolean unused = PanelViewController.this.mIsSpringBackAnimation = false;
                PanelViewController.this.onFlingEnd(this.mCancelled);
            }
        });
        setAnimator(ofFloat);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$springBack$0(ValueAnimator valueAnimator) {
        setOverExpansionInternal(((Float) valueAnimator.getAnimatedValue()).floatValue(), false);
    }

    /* access modifiers changed from: private */
    public void onFlingEnd(boolean z) {
        this.mIsFlinging = false;
        setOverExpansionInternal(0.0f, false);
        setAnimator((ValueAnimator) null);
        this.mKeyguardStateController.notifyPanelFlingEnd();
        if (!z) {
            endJankMonitoring(0);
            notifyExpandingFinished();
        } else {
            cancelJankMonitoring(0);
        }
        notifyBarPanelExpansionChanged();
    }

    public void setExpandedHeight(float f) {
        setExpandedHeightInternal(f);
    }

    /* access modifiers changed from: protected */
    public void requestPanelHeightUpdate() {
        float maxPanelHeight = (float) getMaxPanelHeight();
        if (isFullyCollapsed() || maxPanelHeight == this.mExpandedHeight) {
            return;
        }
        if (this.mTracking && !isTrackingBlocked()) {
            return;
        }
        if (this.mHeightAnimator == null || this.mIsSpringBackAnimation) {
            setExpandedHeight(maxPanelHeight);
        } else {
            this.mPanelUpdateWhenAnimatorEnds = true;
        }
    }

    public void setExpandedHeightInternal(float f) {
        float f2;
        if (Float.isNaN(f)) {
            Log.wtf(TAG, "ExpandedHeight set to NaN + " + f);
        }
        if (this.mExpandLatencyTracking && f != 0.0f) {
            DejankUtils.postAfterTraversal(new PanelViewController$$ExternalSyntheticLambda2(this));
            this.mExpandLatencyTracking = false;
        }
        float maxPanelHeight = (float) getMaxPanelHeight();
        if (this.mHeightAnimator == null) {
            if (this.mTracking) {
                setOverExpansionInternal(Math.max(0.0f, f - maxPanelHeight), true);
            }
            this.mExpandedHeight = Math.min(f, maxPanelHeight);
        } else {
            this.mExpandedHeight = f;
        }
        float f3 = this.mExpandedHeight;
        if (f3 < 1.0f && f3 != 0.0f && this.mClosing) {
            this.mExpandedHeight = 0.0f;
            ValueAnimator valueAnimator = this.mHeightAnimator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
        }
        if (maxPanelHeight == 0.0f) {
            f2 = 0.0f;
        } else {
            f2 = this.mExpandedHeight / maxPanelHeight;
        }
        float min = Math.min(1.0f, f2);
        this.mExpandedFraction = min;
        if (Float.isNaN(min)) {
            this.mExpandedFraction = 0.0f;
        }
        onHeightUpdated(this.mExpandedHeight);
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setExpandedHeightInternal$1() {
        this.mLatencyTracker.onActionEnd(0);
    }

    /* access modifiers changed from: protected */
    public void setOverExpansion(float f) {
        this.mOverExpansion = f;
    }

    private void setOverExpansionInternal(float f, boolean z) {
        if (!z) {
            this.mLastGesturedOverExpansion = -1.0f;
            setOverExpansion(f);
        } else if (this.mLastGesturedOverExpansion != f) {
            this.mLastGesturedOverExpansion = f;
            setOverExpansion(Interpolators.getOvershootInterpolation(MathUtils.saturate(f / (((float) this.mView.getHeight()) / 3.0f))) * this.mPanelFlingOvershootAmount * 2.0f);
        }
    }

    public void setExpandedFraction(float f) {
        setExpandedHeight(((float) getMaxPanelHeight()) * f);
    }

    public float getExpandedHeight() {
        return this.mExpandedHeight;
    }

    public float getExpandedFraction() {
        return this.mExpandedFraction;
    }

    public boolean isFullyExpanded() {
        return this.mExpandedHeight >= ((float) getMaxPanelHeight());
    }

    public boolean isFullyCollapsed() {
        return this.mExpandedFraction <= 0.0f;
    }

    public boolean isCollapsing() {
        return this.mClosing || this.mIsLaunchAnimationRunning;
    }

    public boolean isTracking() {
        return this.mTracking;
    }

    public void setBar(PanelBar panelBar) {
        this.mBar = panelBar;
    }

    public void collapse(boolean z, float f) {
        if (canPanelBeCollapsed()) {
            cancelHeightAnimator();
            notifyExpandingStarted();
            this.mClosing = true;
            if (z) {
                this.mNextCollapseSpeedUpFactor = f;
                this.mView.postDelayed(this.mFlingCollapseRunnable, 120);
                return;
            }
            fling(0.0f, false, f, false);
        }
    }

    public boolean canPanelBeCollapsed() {
        return !isFullyCollapsed() && !this.mTracking && !this.mClosing;
    }

    public void expand(boolean z) {
        if (isFullyCollapsed() || isCollapsing()) {
            this.mInstantExpanding = true;
            this.mAnimateAfterExpanding = z;
            this.mUpdateFlingOnLayout = false;
            abortAnimations();
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            if (this.mExpanding) {
                notifyExpandingFinished();
            }
            notifyBarPanelExpansionChanged();
            this.mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (!PanelViewController.this.mInstantExpanding) {
                        PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else if (PanelViewController.this.mStatusBar.getNotificationShadeWindowView().isVisibleToUser()) {
                        PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (PanelViewController.this.mAnimateAfterExpanding) {
                            PanelViewController.this.notifyExpandingStarted();
                            PanelViewController.this.beginJankMonitoring(0);
                            PanelViewController.this.fling(0.0f, true);
                        } else {
                            PanelViewController.this.setExpandedFraction(1.0f);
                        }
                        boolean unused = PanelViewController.this.mInstantExpanding = false;
                    }
                }
            });
            this.mView.requestLayout();
            return;
        }
        cancelExpandImmediate();
    }

    public void instantCollapse() {
        abortAnimations();
        setExpandedFraction(0.0f);
        if (this.mExpanding) {
            notifyExpandingFinished();
        }
        if (this.mInstantExpanding) {
            this.mInstantExpanding = false;
            notifyBarPanelExpansionChanged();
        }
    }

    /* access modifiers changed from: private */
    public void abortAnimations() {
        cancelHeightAnimator();
        this.mView.removeCallbacks(this.mPostCollapseRunnable);
        this.mView.removeCallbacks(this.mFlingCollapseRunnable);
    }

    /* access modifiers changed from: protected */
    public void onClosingFinished() {
        this.mBar.onClosingFinished();
    }

    /* access modifiers changed from: protected */
    public void startUnlockHintAnimation() {
        if (this.mHeightAnimator == null && !this.mTracking) {
            notifyExpandingStarted();
            startUnlockHintAnimationPhase1(new PanelViewController$$ExternalSyntheticLambda3(this));
            onUnlockHintStarted();
            this.mHintAnimationRunning = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startUnlockHintAnimation$2() {
        notifyExpandingFinished();
        onUnlockHintFinished();
        this.mHintAnimationRunning = false;
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintFinished() {
        this.mStatusBar.onHintFinished();
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintStarted() {
        this.mStatusBar.onUnlockHintStarted();
    }

    public boolean isUnlockHintRunning() {
        return this.mHintAnimationRunning;
    }

    private void startUnlockHintAnimationPhase1(final Runnable runnable) {
        ValueAnimator createHeightAnimator = createHeightAnimator(Math.max(0.0f, ((float) getMaxPanelHeight()) - this.mHintDistance));
        createHeightAnimator.setDuration(250);
        createHeightAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mCancelled) {
                    PanelViewController.this.setAnimator((ValueAnimator) null);
                    runnable.run();
                    return;
                }
                PanelViewController.this.startUnlockHintAnimationPhase2(runnable);
            }
        });
        createHeightAnimator.start();
        setAnimator(createHeightAnimator);
        View[] viewArr = {this.mKeyguardBottomArea.getIndicationArea(), this.mStatusBar.getAmbientIndicationContainer()};
        for (int i = 0; i < 2; i++) {
            View view = viewArr[i];
            if (view != null) {
                view.animate().cancel();
                view.setTranslationY(0.0f);
                view.animate().translationY(-this.mHintDistance).setDuration(250).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new PanelViewController$$ExternalSyntheticLambda4(this, view)).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startUnlockHintAnimationPhase1$3(View view) {
        view.animate().translationY(0.0f).setDuration(450).setInterpolator(this.mBounceInterpolator).start();
    }

    /* access modifiers changed from: private */
    public void setAnimator(ValueAnimator valueAnimator) {
        this.mHeightAnimator = valueAnimator;
        if (valueAnimator == null && this.mPanelUpdateWhenAnimatorEnds) {
            this.mPanelUpdateWhenAnimatorEnds = false;
            requestPanelHeightUpdate();
        }
    }

    /* access modifiers changed from: private */
    public void startUnlockHintAnimationPhase2(final Runnable runnable) {
        ValueAnimator createHeightAnimator = createHeightAnimator((float) getMaxPanelHeight());
        createHeightAnimator.setDuration(450);
        createHeightAnimator.setInterpolator(this.mBounceInterpolator);
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PanelViewController.this.setAnimator((ValueAnimator) null);
                runnable.run();
                PanelViewController.this.notifyBarPanelExpansionChanged();
            }
        });
        createHeightAnimator.start();
        setAnimator(createHeightAnimator);
    }

    private ValueAnimator createHeightAnimator(float f) {
        return createHeightAnimator(f, 0.0f);
    }

    private ValueAnimator createHeightAnimator(float f, float f2) {
        float f3 = this.mOverExpansion;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mExpandedHeight, f});
        ofFloat.addUpdateListener(new PanelViewController$$ExternalSyntheticLambda1(this, f2, f, f3, ofFloat));
        return ofFloat;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createHeightAnimator$4(float f, float f2, float f3, ValueAnimator valueAnimator, ValueAnimator valueAnimator2) {
        if (f > 0.0f || (f2 == 0.0f && f3 != 0.0f)) {
            setOverExpansionInternal(MathUtils.lerp(f3, this.mPanelFlingOvershootAmount * f, Interpolators.FAST_OUT_SLOW_IN.getInterpolation(valueAnimator.getAnimatedFraction())), false);
        }
        setExpandedHeightInternal(((Float) valueAnimator2.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public void notifyBarPanelExpansionChanged() {
        PanelBar panelBar = this.mBar;
        if (panelBar != null) {
            float f = this.mExpandedFraction;
            panelBar.panelExpansionChanged(f, f > 0.0f || this.mInstantExpanding || isPanelVisibleBecauseOfHeadsUp() || this.mTracking || (this.mHeightAnimator != null && !this.mIsSpringBackAnimation));
        }
        for (int i = 0; i < this.mExpansionListeners.size(); i++) {
            this.mExpansionListeners.get(i).onPanelExpansionChanged(this.mExpandedFraction, this.mTracking);
        }
    }

    public void addExpansionListener(PanelExpansionListener panelExpansionListener) {
        this.mExpansionListeners.add(panelExpansionListener);
    }

    public void removeExpansionListener(PanelExpansionListener panelExpansionListener) {
        this.mExpansionListeners.remove(panelExpansionListener);
    }

    /* access modifiers changed from: protected */
    public boolean onEmptySpaceClick(float f) {
        if (this.mHintAnimationRunning) {
            return true;
        }
        return onMiddleClicked();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Object[] objArr = new Object[8];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Float.valueOf(getExpandedHeight());
        objArr[2] = Integer.valueOf(getMaxPanelHeight());
        String str = "T";
        objArr[3] = this.mClosing ? str : "f";
        objArr[4] = this.mTracking ? str : "f";
        ValueAnimator valueAnimator = this.mHeightAnimator;
        objArr[5] = valueAnimator;
        objArr[6] = (valueAnimator == null || !valueAnimator.isStarted()) ? "" : " (started)";
        if (!this.mTouchDisabled) {
            str = "f";
        }
        objArr[7] = str;
        printWriter.println(String.format("[PanelView(%s): expandedHeight=%f maxPanelHeight=%d closing=%s tracking=%s timeAnim=%s%s touchDisabled=%s]", objArr));
    }

    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManagerPhone) {
        this.mHeadsUpManager = headsUpManagerPhone;
    }

    public void setIsLaunchAnimationRunning(boolean z) {
        this.mIsLaunchAnimationRunning = z;
    }

    public void collapseWithDuration(int i) {
        this.mFixedDuration = i;
        collapse(false, 1.0f);
        this.mFixedDuration = -1;
    }

    public ViewGroup getView() {
        return this.mView;
    }

    public boolean isEnabled() {
        return this.mView.isEnabled();
    }

    public class TouchHandler implements View.OnTouchListener {
        public TouchHandler() {
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int pointerId;
            if (PanelViewController.this.mInstantExpanding || !PanelViewController.this.mNotificationsDragEnabled || PanelViewController.this.mTouchDisabled) {
                return false;
            }
            if (PanelViewController.this.mMotionAborted && motionEvent.getActionMasked() != 0) {
                return false;
            }
            int findPointerIndex = motionEvent.findPointerIndex(PanelViewController.this.mTrackingPointer);
            if (findPointerIndex < 0) {
                int unused = PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(0);
                findPointerIndex = 0;
            }
            float x = motionEvent.getX(findPointerIndex);
            float y = motionEvent.getY(findPointerIndex);
            boolean canCollapsePanelOnTouch = PanelViewController.this.canCollapsePanelOnTouch();
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        float access$2100 = y - PanelViewController.this.mInitialTouchY;
                        PanelViewController.this.addMovement(motionEvent);
                        if (canCollapsePanelOnTouch || PanelViewController.this.mTouchStartedInEmptyArea || PanelViewController.this.mAnimatingOnDown) {
                            float abs = Math.abs(access$2100);
                            float touchSlop = PanelViewController.this.getTouchSlop(motionEvent);
                            if ((access$2100 < (-touchSlop) || (PanelViewController.this.mAnimatingOnDown && abs > touchSlop)) && abs > Math.abs(x - PanelViewController.this.mInitialTouchX)) {
                                PanelViewController.this.cancelHeightAnimator();
                                PanelViewController panelViewController = PanelViewController.this;
                                panelViewController.startExpandMotion(x, y, true, panelViewController.mExpandedHeight);
                                return true;
                            }
                        }
                    } else if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6 && PanelViewController.this.mTrackingPointer == (pointerId = motionEvent.getPointerId(motionEvent.getActionIndex()))) {
                                int i = motionEvent.getPointerId(0) != pointerId ? 0 : 1;
                                int unused2 = PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(i);
                                float unused3 = PanelViewController.this.mInitialTouchX = motionEvent.getX(i);
                                float unused4 = PanelViewController.this.mInitialTouchY = motionEvent.getY(i);
                            }
                        } else if (PanelViewController.this.mStatusBarStateController.getState() == 1) {
                            boolean unused5 = PanelViewController.this.mMotionAborted = true;
                            PanelViewController.this.mVelocityTracker.clear();
                        }
                    }
                }
                PanelViewController.this.mVelocityTracker.clear();
            } else {
                PanelViewController.this.mStatusBar.userActivity();
                PanelViewController panelViewController2 = PanelViewController.this;
                boolean unused6 = panelViewController2.mAnimatingOnDown = panelViewController2.mHeightAnimator != null && !PanelViewController.this.mIsSpringBackAnimation;
                float unused7 = PanelViewController.this.mMinExpandHeight = 0.0f;
                PanelViewController.this.mDownTime = SystemClock.uptimeMillis();
                if (PanelViewController.this.mAnimatingOnDown && PanelViewController.this.mClosing) {
                    PanelViewController panelViewController3 = PanelViewController.this;
                    if (!panelViewController3.mHintAnimationRunning) {
                        panelViewController3.cancelHeightAnimator();
                        boolean unused8 = PanelViewController.this.mTouchSlopExceeded = true;
                        return true;
                    }
                }
                float unused9 = PanelViewController.this.mInitialTouchY = y;
                float unused10 = PanelViewController.this.mInitialTouchX = x;
                PanelViewController panelViewController4 = PanelViewController.this;
                boolean unused11 = panelViewController4.mTouchStartedInEmptyArea = !panelViewController4.isInContentBounds(x, y);
                PanelViewController panelViewController5 = PanelViewController.this;
                boolean unused12 = panelViewController5.mTouchSlopExceeded = panelViewController5.mTouchSlopExceededBeforeDown;
                boolean unused13 = PanelViewController.this.mMotionAborted = false;
                PanelViewController panelViewController6 = PanelViewController.this;
                boolean unused14 = panelViewController6.mPanelClosedOnDown = panelViewController6.isFullyCollapsed();
                boolean unused15 = PanelViewController.this.mCollapsedAndHeadsUpOnDown = false;
                boolean unused16 = PanelViewController.this.mHasLayoutedSinceDown = false;
                boolean unused17 = PanelViewController.this.mUpdateFlingOnLayout = false;
                boolean unused18 = PanelViewController.this.mTouchAboveFalsingThreshold = false;
                PanelViewController.this.addMovement(motionEvent);
            }
            if (PanelViewController.this.mView.getVisibility() != 0) {
                return true;
            }
            return false;
        }

        /* JADX WARNING: Removed duplicated region for block: B:113:0x0264  */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0266  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
            /*
                r7 = this;
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mInstantExpanding
                r0 = 0
                if (r8 != 0) goto L_0x02a4
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mTouchDisabled
                r1 = 3
                if (r8 == 0) goto L_0x0018
                int r8 = r9.getActionMasked()
                if (r8 != r1) goto L_0x02a4
            L_0x0018:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mMotionAborted
                if (r8 == 0) goto L_0x0028
                int r8 = r9.getActionMasked()
                if (r8 == 0) goto L_0x0028
                goto L_0x02a4
            L_0x0028:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mNotificationsDragEnabled
                r2 = 1
                if (r8 != 0) goto L_0x003b
                com.android.systemui.statusbar.phone.PanelViewController r7 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r7.mTracking
                if (r8 == 0) goto L_0x003a
                r7.onTrackingStopped(r2)
            L_0x003a:
                return r0
            L_0x003b:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.isFullyCollapsed()
                if (r8 == 0) goto L_0x0062
                r8 = 8194(0x2002, float:1.1482E-41)
                boolean r8 = r9.isFromSource(r8)
                if (r8 == 0) goto L_0x0062
                int r8 = r9.getAction()
                if (r8 != 0) goto L_0x0056
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mMotionAborted = r0
            L_0x0056:
                int r8 = r9.getAction()
                if (r8 != r2) goto L_0x0061
                com.android.systemui.statusbar.phone.PanelViewController r7 = com.android.systemui.statusbar.phone.PanelViewController.this
                r7.expand(r2)
            L_0x0061:
                return r2
            L_0x0062:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                int r8 = r8.mTrackingPointer
                int r8 = r9.findPointerIndex(r8)
                if (r8 >= 0) goto L_0x0078
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                int r3 = r9.getPointerId(r0)
                int unused = r8.mTrackingPointer = r3
                r8 = r0
            L_0x0078:
                float r3 = r9.getX(r8)
                float r8 = r9.getY(r8)
                int r4 = r9.getActionMasked()
                if (r4 != 0) goto L_0x00a6
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r5 = r4.shouldGestureWaitForTouchSlop()
                boolean unused = r4.mGestureWaitForTouchSlop = r5
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r5 = r4.isFullyCollapsed()
                if (r5 != 0) goto L_0x00a2
                com.android.systemui.statusbar.phone.PanelViewController r5 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r5 = r5.shouldGestureIgnoreXTouchSlop(r3, r8)
                if (r5 == 0) goto L_0x00a0
                goto L_0x00a2
            L_0x00a0:
                r5 = r0
                goto L_0x00a3
            L_0x00a2:
                r5 = r2
            L_0x00a3:
                boolean unused = r4.mIgnoreXTouchSlop = r5
            L_0x00a6:
                int r4 = r9.getActionMasked()
                r5 = 0
                if (r4 == 0) goto L_0x01ec
                if (r4 == r2) goto L_0x01c6
                r6 = 2
                if (r4 == r6) goto L_0x010e
                if (r4 == r1) goto L_0x01c6
                r1 = 5
                if (r4 == r1) goto L_0x00f9
                r8 = 6
                if (r4 == r8) goto L_0x00bc
                goto L_0x0295
            L_0x00bc:
                int r8 = r9.getActionIndex()
                int r8 = r9.getPointerId(r8)
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                int r1 = r1.mTrackingPointer
                if (r1 != r8) goto L_0x0295
                int r1 = r9.getPointerId(r0)
                if (r1 == r8) goto L_0x00d4
                r8 = r0
                goto L_0x00d5
            L_0x00d4:
                r8 = r2
            L_0x00d5:
                float r1 = r9.getY(r8)
                float r3 = r9.getX(r8)
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                int r8 = r9.getPointerId(r8)
                int unused = r4.mTrackingPointer = r8
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mHandlingPointerUp = r2
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r9 = r8.mExpandedHeight
                r8.startExpandMotion(r3, r1, r2, r9)
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mHandlingPointerUp = r0
                goto L_0x0295
            L_0x00f9:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                com.android.systemui.statusbar.SysuiStatusBarStateController r1 = r1.mStatusBarStateController
                int r1 = r1.getState()
                if (r1 != r2) goto L_0x0295
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r1.mMotionAborted = r2
                com.android.systemui.statusbar.phone.PanelViewController r7 = com.android.systemui.statusbar.phone.PanelViewController.this
                r7.endMotionEvent(r9, r3, r8, r2)
                return r0
            L_0x010e:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                r1.addMovement(r9)
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r1 = r1.mInitialTouchY
                float r1 = r8 - r1
                float r4 = java.lang.Math.abs(r1)
                com.android.systemui.statusbar.phone.PanelViewController r6 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r9 = r6.getTouchSlop(r9)
                int r9 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r9 <= 0) goto L_0x017a
                float r9 = java.lang.Math.abs(r1)
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r4 = r4.mInitialTouchX
                float r4 = r3 - r4
                float r4 = java.lang.Math.abs(r4)
                int r9 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
                if (r9 > 0) goto L_0x0145
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r9 = r9.mIgnoreXTouchSlop
                if (r9 == 0) goto L_0x017a
            L_0x0145:
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r9.mTouchSlopExceeded = r2
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r9 = r9.mGestureWaitForTouchSlop
                if (r9 == 0) goto L_0x017a
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r4 = r9.mTracking
                if (r4 != 0) goto L_0x017a
                boolean r9 = r9.mCollapsedAndHeadsUpOnDown
                if (r9 != 0) goto L_0x017a
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r9 = r9.mInitialOffsetOnTouch
                int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
                if (r9 == 0) goto L_0x0170
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r1 = r9.mExpandedHeight
                r9.startExpandMotion(r3, r8, r0, r1)
                r1 = r5
            L_0x0170:
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                r9.cancelHeightAnimator()
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                r9.onTrackingStarted()
            L_0x017a:
                com.android.systemui.statusbar.phone.PanelViewController r9 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r9 = r9.mInitialOffsetOnTouch
                float r9 = r9 + r1
                float r9 = java.lang.Math.max(r5, r9)
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r4 = r4.mMinExpandHeight
                float r9 = java.lang.Math.max(r9, r4)
                float r1 = -r1
                com.android.systemui.statusbar.phone.PanelViewController r4 = com.android.systemui.statusbar.phone.PanelViewController.this
                int r4 = r4.getFalsingThreshold()
                float r4 = (float) r4
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 < 0) goto L_0x01a9
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r1.mTouchAboveFalsingThreshold = r2
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r1.isDirectionUpwards(r3, r8)
                boolean unused = r1.mUpwardsWhenThresholdReached = r8
            L_0x01a9:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mGestureWaitForTouchSlop
                if (r8 == 0) goto L_0x01b7
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mTracking
                if (r8 == 0) goto L_0x0295
            L_0x01b7:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.isTrackingBlocked()
                if (r8 != 0) goto L_0x0295
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.setExpandedHeightInternal(r9)
                goto L_0x0295
            L_0x01c6:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                r1.addMovement(r9)
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                r1.endMotionEvent(r9, r3, r8, r0)
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                android.animation.ValueAnimator r8 = r8.mHeightAnimator
                if (r8 != 0) goto L_0x0295
                int r8 = r9.getActionMasked()
                if (r8 != r2) goto L_0x01e5
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.endJankMonitoring(r0)
                goto L_0x0295
            L_0x01e5:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.cancelJankMonitoring(r0)
                goto L_0x0295
            L_0x01ec:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                float r4 = r1.mExpandedHeight
                r1.startExpandMotion(r3, r8, r0, r4)
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                float unused = r8.mMinExpandHeight = r5
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r1 = r8.isFullyCollapsed()
                boolean unused = r8.mPanelClosedOnDown = r1
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mHasLayoutedSinceDown = r0
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mUpdateFlingOnLayout = r0
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mMotionAborted = r0
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                long r3 = android.os.SystemClock.uptimeMillis()
                r8.mDownTime = r3
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean unused = r8.mTouchAboveFalsingThreshold = r0
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r1 = r8.isFullyCollapsed()
                if (r1 == 0) goto L_0x0231
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                com.android.systemui.statusbar.phone.HeadsUpManagerPhone r1 = r1.mHeadsUpManager
                boolean r1 = r1.hasPinnedHeadsUp()
                if (r1 == 0) goto L_0x0231
                r1 = r2
                goto L_0x0232
            L_0x0231:
                r1 = r0
            L_0x0232:
                boolean unused = r8.mCollapsedAndHeadsUpOnDown = r1
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.addMovement(r9)
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                android.animation.ValueAnimator r8 = r8.mHeightAnimator
                if (r8 == 0) goto L_0x0250
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r1 = r8.mHintAnimationRunning
                if (r1 != 0) goto L_0x0250
                boolean r8 = r8.mIsSpringBackAnimation
                if (r8 != 0) goto L_0x0250
                r8 = r2
                goto L_0x0251
            L_0x0250:
                r8 = r0
            L_0x0251:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r1 = r1.mGestureWaitForTouchSlop
                if (r1 == 0) goto L_0x025b
                if (r8 == 0) goto L_0x0274
            L_0x025b:
                com.android.systemui.statusbar.phone.PanelViewController r1 = com.android.systemui.statusbar.phone.PanelViewController.this
                if (r8 != 0) goto L_0x0266
                boolean r8 = r1.mTouchSlopExceededBeforeDown
                if (r8 == 0) goto L_0x0264
                goto L_0x0266
            L_0x0264:
                r8 = r0
                goto L_0x0267
            L_0x0266:
                r8 = r2
            L_0x0267:
                boolean unused = r1.mTouchSlopExceeded = r8
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.cancelHeightAnimator()
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.onTrackingStarted()
            L_0x0274:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.isFullyCollapsed()
                if (r8 == 0) goto L_0x0295
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                com.android.systemui.statusbar.phone.HeadsUpManagerPhone r8 = r8.mHeadsUpManager
                boolean r8 = r8.hasPinnedHeadsUp()
                if (r8 != 0) goto L_0x0295
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                com.android.systemui.statusbar.phone.StatusBar r8 = r8.mStatusBar
                boolean r8 = r8.isBouncerShowing()
                if (r8 != 0) goto L_0x0295
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                r8.startOpening(r9)
            L_0x0295:
                com.android.systemui.statusbar.phone.PanelViewController r8 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r8 = r8.mGestureWaitForTouchSlop
                if (r8 == 0) goto L_0x02a3
                com.android.systemui.statusbar.phone.PanelViewController r7 = com.android.systemui.statusbar.phone.PanelViewController.this
                boolean r7 = r7.mTracking
                if (r7 == 0) goto L_0x02a4
            L_0x02a3:
                r0 = r2
            L_0x02a4:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PanelViewController.TouchHandler.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    public boolean isOpenQSState() {
        return this.mPanelState == 2;
    }

    public boolean isOpenNotificationState() {
        return this.mPanelState == 1;
    }

    public boolean isKeyguardOpenQSState() {
        return this.mPanelState == 4;
    }

    public boolean isNormalState() {
        return this.mPanelState == 0;
    }

    public void updatePanelViewState(int i) {
        if (this.mIsPrcCustom && this.mPanelState != i) {
            if (DEBUG_PANEL) {
                String str = TAG;
                Log.i(str, "PrcPanel updatePanelViewState " + i);
            }
            this.mPanelState = i;
            setHeaderState(i);
            if (this.mPanelState == 0) {
                resetViewPrc();
            }
        }
    }

    public boolean isStatusBarShadeLockedOrKeyguard() {
        if (this.mStatusBarStateController.getState() == 2 || this.mStatusBarStateController.getState() == 1) {
            return true;
        }
        return false;
    }

    public boolean isStatusBarShade() {
        return this.mStatusBarStateController.getState() == 0;
    }

    public boolean isKeyguardNotificationState() {
        return this.mPanelState == 3;
    }

    public boolean isStatusBarShadeLocked() {
        return this.mStatusBarStateController.getState() == 2;
    }

    public class OnLayoutChangeListener implements View.OnLayoutChangeListener {
        public OnLayoutChangeListener() {
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            PanelViewController.this.requestPanelHeightUpdate();
            boolean unused = PanelViewController.this.mHasLayoutedSinceDown = true;
            if (PanelViewController.this.mUpdateFlingOnLayout) {
                PanelViewController.this.abortAnimations();
                PanelViewController panelViewController = PanelViewController.this;
                panelViewController.fling(panelViewController.mUpdateFlingVelocity, true);
                boolean unused2 = PanelViewController.this.mUpdateFlingOnLayout = false;
            }
        }
    }

    public class OnConfigurationChangedListener implements PanelView.OnConfigurationChangedListener {
        public OnConfigurationChangedListener() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            PanelViewController.this.loadDimens();
        }
    }

    /* access modifiers changed from: private */
    public void beginJankMonitoring(int i) {
        InteractionJankMonitor.getInstance().begin(new InteractionJankMonitor.Configuration.Builder(i).setView(this.mView).setTag(isFullyCollapsed() ? "Expand" : "Collapse"));
    }

    /* access modifiers changed from: private */
    public void endJankMonitoring(int i) {
        InteractionJankMonitor.getInstance().end(i);
    }

    /* access modifiers changed from: private */
    public void cancelJankMonitoring(int i) {
        InteractionJankMonitor.getInstance().cancel(i);
    }

    public void resetPanelViewForBiometric() {
        if (this.mStatusBarStateController.getState() == 1 || this.mStatusBarStateController.getState() == 2) {
            this.mMotionAborted = true;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
            cancelHeightAnimator();
            onTrackingStopped(false);
        }
    }
}
