package com.motorola.systemui.cli.navgesture.animation.remote;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.InputMonitor;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import com.android.systemui.R$dimen;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.ActivityInitListener;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.SysUINavigationMode;
import com.motorola.systemui.cli.navgesture.animation.AnimatedFloat;
import com.motorola.systemui.cli.navgesture.animation.AnimationSuccessListener;
import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import com.motorola.systemui.cli.navgesture.animation.GestureState;
import com.motorola.systemui.cli.navgesture.animation.MultiStateCallback;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationCallbacks;
import com.motorola.systemui.cli.navgesture.inputconsumers.InputConsumer;
import com.motorola.systemui.cli.navgesture.inputconsumers.OverviewInputConsumer;
import com.motorola.systemui.cli.navgesture.notifier.IGestureEndTargetNotifier;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.recents.RecentsModel;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.DeviceProfileProvider;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class WindowTransformHelper implements RecentsAnimationCallbacks.RecentsAnimationListener, View.OnApplyWindowInsetsListener {
    private static final int LAUNCHER_UI_STATES;
    private static final Interpolator PULLBACK_INTERPOLATOR = Interpolators.DEACCEL;
    private static final int STATE_APP_CONTROLLER_RECEIVED = getFlagForIndex(3, "STATE_APP_CONTROLLER_RECEIVED");
    private static final int STATE_CAPTURE_SCREENSHOT = getFlagForIndex(10, "STATE_CAPTURE_SCREENSHOT");
    private static final int STATE_CURRENT_TASK_FINISHED = getFlagForIndex(15, "STATE_CURRENT_TASK_FINISHED");
    private static final int STATE_GESTURE_CANCELLED = getFlagForIndex(8, "STATE_GESTURE_CANCELLED");
    private static final int STATE_GESTURE_COMPLETED = getFlagForIndex(9, "STATE_GESTURE_COMPLETED");
    private static final int STATE_GESTURE_STARTED = getFlagForIndex(7, "STATE_GESTURE_STARTED");
    private static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(6, "STATE_HANDLER_INVALIDATED");
    /* access modifiers changed from: private */
    public static final int STATE_LAUNCHER_DRAWN;
    private static final int STATE_LAUNCHER_PRESENT;
    private static final int STATE_LAUNCHER_STARTED;
    private static final String[] STATE_NAMES = (MultiStateCallback.DEBUG_STATES ? new String[16] : null);
    private static final int STATE_RESUME_LAST_TASK = getFlagForIndex(13, "STATE_RESUME_LAST_TASK");
    private static final int STATE_SCALED_CONTROLLER_HOME = getFlagForIndex(4, "STATE_SCALED_CONTROLLER_HOME");
    private static final int STATE_SCALED_CONTROLLER_RECENTS = getFlagForIndex(5, "STATE_SCALED_CONTROLLER_RECENTS");
    private static final int STATE_SCREENSHOT_CAPTURED = getFlagForIndex(11, "STATE_SCREENSHOT_CAPTURED");
    private static final int STATE_SCREENSHOT_VIEW_SHOWN = getFlagForIndex(12, "STATE_SCREENSHOT_VIEW_SHOWN");
    private static final int STATE_START_NEW_TASK = getFlagForIndex(14, "STATE_START_NEW_TASK");
    private static final float SWIPE_DURATION_MULTIPLIER = Math.min(1.4285715f, 3.3333333f);
    /* access modifiers changed from: private */
    public static final String TAG = WindowTransformHelper.class.getSimpleName();
    /* access modifiers changed from: private */
    public BaseGestureActivity mActivity;
    private final ActivityControlHelper<BaseGestureActivity> mActivityControlHelper;
    private final ActivityInitListener<BaseGestureActivity> mActivityInitListener;
    /* access modifiers changed from: private */
    public final TaskStackChangeListener mActivityRestartListener = new TaskStackChangeListener() {
        public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
            if (runningTaskInfo.taskId == WindowTransformHelper.this.mGestureState.getRunningTaskId() && runningTaskInfo.configuration.windowConfiguration.getActivityType() != 2) {
                String access$000 = WindowTransformHelper.TAG;
                Log.d(access$000, "onActivityRestartAttempt getActivityType = " + runningTaskInfo.configuration.windowConfiguration.getActivityType());
                WindowTransformHelper.this.endRunningWindowAnim(true);
                ActivityManagerWrapper.getInstance().unregisterTaskStackListener(WindowTransformHelper.this.mActivityRestartListener);
                ActivityManagerWrapper.getInstance().startActivityFromRecents(runningTaskInfo.taskId, (ActivityOptions) null);
            }
        }
    };
    private ActivityControlHelper.AnimationFactory mAnimationFactory = WindowTransformHelper$$ExternalSyntheticLambda3.INSTANCE;
    private boolean mCanceled;
    private final ClipAnimationHelper mClipAnimationHelper;
    private final Context mContext;
    private final AnimatedFloat mCurrentShift = new AnimatedFloat(new WindowTransformHelper$$ExternalSyntheticLambda10(this));
    private final SysUINavigationMode mDeviceState;
    private float mDragLengthFactor = 1.0f;
    private Runnable mGestureEndCallback;
    private IGestureEndTargetNotifier mGestureEndTargetNotifier;
    private boolean mGestureStarted;
    /* access modifiers changed from: private */
    public final GestureState mGestureState;
    private boolean mHasLauncherTransitionControllerStarted;
    private InputConsumerController mInputConsumerController;
    private boolean mIsLikelyToStartNewTask;
    private boolean mIsOverviewPeeking;
    private AnimatorPlaybackController mLauncherTransitionController;
    private boolean mPassedOverviewThreshold;
    private final ArrayList<Runnable> mRecentsAnimationStartCallbacks = new ArrayList<>();
    private RecentsAnimationTargetSet mRecentsAnimationTargetSet;
    /* access modifiers changed from: private */
    public RecentsAnimationTargetSetController mRecentsAnimationTargetSetController;
    private IRecentsView mRecentsView;
    private RunningWindowAnim mRunningWindowAnim;
    private float mShiftAtGestureStart = 0.0f;
    /* access modifiers changed from: private */
    public MultiStateCallback mStateCallback;
    private ThumbnailData mTaskSnapshot;
    private final ClipAnimationHelper.TransformParams mTransformParams;
    private int mTransitionDragLength;
    private boolean mWasLauncherAlreadyVisible;

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$animateToProgressInternal$13(float f, float f2) {
        return f;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(long j) {
    }

    static {
        int flagForIndex = getFlagForIndex(0, "STATE_LAUNCHER_PRESENT");
        STATE_LAUNCHER_PRESENT = flagForIndex;
        int flagForIndex2 = getFlagForIndex(1, "STATE_LAUNCHER_STARTED");
        STATE_LAUNCHER_STARTED = flagForIndex2;
        int flagForIndex3 = getFlagForIndex(2, "STATE_LAUNCHER_DRAWN");
        STATE_LAUNCHER_DRAWN = flagForIndex3;
        LAUNCHER_UI_STATES = flagForIndex | flagForIndex3 | flagForIndex2;
    }

    private static int getFlagForIndex(int i, String str) {
        if (MultiStateCallback.DEBUG_STATES) {
            STATE_NAMES[i] = str;
        }
        return 1 << i;
    }

    public WindowTransformHelper(Context context, ActivityControlHelper<BaseGestureActivity> activityControlHelper, InputConsumerController inputConsumerController, IGestureEndTargetNotifier iGestureEndTargetNotifier, GestureState gestureState) {
        this.mContext = context;
        this.mDeviceState = SysUINavigationMode.getInstance(context);
        this.mActivityControlHelper = activityControlHelper;
        this.mGestureState = gestureState;
        this.mActivityInitListener = activityControlHelper.createActivityInitListener(new WindowTransformHelper$$ExternalSyntheticLambda36(this));
        this.mInputConsumerController = inputConsumerController;
        this.mClipAnimationHelper = new ClipAnimationHelper(context);
        this.mTransformParams = new ClipAnimationHelper.TransformParams();
        initStateCallbacks();
        initTransitionEndpoints(DeviceProfileProvider.INSTANCE.lambda$get$0(context).getDeviceProfile(context));
        this.mGestureEndTargetNotifier = iGestureEndTargetNotifier;
    }

    private void initStateCallbacks() {
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        int i = STATE_LAUNCHER_PRESENT;
        int i2 = STATE_GESTURE_STARTED;
        multiStateCallback.runOnceAtState(i | i2, new WindowTransformHelper$$ExternalSyntheticLambda18(this));
        MultiStateCallback multiStateCallback2 = this.mStateCallback;
        int i3 = STATE_LAUNCHER_DRAWN;
        multiStateCallback2.runOnceAtState(i3 | i2, new WindowTransformHelper$$ExternalSyntheticLambda22(this));
        this.mStateCallback.runOnceAtState(i | i3, new WindowTransformHelper$$ExternalSyntheticLambda13(this));
        this.mStateCallback.runOnceAtState(STATE_LAUNCHER_STARTED | i | STATE_GESTURE_CANCELLED, new WindowTransformHelper$$ExternalSyntheticLambda21(this));
        MultiStateCallback multiStateCallback3 = this.mStateCallback;
        int i4 = STATE_RESUME_LAST_TASK;
        int i5 = STATE_APP_CONTROLLER_RECEIVED;
        multiStateCallback3.runOnceAtState(i4 | i5, new WindowTransformHelper$$ExternalSyntheticLambda9(this));
        MultiStateCallback multiStateCallback4 = this.mStateCallback;
        int i6 = STATE_START_NEW_TASK;
        int i7 = STATE_SCREENSHOT_CAPTURED;
        multiStateCallback4.runOnceAtState(i6 | i7, new WindowTransformHelper$$ExternalSyntheticLambda12(this));
        MultiStateCallback multiStateCallback5 = this.mStateCallback;
        int i8 = STATE_CAPTURE_SCREENSHOT;
        multiStateCallback5.runOnceAtState(i | i5 | i3 | i8, new WindowTransformHelper$$ExternalSyntheticLambda16(this));
        MultiStateCallback multiStateCallback6 = this.mStateCallback;
        int i9 = STATE_GESTURE_COMPLETED;
        int i10 = STATE_SCALED_CONTROLLER_RECENTS;
        multiStateCallback6.runOnceAtState(i7 | i9 | i10, new WindowTransformHelper$$ExternalSyntheticLambda20(this));
        MultiStateCallback multiStateCallback7 = this.mStateCallback;
        int i11 = STATE_SCALED_CONTROLLER_HOME;
        multiStateCallback7.runOnceAtState(i7 | i9 | i11, new WindowTransformHelper$$ExternalSyntheticLambda23(this));
        MultiStateCallback multiStateCallback8 = this.mStateCallback;
        int i12 = STATE_CURRENT_TASK_FINISHED;
        multiStateCallback8.runOnceAtState(i11 | i12, new WindowTransformHelper$$ExternalSyntheticLambda5(this));
        this.mStateCallback.runOnceAtState(i2 | i3 | i | i5 | i10 | i12 | i9, new WindowTransformHelper$$ExternalSyntheticLambda19(this));
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED | GestureState.STATE_RECENTS_SCROLLING_FINISHED, new WindowTransformHelper$$ExternalSyntheticLambda11(this));
        MultiStateCallback multiStateCallback9 = this.mStateCallback;
        int i13 = STATE_HANDLER_INVALIDATED;
        multiStateCallback9.runOnceAtState(i13, new WindowTransformHelper$$ExternalSyntheticLambda6(this));
        this.mStateCallback.runOnceAtState(i | i13, new WindowTransformHelper$$ExternalSyntheticLambda14(this));
        this.mStateCallback.runOnceAtState(i13 | i4, new WindowTransformHelper$$ExternalSyntheticLambda25(this));
        this.mStateCallback.addChangeListener(i | i5 | STATE_SCREENSHOT_VIEW_SHOWN | i8, new WindowTransformHelper$$ExternalSyntheticLambda31(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initStateCallbacks$1(Boolean bool) {
        this.mRecentsView.setRunningTaskHidden(!bool.booleanValue());
    }

    private void initTransitionEndpoints(DeviceProfile deviceProfile) {
        Rect rect = new Rect();
        this.mTransitionDragLength = this.mActivityControlHelper.getSwipeUpDestinationAndLength(deviceProfile, this.mContext, rect);
        this.mClipAnimationHelper.updateTargetRect(rect);
        if (this.mDeviceState.isGestureMode()) {
            this.mDragLengthFactor = ((float) deviceProfile.heightPx) / ((float) this.mTransitionDragLength);
        }
    }

    public void initWhenReady(Intent intent) {
        RecentsModel.INSTANCE.lambda$get$0(this.mContext).getTasks((Consumer<ArrayList<Task>>) null);
        this.mActivityInitListener.register(intent);
    }

    /* access modifiers changed from: private */
    public boolean onActivityInit(Boolean bool) {
        BaseGestureActivity createdActivity = this.mActivityControlHelper.getCreatedActivity();
        String str = TAG;
        DebugLog.m98d(str, "onActivityInit: activity = " + createdActivity);
        BaseGestureActivity baseGestureActivity = this.mActivity;
        if (baseGestureActivity == createdActivity) {
            return true;
        }
        if (baseGestureActivity != null) {
            int state = this.mStateCallback.getState() & (~LAUNCHER_UI_STATES);
            initStateCallbacks();
            this.mStateCallback.lambda$setStateOnUiThread$0(state);
        }
        this.mWasLauncherAlreadyVisible = bool.booleanValue();
        this.mActivity = createdActivity;
        if (bool.booleanValue()) {
            this.mActivity.clearForceInvisibleFlag(9);
        } else {
            this.mActivity.addForceInvisibleFlag(9);
        }
        IRecentsView overviewPanel = createdActivity.getOverviewPanel();
        this.mRecentsView = overviewPanel;
        SyncRtSurfaceTransactionApplierCompat.create(overviewPanel.asView(), new WindowTransformHelper$$ExternalSyntheticLambda29(this));
        this.mRecentsView.setRemoteWindowAnimationDependentSyncRTListener(new WindowTransformHelper$$ExternalSyntheticLambda35(this));
        runOnRecentsAnimationStart(new WindowTransformHelper$$ExternalSyntheticLambda24(this));
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_LAUNCHER_PRESENT);
        if (bool.booleanValue()) {
            onLauncherStart();
        } else {
            createdActivity.runOnceOnStart(new WindowTransformHelper$$ExternalSyntheticLambda8(this));
        }
        setupRecentsViewUi();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityInit$3(SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat) {
        this.mTransformParams.setSyncTransactionApplier(syncRtSurfaceTransactionApplierCompat);
        runOnRecentsAnimationStart(new WindowTransformHelper$$ExternalSyntheticLambda27(this, syncRtSurfaceTransactionApplierCompat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityInit$2(SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat) {
        this.mRecentsAnimationTargetSet.addDependentTransactionApplier(syncRtSurfaceTransactionApplierCompat);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityInit$4(double d) {
        if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
            updateFinalShift();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityInit$5() {
        this.mRecentsView.setRecentsAnimationTargetSetController(this.mRecentsAnimationTargetSetController);
    }

    /* access modifiers changed from: private */
    public void onLauncherStart() {
        final BaseGestureActivity createdActivity = this.mActivityControlHelper.getCreatedActivity();
        String str = TAG;
        DebugLog.m98d(str, "onLauncherStart: activity = " + createdActivity);
        if (this.mActivity == createdActivity && !this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
                WindowTransformHelper$$ExternalSyntheticLambda7 windowTransformHelper$$ExternalSyntheticLambda7 = new WindowTransformHelper$$ExternalSyntheticLambda7(this);
                if (this.mWasLauncherAlreadyVisible) {
                    this.mStateCallback.runOnceAtState(STATE_GESTURE_STARTED, windowTransformHelper$$ExternalSyntheticLambda7);
                } else {
                    windowTransformHelper$$ExternalSyntheticLambda7.run();
                }
            }
            DebugLog.m98d(str, "onLauncherStart: activity = " + createdActivity + "; mWasLauncherAlreadyVisible = " + this.mWasLauncherAlreadyVisible);
            if (this.mWasLauncherAlreadyVisible) {
                this.mStateCallback.lambda$setStateOnUiThread$0(STATE_LAUNCHER_DRAWN);
            } else {
                final ViewGroup rootView = createdActivity.getRootView();
                rootView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    boolean mHandled = false;

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onDraw$0(View view) {
                        view.getViewTreeObserver().removeOnDrawListener(this);
                    }

                    public void onDraw() {
                        View view = rootView;
                        view.post(new WindowTransformHelper$1$$ExternalSyntheticLambda0(this, view));
                        if (!this.mHandled) {
                            this.mHandled = true;
                            String access$000 = WindowTransformHelper.TAG;
                            DebugLog.m98d(access$000, "onLauncherDraw: activity = " + createdActivity);
                            if (createdActivity == WindowTransformHelper.this.mActivity) {
                                WindowTransformHelper.this.mStateCallback.lambda$setStateOnUiThread$0(WindowTransformHelper.STATE_LAUNCHER_DRAWN);
                            }
                        }
                    }
                });
            }
            createdActivity.getRootView().setOnApplyWindowInsetsListener(this);
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_LAUNCHER_STARTED);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLauncherStart$6() {
        this.mAnimationFactory = this.mActivityControlHelper.prepareRecentsUI(this.mWasLauncherAlreadyVisible, true, new WindowTransformHelper$$ExternalSyntheticLambda30(this));
        maybeUpdateRecentsAttachedState(false);
    }

    /* access modifiers changed from: private */
    public void onLauncherPresentAndGestureStarted() {
        setupRecentsViewUi();
        notifyGestureStartedAsync();
    }

    private void setupRecentsViewUi() {
        this.mRecentsView.onGestureAnimationStart(this.mGestureState.getRunningTaskId());
    }

    /* access modifiers changed from: private */
    public void launcherFrameDrawn() {
        DebugLog.m98d(TAG, "launcherFrameDrawn: launcher drawn");
    }

    /* access modifiers changed from: private */
    public void initializeLauncherAnimationController() {
        buildAnimationController();
        RecentsModel.INSTANCE.lambda$get$0(this.mContext).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentsViewDispatcher$8(float f, MotionEvent motionEvent) {
        Optional.ofNullable(this.mRecentsView).ifPresent(new WindowTransformHelper$$ExternalSyntheticLambda28(f, motionEvent));
    }

    public Consumer<MotionEvent> getRecentsViewDispatcher(float f) {
        return new WindowTransformHelper$$ExternalSyntheticLambda33(this, f);
    }

    public void onMotionPauseChanged(boolean z) {
        setOverviewState(z ? 1 : 0);
    }

    public void setIsLikelyToStartNewTask(boolean z) {
        setIsLikelyToStartNewTask(z, true);
    }

    private void setIsLikelyToStartNewTask(boolean z, boolean z2) {
        if (this.mIsLikelyToStartNewTask != z) {
            this.mIsLikelyToStartNewTask = z;
            maybeUpdateRecentsAttachedState(z2);
        }
    }

    public void updateDisplacement(float f) {
        float f2 = -f;
        int i = this.mTransitionDragLength;
        float f3 = this.mDragLengthFactor;
        if (f2 <= ((float) i) * f3 || i <= 0) {
            float f4 = 0.0f;
            float max = Math.max(f2, 0.0f);
            int i2 = this.mTransitionDragLength;
            if (i2 != 0) {
                f4 = max / ((float) i2);
            }
            if (f4 > 1.4f) {
                f4 = (PULLBACK_INTERPOLATOR.getInterpolation(Utilities.getProgress(f4, 1.4f, this.mDragLengthFactor)) * 0.39999998f) + 1.4f;
            }
            this.mCurrentShift.updateValue(f4);
            return;
        }
        this.mCurrentShift.updateValue(f3);
    }

    private void maybeUpdateRecentsAttachedState(boolean z) {
        RemoteAnimationTargetCompat remoteAnimationTargetCompat;
        if (this.mDeviceState.isGestureMode() && this.mRecentsView != null) {
            RecentsAnimationTargetSet recentsAnimationTargetSet = this.mRecentsAnimationTargetSet;
            if (recentsAnimationTargetSet == null) {
                remoteAnimationTargetCompat = null;
            } else {
                remoteAnimationTargetCompat = recentsAnimationTargetSet.findTask(this.mGestureState.getRunningTaskId());
            }
            boolean z2 = false;
            boolean z3 = true;
            if (this.mGestureState.getEndTarget() != null) {
                z3 = this.mGestureState.getEndTarget().recentsAttachedToAppWindow;
            } else if (remoteAnimationTargetCompat == null || !isNotInRecents(remoteAnimationTargetCompat)) {
                if (this.mIsOverviewPeeking || this.mIsLikelyToStartNewTask) {
                    z2 = true;
                }
                z3 = z2;
            } else {
                z = false;
            }
            this.mAnimationFactory.setRecentsAttachedToAppWindow(z3, z);
        }
    }

    private void buildAnimationController() {
        if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME && !this.mHasLauncherTransitionControllerStarted) {
            initTransitionEndpoints(this.mActivity.getDeviceProfile());
            this.mAnimationFactory.createActivityController((long) this.mTransitionDragLength);
        }
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        WindowInsets onApplyWindowInsets = view.onApplyWindowInsets(windowInsets);
        buildAnimationController();
        return onApplyWindowInsets;
    }

    /* access modifiers changed from: private */
    public void onAnimatorPlaybackControllerCreated(AnimatorPlaybackController animatorPlaybackController) {
        this.mLauncherTransitionController = animatorPlaybackController;
        animatorPlaybackController.dispatchSetInterpolator(new WindowTransformHelper$$ExternalSyntheticLambda1(this));
        this.mAnimationFactory.adjustActivityControllerInterpolators();
        this.mLauncherTransitionController.dispatchOnStart();
        updateLauncherTransitionProgress();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ float lambda$onAnimatorPlaybackControllerCreated$9(float f) {
        return f * this.mDragLengthFactor;
    }

    /* access modifiers changed from: private */
    public void updateFinalShift() {
        float f = this.mCurrentShift.value;
        if (this.mRecentsAnimationTargetSet != null) {
            IRecentsView iRecentsView = this.mRecentsView;
            if (iRecentsView == null) {
                this.mTransformParams.setOffsetX(0.0f).setOffsetScale(1.0f);
            } else {
                iRecentsView.fillRemoteWindowTransformParams(this.mClipAnimationHelper, this.mTransformParams);
            }
            this.mTransformParams.setProgress(f);
            this.mClipAnimationHelper.applyTransform(this.mRecentsAnimationTargetSet, this.mTransformParams);
            updateSysUiFlags(f);
        }
        boolean z = this.mCurrentShift.value >= 0.7f;
        if (z != this.mPassedOverviewThreshold) {
            this.mPassedOverviewThreshold = z;
            if (this.mRecentsView != null && !this.mDeviceState.isGestureMode()) {
                this.mRecentsView.asView().performHapticFeedback(1, 1);
            }
        }
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
        if (animatorPlaybackController != null && !animatorPlaybackController.getAnimationPlayer().isStarted()) {
            updateLauncherTransitionProgress();
        }
    }

    private void updateLauncherTransitionProgress() {
        if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
            float f = this.mCurrentShift.value / this.mDragLengthFactor;
            AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
            float f2 = this.mShiftAtGestureStart;
            animatorPlaybackController.setPlayFraction((f <= f2 || f2 >= 1.0f) ? 0.0f : (f - f2) / (1.0f - f2));
        }
    }

    private void updateSysUiFlags(float f) {
        IRecentsView iRecentsView;
        if (this.mRecentsAnimationTargetSetController != null && (iRecentsView = this.mRecentsView) != null) {
            int sysUiStatusNavFlags = iRecentsView.getSysUiStatusNavFlags(f);
            boolean z = true;
            boolean z2 = f > 0.14999998f;
            RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController;
            if (sysUiStatusNavFlags == 0 && !z2) {
                z = false;
            }
            recentsAnimationTargetSetController.setUseLauncherSystemBarFlags(z);
            if (z2) {
                sysUiStatusNavFlags = 0;
            }
            this.mActivity.getSystemUiController().updateUiState(3, sysUiStatusNavFlags);
        }
    }

    public void onRecentsAnimationStart(RecentsAnimationTargetSetController recentsAnimationTargetSetController, RecentsAnimationTargetSet recentsAnimationTargetSet) {
        Rect rect;
        this.mRecentsAnimationTargetSetController = recentsAnimationTargetSetController;
        this.mRecentsAnimationTargetSet = recentsAnimationTargetSet;
        DeviceProfile deviceProfile = DeviceProfileProvider.INSTANCE.lambda$get$0(this.mContext).getDeviceProfile(this.mContext);
        RemoteAnimationTargetCompat findTask = recentsAnimationTargetSet.findTask(this.mGestureState.getRunningTaskId());
        BaseGestureActivity baseGestureActivity = this.mActivity;
        if (baseGestureActivity != null) {
            int[] iArr = new int[2];
            ViewGroup rootView = baseGestureActivity.getRootView();
            rootView.getLocationOnScreen(iArr);
            rect = new Rect(iArr[0], iArr[1], iArr[0] + rootView.getWidth(), iArr[1] + rootView.getHeight());
        } else {
            rect = new Rect(0, 0, deviceProfile.widthPx, deviceProfile.heightPx);
        }
        DeviceProfile copy = deviceProfile.copy(this.mContext);
        copy.updateInsets(recentsAnimationTargetSet.homeContentInsets);
        if (findTask != null) {
            this.mClipAnimationHelper.updateSource(rect, findTask);
        }
        this.mClipAnimationHelper.prepareAnimation(false);
        initTransitionEndpoints(copy);
        if (!this.mRecentsAnimationStartCallbacks.isEmpty()) {
            Iterator it = new ArrayList(this.mRecentsAnimationStartCallbacks).iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            this.mRecentsAnimationStartCallbacks.clear();
        }
        String str = TAG;
        Log.d(str, "startRecentsAnimationCallback length = " + recentsAnimationTargetSet.apps.length);
        MultiStateCallback multiStateCallback = this.mStateCallback;
        int i = STATE_APP_CONTROLLER_RECEIVED;
        RecentsAnimationTargetSetController recentsAnimationTargetSetController2 = this.mRecentsAnimationTargetSetController;
        Objects.requireNonNull(recentsAnimationTargetSetController2);
        multiStateCallback.runOnceAtState(STATE_GESTURE_STARTED | i, new WindowTransformHelper$$ExternalSyntheticLambda4(recentsAnimationTargetSetController2));
        this.mStateCallback.setStateOnUiThread(i);
        this.mPassedOverviewThreshold = false;
    }

    public void onRecentsAnimationFinished(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.mRecentsAnimationTargetSetController = null;
        this.mRecentsAnimationTargetSet = null;
    }

    public void onRecentsAnimationCanceled(ThumbnailData thumbnailData) {
        this.mActivityInitListener.unregister();
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_CANCELLED | STATE_HANDLER_INVALIDATED);
        this.mRecentsAnimationTargetSetController = null;
        this.mRecentsAnimationTargetSet = null;
        Log.d(TAG, "cancelRecentsAnimation");
    }

    public void onTaskAppeared(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        if (this.mRecentsAnimationTargetSetController != null && handleTaskAppeared(remoteAnimationTargetCompat)) {
            this.mRecentsAnimationTargetSetController.finish(false, (Runnable) null);
            this.mActivityControlHelper.onLaunchTaskSuccess();
            Log.d(TAG, "finishRecentsAnimation false");
        }
    }

    private boolean handleTaskAppeared(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        if (this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            Log.d("chentq2", "handleTaskAppeared STATE_HANDLER_INVALIDATED");
            return false;
        } else if (!this.mStateCallback.hasStates(STATE_START_NEW_TASK) || remoteAnimationTargetCompat.taskId != this.mGestureState.getLastStartedTaskId()) {
            Log.d("chentq2", "handleTaskAppeared return false");
            return false;
        } else {
            reset();
            Log.d("chentq2", "handleTaskAppeared STATE_START_NEW_TASK");
            return true;
        }
    }

    public void onGestureStarted(boolean z) {
        notifyGestureStartedAsync();
        setIsLikelyToStartNewTask(z, false);
        this.mShiftAtGestureStart = this.mCurrentShift.value;
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_STARTED);
        this.mGestureStarted = true;
    }

    private void notifyGestureStartedAsync() {
        BaseGestureActivity baseGestureActivity = this.mActivity;
        if (baseGestureActivity != null) {
            baseGestureActivity.clearForceInvisibleFlag(9);
        }
    }

    public void onGestureCancelled() {
        updateDisplacement(0.0f);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        handleNormalGestureEnd(0.0f, false, new PointF(), true);
    }

    public void onGestureEnded(float f, PointF pointF, PointF pointF2) {
        boolean z = this.mGestureStarted && Math.abs(f) > this.mContext.getResources().getDimension(R$dimen.quickstep_fling_threshold_velocity);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        handleNormalGestureEnd(f, z, pointF, false);
    }

    /* access modifiers changed from: private */
    public InputConsumer createNewInputProxyHandler() {
        endRunningWindowAnim(this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME);
        endLauncherTransitionController();
        BaseGestureActivity createdActivity = this.mActivityControlHelper.getCreatedActivity();
        if (createdActivity == null) {
            return InputConsumer.NO_OP;
        }
        return new OverviewInputConsumer(createdActivity, (InputMonitor) null, true);
    }

    /* access modifiers changed from: private */
    public void endRunningWindowAnim(boolean z) {
        RunningWindowAnim runningWindowAnim = this.mRunningWindowAnim;
        if (runningWindowAnim == null) {
            return;
        }
        if (z) {
            runningWindowAnim.cancel();
        } else {
            runningWindowAnim.end();
        }
    }

    /* renamed from: com.motorola.systemui.cli.navgesture.animation.remote.WindowTransformHelper$5 */
    static /* synthetic */ class C27165 {

        /* renamed from: $SwitchMap$com$motorola$systemui$cli$navgesture$animation$GestureState$GestureEndTarget */
        static final /* synthetic */ int[] f194x6b168cb4;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget[] r0 = com.motorola.systemui.cli.navgesture.animation.GestureState.GestureEndTarget.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f194x6b168cb4 = r0
                com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget r1 = com.motorola.systemui.cli.navgesture.animation.GestureState.GestureEndTarget.HOME     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f194x6b168cb4     // Catch:{ NoSuchFieldError -> 0x001d }
                com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget r1 = com.motorola.systemui.cli.navgesture.animation.GestureState.GestureEndTarget.RECENTS     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f194x6b168cb4     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget r1 = com.motorola.systemui.cli.navgesture.animation.GestureState.GestureEndTarget.NEW_TASK     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f194x6b168cb4     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget r1 = com.motorola.systemui.cli.navgesture.animation.GestureState.GestureEndTarget.LAST_TASK     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.animation.remote.WindowTransformHelper.C27165.<clinit>():void");
        }
    }

    /* access modifiers changed from: private */
    public void onSettledOnEndTarget() {
        int i = C27165.f194x6b168cb4[this.mGestureState.getEndTarget().ordinal()];
        if (i == 1) {
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_SCALED_CONTROLLER_HOME | STATE_CAPTURE_SCREENSHOT);
        } else if (i == 2) {
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_SCALED_CONTROLLER_RECENTS | STATE_CAPTURE_SCREENSHOT | STATE_SCREENSHOT_VIEW_SHOWN);
        } else if (i == 3) {
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_START_NEW_TASK | STATE_CAPTURE_SCREENSHOT);
        } else if (i == 4) {
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RESUME_LAST_TASK);
        }
        String str = TAG;
        Log.d(str, "onSettledOnEndTarget " + this.mGestureState.getEndTarget());
    }

    private GestureState.GestureEndTarget calculateEndTarget(PointF pointF, float f, boolean z, boolean z2) {
        boolean z3;
        GestureState.GestureEndTarget gestureEndTarget;
        boolean z4 = true;
        if (this.mRecentsView != null) {
            z3 = !hasTargetSet() ? true : this.mRecentsView.goingToNewTask(pointF);
        } else {
            z3 = false;
        }
        boolean z5 = this.mCurrentShift.value >= 0.7f;
        if (!z) {
            gestureEndTarget = z2 ? GestureState.GestureEndTarget.LAST_TASK : this.mDeviceState.isGestureMode() ? this.mIsOverviewPeeking ? GestureState.GestureEndTarget.RECENTS : z3 ? GestureState.GestureEndTarget.NEW_TASK : !z5 ? GestureState.GestureEndTarget.LAST_TASK : GestureState.GestureEndTarget.HOME : (!z5 || !this.mGestureStarted) ? z3 ? GestureState.GestureEndTarget.NEW_TASK : GestureState.GestureEndTarget.LAST_TASK : GestureState.GestureEndTarget.RECENTS;
        } else {
            boolean z6 = f < 0.0f;
            boolean z7 = z3 && Math.abs(pointF.x) > Math.abs(f);
            if (this.mDeviceState.isGestureMode() && z6 && !z7) {
                gestureEndTarget = GestureState.GestureEndTarget.HOME;
            } else if (this.mDeviceState.isGestureMode() && z6 && !this.mIsOverviewPeeking) {
                gestureEndTarget = GestureState.GestureEndTarget.NEW_TASK;
            } else if (z6) {
                gestureEndTarget = (z5 || !z7) ? GestureState.GestureEndTarget.RECENTS : GestureState.GestureEndTarget.NEW_TASK;
            } else {
                gestureEndTarget = z3 ? GestureState.GestureEndTarget.NEW_TASK : GestureState.GestureEndTarget.LAST_TASK;
            }
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("calculateEndTarget:  endTarget = ");
        sb.append(gestureEndTarget);
        sb.append(" fling = ");
        sb.append(z);
        sb.append(" isSwipeUp = ");
        sb.append(f < 0.0f);
        sb.append(" willGoToNewTaskOnSwipeUp = ");
        if (!z3 || Math.abs(pointF.x) <= Math.abs(f)) {
            z4 = false;
        }
        sb.append(z4);
        DebugLog.m98d(str, sb.toString());
        return (!this.mDeviceState.isOverviewDisabled() || !(gestureEndTarget == GestureState.GestureEndTarget.RECENTS || gestureEndTarget == GestureState.GestureEndTarget.LAST_TASK)) ? gestureEndTarget : GestureState.GestureEndTarget.LAST_TASK;
    }

    private void handleNormalGestureEnd(float f, boolean z, PointF pointF, boolean z2) {
        float f2;
        IRecentsView iRecentsView;
        long max;
        RecentsAnimationTargetSetController recentsAnimationTargetSetController;
        int i;
        PointF pointF2 = new PointF(pointF.x / 1000.0f, pointF.y / 1000.0f);
        float f3 = this.mCurrentShift.value;
        GestureState.GestureEndTarget calculateEndTarget = calculateEndTarget(pointF, f, z, z2);
        float f4 = calculateEndTarget.isLauncher ? 1.0f : 0.0f;
        Interpolator interpolator = Interpolators.DEACCEL;
        long j = 350;
        if (!z) {
            j = Math.min(350, (long) Math.abs(Math.round((f4 - f3) * 350.0f * SWIPE_DURATION_MULTIPLIER)));
            f2 = f3;
        } else {
            float boundToRange = Utilities.boundToRange(f3 - ((pointF2.y * 16.0f) / ((float) this.mTransitionDragLength)), 0.0f, this.mDragLengthFactor);
            if (Math.abs(f) > this.mContext.getResources().getDimension(R$dimen.quickstep_fling_min_velocity) && (i = this.mTransitionDragLength) > 0) {
                j = Math.min(350, ((long) Math.round(Math.abs(((f4 - f3) * ((float) i)) / pointF2.y))) * 2);
            }
            f2 = boundToRange;
        }
        if (calculateEndTarget.isLauncher && (recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController) != null) {
            recentsAnimationTargetSetController.enableInputProxy(this.mInputConsumerController, new WindowTransformHelper$$ExternalSyntheticLambda37(this));
        }
        String str = TAG;
        Log.d(str, "handleNormalGestureEnd endTarget = " + calculateEndTarget);
        if (calculateEndTarget == GestureState.GestureEndTarget.HOME) {
            setOverviewState(3);
            max = Math.max(120, j);
        } else {
            if (calculateEndTarget == GestureState.GestureEndTarget.RECENTS) {
                IRecentsView iRecentsView2 = this.mRecentsView;
                if (iRecentsView2 != null) {
                    iRecentsView2.snapToNearestCenterOfScreenPosition();
                    j = Math.max(j, this.mRecentsView.getSnapDuration());
                }
                if (this.mDeviceState.isGestureMode()) {
                    setOverviewState(2);
                }
            } else if ((calculateEndTarget == GestureState.GestureEndTarget.NEW_TASK || calculateEndTarget == GestureState.GestureEndTarget.LAST_TASK) && (iRecentsView = this.mRecentsView) != null) {
                max = Math.max(j, iRecentsView.getSnapDuration());
            }
            this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
            animateToProgress(f2, f4, j, interpolator, calculateEndTarget, pointF2);
        }
        j = max;
        this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
        animateToProgress(f2, f4, j, interpolator, calculateEndTarget, pointF2);
    }

    private void setOverviewState(int i) {
        boolean z = this.mIsOverviewPeeking;
        boolean z2 = i == 1;
        this.mIsOverviewPeeking = z2;
        if (z2 != z) {
            maybeUpdateRecentsAttachedState(true);
        }
        IRecentsView iRecentsView = this.mRecentsView;
        if (iRecentsView == null) {
            return;
        }
        if (i == 0 || i == 1) {
            iRecentsView.asView().performHapticFeedback(1, 1);
        }
    }

    private void animateToProgress(float f, float f2, long j, Interpolator interpolator, GestureState.GestureEndTarget gestureEndTarget, PointF pointF) {
        runOnRecentsAnimationStart(new WindowTransformHelper$$ExternalSyntheticLambda26(this, f, f2, j, interpolator, gestureEndTarget, pointF));
    }

    /* access modifiers changed from: protected */
    public void onRestartPreviouslyAppearedTask() {
        RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController;
        if (recentsAnimationTargetSetController != null) {
            recentsAnimationTargetSetController.finish(false, (Runnable) null);
        }
        reset();
    }

    private void runOnRecentsAnimationStart(Runnable runnable) {
        if (this.mRecentsAnimationTargetSet == null) {
            this.mRecentsAnimationStartCallbacks.add(runnable);
        } else {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: animateToProgressInternal */
    public void lambda$animateToProgress$10(float f, float f2, long j, Interpolator interpolator, GestureState.GestureEndTarget gestureEndTarget, PointF pointF) {
        this.mGestureState.setEndTarget(gestureEndTarget, false);
        maybeUpdateRecentsAttachedState(true);
        if (this.mGestureState.getEndTarget().isLauncher) {
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mActivityRestartListener);
        }
        GestureState.GestureEndTarget endTarget = this.mGestureState.getEndTarget();
        GestureState.GestureEndTarget gestureEndTarget2 = GestureState.GestureEndTarget.HOME;
        if (endTarget == gestureEndTarget2) {
            this.mStateCallback.addChangeListener(STATE_LAUNCHER_PRESENT | STATE_HANDLER_INVALIDATED, new WindowTransformHelper$$ExternalSyntheticLambda32(this));
            if (this.mRecentsAnimationTargetSetController != null) {
                this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
            }
            this.mLauncherTransitionController = null;
        } else {
            ObjectAnimator animateToValue = this.mCurrentShift.animateToValue(f, f2);
            animateToValue.setDuration(j).setInterpolator(interpolator);
            animateToValue.addUpdateListener(new WindowTransformHelper$$ExternalSyntheticLambda2(this));
            animateToValue.addListener(new AnimationSuccessListener() {
                public void onAnimationSuccess(Animator animator) {
                    if (WindowTransformHelper.this.mRecentsAnimationTargetSetController != null) {
                        WindowTransformHelper.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                    }
                }
            });
            animateToValue.start();
            this.mRunningWindowAnim = RunningWindowAnim.wrap(animateToValue);
        }
        if (this.mGestureState.getEndTarget() == gestureEndTarget2) {
            f = 0.0f;
        }
        notifyGestureResult(this.mGestureState.getEndTarget());
        if (this.mLauncherTransitionController != null) {
            Interpolator mapToProgress = Interpolators.mapToProgress(interpolator, f, f2);
            if (f == f2 || j <= 0) {
                this.mLauncherTransitionController.dispatchSetInterpolator(new WindowTransformHelper$$ExternalSyntheticLambda0(f2));
            } else {
                this.mLauncherTransitionController.dispatchSetInterpolator(mapToProgress);
                this.mAnimationFactory.adjustActivityControllerInterpolators();
            }
            this.mLauncherTransitionController.getAnimationPlayer().setDuration(Math.max(0, j));
            this.mLauncherTransitionController.getAnimationPlayer().start();
            this.mHasLauncherTransitionControllerStarted = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToProgressInternal$11(Boolean bool) {
        this.mRecentsView.startHome();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToProgressInternal$12(ValueAnimator valueAnimator) {
        IRecentsView iRecentsView = this.mRecentsView;
        if (iRecentsView != null && iRecentsView.asView().getVisibility() != 0) {
            this.mRecentsView.updateWindowAnimationProgress(valueAnimator.getAnimatedFraction());
        }
    }

    /* access modifiers changed from: private */
    public void resumeLastTask() {
        String str = TAG;
        DebugLog.m101w(str, "resumeLastTask: ");
        this.mRecentsAnimationTargetSetController.finish(false, (Runnable) null);
        Log.d(str, "finishRecentsAnimation false");
        reset();
    }

    /* access modifiers changed from: private */
    public void startNewTask() {
        String str = TAG;
        DebugLog.m98d(str, "startNewTask");
        ITaskViewAware checkingBeforeStartNewTask = this.mRecentsView.checkingBeforeStartNewTask();
        if (checkingBeforeStartNewTask == null || checkingBeforeStartNewTask.getTaskId() == -1) {
            Log.w(str, "startNewTask: but recents view not prepare for it");
            resumeLastTask();
            return;
        }
        if (!this.mCanceled) {
            int taskId = checkingBeforeStartNewTask.getTaskId();
            this.mGestureState.updateLastStartedTaskId(taskId);
            this.mRecentsView.startNewTask(false, new WindowTransformHelper$$ExternalSyntheticLambda34(this, this.mGestureState.getPreviouslyAppearedTaskIds().contains(Integer.valueOf(taskId))));
        }
        this.mCanceled = false;
        Log.d(str, "startNewTask finishRecentsAnimation true");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startNewTask$15(boolean z, Boolean bool) {
        if (!bool.booleanValue()) {
            reset();
            endLauncherTransitionController();
            updateSysUiFlags(1.0f);
            this.mActivityControlHelper.onLaunchTaskFailed();
            this.mRecentsAnimationTargetSetController.finish(true, (Runnable) null);
        } else if (z) {
            onRestartPreviouslyAppearedTask();
        }
    }

    public void reset() {
        this.mStateCallback.setStateOnUiThread(STATE_HANDLER_INVALIDATED);
    }

    public void onConsumerAboutToBeSwitched() {
        BaseGestureActivity baseGestureActivity = this.mActivity;
        if (baseGestureActivity != null) {
            baseGestureActivity.clearRunOnceOnStartCallback();
            this.mActivity.getRootView().setOnApplyWindowInsetsListener((View.OnApplyWindowInsetsListener) null);
        }
    }

    /* access modifiers changed from: private */
    public void invalidateHandler() {
        endRunningWindowAnim(false);
        Runnable runnable = this.mGestureEndCallback;
        if (runnable != null) {
            runnable.run();
        }
        this.mActivityInitListener.unregister();
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mActivityRestartListener);
        this.mTaskSnapshot = null;
    }

    /* access modifiers changed from: private */
    public void invalidateHandlerWithLauncher() {
        endLauncherTransitionController();
        this.mRecentsView.setRemoteWindowAnimationDependentSyncRTListener((DoubleConsumer) null);
        this.mRecentsView.onGestureAnimationEnd();
        this.mActivity.getRootView().setOnApplyWindowInsetsListener((View.OnApplyWindowInsetsListener) null);
    }

    private void endLauncherTransitionController() {
        setOverviewState(3);
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().end();
            this.mLauncherTransitionController = null;
        }
    }

    /* access modifiers changed from: private */
    public void notifyTransitionCancelled() {
        this.mAnimationFactory.onTransitionCancelled();
    }

    /* access modifiers changed from: private */
    public void resetStateForAnimationCancel() {
        this.mActivityControlHelper.onTransitionCancelled(this.mWasLauncherAlreadyVisible || this.mGestureStarted);
        this.mActivity.clearForceInvisibleFlag(1);
    }

    /* access modifiers changed from: private */
    public void switchToScreenshot() {
        DebugLog.m98d(TAG, "switchToScreenshot");
        if (!hasTargetSet()) {
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
            return;
        }
        RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController;
        if (recentsAnimationTargetSetController != null) {
            if (this.mTaskSnapshot == null) {
                this.mTaskSnapshot = recentsAnimationTargetSetController.screenshotTask(this.mGestureState.getRunningTaskId());
            }
            if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
                this.mRecentsView.updateTaskToLatestScreenshot(this.mGestureState.getRunningTaskId(), this.mTaskSnapshot);
            }
        }
        this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
    }

    /* access modifiers changed from: private */
    public void finishCurrentTransitionToRecents() {
        RecentsAnimationTargetSetController recentsAnimationTargetSetController;
        DebugLog.m98d(TAG, "finishCurrentTransitionToRecents");
        if (!hasTargetSet() || (recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController) == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            recentsAnimationTargetSetController.finish(true, new WindowTransformHelper$$ExternalSyntheticLambda17(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$finishCurrentTransitionToRecents$16() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    /* access modifiers changed from: private */
    public void finishCurrentTransitionToHome() {
        RecentsAnimationTargetSetController recentsAnimationTargetSetController;
        String str = TAG;
        DebugLog.m98d(str, "finishCurrentTransitionToHome");
        if (!hasTargetSet() || (recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController) == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            recentsAnimationTargetSetController.finish(true, new WindowTransformHelper$$ExternalSyntheticLambda15(this), true);
        }
        Log.d(str, "finishCurrentTransitionToHome true");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$finishCurrentTransitionToHome$17() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    /* access modifiers changed from: private */
    public void setupLauncherUiAfterSwipeUpToRecentsAnimation() {
        endLauncherTransitionController();
        this.mActivityControlHelper.onSwipeUpToRecentsComplete();
        RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mRecentsAnimationTargetSetController;
        if (recentsAnimationTargetSetController != null) {
            recentsAnimationTargetSetController.setDeferCancelUntilNextTransition(true, true);
        }
        this.mRecentsView.onSwipeUpAnimationSuccess();
        reset();
    }

    private boolean hasTargetSet() {
        RecentsAnimationTargetSet recentsAnimationTargetSet = this.mRecentsAnimationTargetSet;
        return recentsAnimationTargetSet != null && recentsAnimationTargetSet.hasTargets();
    }

    public void setGestureEndCallback(Runnable runnable) {
        this.mGestureEndCallback = runnable;
    }

    private static boolean isNotInRecents(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.isNotInRecents || remoteAnimationTargetCompat.activityType == 2;
    }

    private void notifyGestureResult(GestureState.GestureEndTarget gestureEndTarget) {
        IGestureEndTargetNotifier iGestureEndTargetNotifier = this.mGestureEndTargetNotifier;
        if (iGestureEndTargetNotifier != null && gestureEndTarget != null) {
            iGestureEndTargetNotifier.notifyGestureEndTargetChanged(gestureEndTarget.ordinal());
        }
    }

    private interface RunningWindowAnim {
        void cancel();

        void end();

        static RunningWindowAnim wrap(final Animator animator) {
            return new RunningWindowAnim() {
                public void end() {
                    animator.end();
                }

                public void cancel() {
                    animator.cancel();
                }
            };
        }
    }
}
