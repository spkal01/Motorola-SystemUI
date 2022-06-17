package com.motorola.systemui.cli.navgesture.inputconsumers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.InputMonitor;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.systemui.R$dimen;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.animation.GestureState;
import com.motorola.systemui.cli.navgesture.animation.RecentsAnimationDeviceState;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationCallbacks;
import com.motorola.systemui.cli.navgesture.animation.remote.TaskAnimationManager;
import com.motorola.systemui.cli.navgesture.animation.remote.WindowTransformHelper;
import com.motorola.systemui.cli.navgesture.notifier.IGestureEndTargetNotifier;
import com.motorola.systemui.cli.navgesture.util.CachedEventDispatcher;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.util.MotionPauseDetector;
import com.motorola.systemui.cli.navgesture.util.NavBarPosition;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.function.Consumer;

public class OtherActivityInputConsumer extends ContextWrapper implements InputConsumer {
    private static final String LOG_TAG = OtherActivityInputConsumer.class.getSimpleName();
    private RecentsAnimationCallbacks mActiveCallbacks;
    private int mActivePointerId = -1;
    private final ActivityControlHelper<BaseGestureActivity> mActivityControlHelper;
    private final Runnable mCancelRecentsAnimationRunnable = OtherActivityInputConsumer$$ExternalSyntheticLambda2.INSTANCE;
    private final RecentsAnimationDeviceState mDeviceState;
    private final boolean mDisableHorizontalSwipe;
    private final PointF mDownPos = new PointF();
    private final IGestureEndTargetNotifier mGestureEndTargetNotifier;
    private final GestureState mGestureState;
    private final InputConsumerController mInputConsumer;
    private final InputMonitor mInputMonitor;
    private WindowTransformHelper mInteractionHandler;
    private final boolean mIsDeferredDownTarget;
    private final PointF mLastPos = new PointF();
    protected Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private final NavBarPosition mNavBarPosition;
    private final Consumer<OtherActivityInputConsumer> mOnCompleteCallback;
    private boolean mPassedPilferInputSlop;
    private boolean mPassedSlopOnThisGesture;
    private boolean mPassedWindowMoveSlop;
    private final CachedEventDispatcher mRecentsViewDispatcher = new CachedEventDispatcher();
    private final float mSquaredTouchSlop;
    protected float mStartDisplacement;
    private final TaskAnimationManager mTaskAnimationManager;
    private final float mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public int getType() {
        return 4;
    }

    public boolean isConsumerDetachedFromGesture() {
        return true;
    }

