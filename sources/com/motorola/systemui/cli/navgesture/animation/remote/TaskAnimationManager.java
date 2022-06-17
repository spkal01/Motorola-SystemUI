package com.motorola.systemui.cli.navgesture.animation.remote;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.animation.GestureState;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationCallbacks;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.Objects;
import java.util.function.Consumer;

public class TaskAnimationManager implements RecentsAnimationCallbacks.RecentsAnimationListener {
    /* access modifiers changed from: private */
    public RecentsAnimationCallbacks mCallbacks;
    /* access modifiers changed from: private */
    public RecentsAnimationTargetSetController mController;
    /* access modifiers changed from: private */
    public RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    /* access modifiers changed from: private */
    public GestureState mLastGestureState;
    /* access modifiers changed from: private */
    public RecentsAnimationTargetSet mTargets;

    public void preloadRecentsAnimation(Intent intent) {
        AppExecutors.background().execute(new TaskAnimationManager$$ExternalSyntheticLambda0(intent));
    }

    public RecentsAnimationCallbacks startRecentsAnimation(GestureState gestureState, Intent intent, RecentsAnimationCallbacks.RecentsAnimationListener recentsAnimationListener) {
        if (this.mController != null) {
            Log.e("TaskAnimationManager", "New recents animation started before old animation completed", new Exception());
        }
        finishRunningRecentsAnimation(false);
        final ActivityControlHelper activityInterface = gestureState.getActivityInterface();
        this.mLastGestureState = gestureState;
        RecentsAnimationCallbacks recentsAnimationCallbacks = new RecentsAnimationCallbacks(false);
        this.mCallbacks = recentsAnimationCallbacks;
        recentsAnimationCallbacks.addListener(new RecentsAnimationCallbacks.RecentsAnimationListener() {
            public void onRecentsAnimationStart(RecentsAnimationTargetSetController recentsAnimationTargetSetController, RecentsAnimationTargetSet recentsAnimationTargetSet) {
                if (TaskAnimationManager.this.mCallbacks != null) {
                    RecentsAnimationTargetSetController unused = TaskAnimationManager.this.mController = recentsAnimationTargetSetController;
                    RecentsAnimationTargetSet unused2 = TaskAnimationManager.this.mTargets = recentsAnimationTargetSet;
                    TaskAnimationManager taskAnimationManager = TaskAnimationManager.this;
                    RemoteAnimationTargetCompat unused3 = taskAnimationManager.mLastAppearedTaskTarget = taskAnimationManager.mTargets.findTask(TaskAnimationManager.this.mLastGestureState.getRunningTaskId());
                    TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                }
            }

            public void onRecentsAnimationCanceled(ThumbnailData thumbnailData) {
                if (thumbnailData != null) {
                    activityInterface.switchRunningTaskViewToScreenshot(thumbnailData, new TaskAnimationManager$1$$ExternalSyntheticLambda0(this, thumbnailData));
                } else {
                    TaskAnimationManager.this.cleanUpRecentsAnimation((ThumbnailData) null);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onRecentsAnimationCanceled$0(ThumbnailData thumbnailData) {
                TaskAnimationManager.this.cleanUpRecentsAnimation(thumbnailData);
            }

            public void onRecentsAnimationFinished(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
                TaskAnimationManager.this.cleanUpRecentsAnimation((ThumbnailData) null);
            }

            public void onTaskAppeared(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
                if (TaskAnimationManager.this.mController == null) {
                    return;
                }
                if (TaskAnimationManager.this.mLastAppearedTaskTarget == null || remoteAnimationTargetCompat.taskId != TaskAnimationManager.this.mLastAppearedTaskTarget.taskId) {
                    if (TaskAnimationManager.this.mLastAppearedTaskTarget != null) {
                        TaskAnimationManager.this.mController.removeTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                    }
                    RemoteAnimationTargetCompat unused = TaskAnimationManager.this.mLastAppearedTaskTarget = remoteAnimationTargetCompat;
                    TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                }
            }
        });
        this.mCallbacks.addListener(gestureState);
        this.mCallbacks.addListener(recentsAnimationListener);
        AppExecutors.background().execute(new TaskAnimationManager$$ExternalSyntheticLambda2(this, intent));
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED);
        return this.mCallbacks;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecentsAnimation$1(Intent intent) {
        ActivityManagerWrapper.getInstance().startRecentsActivity(intent, SystemClock.currentThreadTimeMillis(), this.mCallbacks, (Consumer<Boolean>) null, (Handler) null);
    }

    public RecentsAnimationCallbacks continueRecentsAnimation(GestureState gestureState) {
        this.mCallbacks.removeListener(this.mLastGestureState);
        this.mLastGestureState = gestureState;
        this.mCallbacks.addListener(gestureState);
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED | GestureState.STATE_RECENTS_ANIMATION_STARTED);
        gestureState.updateLastAppearedTaskTarget(this.mLastAppearedTaskTarget);
        return this.mCallbacks;
    }

    public void finishRunningRecentsAnimation(boolean z) {
        Runnable runnable;
        if (this.mController != null) {
            this.mCallbacks.notifyAnimationCanceled();
            Handler handler = AppExecutors.m97ui().getHandler();
            if (z) {
                RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mController;
                Objects.requireNonNull(recentsAnimationTargetSetController);
                runnable = new TaskAnimationManager$$ExternalSyntheticLambda1(recentsAnimationTargetSetController);
            } else {
                RecentsAnimationTargetSetController recentsAnimationTargetSetController2 = this.mController;
                Objects.requireNonNull(recentsAnimationTargetSetController2);
                runnable = new RecentsAnimationCallbacks$$ExternalSyntheticLambda4(recentsAnimationTargetSetController2);
            }
            Utilities.postAsyncCallback(handler, runnable);
            cleanUpRecentsAnimation((ThumbnailData) null);
        }
    }

    public void notifyRecentsAnimationState(RecentsAnimationCallbacks.RecentsAnimationListener recentsAnimationListener) {
        if (isRecentsAnimationRunning()) {
            recentsAnimationListener.onRecentsAnimationStart(this.mController, this.mTargets);
        }
    }

    public boolean isRecentsAnimationRunning() {
        return this.mController != null;
    }

    /* access modifiers changed from: private */
    public void cleanUpRecentsAnimation(ThumbnailData thumbnailData) {
        RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mController;
        if (!(recentsAnimationTargetSetController == null || thumbnailData == null)) {
            recentsAnimationTargetSetController.cleanupScreenshot();
        }
        RecentsAnimationTargetSet recentsAnimationTargetSet = this.mTargets;
        if (recentsAnimationTargetSet != null) {
            recentsAnimationTargetSet.release();
        }
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeAllListeners();
        }
        this.mController = null;
        this.mCallbacks = null;
        this.mTargets = null;
        this.mLastGestureState = null;
        this.mLastAppearedTaskTarget = null;
    }
}
