package com.android.p011wm.shell.apppairs;

import android.app.ActivityManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.SurfaceUtils;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.split.SplitLayout;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import java.io.PrintWriter;

/* renamed from: com.android.wm.shell.apppairs.AppPair */
class AppPair implements ShellTaskOrganizer.TaskListener, SplitLayout.SplitLayoutHandler {
    private static final String TAG = "AppPair";
    private final AppPairsController mController;
    private SurfaceControl mDimLayer1;
    private SurfaceControl mDimLayer2;
    private final DisplayController mDisplayController;
    private final DisplayImeController mDisplayImeController;
    private ActivityManager.RunningTaskInfo mRootTaskInfo;
    private SurfaceControl mRootTaskLeash;
    private SplitLayout mSplitLayout;
    private final SurfaceSession mSurfaceSession = new SurfaceSession();
    private final SyncTransactionQueue mSyncQueue;
    private ActivityManager.RunningTaskInfo mTaskInfo1;
    private ActivityManager.RunningTaskInfo mTaskInfo2;
    private SurfaceControl mTaskLeash1;
    private SurfaceControl mTaskLeash2;

    AppPair(AppPairsController appPairsController) {
        this.mController = appPairsController;
        this.mSyncQueue = appPairsController.getSyncTransactionQueue();
        this.mDisplayController = appPairsController.getDisplayController();
        this.mDisplayImeController = appPairsController.getDisplayImeController();
    }

