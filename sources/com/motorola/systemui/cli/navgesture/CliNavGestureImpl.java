package com.motorola.systemui.cli.navgesture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.MotionEvent;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.statusbar.CommandQueue;
import com.motorola.systemui.cli.navgesture.animation.GestureState;
import com.motorola.systemui.cli.navgesture.animation.OverviewCommandHelper;
import com.motorola.systemui.cli.navgesture.animation.RecentsAnimationDeviceState;
import com.motorola.systemui.cli.navgesture.animation.remote.TaskAnimationManager;
import com.motorola.systemui.cli.navgesture.inputconsumers.DeviceLockedInputConsumer;
import com.motorola.systemui.cli.navgesture.inputconsumers.InputConsumer;
import com.motorola.systemui.cli.navgesture.inputconsumers.OtherActivityInputConsumer;
import com.motorola.systemui.cli.navgesture.inputconsumers.OverviewInputConsumer;
import com.motorola.systemui.cli.navgesture.inputconsumers.ResetGestureInputConsumer;
import com.motorola.systemui.cli.navgesture.notifier.SystemUIGestureNotifier;

public class CliNavGestureImpl implements CommandQueue.Callbacks {
    private ActivityManagerWrapper mAM;
    private InputConsumer mConsumer;
    private Context mContext;
    private RecentsAnimationDeviceState mDeviceState;
    SystemUIGestureNotifier mGestureEndTargetNotifier;
    private GestureState mGestureState = GestureState.DEFAULT_STATE;
    private Handler mHandler;
    private InputConsumerController mInputConsumer;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mLidClosed = false;
    private OverviewCommandHelper mOverviewCommandHelper;
    private OverviewComponentObserver mOverviewComponentObserver;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                CliNavGestureImpl.this.closeRecentsActivity();
            }
        }
    };
    private ResetGestureInputConsumer mResetGestureInputConsumer;
    private ScreenLifecycle mScreenLifecycle;
    private ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onLidOpen() {
            CliNavGestureImpl.this.closeRecentsActivity();
            CliNavGestureImpl.this.disposeInputChannel();
        }

        public void onLidClosed() {
            CliNavGestureImpl.this.closeRecentsActivity();
            CliNavGestureImpl.this.initInputMonitor();
        }
    };
    private TaskAnimationManager mTaskAnimationManager;
    private InputConsumer mUncheckedConsumer;
    /* access modifiers changed from: private */
    public int mUserId = 0;
    private CurrentUserTracker mUserTracker;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$preloadOverView$0(Boolean bool) {
        return false;
    }

    public CliNavGestureImpl(Context context) {
        InputConsumer inputConsumer = InputConsumer.NO_OP;
        this.mUncheckedConsumer = inputConsumer;
        this.mConsumer = inputConsumer;
        this.mContext = MotoFeature.getCliContext(context);
        this.mAM = ActivityManagerWrapper.getInstance();
        this.mHandler = new Handler(Looper.getMainLooper());
        ScreenLifecycle screenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mScreenLifecycle = screenLifecycle;
        screenLifecycle.addObserver(this.mScreenObserver);
        init();
        C26791 r0 = new CurrentUserTracker((BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class)) {
            public void onUserSwitched(int i) {
                int unused = CliNavGestureImpl.this.mUserId = i;
                if (Process.myUserHandle().equals(UserHandle.SYSTEM)) {
                    if (CliNavGestureImpl.this.mUserId != 0) {
                        CliNavGestureImpl.this.userSwitched();
                    } else {
                        CliNavGestureImpl.this.initInputMonitor();
                    }
                } else if (CliNavGestureImpl.this.mUserId == 0) {
                    CliNavGestureImpl.this.userSwitched();
                } else {
                    CliNavGestureImpl.this.initInputMonitor();
                }
            }
        };
        this.mUserTracker = r0;
        r0.startTracking();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    private boolean isForCurrentUser() {
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        return Process.myUserHandle().equals(UserHandle.SYSTEM) ? currentUserId == 0 : currentUserId != 0;
    }

    /* access modifiers changed from: private */
    public void userSwitched() {
        closeRecentsActivity();
        disposeInputChannel();
    }

    private void init() {
        this.mTaskAnimationManager = new TaskAnimationManager();
        OverviewComponentObserver overviewComponentObserver = new OverviewComponentObserver(this.mContext);
        this.mOverviewComponentObserver = overviewComponentObserver;
        this.mOverviewCommandHelper = new OverviewCommandHelper(this.mContext, overviewComponentObserver);
        this.mResetGestureInputConsumer = new ResetGestureInputConsumer(this.mTaskAnimationManager);
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this.mContext);
        this.mDeviceState = recentsAnimationDeviceState;
        recentsAnimationDeviceState.addNavigationModeChangedCallback(new CliNavGestureImpl$$ExternalSyntheticLambda0(this));
        this.mDeviceState.runOnUserUnlocked(new CliNavGestureImpl$$ExternalSyntheticLambda1(this));
        this.mDeviceState.updateGestureTouchRegions();
    }

    /* access modifiers changed from: private */
    public void closeRecentsActivity() {
        OverviewComponentObserver overviewComponentObserver = this.mOverviewComponentObserver;
        if (overviewComponentObserver != null) {
            CliRecentsActivity cliRecentsActivity = (CliRecentsActivity) overviewComponentObserver.getActivityControlHelper().getCreatedActivity();
            Log.d("CliNavGestureImpl", "closeRecentsActivity activity = " + cliRecentsActivity);
            if (cliRecentsActivity != null) {
                cliRecentsActivity.finish();
            }
        }
    }

    public void onUserUnlocked() {
        Log.i("CliNavGestureImpl", "onUserUnlocked");
        if (!isForCurrentUser()) {
            Log.i("CliNavGestureImpl", "onUserUnlocked not for current user.");
            return;
        }
        InputConsumerController recentsAnimationInputConsumer = InputConsumerController.getRecentsAnimationInputConsumer(this.mDeviceState.getDisplayId());
        this.mInputConsumer = recentsAnimationInputConsumer;
        recentsAnimationInputConsumer.registerInputConsumer();
        this.mGestureEndTargetNotifier = new SystemUIGestureNotifier();
        initInputMonitor();
    }

    /* access modifiers changed from: private */
    public void onNavigationModeChanged(int i) {
        initInputMonitor();
    }

    /* access modifiers changed from: private */
    public void initInputMonitor() {
        if (!isForCurrentUser()) {
            Log.i("CliNavGestureImpl", "initInputMonitor not for current user.");
            return;
        }
        Log.i("CliNavGestureImpl", "initInputMonitor displayId = " + this.mDeviceState.getDisplayId() + "; mUserId = " + this.mUserId);
        disposeInputChannel();
        if (!this.mDeviceState.isButtonNavMode()) {
            this.mInputMonitor = InputManager.getInstance().monitorGestureInput("cli_nav_gesture", this.mDeviceState.getDisplayId());
            this.mInputEventReceiver = new CLINavGestureInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
        }
    }

    public void setSystemUiFlag(int i, boolean z) {
        RecentsAnimationDeviceState recentsAnimationDeviceState = this.mDeviceState;
        if (recentsAnimationDeviceState != null) {
            recentsAnimationDeviceState.setFlags(i, z);
        }
    }

    /* access modifiers changed from: private */
    public void disposeInputChannel() {
        InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    public void onOverviewToggle() {
        this.mOverviewCommandHelper.onOverviewToggle();
    }

    public void preloadOverView() {
        if (this.mDeviceState.isUserUnlocked() && this.mDeviceState.isFullyGesturalNavMode()) {
            ActivityControlHelper activityControlHelper = this.mOverviewComponentObserver.getActivityControlHelper();
            Intent intent = new Intent(this.mOverviewComponentObserver.getOverviewIntent());
            if (activityControlHelper.getCreatedActivity() == null) {
                activityControlHelper.createActivityInitListener(CliNavGestureImpl$$ExternalSyntheticLambda3.INSTANCE).register(intent);
                Log.i("CliNavGestureImpl", "preloadOverView");
                this.mTaskAnimationManager.preloadRecentsAnimation(intent);
                return;
            }
            Log.d("CliNavGestureImpl", "preloadOverView activityInterface.getCreatedActivity() = " + activityControlHelper.getCreatedActivity());
        }
    }

    /* access modifiers changed from: private */
    public void onInputEvent(InputEvent inputEvent) {
        InputConsumer inputConsumer;
        int actionMasked;
        if (!(inputEvent instanceof MotionEvent)) {
            Log.e("CliNavGestureImpl", "Unknown event " + inputEvent);
        } else if (!this.mDeviceState.isKeyguardShowing() || this.mDeviceState.isKeyguardShowingOccluded()) {
            MotionEvent motionEvent = (MotionEvent) inputEvent;
            int action = motionEvent.getAction();
            if (action == 0) {
                if (this.mDeviceState.isInSwipeUpTouchRegion(motionEvent)) {
                    GestureState gestureState = new GestureState(this.mGestureState);
                    GestureState createGestureState = createGestureState(this.mGestureState);
                    this.mConsumer.onConsumerAboutToBeSwitched();
                    this.mGestureState = createGestureState;
                    this.mConsumer = newConsumer(gestureState, createGestureState, motionEvent);
                    Log.i("CliNavGestureImpl", "setInputConsumer = " + this.mConsumer.getName());
                    this.mUncheckedConsumer = this.mConsumer;
                } else {
                    this.mUncheckedConsumer = InputConsumer.NO_OP;
                }
            }
            boolean z = true;
            if (this.mUncheckedConsumer != InputConsumer.NO_OP && ((actionMasked = motionEvent.getActionMasked()) == 0 || actionMasked == 1)) {
                Log.d("CliNavGestureImpl", "onMotionEvent(" + ((int) motionEvent.getRawX()) + ", " + ((int) motionEvent.getRawY()) + ") , action = " + motionEvent.getActionMasked());
            }
            if (!(action == 1 || action == 3) || (inputConsumer = this.mConsumer) == null || inputConsumer.getActiveConsumerInHierarchy().isConsumerDetachedFromGesture()) {
                z = false;
            }
            this.mUncheckedConsumer.onMotionEvent(motionEvent);
            if (z) {
                reset();
            }
        }
    }

    private GestureState createGestureState(GestureState gestureState) {
        GestureState gestureState2 = new GestureState(this.mOverviewComponentObserver, 1);
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            gestureState2.updateRunningTask(gestureState.getRunningTask());
            gestureState2.updateLastStartedTaskId(gestureState.getLastStartedTaskId());
            gestureState2.updatePreviouslyAppearedTaskIds(gestureState.getPreviouslyAppearedTaskIds());
        } else {
            gestureState2.updateRunningTask(this.mAM.getRunningTask(false));
        }
        return gestureState2;
    }

    private InputConsumer newConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent) {
        InputConsumer inputConsumer;
        boolean canStartSystemGesture = this.mDeviceState.canStartSystemGesture();
        Log.i("CliNavGestureImpl", "newConsumer isUserUnlocked = " + this.mDeviceState.isUserUnlocked() + "; canStartSystemGesture = " + canStartSystemGesture + "; getSystemUiFlags = " + this.mDeviceState.getSystemUiStateFlags());
        if (this.mDeviceState.isUserUnlocked()) {
            if (canStartSystemGesture || gestureState.isRecentsAnimationRunning()) {
                inputConsumer = newBaseConsumer(gestureState, gestureState2, motionEvent);
            } else {
                inputConsumer = this.mResetGestureInputConsumer;
            }
            return this.mDeviceState.isScreenPinningActive() ? this.mResetGestureInputConsumer : inputConsumer;
        } else if (!canStartSystemGesture || !this.mDeviceState.isKeyguardShowingOccluded()) {
            return this.mResetGestureInputConsumer;
        } else {
            return createDeviceLockedInputConsumer(gestureState2);
        }
    }

    private InputConsumer newBaseConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent) {
        if (this.mDeviceState.isKeyguardShowingOccluded()) {
            return createDeviceLockedInputConsumer(gestureState2);
        }
        if (gestureState2.getRunningTask() == null) {
            return this.mResetGestureInputConsumer;
        }
        if (gestureState.isRunningAnimationToLauncher() || gestureState2.getActivityInterface().isResumed()) {
            return createOverviewInputConsumer(gestureState, gestureState2, motionEvent);
        }
        if (this.mDeviceState.isGestureBlockedActivity(gestureState2.getRunningTask())) {
            return this.mResetGestureInputConsumer;
        }
        return createOtherActivityInputConsumer(gestureState2, motionEvent);
    }

    private InputConsumer createOtherActivityInputConsumer(GestureState gestureState, MotionEvent motionEvent) {
        return new OtherActivityInputConsumer(this.mContext, this.mDeviceState, this.mTaskAnimationManager, gestureState, this.mInputConsumer, gestureState.getActivityInterface().deferStartingActivity(this.mDeviceState, motionEvent), this.mDeviceState.isInExclusionRegion(motionEvent), this.mInputMonitor, new CliNavGestureImpl$$ExternalSyntheticLambda2(this), this.mGestureEndTargetNotifier);
    }

    private InputConsumer createDeviceLockedInputConsumer(GestureState gestureState) {
        if (!this.mDeviceState.isFullyGesturalNavMode() || gestureState.getRunningTask() == null) {
            return this.mResetGestureInputConsumer;
        }
        return new DeviceLockedInputConsumer(this.mContext, this.mDeviceState, this.mTaskAnimationManager, gestureState, this.mInputMonitor);
    }

    public InputConsumer createOverviewInputConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent) {
        BaseGestureActivity createdActivity = this.mOverviewComponentObserver.getActivityControlHelper().getCreatedActivity();
        if (createdActivity == null) {
            return this.mResetGestureInputConsumer;
        }
        if (createdActivity.getRootView().hasWindowFocus()) {
            return new OverviewInputConsumer(createdActivity, this.mInputMonitor, false);
        }
        return this.mResetGestureInputConsumer;
    }

    /* access modifiers changed from: private */
    public void onConsumerInactive(InputConsumer inputConsumer) {
        InputConsumer inputConsumer2 = this.mConsumer;
        if (inputConsumer2 != null && inputConsumer2.getActiveConsumerInHierarchy() == inputConsumer) {
            reset();
        }
    }

    private void reset() {
        ResetGestureInputConsumer resetGestureInputConsumer = this.mResetGestureInputConsumer;
        this.mUncheckedConsumer = resetGestureInputConsumer;
        this.mConsumer = resetGestureInputConsumer;
        this.mGestureState = GestureState.DEFAULT_STATE;
    }

    class CLINavGestureInputEventReceiver extends InputEventReceiver {
        CLINavGestureInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent inputEvent) {
            CliNavGestureImpl.this.onInputEvent(inputEvent);
            finishInputEvent(inputEvent, true);
        }
    }
}
