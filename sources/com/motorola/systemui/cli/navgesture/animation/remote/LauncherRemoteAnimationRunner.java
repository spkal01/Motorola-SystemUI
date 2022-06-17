package com.motorola.systemui.cli.navgesture.animation.remote;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
import com.android.systemui.shared.recents.utilities.Utilities;
import com.android.systemui.shared.system.RemoteAnimationRunnerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.util.DebugLog;

public abstract class LauncherRemoteAnimationRunner implements WrappedAnimationRunnerImpl, RemoteAnimationRunnerCompat {
    private static final String LOG_TAG = "LauncherRemoteAnimationRunner";
    private AnimationResult mAnimationResult;
    private final Handler mHandler;
    private final boolean mStartAtFrontOfQueue;

    public abstract void onCreateAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, AnimationResult animationResult);

    public LauncherRemoteAnimationRunner(Handler handler, boolean z) {
        this.mHandler = handler;
        this.mStartAtFrontOfQueue = z;
    }

    public void onAnimationStart(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Runnable runnable) {
        DebugLog.m98d(LOG_TAG, "onAnimationStart: RemoteAnimationTargetCompat ");
        LauncherRemoteAnimationRunner$$ExternalSyntheticLambda1 launcherRemoteAnimationRunner$$ExternalSyntheticLambda1 = new LauncherRemoteAnimationRunner$$ExternalSyntheticLambda1(this, runnable, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
        if (this.mStartAtFrontOfQueue) {
            Utilities.postAtFrontOfQueueAsynchronously(this.mHandler, launcherRemoteAnimationRunner$$ExternalSyntheticLambda1);
        } else {
            com.motorola.systemui.cli.navgesture.util.Utilities.postAsyncCallback(this.mHandler, launcherRemoteAnimationRunner$$ExternalSyntheticLambda1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAnimationStart$0(Runnable runnable, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        finishExistingAnimation();
        AnimationResult animationResult = new AnimationResult(runnable);
        this.mAnimationResult = animationResult;
        onCreateAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, animationResult);
    }

    /* access modifiers changed from: private */
    public void finishExistingAnimation() {
        AnimationResult animationResult = this.mAnimationResult;
        if (animationResult != null) {
            animationResult.finish();
            this.mAnimationResult = null;
        }
    }

    public void onAnimationCancelled() {
        com.motorola.systemui.cli.navgesture.util.Utilities.postAsyncCallback(this.mHandler, new LauncherRemoteAnimationRunner$$ExternalSyntheticLambda0(this));
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public static final class AnimationResult {
        private AnimatorSet mAnimator;
        private final Runnable mFinishRunnable;
        private boolean mFinished;
        private boolean mInitialized;

        private AnimationResult(Runnable runnable) {
            this.mFinished = false;
            this.mInitialized = false;
            this.mFinishRunnable = runnable;
        }

        /* access modifiers changed from: private */
        public void finish() {
            if (!this.mFinished) {
                this.mFinishRunnable.run();
                this.mFinished = true;
            }
        }

        public void setAnimation(AnimatorSet animatorSet) {
            if (!this.mInitialized) {
                this.mInitialized = true;
                this.mAnimator = animatorSet;
                if (animatorSet == null) {
                    finish();
                } else if (this.mFinished) {
                    animatorSet.start();
                    this.mAnimator.end();
                } else {
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimationResult.this.finish();
                        }
                    });
                    this.mAnimator.start();
                    this.mAnimator.setCurrentPlayTime(16);
                }
            } else {
                throw new IllegalStateException("Animation already initialized");
            }
        }
    }
}