    /* access modifiers changed from: package-private */
    public int getRootTaskId() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mRootTaskInfo;
        if (runningTaskInfo != null) {
            return runningTaskInfo.taskId;
        }
        return -1;
    }

    private int getTaskId1() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mTaskInfo1;
        if (runningTaskInfo != null) {
            return runningTaskInfo.taskId;
        }
        return -1;
    }

    private int getTaskId2() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mTaskInfo2;
        if (runningTaskInfo != null) {
            return runningTaskInfo.taskId;
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public boolean contains(int i) {
        return i == getRootTaskId() || i == getTaskId1() || i == getTaskId2();
    }

    /* access modifiers changed from: package-private */
    public boolean pair(ActivityManager.RunningTaskInfo runningTaskInfo, ActivityManager.RunningTaskInfo runningTaskInfo2) {
        ActivityManager.RunningTaskInfo runningTaskInfo3 = runningTaskInfo;
        ActivityManager.RunningTaskInfo runningTaskInfo4 = runningTaskInfo2;
        if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
            long j = (long) runningTaskInfo3.taskId;
            long j2 = (long) runningTaskInfo4.taskId;
            String valueOf = String.valueOf(this);
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, -742394458, 5, (String) null, Long.valueOf(j), Long.valueOf(j2), valueOf);
        }
        boolean z = runningTaskInfo3.supportsMultiWindow;
        if (!z || !runningTaskInfo4.supportsMultiWindow) {
            if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
                boolean z2 = runningTaskInfo4.supportsMultiWindow;
                ShellProtoLogImpl.m92e(ShellProtoLogGroup.WM_SHELL_TASK_ORG, -553798917, 15, (String) null, Boolean.valueOf(z), Boolean.valueOf(z2));
            }
            return false;
        }
        this.mTaskInfo1 = runningTaskInfo3;
        this.mTaskInfo2 = runningTaskInfo4;
        this.mSplitLayout = new SplitLayout(TAG + "SplitDivider", this.mDisplayController.getDisplayContext(this.mRootTaskInfo.displayId), this.mRootTaskInfo.configuration, this, new AppPair$$ExternalSyntheticLambda8(this), this.mDisplayImeController, this.mController.getTaskOrganizer());
        WindowContainerToken windowContainerToken = runningTaskInfo3.token;
        WindowContainerToken windowContainerToken2 = runningTaskInfo4.token;
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        windowContainerTransaction.setHidden(this.mRootTaskInfo.token, false).reparent(windowContainerToken, this.mRootTaskInfo.token, true).reparent(windowContainerToken2, this.mRootTaskInfo.token, true).setWindowingMode(windowContainerToken, 6).setWindowingMode(windowContainerToken2, 6).setBounds(windowContainerToken, this.mSplitLayout.getBounds1()).setBounds(windowContainerToken2, this.mSplitLayout.getBounds2()).reorder(this.mRootTaskInfo.token, true);
        this.mController.getTaskOrganizer().applyTransaction(windowContainerTransaction);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$pair$0(SurfaceControl.Builder builder) {
        builder.setParent(this.mRootTaskLeash);
    }

    /* access modifiers changed from: package-private */
    public void unpair() {
        unpair((WindowContainerToken) null);
    }

    private void unpair(WindowContainerToken windowContainerToken) {
        WindowContainerToken windowContainerToken2 = this.mTaskInfo1.token;
        WindowContainerToken windowContainerToken3 = this.mTaskInfo2.token;
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        boolean z = true;
        WindowContainerTransaction reparent = windowContainerTransaction.setHidden(this.mRootTaskInfo.token, true).reorder(this.mRootTaskInfo.token, false).reparent(windowContainerToken2, (WindowContainerToken) null, windowContainerToken2 == windowContainerToken);
        if (windowContainerToken3 != windowContainerToken) {
            z = false;
        }
        reparent.reparent(windowContainerToken3, (WindowContainerToken) null, z).setWindowingMode(windowContainerToken2, 0).setWindowingMode(windowContainerToken3, 0);
        this.mController.getTaskOrganizer().applyTransaction(windowContainerTransaction);
        this.mTaskInfo1 = null;
        this.mTaskInfo2 = null;
        this.mSplitLayout.release();
        this.mSplitLayout = null;
    }

    public void onTaskAppeared(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl) {
        int i;
        ActivityManager.RunningTaskInfo runningTaskInfo2 = this.mRootTaskInfo;
        if (runningTaskInfo2 == null || (i = runningTaskInfo.taskId) == runningTaskInfo2.taskId) {
            this.mRootTaskInfo = runningTaskInfo;
            this.mRootTaskLeash = surfaceControl;
        } else if (i == getTaskId1()) {
            this.mTaskInfo1 = runningTaskInfo;
            this.mTaskLeash1 = surfaceControl;
            this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda3(this));
        } else if (runningTaskInfo.taskId == getTaskId2()) {
            this.mTaskInfo2 = runningTaskInfo;
            this.mTaskLeash2 = surfaceControl;
            this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda0(this));
        } else {
            throw new IllegalStateException("Unknown task=" + runningTaskInfo.taskId);
        }
        if (this.mTaskLeash1 != null && this.mTaskLeash2 != null) {
            this.mSplitLayout.init();
            this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda5(this, this.mSplitLayout.getDividerLeash(), this.mSplitLayout.getDividerBounds()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskAppeared$1(SurfaceControl.Transaction transaction) {
        this.mDimLayer1 = SurfaceUtils.makeDimLayer(transaction, this.mTaskLeash1, "Dim layer", this.mSurfaceSession);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskAppeared$2(SurfaceControl.Transaction transaction) {
        this.mDimLayer2 = SurfaceUtils.makeDimLayer(transaction, this.mTaskLeash2, "Dim layer", this.mSurfaceSession);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskAppeared$3(SurfaceControl surfaceControl, Rect rect, SurfaceControl.Transaction transaction) {
        SurfaceControl.Transaction layer = transaction.setLayer(surfaceControl, Integer.MAX_VALUE);
        SurfaceControl surfaceControl2 = this.mTaskLeash1;
        Point point = this.mTaskInfo1.positionInParent;
        SurfaceControl.Transaction position = layer.setPosition(surfaceControl2, (float) point.x, (float) point.y);
        SurfaceControl surfaceControl3 = this.mTaskLeash2;
        Point point2 = this.mTaskInfo2.positionInParent;
        position.setPosition(surfaceControl3, (float) point2.x, (float) point2.y).setPosition(surfaceControl, (float) rect.left, (float) rect.top).show(this.mRootTaskLeash).show(this.mTaskLeash1).show(this.mTaskLeash2);
    }

    public void onTaskInfoChanged(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!runningTaskInfo.supportsMultiWindow) {
            this.mController.unpair(this.mRootTaskInfo.taskId);
        } else if (runningTaskInfo.taskId == getRootTaskId()) {
            if (this.mRootTaskInfo.isVisible != runningTaskInfo.isVisible) {
                this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda4(this, runningTaskInfo));
            }
            this.mRootTaskInfo = runningTaskInfo;
            SplitLayout splitLayout = this.mSplitLayout;
            if (splitLayout != null && splitLayout.updateConfiguration(runningTaskInfo.configuration)) {
                onBoundsChanged(this.mSplitLayout);
            }
        } else if (runningTaskInfo.taskId == getTaskId1()) {
            this.mTaskInfo1 = runningTaskInfo;
        } else if (runningTaskInfo.taskId == getTaskId2()) {
            this.mTaskInfo2 = runningTaskInfo;
        } else {
            throw new IllegalStateException("Unknown task=" + runningTaskInfo.taskId);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskInfoChanged$4(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl.Transaction transaction) {
        if (runningTaskInfo.isVisible) {
            transaction.show(this.mRootTaskLeash);
        } else {
            transaction.hide(this.mRootTaskLeash);
        }
    }

    public int getSplitItemPosition(WindowContainerToken windowContainerToken) {
        if (windowContainerToken == null) {
            return -1;
        }
        if (windowContainerToken.equals(this.mTaskInfo1.getToken())) {
            return 0;
        }
        if (windowContainerToken.equals(this.mTaskInfo2.getToken())) {
            return 1;
        }
        return -1;
    }

    public void onTaskVanished(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (runningTaskInfo.taskId == getRootTaskId()) {
            this.mController.unpair(this.mRootTaskInfo.taskId, false);
        } else if (runningTaskInfo.taskId == getTaskId1()) {
            this.mController.unpair(this.mRootTaskInfo.taskId);
            this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda2(this));
        } else if (runningTaskInfo.taskId == getTaskId2()) {
            this.mController.unpair(this.mRootTaskInfo.taskId);
            this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskVanished$5(SurfaceControl.Transaction transaction) {
        transaction.remove(this.mDimLayer1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskVanished$6(SurfaceControl.Transaction transaction) {
        transaction.remove(this.mDimLayer2);
    }

    public void attachChildSurfaceToTask(int i, SurfaceControl.Builder builder) {
        if (getRootTaskId() == i) {
            builder.setParent(this.mRootTaskLeash);
        } else if (getTaskId1() == i) {
            builder.setParent(this.mTaskLeash1);
        } else if (getTaskId2() == i) {
            builder.setParent(this.mTaskLeash2);
        } else {
            throw new IllegalArgumentException("There is no surface for taskId=" + i);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.println(str + this);
        if (this.mRootTaskInfo != null) {
            printWriter.println(str2 + "Root taskId=" + getRootTaskId() + " winMode=" + this.mRootTaskInfo.getWindowingMode());
        } else {
            printWriter.println(str2 + "Root taskId=" + getRootTaskId() + " mRootTaskInfo=null");
        }
        if (this.mTaskInfo1 != null) {
            printWriter.println(str2 + "1 taskId=" + this.mTaskInfo1.taskId + " winMode=" + this.mTaskInfo1.getWindowingMode());
        }
        if (this.mTaskInfo2 != null) {
            printWriter.println(str2 + "2 taskId=" + this.mTaskInfo2.taskId + " winMode=" + this.mTaskInfo2.getWindowingMode());
        }
    }

    public String toString() {
        return TAG + "#" + getRootTaskId();
    }

    public void onSnappedToDismiss(boolean z) {
        unpair((z ? this.mTaskInfo1 : this.mTaskInfo2).token);
    }

    public void onBoundsChanging(SplitLayout splitLayout) {
        this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda7(this, splitLayout));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBoundsChanging$7(SplitLayout splitLayout, SurfaceControl.Transaction transaction) {
        splitLayout.applySurfaceChanges(transaction, this.mTaskLeash1, this.mTaskLeash2, this.mDimLayer1, this.mDimLayer2);
    }

    public void onBoundsChanged(SplitLayout splitLayout) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        splitLayout.applyTaskChanges(windowContainerTransaction, this.mTaskInfo1, this.mTaskInfo2);
        this.mSyncQueue.queue(windowContainerTransaction);
        this.mSyncQueue.runInSync(new AppPair$$ExternalSyntheticLambda6(this, splitLayout));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBoundsChanged$8(SplitLayout splitLayout, SurfaceControl.Transaction transaction) {
        splitLayout.applySurfaceChanges(transaction, this.mTaskLeash1, this.mTaskLeash2, this.mDimLayer1, this.mDimLayer2);
    }
}
