package com.android.p011wm.shell.splitscreen;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowManager;
import android.window.DisplayAreaInfo;
import android.window.IRemoteTransition;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.RootTaskDisplayAreaOrganizer;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.common.split.SplitLayout;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.splitscreen.StageTaskListener;
import com.android.p011wm.shell.transition.Transitions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.splitscreen.StageCoordinator */
class StageCoordinator implements SplitLayout.SplitLayoutHandler, RootTaskDisplayAreaOrganizer.RootTaskDisplayAreaListener, Transitions.TransitionHandler {
    private static final String TAG = "StageCoordinator";
    private final Context mContext;
    int mDismissTop;
    private DisplayAreaInfo mDisplayAreaInfo;
    private final int mDisplayId;
    private final DisplayImeController mDisplayImeController;
    private boolean mDividerVisible;
    private boolean mExitSplitScreenOnHide;
    private final List<SplitScreen.SplitScreenListener> mListeners;
    /* access modifiers changed from: private */
    public final MainStage mMainStage;
    private final StageListenerImpl mMainStageListener;
    private final Runnable mOnTransitionAnimationComplete;
    private final RootTaskDisplayAreaOrganizer mRootTDAOrganizer;
    private final SideStage mSideStage;
    private final StageListenerImpl mSideStageListener;
    private int mSideStagePosition;
    private SplitLayout mSplitLayout;
    private final SplitScreenTransitions mSplitTransitions;
    private final SurfaceSession mSurfaceSession;
    private final SyncTransactionQueue mSyncQueue;
    private final ShellTaskOrganizer mTaskOrganizer;
    private boolean mUseLegacySplit;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (!isSplitScreenVisible()) {
            setDividerVisibility(false);
            this.mSplitLayout.resetDividerPosition();
        }
        this.mDismissTop = -2;
    }

    StageCoordinator(Context context, int i, SyncTransactionQueue syncTransactionQueue, RootTaskDisplayAreaOrganizer rootTaskDisplayAreaOrganizer, ShellTaskOrganizer shellTaskOrganizer, DisplayImeController displayImeController, Transitions transitions, TransactionPool transactionPool) {
        int i2 = i;
        RootTaskDisplayAreaOrganizer rootTaskDisplayAreaOrganizer2 = rootTaskDisplayAreaOrganizer;
        Transitions transitions2 = transitions;
        SurfaceSession surfaceSession = new SurfaceSession();
        this.mSurfaceSession = surfaceSession;
        StageListenerImpl stageListenerImpl = new StageListenerImpl();
        this.mMainStageListener = stageListenerImpl;
        StageListenerImpl stageListenerImpl2 = new StageListenerImpl();
        this.mSideStageListener = stageListenerImpl2;
        this.mSideStagePosition = 1;
        this.mListeners = new ArrayList();
        this.mExitSplitScreenOnHide = true;
        this.mDismissTop = -2;
        StageCoordinator$$ExternalSyntheticLambda4 stageCoordinator$$ExternalSyntheticLambda4 = new StageCoordinator$$ExternalSyntheticLambda4(this);
        this.mOnTransitionAnimationComplete = stageCoordinator$$ExternalSyntheticLambda4;
        this.mContext = context;
        this.mDisplayId = i2;
        this.mSyncQueue = syncTransactionQueue;
        this.mRootTDAOrganizer = rootTaskDisplayAreaOrganizer2;
        this.mTaskOrganizer = shellTaskOrganizer;
        ShellTaskOrganizer shellTaskOrganizer2 = shellTaskOrganizer;
        int i3 = i;
        SyncTransactionQueue syncTransactionQueue2 = syncTransactionQueue;
        SurfaceSession surfaceSession2 = surfaceSession;
        this.mMainStage = new MainStage(shellTaskOrganizer2, i3, stageListenerImpl, syncTransactionQueue2, surfaceSession2);
        this.mSideStage = new SideStage(shellTaskOrganizer2, i3, stageListenerImpl2, syncTransactionQueue2, surfaceSession2);
        this.mDisplayImeController = displayImeController;
        rootTaskDisplayAreaOrganizer2.registerListener(i2, this);
        this.mSplitTransitions = new SplitScreenTransitions(transactionPool, transitions2, stageCoordinator$$ExternalSyntheticLambda4);
        transitions2.addHandler(this);
    }

    @VisibleForTesting
    StageCoordinator(Context context, int i, SyncTransactionQueue syncTransactionQueue, RootTaskDisplayAreaOrganizer rootTaskDisplayAreaOrganizer, ShellTaskOrganizer shellTaskOrganizer, MainStage mainStage, SideStage sideStage, DisplayImeController displayImeController, SplitLayout splitLayout, Transitions transitions, TransactionPool transactionPool) {
        this.mSurfaceSession = new SurfaceSession();
        this.mMainStageListener = new StageListenerImpl();
        this.mSideStageListener = new StageListenerImpl();
        this.mSideStagePosition = 1;
        this.mListeners = new ArrayList();
        this.mExitSplitScreenOnHide = true;
        this.mDismissTop = -2;
        StageCoordinator$$ExternalSyntheticLambda4 stageCoordinator$$ExternalSyntheticLambda4 = new StageCoordinator$$ExternalSyntheticLambda4(this);
        this.mOnTransitionAnimationComplete = stageCoordinator$$ExternalSyntheticLambda4;
        this.mContext = context;
        this.mDisplayId = i;
        this.mSyncQueue = syncTransactionQueue;
        this.mRootTDAOrganizer = rootTaskDisplayAreaOrganizer;
        this.mTaskOrganizer = shellTaskOrganizer;
        this.mMainStage = mainStage;
        this.mSideStage = sideStage;
        this.mDisplayImeController = displayImeController;
        rootTaskDisplayAreaOrganizer.registerListener(i, this);
        this.mSplitLayout = splitLayout;
        this.mSplitTransitions = new SplitScreenTransitions(transactionPool, transitions, stageCoordinator$$ExternalSyntheticLambda4);
        transitions.addHandler(this);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public SplitScreenTransitions getSplitTransitions() {
        return this.mSplitTransitions;
    }

    /* access modifiers changed from: package-private */
    public boolean isSplitScreenVisible() {
        return this.mSideStageListener.mVisible && this.mMainStageListener.mVisible;
    }

    /* access modifiers changed from: package-private */
    public boolean moveToSideStage(ActivityManager.RunningTaskInfo runningTaskInfo, int i) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        setSideStagePosition(i);
        this.mMainStage.activate(getMainStageBounds(), windowContainerTransaction);
        this.mSideStage.addTask(runningTaskInfo, getSideStageBounds(), windowContainerTransaction);
        this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean removeFromSideStage(int i) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        boolean removeTask = this.mSideStage.removeTask(i, this.mMainStage.isActive() ? this.mMainStage.mRootTaskInfo.token : null, windowContainerTransaction);
        this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        return removeTask;
    }

    /* access modifiers changed from: package-private */
    public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, IRemoteTransition iRemoteTransition) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (bundle2 == null) {
            bundle2 = new Bundle();
        }
        setSideStagePosition(i3);
        this.mMainStage.activate(getMainStageBounds(), windowContainerTransaction);
        this.mSideStage.setBounds(getSideStageBounds(), windowContainerTransaction);
        addActivityOptions(bundle, this.mMainStage);
        addActivityOptions(bundle2, this.mSideStage);
        windowContainerTransaction.startTask(i, bundle);
        windowContainerTransaction.startTask(i2, bundle2);
        this.mSplitTransitions.startEnterTransition(12, windowContainerTransaction, iRemoteTransition, this);
    }

    /* access modifiers changed from: package-private */
    public int getSideStagePosition() {
        return this.mSideStagePosition;
    }

    /* access modifiers changed from: package-private */
    public int getMainStagePosition() {
        return this.mSideStagePosition == 0 ? 1 : 0;
    }

    /* access modifiers changed from: package-private */
    public void setSideStagePosition(int i) {
        setSideStagePosition(i, true);
    }

    private void setSideStagePosition(int i, boolean z) {
        if (this.mSideStagePosition != i) {
            this.mSideStagePosition = i;
            sendOnStagePositionChanged();
            StageListenerImpl stageListenerImpl = this.mSideStageListener;
            if (stageListenerImpl.mVisible && z) {
                onStageVisibilityChanged(stageListenerImpl);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setSideStageVisibility(boolean z) {
        if (this.mSideStageListener.mVisible != z) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            this.mSideStage.setVisibility(z, windowContainerTransaction);
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    /* access modifiers changed from: package-private */
    public void exitSplitScreen() {
        exitSplitScreen((StageTaskListener) null);
    }

    /* access modifiers changed from: package-private */
    public void exitSplitScreenOnHide(boolean z) {
        this.mExitSplitScreenOnHide = z;
    }

    private void exitSplitScreen(StageTaskListener stageTaskListener) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        SideStage sideStage = this.mSideStage;
        boolean z = true;
        sideStage.removeAllTasks(windowContainerTransaction, stageTaskListener == sideStage);
        MainStage mainStage = this.mMainStage;
        if (stageTaskListener != mainStage) {
            z = false;
        }
        mainStage.deactivate(windowContainerTransaction, z);
        this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        this.mSplitLayout.resetDividerPosition();
    }

    private void prepareExitSplitScreen(int i, WindowContainerTransaction windowContainerTransaction) {
        boolean z = false;
        this.mSideStage.removeAllTasks(windowContainerTransaction, i == 1);
        MainStage mainStage = this.mMainStage;
        if (i == 0) {
            z = true;
        }
        mainStage.deactivate(windowContainerTransaction, z);
    }

    /* access modifiers changed from: package-private */
    public void getStageBounds(Rect rect, Rect rect2) {
        rect.set(this.mSplitLayout.getBounds1());
        rect2.set(this.mSplitLayout.getBounds2());
    }

    private void addActivityOptions(Bundle bundle, StageTaskListener stageTaskListener) {
        bundle.putParcelable("android.activity.launchRootTaskToken", stageTaskListener.mRootTaskInfo.token);
    }

    /* access modifiers changed from: package-private */
    public void updateActivityOptions(Bundle bundle, int i) {
        addActivityOptions(bundle, i == this.mSideStagePosition ? this.mSideStage : this.mMainStage);
        if (!this.mMainStage.isActive()) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            this.mMainStage.activate(getMainStageBounds(), windowContainerTransaction);
            this.mSideStage.setBounds(getSideStageBounds(), windowContainerTransaction);
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    /* access modifiers changed from: package-private */
    public void registerSplitScreenListener(SplitScreen.SplitScreenListener splitScreenListener) {
        if (!this.mListeners.contains(splitScreenListener)) {
            this.mListeners.add(splitScreenListener);
            splitScreenListener.onStagePositionChanged(0, getMainStagePosition());
            splitScreenListener.onStagePositionChanged(1, getSideStagePosition());
            this.mSideStage.onSplitScreenListenerRegistered(splitScreenListener, 1);
            this.mMainStage.onSplitScreenListenerRegistered(splitScreenListener, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterSplitScreenListener(SplitScreen.SplitScreenListener splitScreenListener) {
        this.mListeners.remove(splitScreenListener);
    }

    private void sendOnStagePositionChanged() {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            SplitScreen.SplitScreenListener splitScreenListener = this.mListeners.get(size);
            splitScreenListener.onStagePositionChanged(0, getMainStagePosition());
            splitScreenListener.onStagePositionChanged(1, getSideStagePosition());
        }
    }

    /* access modifiers changed from: private */
    public void onStageChildTaskStatusChanged(StageListenerImpl stageListenerImpl, int i, boolean z, boolean z2) {
        int i2 = z ? stageListenerImpl == this.mSideStageListener ? 1 : 0 : -1;
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onTaskStageChanged(i, i2, z2);
        }
    }

    /* access modifiers changed from: private */
    public void onStageRootTaskAppeared(StageListenerImpl stageListenerImpl) {
        if (this.mMainStageListener.mHasRootTask && this.mSideStageListener.mHasRootTask) {
            this.mUseLegacySplit = this.mContext.getResources().getBoolean(17891778);
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            windowContainerTransaction.setAdjacentRoots(this.mMainStage.mRootTaskInfo.token, this.mSideStage.mRootTaskInfo.token);
            if (!this.mUseLegacySplit) {
                windowContainerTransaction.setLaunchAdjacentFlagRoot(this.mSideStage.mRootTaskInfo.token);
            }
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    /* access modifiers changed from: private */
    public void onStageRootTaskVanished(StageListenerImpl stageListenerImpl) {
        if (stageListenerImpl == this.mMainStageListener || stageListenerImpl == this.mSideStageListener) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            this.mMainStage.deactivate(windowContainerTransaction);
            if (!this.mUseLegacySplit) {
                windowContainerTransaction.clearLaunchAdjacentFlagRoot(this.mSideStage.mRootTaskInfo.token);
            }
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    private void setDividerVisibility(boolean z) {
        if (this.mDividerVisible != z) {
            this.mDividerVisible = z;
            if (z) {
                this.mSplitLayout.init();
            } else {
                this.mSplitLayout.release();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onStageVisibilityChanged(StageListenerImpl stageListenerImpl) {
        boolean z = this.mSideStageListener.mVisible;
        boolean z2 = this.mMainStageListener.mVisible;
        setDividerVisibility(isSplitScreenVisible());
        if (this.mExitSplitScreenOnHide && !z2 && !z) {
            exitSplitScreen();
        }
        if (z2) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (z) {
                this.mMainStage.updateConfiguration(6, getMainStageBounds(), windowContainerTransaction);
            } else {
                this.mMainStage.updateConfiguration(1, (Rect) null, windowContainerTransaction);
            }
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
        this.mSyncQueue.runInSync(new StageCoordinator$$ExternalSyntheticLambda2(this, z, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStageVisibilityChanged$1(boolean z, boolean z2, SurfaceControl.Transaction transaction) {
        SurfaceControl dividerLeash = this.mSplitLayout.getDividerLeash();
        SurfaceControl surfaceControl = this.mSideStage.mRootLeash;
        SurfaceControl surfaceControl2 = this.mMainStage.mRootLeash;
        if (dividerLeash != null) {
            if (this.mDividerVisible) {
                transaction.show(dividerLeash).setLayer(dividerLeash, Integer.MAX_VALUE).setPosition(dividerLeash, (float) this.mSplitLayout.getDividerBounds().left, (float) this.mSplitLayout.getDividerBounds().top);
            } else {
                transaction.hide(dividerLeash);
            }
        }
        if (z) {
            Rect sideStageBounds = getSideStageBounds();
            transaction.show(surfaceControl).setPosition(surfaceControl, (float) sideStageBounds.left, (float) sideStageBounds.top).setWindowCrop(surfaceControl, sideStageBounds.width(), sideStageBounds.height());
        } else {
            transaction.hide(surfaceControl);
        }
        if (z2) {
            Rect mainStageBounds = getMainStageBounds();
            transaction.show(surfaceControl2);
            if (z) {
                transaction.setPosition(surfaceControl2, (float) mainStageBounds.left, (float) mainStageBounds.top).setWindowCrop(surfaceControl2, mainStageBounds.width(), mainStageBounds.height());
            } else {
                transaction.setPosition(surfaceControl2, 0.0f, 0.0f).setWindowCrop(surfaceControl2, (Rect) null);
            }
        } else {
            transaction.hide(surfaceControl2);
        }
    }

    /* access modifiers changed from: private */
    public void onStageHasChildrenChanged(StageListenerImpl stageListenerImpl) {
        boolean z = stageListenerImpl.mHasChildren;
        StageListenerImpl stageListenerImpl2 = this.mSideStageListener;
        boolean z2 = stageListenerImpl == stageListenerImpl2;
        if (!z) {
            if (z2 && this.mMainStageListener.mVisible) {
                exitSplitScreen(this.mMainStage);
            } else if (!z2 && stageListenerImpl2.mVisible) {
                exitSplitScreen(this.mSideStage);
            }
        } else if (z2) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            this.mMainStage.activate(getMainStageBounds(), windowContainerTransaction);
            this.mSideStage.setBounds(getSideStageBounds(), windowContainerTransaction);
            windowContainerTransaction.reorder(this.mSideStage.mRootTaskInfo.token, true);
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public IBinder onSnappedToDismissTransition(boolean z) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        prepareExitSplitScreen(z ^ true ? 1 : 0, windowContainerTransaction);
        return this.mSplitTransitions.startSnapToDismiss(windowContainerTransaction, this);
    }

    public void onSnappedToDismiss(boolean z) {
        boolean z2 = false;
        if (!z ? this.mSideStagePosition == 0 : this.mSideStagePosition == 1) {
            z2 = true;
        }
        if (Transitions.ENABLE_SHELL_TRANSITIONS) {
            onSnappedToDismissTransition(z2);
        } else {
            exitSplitScreen(z2 ? this.mMainStage : this.mSideStage);
        }
    }

    public void onDoubleTappedDivider() {
        setSideStagePosition(this.mSideStagePosition == 0 ? 1 : 0);
    }

    public void onBoundsChanging(SplitLayout splitLayout) {
        int i = this.mSideStagePosition;
        this.mSyncQueue.runInSync(new StageCoordinator$$ExternalSyntheticLambda1(splitLayout, i == 0 ? this.mSideStage : this.mMainStage, i == 0 ? this.mMainStage : this.mSideStage));
    }

    public void onBoundsChanged(SplitLayout splitLayout) {
        int i = this.mSideStagePosition;
        StageTaskListener stageTaskListener = i == 0 ? this.mSideStage : this.mMainStage;
        StageTaskListener stageTaskListener2 = i == 0 ? this.mMainStage : this.mSideStage;
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        splitLayout.applyTaskChanges(windowContainerTransaction, stageTaskListener.mRootTaskInfo, stageTaskListener2.mRootTaskInfo);
        this.mSyncQueue.queue(windowContainerTransaction);
        this.mSyncQueue.runInSync(new StageCoordinator$$ExternalSyntheticLambda0(splitLayout, stageTaskListener, stageTaskListener2));
    }

    public int getSplitItemPosition(WindowContainerToken windowContainerToken) {
        if (windowContainerToken == null) {
            return -1;
        }
        if (windowContainerToken.equals(this.mMainStage.mRootTaskInfo.getToken())) {
            return getMainStagePosition();
        }
        if (windowContainerToken.equals(this.mSideStage.mRootTaskInfo.getToken())) {
            return getSideStagePosition();
        }
        return -1;
    }

    public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo) {
        this.mDisplayAreaInfo = displayAreaInfo;
        if (this.mSplitLayout == null) {
            this.mSplitLayout = new SplitLayout(TAG + "SplitDivider", this.mContext, this.mDisplayAreaInfo.configuration, this, new StageCoordinator$$ExternalSyntheticLambda3(this), this.mDisplayImeController, this.mTaskOrganizer);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDisplayAreaAppeared$4(SurfaceControl.Builder builder) {
        this.mRootTDAOrganizer.attachToDisplayArea(this.mDisplayId, builder);
    }

    public void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) {
        throw new IllegalStateException("Well that was unexpected...");
    }

    public void onDisplayAreaInfoChanged(DisplayAreaInfo displayAreaInfo) {
        this.mDisplayAreaInfo = displayAreaInfo;
        SplitLayout splitLayout = this.mSplitLayout;
        if (splitLayout != null && splitLayout.updateConfiguration(displayAreaInfo.configuration)) {
            onBoundsChanged(this.mSplitLayout);
        }
    }

    private Rect getSideStageBounds() {
        return this.mSideStagePosition == 0 ? this.mSplitLayout.getBounds1() : this.mSplitLayout.getBounds2();
    }

    private Rect getMainStageBounds() {
        return this.mSideStagePosition == 0 ? this.mSplitLayout.getBounds2() : this.mSplitLayout.getBounds1();
    }

    private StageTaskListener getStageOfTask(ActivityManager.RunningTaskInfo runningTaskInfo) {
        MainStage mainStage = this.mMainStage;
        ActivityManager.RunningTaskInfo runningTaskInfo2 = mainStage.mRootTaskInfo;
        if (runningTaskInfo2 != null && runningTaskInfo.parentTaskId == runningTaskInfo2.taskId) {
            return mainStage;
        }
        SideStage sideStage = this.mSideStage;
        ActivityManager.RunningTaskInfo runningTaskInfo3 = sideStage.mRootTaskInfo;
        if (runningTaskInfo3 == null || runningTaskInfo.parentTaskId != runningTaskInfo3.taskId) {
            return null;
        }
        return sideStage;
    }

    private int getStageType(StageTaskListener stageTaskListener) {
        return stageTaskListener == this.mMainStage ? 0 : 1;
    }

    public WindowContainerTransaction handleRequest(IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        ActivityManager.RunningTaskInfo triggerTask = transitionRequestInfo.getTriggerTask();
        WindowContainerTransaction windowContainerTransaction = null;
        if (triggerTask != null) {
            int type = transitionRequestInfo.getType();
            if (isSplitScreenVisible()) {
                if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
                    long j = (long) triggerTask.taskId;
                    ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, 165317020, 81, "  split is active so using splitTransition to handle request. triggerTask=%d type=%s mainChildren=%d sideChildren=%d", Long.valueOf(j), String.valueOf(WindowManager.transitTypeToString(type)), Long.valueOf((long) this.mMainStage.getChildCount()), Long.valueOf((long) this.mSideStage.getChildCount()));
                }
                windowContainerTransaction = new WindowContainerTransaction();
                StageTaskListener stageOfTask = getStageOfTask(triggerTask);
                if (stageOfTask != null) {
                    if (Transitions.isClosingType(type) && stageOfTask.getChildCount() == 1) {
                        this.mDismissTop = getStageType(stageOfTask) == 0 ? 1 : 0;
                    }
                } else if (triggerTask.getActivityType() == 2 && Transitions.isOpeningType(type)) {
                    this.mDismissTop = -1;
                }
                int i = this.mDismissTop;
                if (i != -2) {
                    if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
                        ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, 1373022370, 0, "  splitTransition  deduced Dismiss from request. toTop=%s", String.valueOf(SplitScreen.stageTypeToString(i)));
                    }
                    prepareExitSplitScreen(this.mDismissTop, windowContainerTransaction);
                    this.mSplitTransitions.mPendingDismiss = iBinder;
                }
            } else if ((type == 1 || type == 3) && getStageOfTask(triggerTask) != null) {
                throw new IllegalStateException("Entering split implicitly with only one task isn't supported.");
            }
            return windowContainerTransaction;
        } else if (isSplitScreenVisible()) {
            return new WindowContainerTransaction();
        } else {
            return null;
        }
    }

    public boolean startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Transitions.TransitionFinishCallback transitionFinishCallback) {
        boolean z;
        StageTaskListener stageOfTask;
        SplitScreenTransitions splitScreenTransitions = this.mSplitTransitions;
        IBinder iBinder2 = splitScreenTransitions.mPendingDismiss;
        if (iBinder == iBinder2 || iBinder == splitScreenTransitions.mPendingEnter) {
            if (splitScreenTransitions.mPendingEnter == iBinder) {
                z = startPendingEnterAnimation(iBinder, transitionInfo, transaction);
            } else {
                z = iBinder2 == iBinder ? startPendingDismissAnimation(iBinder, transitionInfo, transaction) : true;
            }
            if (!z) {
                return false;
            }
            this.mSplitTransitions.playAnimation(iBinder, transitionInfo, transaction, transitionFinishCallback, this.mMainStage.mRootTaskInfo.token, this.mSideStage.mRootTaskInfo.token);
            return true;
        } else if (!isSplitScreenVisible()) {
            return false;
        } else {
            for (int i = 0; i < transitionInfo.getChanges().size(); i++) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(i);
                ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                if (!(taskInfo == null || !taskInfo.hasParentTask() || (stageOfTask = getStageOfTask(taskInfo)) == null)) {
                    if (Transitions.isOpeningType(change.getMode())) {
                        if (!stageOfTask.containsTask(taskInfo.taskId)) {
                            Log.w(TAG, "Expected onTaskAppeared on " + stageOfTask + " to have been called with " + taskInfo.taskId + " before startAnimation().");
                        }
                    } else if (Transitions.isClosingType(change.getMode()) && stageOfTask.containsTask(taskInfo.taskId)) {
                        Log.w(TAG, "Expected onTaskVanished on " + stageOfTask + " to have been called with " + taskInfo.taskId + " before startAnimation().");
                    }
                }
            }
            if (this.mMainStage.getChildCount() != 0 && this.mSideStage.getChildCount() != 0) {
                return false;
            }
            throw new IllegalStateException("Somehow removed the last task in a stage outside of a proper transition");
        }
    }

    private boolean startPendingEnterAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
        if (transitionInfo.getType() == 12) {
            TransitionInfo.Change change = null;
            TransitionInfo.Change change2 = null;
            for (int i = 0; i < transitionInfo.getChanges().size(); i++) {
                TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(i);
                ActivityManager.RunningTaskInfo taskInfo = change3.getTaskInfo();
                if (taskInfo != null && taskInfo.hasParentTask()) {
                    int stageType = getStageType(getStageOfTask(taskInfo));
                    if (stageType == 0) {
                        change = change3;
                    } else if (stageType == 1) {
                        change2 = change3;
                    }
                }
            }
            if (change == null || change2 == null) {
                throw new IllegalStateException("Launched 2 tasks in split, but didn't receive 2 tasks in transition. Possibly one of them failed to launch");
            }
            setDividerVisibility(true);
            setSideStagePosition(1, false);
            setSplitsVisible(true);
            addDividerBarToTransition(transitionInfo, transaction, true);
            if (!this.mMainStage.containsTask(change.getTaskInfo().taskId)) {
                Log.w(TAG, "Expected onTaskAppeared on " + this.mMainStage + " to have been called with " + change.getTaskInfo().taskId + " before startAnimation().");
            }
            if (!this.mSideStage.containsTask(change2.getTaskInfo().taskId)) {
                Log.w(TAG, "Expected onTaskAppeared on " + this.mSideStage + " to have been called with " + change2.getTaskInfo().taskId + " before startAnimation().");
            }
            return true;
        }
        throw new RuntimeException("Unsupported split-entry");
    }

    private boolean startPendingDismissAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
        if (this.mMainStage.getChildCount() != 0) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.mMainStage.getChildCount()) {
                sb.append(i != 0 ? ", " : "");
                sb.append(this.mMainStage.mChildrenTaskInfo.keyAt(i));
                i++;
            }
            Log.w(TAG, "Expected onTaskVanished on " + this.mMainStage + " to have been called with [" + sb.toString() + "] before startAnimation().");
        }
        if (this.mSideStage.getChildCount() != 0) {
            StringBuilder sb2 = new StringBuilder();
            int i2 = 0;
            while (i2 < this.mSideStage.getChildCount()) {
                sb2.append(i2 != 0 ? ", " : "");
                sb2.append(this.mSideStage.mChildrenTaskInfo.keyAt(i2));
                i2++;
            }
            Log.w(TAG, "Expected onTaskVanished on " + this.mSideStage + " to have been called with [" + sb2.toString() + "] before startAnimation().");
        }
        setSplitsVisible(false);
        if (transitionInfo.getType() == 11) {
            transaction.setWindowCrop(this.mMainStage.mRootLeash, (Rect) null);
            transaction.setWindowCrop(this.mSideStage.mRootLeash, (Rect) null);
        }
        if (this.mDismissTop == -1) {
            transaction.hide(this.mSplitLayout.getDividerLeash());
            setDividerVisibility(false);
            this.mSplitTransitions.mPendingDismiss = null;
            return false;
        }
        addDividerBarToTransition(transitionInfo, transaction, false);
        return true;
    }

    private void addDividerBarToTransition(TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, boolean z) {
        SurfaceControl dividerLeash = this.mSplitLayout.getDividerLeash();
        TransitionInfo.Change change = new TransitionInfo.Change((WindowContainerToken) null, dividerLeash);
        Rect dividerBounds = this.mSplitLayout.getDividerBounds();
        change.setStartAbsBounds(dividerBounds);
        change.setEndAbsBounds(dividerBounds);
        change.setMode(z ? 3 : 4);
        change.setFlags(32);
        transitionInfo.addChange(change);
        if (z) {
            transaction.setAlpha(dividerLeash, 1.0f);
            transaction.setLayer(dividerLeash, Integer.MAX_VALUE);
            transaction.setPosition(dividerLeash, (float) dividerBounds.left, (float) dividerBounds.top);
            transaction.show(dividerLeash);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        String str3 = str2 + "  ";
        printWriter.println(str + TAG + " mDisplayId=" + this.mDisplayId);
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("mDividerVisible=");
        sb.append(this.mDividerVisible);
        printWriter.println(sb.toString());
        printWriter.println(str2 + "MainStage");
        printWriter.println(str3 + "isActive=" + this.mMainStage.isActive());
        this.mMainStageListener.dump(printWriter, str3);
        printWriter.println(str2 + "SideStage");
        this.mSideStageListener.dump(printWriter, str3);
        printWriter.println(str2 + "mSplitLayout=" + this.mSplitLayout);
    }

    private void setSplitsVisible(boolean z) {
        StageListenerImpl stageListenerImpl = this.mMainStageListener;
        StageListenerImpl stageListenerImpl2 = this.mSideStageListener;
        stageListenerImpl2.mVisible = z;
        stageListenerImpl.mVisible = z;
        stageListenerImpl2.mHasChildren = z;
        stageListenerImpl.mHasChildren = z;
    }

    /* renamed from: com.android.wm.shell.splitscreen.StageCoordinator$StageListenerImpl */
    class StageListenerImpl implements StageTaskListener.StageListenerCallbacks {
        boolean mHasChildren = false;
        boolean mHasRootTask = false;
        boolean mVisible = false;

        StageListenerImpl() {
        }

        public void onRootTaskAppeared() {
            this.mHasRootTask = true;
            StageCoordinator.this.onStageRootTaskAppeared(this);
        }

        public void onStatusChanged(boolean z, boolean z2) {
            if (this.mHasRootTask) {
                if (this.mHasChildren != z2) {
                    this.mHasChildren = z2;
                    StageCoordinator.this.onStageHasChildrenChanged(this);
                }
                if (this.mVisible != z) {
                    this.mVisible = z;
                    StageCoordinator.this.onStageVisibilityChanged(this);
                }
            }
        }

        public void onChildTaskStatusChanged(int i, boolean z, boolean z2) {
            StageCoordinator.this.onStageChildTaskStatusChanged(this, i, z, z2);
        }

        public void onRootTaskVanished() {
            reset();
            StageCoordinator.this.onStageRootTaskVanished(this);
        }

        public void onNoLongerSupportMultiWindow() {
            if (StageCoordinator.this.mMainStage.isActive()) {
                StageCoordinator.this.exitSplitScreen();
            }
        }

        private void reset() {
            this.mHasRootTask = false;
            this.mVisible = false;
            this.mHasChildren = false;
        }

        public void dump(PrintWriter printWriter, String str) {
            printWriter.println(str + "mHasRootTask=" + this.mHasRootTask);
            printWriter.println(str + "mVisible=" + this.mVisible);
            printWriter.println(str + "mHasChildren=" + this.mHasChildren);
        }
    }
}
