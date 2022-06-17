package com.motorola.systemui.cli.navgesture.states;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;
import com.motorola.systemui.cli.navgesture.animation.AnimationSuccessListener;
import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import com.motorola.systemui.cli.navgesture.animation.AnimatorSetBuilder;
import com.motorola.systemui.cli.navgesture.animation.AppTransitionManager;
import com.motorola.systemui.cli.navgesture.animation.PropertySetter;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

public class StateManager {
    public static final LauncherState BACKGROUND = new BackgroundState();
    public static final LauncherState NORMAL = new NormalState();
    public static final LauncherState OVERVIEW = new OverviewState();
    /* access modifiers changed from: private */
    public final AnimationConfig mConfig = new AnimationConfig();
    private LauncherState mCurrentStableState;
    private LauncherState mLastStableState;
    private AbstractRecentGestureLauncher mLauncher;
    private final ArrayList<StateListener> mListeners = new ArrayList<>();
    private LauncherState mRestState;
    private LauncherState mState;
    /* access modifiers changed from: private */
    public Animator[] mStateElementAnimators;
    private Set<StateHandler> mStateHandlers;
    private final Handler mUiHandler;

    public interface StateListener {
        void onStateTransitionComplete(LauncherState launcherState);

        void onStateTransitionStart(LauncherState launcherState);
    }

    public StateManager(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        LauncherState launcherState = NORMAL;
        this.mState = launcherState;
        this.mLastStableState = launcherState;
        this.mCurrentStableState = launcherState;
        this.mLauncher = abstractRecentGestureLauncher;
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mStateHandlers = new ArraySet();
    }

