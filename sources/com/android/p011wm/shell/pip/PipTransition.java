package com.android.p011wm.shell.pip;

import android.app.TaskInfo;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.SurfaceControl;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.pip.PipAnimationController;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.pip.PipTransition */
public class PipTransition extends PipTransitionController {
    private final int mEnterExitAnimationDuration;
    private Transitions.TransitionFinishCallback mFinishCallback;
    private int mOneShotAnimationType = 0;

    public WindowContainerTransaction handleRequest(IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        return null;
    }

    public PipTransition(Context context, PipBoundsState pipBoundsState, PipMenuController pipMenuController, PipBoundsAlgorithm pipBoundsAlgorithm, PipAnimationController pipAnimationController, Transitions transitions, ShellTaskOrganizer shellTaskOrganizer) {
        super(pipBoundsState, pipMenuController, pipBoundsAlgorithm, pipAnimationController, transitions, shellTaskOrganizer);
        this.mEnterExitAnimationDuration = context.getResources().getInteger(C2219R.integer.config_pipResizeAnimationDuration);
    }

    public boolean startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Transitions.TransitionFinishCallback transitionFinishCallback) {
        int size = transitionInfo.getChanges().size() - 1;
        while (size >= 0) {
            TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
            if (change.getTaskInfo() == null || change.getTaskInfo().configuration.windowConfiguration.getWindowingMode() != 2) {
                size--;
            } else {
                this.mFinishCallback = transitionFinishCallback;
                return startEnterAnimation(change.getTaskInfo(), change.getLeash(), transaction);
            }
        }
        return false;
    }

    public void onFinishResize(TaskInfo taskInfo, Rect rect, int i, SurfaceControl.Transaction transaction) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        prepareFinishResizeTransaction(taskInfo, rect, i, transaction, windowContainerTransaction);
        this.mFinishCallback.onTransitionFinished(windowContainerTransaction, (WindowContainerTransactionCallback) null);
        finishResizeForMenu(rect);
    }

    private boolean startEnterAnimation(TaskInfo taskInfo, SurfaceControl surfaceControl, SurfaceControl.Transaction transaction) {
        PipAnimationController.PipTransitionAnimator pipTransitionAnimator;
        setBoundsStateForEntry(taskInfo.topActivity, taskInfo.pictureInPictureParams, taskInfo.topActivityInfo);
        Rect entryDestinationBounds = this.mPipBoundsAlgorithm.getEntryDestinationBounds();
        Rect bounds = taskInfo.configuration.windowConfiguration.getBounds();
        int i = this.mOneShotAnimationType;
        if (i == 0) {
            pipTransitionAnimator = this.mPipAnimationController.getAnimator(taskInfo, surfaceControl, bounds, bounds, entryDestinationBounds, PipBoundsAlgorithm.getValidSourceHintRect(taskInfo.pictureInPictureParams, bounds), 2, 0.0f, 0);
        } else if (i == 1) {
            transaction.setAlpha(surfaceControl, 0.0f);
            transaction.apply();
            pipTransitionAnimator = this.mPipAnimationController.getAnimator(taskInfo, surfaceControl, entryDestinationBounds, 0.0f, 1.0f);
            this.mOneShotAnimationType = 0;
        } else {
            throw new RuntimeException("Unrecognized animation type: " + this.mOneShotAnimationType);
        }
        pipTransitionAnimator.setTransitionDirection(2).setPipAnimationCallback(this.mPipAnimationCallback).setDuration((long) this.mEnterExitAnimationDuration).start();
        return true;
    }

    private void finishResizeForMenu(Rect rect) {
        this.mPipMenuController.movePipMenu((SurfaceControl) null, (SurfaceControl.Transaction) null, rect);
        this.mPipMenuController.updateMenuBounds(rect);
    }

    private void prepareFinishResizeTransaction(TaskInfo taskInfo, Rect rect, int i, SurfaceControl.Transaction transaction, WindowContainerTransaction windowContainerTransaction) {
        if (PipAnimationController.isInPipDirection(i)) {
            windowContainerTransaction.setActivityWindowingMode(taskInfo.token, 0);
            windowContainerTransaction.scheduleFinishEnterPip(taskInfo.token, rect);
        } else if (PipAnimationController.isOutPipDirection(i)) {
            if (i == 3) {
                rect = null;
            }
            windowContainerTransaction.setWindowingMode(taskInfo.token, getOutPipWindowingMode());
            windowContainerTransaction.setActivityWindowingMode(taskInfo.token, 0);
        } else {
            rect = null;
        }
        windowContainerTransaction.setBounds(taskInfo.token, rect);
        windowContainerTransaction.setBoundsChangeTransaction(taskInfo.token, transaction);
    }
}
