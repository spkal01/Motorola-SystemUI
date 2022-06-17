package com.android.p011wm.shell.splitscreen;

import android.app.ActivityManager;
import android.graphics.Rect;
import android.view.SurfaceSession;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.splitscreen.StageTaskListener;

/* renamed from: com.android.wm.shell.splitscreen.SideStage */
class SideStage extends StageTaskListener {
    SideStage(ShellTaskOrganizer shellTaskOrganizer, int i, StageTaskListener.StageListenerCallbacks stageListenerCallbacks, SyncTransactionQueue syncTransactionQueue, SurfaceSession surfaceSession) {
        super(shellTaskOrganizer, i, stageListenerCallbacks, syncTransactionQueue, surfaceSession);
    }

    /* access modifiers changed from: package-private */
    public void addTask(ActivityManager.RunningTaskInfo runningTaskInfo, Rect rect, WindowContainerTransaction windowContainerTransaction) {
        WindowContainerToken windowContainerToken = this.mRootTaskInfo.token;
        windowContainerTransaction.setBounds(windowContainerToken, rect).reparent(runningTaskInfo.token, windowContainerToken, true).reorder(windowContainerToken, true);
    }

    /* access modifiers changed from: package-private */
    public boolean removeAllTasks(WindowContainerTransaction windowContainerTransaction, boolean z) {
        windowContainerTransaction.reorder(this.mRootTaskInfo.token, false);
        if (this.mChildrenTaskInfo.size() == 0) {
            return false;
        }
        windowContainerTransaction.reparentTasks(this.mRootTaskInfo.token, (WindowContainerToken) null, StageTaskListener.CONTROLLED_WINDOWING_MODES_WHEN_ACTIVE, StageTaskListener.CONTROLLED_ACTIVITY_TYPES, z);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean removeTask(int i, WindowContainerToken windowContainerToken, WindowContainerTransaction windowContainerTransaction) {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mChildrenTaskInfo.get(i);
        if (runningTaskInfo == null) {
            return false;
        }
        windowContainerTransaction.reparent(runningTaskInfo.token, windowContainerToken, false);
        return true;
    }
}
