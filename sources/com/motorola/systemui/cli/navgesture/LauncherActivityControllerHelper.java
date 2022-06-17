package com.motorola.systemui.cli.navgesture;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.Log;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import com.motorola.systemui.cli.navgesture.states.LauncherState;
import com.motorola.systemui.cli.navgesture.states.StateManager;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LauncherActivityControllerHelper implements ActivityControlHelper<AbstractRecentGestureLauncher> {
    public AbstractRecentGestureLauncher getCreatedActivity() {
        return (AbstractRecentGestureLauncher) AbstractRecentGestureLauncher.ACTIVITY_TRACKER.getCreatedActivity();
    }

    public ActivityInitListener<AbstractRecentGestureLauncher> createActivityInitListener(Predicate<Boolean> predicate) {
        return new LauncherInitListener(new LauncherActivityControllerHelper$$ExternalSyntheticLambda0(predicate));
    }

    public IRecentsView getVisibleRecentsView() {
        AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null || !createdActivity.hasWindowFocus()) {
            return null;
        }
        return createdActivity.getOverviewPanel();
    }

    public ActivityControlHelper.AnimationFactory prepareRecentsUI(boolean z, boolean z2, Consumer<AnimatorPlaybackController> consumer) {
        final AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        final LauncherState state = createdActivity.getStateManager().getState();
        createdActivity.getStateManager().setRestState(state.disableRestore() ? createdActivity.getStateManager().getRestState() : state);
        final LauncherState launcherState = z2 ? StateManager.BACKGROUND : StateManager.OVERVIEW;
        createdActivity.getStateManager().goToState(launcherState, false);
        final Consumer<AnimatorPlaybackController> consumer2 = consumer;
        return new ActivityControlHelper.AnimationFactory() {
            private boolean mIsAttachedToWindow;

            public void createActivityController(long j) {
                LauncherState launcherState = StateManager.OVERVIEW;
                if (launcherState != launcherState) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    createdActivity.getOverviewPanel().createRecentsShowHideTransitionAnimation(launcherState, launcherState, animatorSet);
                    long j2 = j * 2;
                    animatorSet.setDuration(j2);
                    animatorSet.setInterpolator(Interpolators.LINEAR);
                    AnimatorPlaybackController wrap = AnimatorPlaybackController.wrap(animatorSet, j2);
                    createdActivity.getStateManager().setCurrentUserControlledAnimation(wrap);
                    wrap.setEndAction(new LauncherActivityControllerHelper$1$$ExternalSyntheticLambda0(createdActivity, wrap, launcherState, launcherState));
                    consumer2.accept(wrap);
                }
                if (SysUINavigationMode.getInstance(createdActivity).isGestureMode()) {
                    setRecentsAttachedToAppWindow(this.mIsAttachedToWindow, false);
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$createActivityController$0(AbstractRecentGestureLauncher abstractRecentGestureLauncher, AnimatorPlaybackController animatorPlaybackController, LauncherState launcherState, LauncherState launcherState2) {
                StateManager stateManager = abstractRecentGestureLauncher.getStateManager();
                if (((double) animatorPlaybackController.getInterpolatedProgress()) <= 0.5d) {
                    launcherState = launcherState2;
                }
                stateManager.goToState(launcherState, false);
            }

            public void onTransitionCancelled() {
                createdActivity.getStateManager().goToState(state, false);
            }

            public void setRecentsAttachedToAppWindow(boolean z, boolean z2) {
                if (this.mIsAttachedToWindow != z || !z2) {
                    Log.d("LauncherActivityControllerHelper", "setRecentsAttachedToAppWindow attached = " + z + "; mIsAttachedToWindow = " + this.mIsAttachedToWindow + "; animate = " + z2);
                    this.mIsAttachedToWindow = z;
                    IRecentsView overviewPanel = createdActivity.getOverviewPanel();
                    StateManager stateManager = createdActivity.getStateManager();
                    float[] fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.0f;
                    Animator createStateElementAnimation = stateManager.createStateElementAnimation(0, fArr);
                    if (overviewPanel.getRunningTaskIndex() == 0) {
                        createdActivity.getStateManager().cancelStateElementAnimation(1);
                        Animator recentsAttachedToAppWindow = overviewPanel.setRecentsAttachedToAppWindow(z, z2);
                        if (recentsAttachedToAppWindow != null) {
                            createdActivity.getStateManager().createStateElementAnimation(1, recentsAttachedToAppWindow).start();
                        }
                        createStateElementAnimation.setInterpolator(z ? Interpolators.INSTANT : Interpolators.ACCEL_2);
                    } else {
                        createStateElementAnimation.setInterpolator(Interpolators.ACCEL_DEACCEL);
                    }
                    createStateElementAnimation.setDuration(z2 ? 300 : 0).start();
                }
            }
        };
    }

    public void onTransitionCancelled(boolean z) {
        AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            StateManager stateManager = createdActivity.getStateManager();
            stateManager.goToState(stateManager.getRestState(), z);
        }
    }

    public void onLaunchTaskFailed() {
        AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getStateManager().goToState(StateManager.OVERVIEW);
        }
    }

    public void onLaunchTaskSuccess() {
        AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getStateManager().moveToRestState();
        }
    }

    public void switchRunningTaskViewToScreenshot(ThumbnailData thumbnailData, Runnable runnable) {
        AbstractRecentGestureLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            IRecentsView overviewPanel = createdActivity.getOverviewPanel();
            if (overviewPanel != null) {
                overviewPanel.switchToScreenshot(thumbnailData, runnable);
            } else if (runnable != null) {
                runnable.run();
            }
        }
    }
}