    public LauncherState getState() {
        return this.mState;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "LauncherState");
        printWriter.println(str + "\tmLastStableState:" + this.mLastStableState);
        printWriter.println(str + "\tmCurrentStableState:" + this.mCurrentStableState);
        printWriter.println(str + "\tmState:" + this.mState);
        printWriter.println(str + "\tmRestState:" + this.mRestState);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("\tisInTransition:");
        sb.append(this.mConfig.mCurrentAnimation != null);
        printWriter.println(sb.toString());
    }

    public void addStateListener(StateListener stateListener) {
        this.mListeners.add(stateListener);
    }

    public boolean shouldAnimateStateChange() {
        return !this.mLauncher.isForceInvisible() && this.mLauncher.isStarted();
    }

    public void goToState(LauncherState launcherState) {
        goToState(launcherState, shouldAnimateStateChange());
    }

    public void goToState(LauncherState launcherState, boolean z) {
        goToState(launcherState, z, 0, (Runnable) null);
    }

    public void reapplyState() {
        reapplyState(false);
    }

    public void reapplyState(boolean z) {
        boolean z2 = this.mConfig.mCurrentAnimation != null;
        if (z) {
            cancelAllStateElementAnimation();
            cancelAnimation();
        }
        if (this.mConfig.mCurrentAnimation == null) {
            DebugLog.m98d("StateManager", "LauncherStateManager#reapplyState: mState = " + this.mState.toShortString());
            for (StateHandler state : this.mStateHandlers) {
                state.setState(this.mState);
            }
            if (z2) {
                onStateTransitionEnd(this.mState);
                return;
            }
            return;
        }
        DebugLog.m101w("StateManager", "StateManager:reapplyState: but has running animation mState = " + this.mState.toShortString() + " " + this.mConfig.mCurrentAnimation);
    }

    private void goToState(LauncherState launcherState, boolean z, long j, final Runnable runnable) {
        DebugLog.m98d("StateManager", "LauncherStateManager#goToState: state = " + launcherState.toShortString() + " animated = " + z);
        boolean areAnimatorsEnabled = z & ValueAnimator.areAnimatorsEnabled();
        if (getState() == launcherState) {
            if (this.mConfig.mCurrentAnimation != null) {
                AnimationConfig animationConfig = this.mConfig;
                if (!animationConfig.userControlled && areAnimatorsEnabled && animationConfig.mTargetState == launcherState) {
                    if (runnable != null) {
                        this.mConfig.mCurrentAnimation.addListener(new AnimationSuccessListener() {
                            public void onAnimationSuccess(Animator animator) {
                                runnable.run();
                            }
                        });
                        return;
                    }
                    return;
                }
            } else if (runnable != null) {
                runnable.run();
                return;
            } else {
                return;
            }
        }
        LauncherState launcherState2 = this.mState;
        DebugLog.m98d("StateManager", "LauncherStateManager#goToState: state = " + launcherState.toShortString() + " , before config reset");
        this.mConfig.reset();
        if (!areAnimatorsEnabled) {
            cancelAllStateElementAnimation();
            onStateTransitionStart(launcherState);
            for (StateHandler state : this.mStateHandlers) {
                state.setState(launcherState);
            }
            onStateTransitionEnd(launcherState);
            if (runnable != null) {
                runnable.run();
            }
        } else if (j > 0) {
            this.mUiHandler.postDelayed(new StateManager$$ExternalSyntheticLambda0(this, this.mConfig.mChangeId, launcherState, launcherState2, runnable), j);
        } else {
            goToStateAnimated(launcherState, launcherState2, runnable);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$goToState$0(int i, LauncherState launcherState, LauncherState launcherState2, Runnable runnable) {
        if (this.mConfig.mChangeId == i) {
            goToStateAnimated(launcherState, launcherState2, runnable);
        }
    }

    private void goToStateAnimated(LauncherState launcherState, LauncherState launcherState2, Runnable runnable) {
        this.mConfig.duration = (long) (launcherState == NORMAL ? launcherState2.transitionDuration() : launcherState.transitionDuration());
        this.mUiHandler.postAtFrontOfQueue(new StartAnimRunnable(createAnimationToNewWorkspaceInternal(launcherState, new AnimatorSetBuilder(), runnable)));
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState launcherState, long j) {
        return createAnimationToNewWorkspace(launcherState, new AnimatorSetBuilder(), j, (Runnable) null);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState launcherState, AnimatorSetBuilder animatorSetBuilder, long j, Runnable runnable) {
        this.mConfig.reset();
        AnimationConfig animationConfig = this.mConfig;
        animationConfig.userControlled = true;
        animationConfig.duration = j;
        animationConfig.playbackController = AnimatorPlaybackController.wrap(createAnimationToNewWorkspaceInternal(launcherState, animatorSetBuilder, (Runnable) null), j, runnable);
        return this.mConfig.playbackController;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet createAnimationToNewWorkspaceInternal(final LauncherState launcherState, AnimatorSetBuilder animatorSetBuilder, final Runnable runnable) {
        for (StateHandler stateWithAnimation : this.mStateHandlers) {
            stateWithAnimation.setStateWithAnimation(launcherState, animatorSetBuilder, this.mConfig);
        }
        AnimatorSet build = animatorSetBuilder.build();
        build.addListener(new AnimationSuccessListener() {
            public void onAnimationStart(Animator animator) {
                StateManager.this.onStateTransitionStart(launcherState);
            }

            public void onAnimationSuccess(Animator animator) {
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
                StateManager.this.onStateTransitionEnd(launcherState);
            }
        });
        this.mConfig.setAnimation(build, launcherState);
        return this.mConfig.mCurrentAnimation;
    }

    /* access modifiers changed from: private */
    public void onStateTransitionStart(LauncherState launcherState) {
        LauncherState launcherState2 = this.mState;
        if (launcherState2 != launcherState) {
            launcherState2.onStateDisabled(this.mLauncher);
        }
        this.mState = launcherState;
        launcherState.onStateEnabled(this.mLauncher);
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onStateTransitionStart(launcherState);
        }
    }

    /* access modifiers changed from: private */
    public void onStateTransitionEnd(LauncherState launcherState) {
        if (launcherState != this.mCurrentStableState) {
            this.mLastStableState = NORMAL;
            this.mCurrentStableState = launcherState;
        }
        launcherState.onStateTransitionEnd(this.mLauncher);
        if (launcherState == NORMAL) {
            setRestState((LauncherState) null);
        }
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onStateTransitionComplete(launcherState);
        }
    }

    public LauncherState getLastState() {
        return this.mLastStableState;
    }

    public void moveToRestState() {
        if (this.mConfig.mCurrentAnimation != null && this.mConfig.userControlled) {
            DebugLog.m99e("StateManager", "StateManager:moveToRestState: running user animation");
        } else if (this.mState.disableRestore()) {
            DebugLog.m99e("StateManager", "LauncherStateManager#moveToRestState: getRestState() = " + getRestState().toShortString());
            goToState(getRestState());
            this.mLastStableState = NORMAL;
        }
    }

    public LauncherState getRestState() {
        LauncherState launcherState = this.mRestState;
        return launcherState == null ? NORMAL : launcherState;
    }

    public void setRestState(LauncherState launcherState) {
        this.mRestState = launcherState;
    }

    public void cancelAnimation() {
        this.mConfig.reset();
    }

    public void setCurrentUserControlledAnimation(AnimatorPlaybackController animatorPlaybackController) {
        clearCurrentAnimation();
        setCurrentAnimation(animatorPlaybackController.getTarget(), new Animator[0]);
        AnimationConfig animationConfig = this.mConfig;
        animationConfig.userControlled = true;
        animationConfig.playbackController = animatorPlaybackController;
    }

    public void setCurrentAnimation(AnimatorSet animatorSet, Animator... animatorArr) {
        int length = animatorArr.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AnimatorSet animatorSet2 = animatorArr[i];
            if (animatorSet2 != null) {
                AnimatorPlaybackController animatorPlaybackController = this.mConfig.playbackController;
                if (animatorPlaybackController != null && animatorPlaybackController.getTarget() == animatorSet2) {
                    clearCurrentAnimation();
                    break;
                } else if (this.mConfig.mCurrentAnimation == animatorSet2) {
                    clearCurrentAnimation();
                    break;
                }
            }
            i++;
        }
        if (this.mConfig.mCurrentAnimation != null) {
            z = true;
        }
        cancelAnimation();
        if (z) {
            reapplyState();
            onStateTransitionEnd(this.mState);
        }
        this.mConfig.setAnimation(animatorSet, (LauncherState) null);
    }

    private void cancelAllStateElementAnimation() {
        Animator[] animatorArr = this.mStateElementAnimators;
        if (animatorArr != null) {
            for (Animator animator : animatorArr) {
                if (animator != null) {
                    animator.cancel();
                }
            }
        }
    }

    public void cancelStateElementAnimation(int i) {
        Animator[] animatorArr = this.mStateElementAnimators;
        if (animatorArr != null && animatorArr[i] != null) {
            animatorArr[i].cancel();
        }
    }

    public Animator createStateElementAnimation(final int i, float... fArr) {
        cancelStateElementAnimation(i);
        AppTransitionManager appTransitionManager = this.mLauncher.getAppTransitionManager();
        if (this.mStateElementAnimators == null) {
            this.mStateElementAnimators = new Animator[appTransitionManager.getStateElementAnimationsCount()];
        }
        Animator createStateElementAnimation = appTransitionManager.createStateElementAnimation(i, fArr);
        this.mStateElementAnimators[i] = createStateElementAnimation;
        createStateElementAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StateManager.this.mStateElementAnimators[i] = null;
            }
        });
        return createStateElementAnimation;
    }

    public Animator createStateElementAnimation(final int i, Animator animator) {
        cancelStateElementAnimation(i);
        AppTransitionManager appTransitionManager = this.mLauncher.getAppTransitionManager();
        if (this.mStateElementAnimators == null) {
            this.mStateElementAnimators = new Animator[appTransitionManager.getStateElementAnimationsCount()];
        }
        this.mStateElementAnimators[i] = animator;
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StateManager.this.mStateElementAnimators[i] = null;
            }
        });
        return animator;
    }

    private void clearCurrentAnimation() {
        if (this.mConfig.mCurrentAnimation != null) {
            this.mConfig.mCurrentAnimation.removeListener(this.mConfig);
            AnimatorSet unused = this.mConfig.mCurrentAnimation = null;
        }
        this.mConfig.playbackController = null;
    }

    private class StartAnimRunnable implements Runnable {
        private final AnimatorSet mAnim;

        public StartAnimRunnable(AnimatorSet animatorSet) {
            this.mAnim = animatorSet;
        }

        public void run() {
            AnimatorSet access$000 = StateManager.this.mConfig.mCurrentAnimation;
            AnimatorSet animatorSet = this.mAnim;
            if (access$000 == animatorSet) {
                animatorSet.start();
            }
        }
    }

    public static class AnimationConfig extends AnimatorListenerAdapter {
        public long duration;
        /* access modifiers changed from: private */
        public int mChangeId = 0;
        /* access modifiers changed from: private */
        public AnimatorSet mCurrentAnimation;
        private PropertySetter mPropertySetter;
        /* access modifiers changed from: private */
        public LauncherState mTargetState;
        public AnimatorPlaybackController playbackController;
        public boolean userControlled;

        public void reset() {
            this.duration = 0;
            this.userControlled = false;
            this.mPropertySetter = null;
            this.mTargetState = null;
            AnimatorPlaybackController animatorPlaybackController = this.playbackController;
            if (animatorPlaybackController != null) {
                animatorPlaybackController.getAnimationPlayer().cancel();
                this.playbackController.dispatchOnCancel();
            } else {
                AnimatorSet animatorSet = this.mCurrentAnimation;
                if (animatorSet != null) {
                    animatorSet.setDuration(0);
                    this.mCurrentAnimation.cancel();
                }
            }
            this.mCurrentAnimation = null;
            this.playbackController = null;
            this.mChangeId++;
        }

        public PropertySetter getPropertySetter(AnimatorSetBuilder animatorSetBuilder) {
            PropertySetter propertySetter;
            if (this.mPropertySetter == null) {
                if (this.duration == 0) {
                    propertySetter = PropertySetter.NO_ANIM_PROPERTY_SETTER;
                } else {
                    propertySetter = new PropertySetter.AnimatedPropertySetter(this.duration, animatorSetBuilder);
                }
                this.mPropertySetter = propertySetter;
            }
            return this.mPropertySetter;
        }

        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
        }

        public void onAnimationEnd(Animator animator) {
            AnimatorPlaybackController animatorPlaybackController = this.playbackController;
            if (animatorPlaybackController != null && animatorPlaybackController.getTarget() == animator) {
                this.playbackController = null;
            }
            if (this.mCurrentAnimation == animator) {
                this.mCurrentAnimation = null;
            }
        }

        public void setAnimation(AnimatorSet animatorSet, LauncherState launcherState) {
            this.mCurrentAnimation = animatorSet;
            this.mTargetState = launcherState;
            animatorSet.addListener(this);
        }
    }

    public void registerStateHandler(StateHandler stateHandler) {
        this.mStateHandlers.add(stateHandler);
    }
}
