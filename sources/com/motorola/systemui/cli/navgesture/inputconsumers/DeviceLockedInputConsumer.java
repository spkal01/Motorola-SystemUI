package com.motorola.systemui.cli.navgesture.inputconsumers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputMonitor;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.systemui.R$dimen;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.LockScreenRecentsActivity;
import com.motorola.systemui.cli.navgesture.animation.AnimatedFloat;
import com.motorola.systemui.cli.navgesture.animation.GestureState;
import com.motorola.systemui.cli.navgesture.animation.MultiStateCallback;
import com.motorola.systemui.cli.navgesture.animation.RecentsAnimationDeviceState;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationCallbacks;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSetController;
import com.motorola.systemui.cli.navgesture.animation.remote.TaskAnimationManager;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.Utilities;

public class DeviceLockedInputConsumer implements InputConsumer, RecentsAnimationCallbacks.RecentsAnimationListener {
    /* access modifiers changed from: private */
    public static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");
    private static final String[] STATE_NAMES = (MultiStateCallback.DEBUG_STATES ? new String[2] : null);
    private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
    private final ClipAnimationHelper mClipAnimationHelper;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Point mDisplaySize;
    private final GestureState mGestureState;
    /* access modifiers changed from: private */
    public boolean mHomeLaunched = false;
    private final InputMonitor mInputMonitor;
    private final AnimatedFloat mProgress = new AnimatedFloat(new DeviceLockedInputConsumer$$ExternalSyntheticLambda0(this));
    private RecentsAnimationTargetSetController mRecentsAnimationController;
    /* access modifiers changed from: private */
    public String mSecondLauncherPkg;
    /* access modifiers changed from: private */
    public final MultiStateCallback mStateCallback;
    private RecentsAnimationTargetSet mTargetSet;
    private final TaskAnimationManager mTaskAnimationManager;
    private boolean mThresholdCrossed = false;
    private final PointF mTouchDown = new PointF();
    private final float mTouchSlopSquared;
    private final ClipAnimationHelper.TransformParams mTransformParams;
    private VelocityTracker mVelocityTracker;

    public int getType() {
        return 16;
    }

    private static int getFlagForIndex(int i, String str) {
        if (MultiStateCallback.DEBUG_STATES) {
            STATE_NAMES[i] = str;
        }
        return 1 << i;
    }