    public OtherActivityInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputConsumerController inputConsumerController, boolean z, boolean z2, InputMonitor inputMonitor, Consumer<OtherActivityInputConsumer> consumer, IGestureEndTargetNotifier iGestureEndTargetNotifier) {
        super(context);
        this.mDeviceState = recentsAnimationDeviceState;
        this.mGestureState = gestureState;
        this.mInputConsumer = inputConsumerController;
        this.mOnCompleteCallback = consumer;
        this.mInputMonitor = inputMonitor;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mActivityControlHelper = gestureState.getActivityInterface();
        NavBarPosition navBarPosition = recentsAnimationDeviceState.getNavBarPosition();
        this.mNavBarPosition = navBarPosition;
        this.mVelocityTracker = VelocityTracker.obtain();
        boolean z3 = true;
        this.mMotionPauseDetector = new MotionPauseDetector(context, false, (navBarPosition.isLeftEdge() || navBarPosition.isRightEdge()) ? 0 : 1);
        this.mMotionPauseMinDisplacement = context.getResources().getDimension(R$dimen.motion_pause_detector_min_displacement_from_app);
        boolean isRecentsAnimationRunning = taskAnimationManager.isRecentsAnimationRunning();
        this.mIsDeferredDownTarget = !isRecentsAnimationRunning && z;
        float scaledTouchSlop = (float) ViewConfiguration.get(this).getScaledTouchSlop();
        this.mTouchSlop = scaledTouchSlop;
        float f = scaledTouchSlop * 2.0f;
        this.mSquaredTouchSlop = f * f;
        this.mPassedWindowMoveSlop = isRecentsAnimationRunning;
        this.mPassedPilferInputSlop = isRecentsAnimationRunning;
        this.mDisableHorizontalSwipe = (isRecentsAnimationRunning || !z2) ? false : z3;
        this.mGestureEndTargetNotifier = iGestureEndTargetNotifier;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker != null) {
            if (this.mPassedWindowMoveSlop && this.mInteractionHandler != null && !this.mRecentsViewDispatcher.hasConsumer()) {
                this.mRecentsViewDispatcher.setConsumer(this.mInteractionHandler.getRecentsViewDispatcher(this.mNavBarPosition.getRotation()));
            }
            int edgeFlags = motionEvent.getEdgeFlags();
            motionEvent.setEdgeFlags(edgeFlags | 256);
            this.mRecentsViewDispatcher.dispatchEvent(motionEvent);
            motionEvent.setEdgeFlags(edgeFlags);
            this.mVelocityTracker.addMovement(motionEvent);
            if (motionEvent.getActionMasked() == 6) {
                this.mVelocityTracker.clear();
                this.mMotionPauseDetector.clear();
            }
            int actionMasked = motionEvent.getActionMasked();
            boolean z = false;
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (findPointerIndex != -1) {
                            this.mLastPos.set(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                            float displacement = getDisplacement(motionEvent);
                            PointF pointF = this.mLastPos;
                            float f = pointF.x;
                            PointF pointF2 = this.mDownPos;
                            float f2 = f - pointF2.x;
                            float f3 = pointF.y - pointF2.y;
                            if (!this.mPassedWindowMoveSlop && !this.mIsDeferredDownTarget) {
                                float abs = Math.abs(displacement);
                                float f4 = this.mTouchSlop;
                                if (abs > f4) {
                                    this.mPassedWindowMoveSlop = true;
                                    this.mStartDisplacement = Math.min(displacement, -f4);
                                }
                            }
                            float abs2 = Math.abs(f2);
                            float f5 = -displacement;
                            boolean z2 = Utilities.squaredHypot(f2, f3) >= this.mSquaredTouchSlop;
                            if (!this.mPassedSlopOnThisGesture && z2) {
                                this.mPassedSlopOnThisGesture = true;
                            }
                            boolean z3 = (!this.mPassedSlopOnThisGesture && this.mPassedPilferInputSlop) || abs2 > f5;
                            if (!this.mPassedPilferInputSlop && z2) {
                                if (!this.mDisableHorizontalSwipe || Math.abs(f2) <= Math.abs(f3)) {
                                    this.mPassedPilferInputSlop = true;
                                    if (this.mIsDeferredDownTarget) {
                                        startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
                                    }
                                    if (!this.mPassedWindowMoveSlop) {
                                        this.mPassedWindowMoveSlop = true;
                                        this.mStartDisplacement = Math.min(displacement, -this.mTouchSlop);
                                    }
                                    notifyGestureStarted(z3);
                                } else {
                                    forceCancelGesture(motionEvent);
                                    return;
                                }
                            }
                            WindowTransformHelper windowTransformHelper = this.mInteractionHandler;
                            if (windowTransformHelper != null) {
                                if (this.mPassedWindowMoveSlop) {
                                    windowTransformHelper.updateDisplacement(displacement - this.mStartDisplacement);
                                }
                                if (this.mDeviceState.isFullyGesturalNavMode()) {
                                    MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
                                    if (f5 < this.mMotionPauseMinDisplacement || z3) {
                                        z = true;
                                    }
                                    motionPauseDetector.setDisallowPause(z);
                                    this.mMotionPauseDetector.addPosition(motionEvent);
                                    this.mInteractionHandler.setIsLikelyToStartNewTask(z3);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                int actionIndex = motionEvent.getActionIndex();
                                if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                                    if (actionIndex == 0) {
                                        z = true;
                                    }
                                    this.mDownPos.set(motionEvent.getX(z ? 1 : 0) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getY(z) - (this.mLastPos.y - this.mDownPos.y));
                                    this.mLastPos.set(motionEvent.getX(z), motionEvent.getY(z));
                                    this.mActivePointerId = motionEvent.getPointerId(z);
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (!this.mPassedPilferInputSlop) {
                            if (!this.mDeviceState.isInSwipeUpTouchRegion(motionEvent, motionEvent.getActionIndex())) {
                                forceCancelGesture(motionEvent);
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                }
                finishTouchTracking(motionEvent);
                return;
            }
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            this.mLastPos.set(this.mDownPos);
            Log.d(LOG_TAG, "mIsDeferredDownTarget = " + this.mIsDeferredDownTarget);
            if (!this.mIsDeferredDownTarget) {
                startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
            }
        }
    }

    private void forceCancelGesture(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        motionEvent.setAction(3);
        finishTouchTracking(motionEvent);
        motionEvent.setAction(action);
    }

    /* access modifiers changed from: protected */
    public void notifyGestureStarted(boolean z) {
        Log.d(LOG_TAG, "startQuickstep notifyGestureStarted");
        if (this.mInteractionHandler != null) {
            this.mInputMonitor.pilferPointers();
            ActivityManagerWrapper.getInstance().closeSystemWindows("recentapps");
            this.mInteractionHandler.onGestureStarted(z);
        }
    }

    /* access modifiers changed from: protected */
    public void startTouchTrackingForWindowAnimation(long j) {
        WindowTransformHelper windowTransformHelper = new WindowTransformHelper(this, this.mActivityControlHelper, this.mInputConsumer, this.mGestureEndTargetNotifier, this.mGestureState);
        this.mInteractionHandler = windowTransformHelper;
        windowTransformHelper.setGestureEndCallback(new OtherActivityInputConsumer$$ExternalSyntheticLambda1(this));
        this.mMotionPauseDetector.setOnMotionPauseListener(new OtherActivityInputConsumer$$ExternalSyntheticLambda0(windowTransformHelper));
        Intent intent = new Intent(this.mGestureState.getOverviewIntent());
        windowTransformHelper.initWhenReady(intent);
        String str = LOG_TAG;
        Log.d(str, "startTouchTrackingForWindowAnimation intent = " + intent.toString() + "; isRecentsAnimationRunning = " + this.mTaskAnimationManager.isRecentsAnimationRunning());
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            RecentsAnimationCallbacks continueRecentsAnimation = this.mTaskAnimationManager.continueRecentsAnimation(this.mGestureState);
            this.mActiveCallbacks = continueRecentsAnimation;
            continueRecentsAnimation.addListener(this.mInteractionHandler);
            this.mTaskAnimationManager.notifyRecentsAnimationState(this.mInteractionHandler);
            notifyGestureStarted(true);
            return;
        }
        this.mActiveCallbacks = this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, intent, this.mInteractionHandler);
    }

    /* access modifiers changed from: protected */
    public void finishTouchTracking(MotionEvent motionEvent) {
        float f;
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("OtherActivityInputConsumer:finishTouchTracking: cancel? = ");
        sb.append(motionEvent.getActionMasked() == 3);
        DebugLog.m98d(str, sb.toString());
        if (!this.mPassedWindowMoveSlop || this.mInteractionHandler == null) {
            onConsumerAboutToBeSwitched();
            onInteractionGestureFinished();
            this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
            this.mMainThreadHandler.postDelayed(this.mCancelRecentsAnimationRunnable, 100);
        } else if (motionEvent.getActionMasked() == 3) {
            this.mInteractionHandler.onGestureCancelled();
        } else {
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this).getScaledMaximumFlingVelocity());
            float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
            float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            if (this.mNavBarPosition.isRightEdge()) {
                f = xVelocity;
            } else {
                f = this.mNavBarPosition.isLeftEdge() ? -xVelocity : yVelocity;
            }
            this.mInteractionHandler.updateDisplacement(getDisplacement(motionEvent) - this.mStartDisplacement);
            this.mInteractionHandler.onGestureEnded(f, new PointF(xVelocity, yVelocity), this.mDownPos);
        }
        this.mVelocityTracker.recycle();
        this.mMotionPauseDetector.clear();
        this.mVelocityTracker = null;
    }

    public void onConsumerAboutToBeSwitched() {
        this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
        if (this.mInteractionHandler != null) {
            removeListener();
            this.mInteractionHandler.onConsumerAboutToBeSwitched();
        }
    }

    /* access modifiers changed from: private */
    public void onInteractionGestureFinished() {
        removeListener();
        this.mInteractionHandler = null;
        this.mOnCompleteCallback.accept(this);
    }

    private void removeListener() {
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mActiveCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeListener(this.mInteractionHandler);
        }
    }

    private float getDisplacement(MotionEvent motionEvent) {
        float y;
        float f;
        if (this.mNavBarPosition.isRightEdge()) {
            y = motionEvent.getX();
            f = this.mDownPos.x;
        } else if (this.mNavBarPosition.isLeftEdge()) {
            return this.mDownPos.x - motionEvent.getX();
        } else {
            y = motionEvent.getY();
            f = this.mDownPos.y;
        }
        return y - f;
    }
}
