package com.android.p011wm.shell;

import android.app.ActivityManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Slog;
import android.util.SparseArray;
import android.view.SurfaceControl;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.p011wm.shell.transition.Transitions;
import java.io.PrintWriter;

/* renamed from: com.android.wm.shell.FullscreenTaskListener */
public class FullscreenTaskListener implements ShellTaskOrganizer.TaskListener {
    private final SparseArray<TaskData> mDataByTaskId = new SparseArray<>();
    private final SyncTransactionQueue mSyncQueue;

    public FullscreenTaskListener(SyncTransactionQueue syncTransactionQueue) {
        this.mSyncQueue = syncTransactionQueue;
    }

    public void onTaskAppeared(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl) {
        if (this.mDataByTaskId.get(runningTaskInfo.taskId) == null) {
            if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
                ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, -1501874464, 1, (String) null, Long.valueOf((long) runningTaskInfo.taskId));
            }
            Point point = runningTaskInfo.positionInParent;
            this.mDataByTaskId.put(runningTaskInfo.taskId, new TaskData(surfaceControl, point));
            if (!Transitions.ENABLE_SHELL_TRANSITIONS) {
                this.mSyncQueue.runInSync(new FullscreenTaskListener$$ExternalSyntheticLambda0(surfaceControl, point));
                return;
            }
            return;
        }
        throw new IllegalStateException("Task appeared more than once: #" + runningTaskInfo.taskId);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onTaskAppeared$0(SurfaceControl surfaceControl, Point point, SurfaceControl.Transaction transaction) {
        transaction.setWindowCrop(surfaceControl, (Rect) null);
        transaction.setPosition(surfaceControl, (float) point.x, (float) point.y);
        transaction.setAlpha(surfaceControl, 1.0f);
        transaction.setMatrix(surfaceControl, 1.0f, 0.0f, 0.0f, 1.0f);
        transaction.show(surfaceControl);
    }

    public void onTaskInfoChanged(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!Transitions.ENABLE_SHELL_TRANSITIONS) {
            TaskData taskData = this.mDataByTaskId.get(runningTaskInfo.taskId);
            Point point = runningTaskInfo.positionInParent;
            if (!point.equals(taskData.positionInParent)) {
                taskData.positionInParent.set(point.x, point.y);
                this.mSyncQueue.runInSync(new FullscreenTaskListener$$ExternalSyntheticLambda1(taskData, point));
            }
        }
    }

    public void onTaskVanished(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (this.mDataByTaskId.get(runningTaskInfo.taskId) == null) {
            Slog.e("FullscreenTaskListener", "Task already vanished: #" + runningTaskInfo.taskId);
            return;
        }
        this.mDataByTaskId.remove(runningTaskInfo.taskId);
        if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, 564235578, 1, (String) null, Long.valueOf((long) runningTaskInfo.taskId));
        }
    }

    public void attachChildSurfaceToTask(int i, SurfaceControl.Builder builder) {
        if (this.mDataByTaskId.contains(i)) {
            builder.setParent(this.mDataByTaskId.get(i).surface);
            return;
        }
        throw new IllegalArgumentException("There is no surface for taskId=" + i);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + this);
        printWriter.println((str + "  ") + this.mDataByTaskId.size() + " Tasks");
    }

    public String toString() {
        return "FullscreenTaskListener:" + ShellTaskOrganizer.taskListenerTypeToString(-2);
    }

    /* renamed from: com.android.wm.shell.FullscreenTaskListener$TaskData */
    private static class TaskData {
        public final Point positionInParent;
        public final SurfaceControl surface;

        public TaskData(SurfaceControl surfaceControl, Point point) {
            this.surface = surfaceControl;
            this.positionInParent = point;
        }
    }
}