    public DeviceLockedInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputMonitor inputMonitor) {
        this.mContext = context;
        this.mDeviceState = recentsAnimationDeviceState;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mTouchSlopSquared = Utilities.squaredTouchSlop(context);
        this.mClipAnimationHelper = new ClipAnimationHelper(context);
        this.mTransformParams = new ClipAnimationHelper.TransformParams();
        this.mInputMonitor = inputMonitor;
        this.mSecondLauncherPkg = context.getString(17040020);
        this.mDisplaySize = SecondaryDisplay.INSTANCE.lambda$get$0(context).getDisplaySize();
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        multiStateCallback.runOnceAtState(STATE_TARGET_RECEIVED | STATE_HANDLER_INVALIDATED, new DeviceLockedInputConsumer$$ExternalSyntheticLambda1(this));
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            if (action == 5 && !this.mThresholdCrossed) {
                                if (!this.mDeviceState.isInSwipeUpTouchRegion(motionEvent, motionEvent.getActionIndex())) {
                                    int action2 = motionEvent.getAction();
                                    motionEvent.setAction(3);
                                    finishTouchTracking(motionEvent);
                                    motionEvent.setAction(action2);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                    } else if (!this.mThresholdCrossed) {
                        PointF pointF = this.mTouchDown;
                        if (Utilities.squaredHypot(x - pointF.x, y - pointF.y) > this.mTouchSlopSquared) {
                            startRecentsTransition();
                            return;
                        }
                        return;
                    } else {
                        this.mProgress.updateValue(Math.max(this.mTouchDown.y - y, 0.0f) / ((float) this.mDisplaySize.y));
                        return;
                    }
                }
                finishTouchTracking(motionEvent);
                return;
            }
            this.mTouchDown.set(x, y);
        }
    }

    private void finishTouchTracking(MotionEvent motionEvent) {
        if (this.mThresholdCrossed) {
            final boolean z = true;
            if (motionEvent.getAction() == 1) {
                this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this.mContext).getScaledMaximumFlingVelocity());
                float yVelocity = this.mVelocityTracker.getYVelocity();
                if (Math.abs(yVelocity) <= this.mContext.getResources().getDimension(R$dimen.quickstep_fling_threshold_velocity) ? this.mProgress.value < 0.3f : yVelocity >= 0.0f) {
                    z = false;
                }
                AnimatedFloat animatedFloat = this.mProgress;
                ObjectAnimator animateToValue = animatedFloat.animateToValue(animatedFloat.value, 0.0f);
                animateToValue.setDuration(100);
                animateToValue.setInterpolator(Interpolators.ACCEL);
                animateToValue.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (z) {
                            ActivityOptions makeBasic = ActivityOptions.makeBasic();
                            makeBasic.setLaunchDisplayId(SecondaryDisplay.INSTANCE.lambda$get$0(DeviceLockedInputConsumer.this.mContext).getDisplayId());
                            Bundle bundle = makeBasic.toBundle();
                            Intent intent = new Intent("android.intent.action.MAIN");
                            if (!TextUtils.isEmpty(DeviceLockedInputConsumer.this.mSecondLauncherPkg)) {
                                intent.setPackage(DeviceLockedInputConsumer.this.mSecondLauncherPkg);
                            }
                            DeviceLockedInputConsumer.this.mContext.startActivity(intent.addCategory("android.intent.category.SECONDARY_HOME").setFlags(268435456), bundle);
                            boolean unused = DeviceLockedInputConsumer.this.mHomeLaunched = true;
                        }
                        DeviceLockedInputConsumer.this.mStateCallback.lambda$setStateOnUiThread$0(DeviceLockedInputConsumer.STATE_HANDLER_INVALIDATED);
                    }
                });
                animateToValue.start();
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_HANDLER_INVALIDATED);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    private void startRecentsTransition() {
        Log.d("DeviceLockedInputConsumer", "startRecentsTransition");
        this.mThresholdCrossed = true;
        this.mHomeLaunched = false;
        this.mInputMonitor.pilferPointers();
        this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, new Intent("android.intent.action.MAIN").addCategory("android.intent.category.DEFAULT").setComponent(new ComponentName(this.mContext, LockScreenRecentsActivity.class)).setFlags(268468224), this);
    }

    public void onRecentsAnimationStart(RecentsAnimationTargetSetController recentsAnimationTargetSetController, RecentsAnimationTargetSet recentsAnimationTargetSet) {
        Log.d("DeviceLockedInputConsumer", "startRecentsAnimationCallback length = " + recentsAnimationTargetSet.apps.length);
        this.mRecentsAnimationController = recentsAnimationTargetSetController;
        this.mTargetSet = recentsAnimationTargetSet;
        Point point = this.mDisplaySize;
        Rect rect = new Rect(0, 0, point.x, point.y);
        RemoteAnimationTargetCompat findTask = recentsAnimationTargetSet.findTask(this.mGestureState.getRunningTaskId());
        if (findTask != null) {
            this.mClipAnimationHelper.updateSource(rect, findTask);
        }
        Utilities.scaleRectAboutCenter(rect, 0.75f);
        rect.offsetTo(rect.left, 0);
        this.mClipAnimationHelper.updateTargetRect(rect);
        this.mClipAnimationHelper.applyTransform(this.mTargetSet, this.mTransformParams);
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_TARGET_RECEIVED);
    }

    public void onRecentsAnimationCanceled(ThumbnailData thumbnailData) {
        Log.d("DeviceLockedInputConsumer", "onRecentsAnimationCanceled");
        this.mRecentsAnimationController = null;
        this.mTargetSet = null;
    }

    /* access modifiers changed from: private */
    public void endRemoteAnimation() {
        if (this.mHomeLaunched) {
            ActivityManagerWrapper.getInstance().cancelRecentsAnimation(false);
            return;
        }
        RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mRecentsAnimationController;
        if (recentsAnimationTargetSetController != null) {
            recentsAnimationTargetSetController.finishController(false, (Runnable) null, false);
        }
    }

    /* access modifiers changed from: private */
    public void applyTransform() {
        this.mTransformParams.setProgress(this.mProgress.value);
        RecentsAnimationTargetSet recentsAnimationTargetSet = this.mTargetSet;
        if (recentsAnimationTargetSet != null) {
            this.mClipAnimationHelper.applyTransform(recentsAnimationTargetSet, this.mTransformParams);
        }
    }

    public void onConsumerAboutToBeSwitched() {
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_HANDLER_INVALIDATED);
    }
}
