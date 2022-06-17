package com.android.p011wm.shell.legacysplitscreen;

import android.app.ActivityManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.window.TaskOrganizer;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.transition.Transitions;
import java.io.PrintWriter;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTaskListener */
class LegacySplitScreenTaskListener implements ShellTaskOrganizer.TaskListener {
    private static final String TAG = "LegacySplitScreenTaskListener";
    Rect mHomeBounds = new Rect();
    private final SparseArray<SurfaceControl> mLeashByTaskId = new SparseArray<>();
    private final SparseArray<Point> mPositionByTaskId = new SparseArray<>();
    ActivityManager.RunningTaskInfo mPrimary;
    SurfaceControl mPrimaryDim;
    SurfaceControl mPrimarySurface;
    ActivityManager.RunningTaskInfo mSecondary;
    SurfaceControl mSecondaryDim;
    SurfaceControl mSecondarySurface;
    final LegacySplitScreenController mSplitScreenController;
    private boolean mSplitScreenSupported = false;
    private final LegacySplitScreenTransitions mSplitTransitions;
    final SurfaceSession mSurfaceSession = new SurfaceSession();
    private final SyncTransactionQueue mSyncQueue;
    private final ShellTaskOrganizer mTaskOrganizer;

    LegacySplitScreenTaskListener(LegacySplitScreenController legacySplitScreenController, ShellTaskOrganizer shellTaskOrganizer, Transitions transitions, SyncTransactionQueue syncTransactionQueue) {
        this.mSplitScreenController = legacySplitScreenController;
        this.mTaskOrganizer = shellTaskOrganizer;
        LegacySplitScreenTransitions legacySplitScreenTransitions = new LegacySplitScreenTransitions(legacySplitScreenController.mTransactionPool, transitions, legacySplitScreenController, this);
        this.mSplitTransitions = legacySplitScreenTransitions;
        transitions.addHandler(legacySplitScreenTransitions);
        this.mSyncQueue = syncTransactionQueue;
    }

    /* access modifiers changed from: package-private */
    public void init() {
        synchronized (this) {
            try {
                this.mTaskOrganizer.createRootTask(0, 3, this);
                this.mTaskOrganizer.createRootTask(0, 4, this);
            } catch (Exception e) {
                this.mTaskOrganizer.removeListener(this);
                throw e;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSplitScreenSupported() {
        return this.mSplitScreenSupported;
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl.Transaction getTransaction() {
        return this.mSplitScreenController.mTransactionPool.acquire();
    }

    /* access modifiers changed from: package-private */
    public void releaseTransaction(SurfaceControl.Transaction transaction) {
        this.mSplitScreenController.mTransactionPool.release(transaction);
    }

    /* access modifiers changed from: package-private */
    public TaskOrganizer getTaskOrganizer() {
        return this.mTaskOrganizer;
    }

    /* access modifiers changed from: package-private */
    public LegacySplitScreenTransitions getSplitTransitions() {
        return this.mSplitTransitions;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00d6, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTaskAppeared(android.app.ActivityManager.RunningTaskInfo r12, android.view.SurfaceControl r13) {
        /*
            r11 = this;
            monitor-enter(r11)
            boolean r0 = r12.hasParentTask()     // Catch:{ all -> 0x00d7 }
            if (r0 == 0) goto L_0x000c
            r11.handleChildTaskAppeared(r12, r13)     // Catch:{ all -> 0x00d7 }
            monitor-exit(r11)     // Catch:{ all -> 0x00d7 }
            return
        L_0x000c:
            int r0 = r12.getWindowingMode()     // Catch:{ all -> 0x00d7 }
            r1 = 3
            r2 = 2
            r3 = 4
            r4 = 0
            r5 = 1
            r6 = 0
            if (r0 != r1) goto L_0x003c
            boolean r0 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled     // Catch:{ all -> 0x00d7 }
            if (r0 == 0) goto L_0x0037
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00d7 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x00d7 }
            int r1 = r12.taskId     // Catch:{ all -> 0x00d7 }
            long r7 = (long) r1     // Catch:{ all -> 0x00d7 }
            com.android.wm.shell.protolog.ShellProtoLogGroup r1 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_TASK_ORG     // Catch:{ all -> 0x00d7 }
            r9 = -1362429294(0xffffffffaecafa92, float:-9.230407E-11)
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x00d7 }
            r2[r6] = r0     // Catch:{ all -> 0x00d7 }
            java.lang.Long r0 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x00d7 }
            r2[r5] = r0     // Catch:{ all -> 0x00d7 }
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r1, r9, r3, r4, r2)     // Catch:{ all -> 0x00d7 }
        L_0x0037:
            r11.mPrimary = r12     // Catch:{ all -> 0x00d7 }
            r11.mPrimarySurface = r13     // Catch:{ all -> 0x00d7 }
            goto L_0x008a
        L_0x003c:
            if (r0 != r3) goto L_0x0062
            boolean r0 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled     // Catch:{ all -> 0x00d7 }
            if (r0 == 0) goto L_0x005d
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00d7 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x00d7 }
            int r1 = r12.taskId     // Catch:{ all -> 0x00d7 }
            long r7 = (long) r1     // Catch:{ all -> 0x00d7 }
            com.android.wm.shell.protolog.ShellProtoLogGroup r1 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_TASK_ORG     // Catch:{ all -> 0x00d7 }
            r9 = 982027396(0x3a888c84, float:0.0010417853)
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x00d7 }
            r2[r6] = r0     // Catch:{ all -> 0x00d7 }
            java.lang.Long r0 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x00d7 }
            r2[r5] = r0     // Catch:{ all -> 0x00d7 }
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r1, r9, r3, r4, r2)     // Catch:{ all -> 0x00d7 }
        L_0x005d:
            r11.mSecondary = r12     // Catch:{ all -> 0x00d7 }
            r11.mSecondarySurface = r13     // Catch:{ all -> 0x00d7 }
            goto L_0x008a
        L_0x0062:
            boolean r13 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled     // Catch:{ all -> 0x00d7 }
            if (r13 == 0) goto L_0x008a
            java.lang.String r13 = TAG     // Catch:{ all -> 0x00d7 }
            java.lang.String r13 = java.lang.String.valueOf(r13)     // Catch:{ all -> 0x00d7 }
            int r12 = r12.taskId     // Catch:{ all -> 0x00d7 }
            long r7 = (long) r12     // Catch:{ all -> 0x00d7 }
            long r9 = (long) r0     // Catch:{ all -> 0x00d7 }
            com.android.wm.shell.protolog.ShellProtoLogGroup r12 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_TASK_ORG     // Catch:{ all -> 0x00d7 }
            r0 = -298656957(0xffffffffee32db43, float:-1.3838351E28)
            r3 = 20
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x00d7 }
            r1[r6] = r13     // Catch:{ all -> 0x00d7 }
            java.lang.Long r13 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x00d7 }
            r1[r5] = r13     // Catch:{ all -> 0x00d7 }
            java.lang.Long r13 = java.lang.Long.valueOf(r9)     // Catch:{ all -> 0x00d7 }
            r1[r2] = r13     // Catch:{ all -> 0x00d7 }
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r12, r0, r3, r4, r1)     // Catch:{ all -> 0x00d7 }
        L_0x008a:
            boolean r12 = r11.mSplitScreenSupported     // Catch:{ all -> 0x00d7 }
            if (r12 != 0) goto L_0x00d5
            android.view.SurfaceControl r12 = r11.mPrimarySurface     // Catch:{ all -> 0x00d7 }
            if (r12 == 0) goto L_0x00d5
            android.view.SurfaceControl r12 = r11.mSecondarySurface     // Catch:{ all -> 0x00d7 }
            if (r12 == 0) goto L_0x00d5
            r11.mSplitScreenSupported = r5     // Catch:{ all -> 0x00d7 }
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenController r12 = r11.mSplitScreenController     // Catch:{ all -> 0x00d7 }
            r12.onSplitScreenSupported()     // Catch:{ all -> 0x00d7 }
            boolean r12 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled     // Catch:{ all -> 0x00d7 }
            if (r12 == 0) goto L_0x00b3
            java.lang.String r12 = TAG     // Catch:{ all -> 0x00d7 }
            java.lang.String r12 = java.lang.String.valueOf(r12)     // Catch:{ all -> 0x00d7 }
            com.android.wm.shell.protolog.ShellProtoLogGroup r13 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_TASK_ORG     // Catch:{ all -> 0x00d7 }
            r0 = 473543554(0x1c39b382, float:6.1443374E-22)
            java.lang.Object[] r1 = new java.lang.Object[r5]     // Catch:{ all -> 0x00d7 }
            r1[r6] = r12     // Catch:{ all -> 0x00d7 }
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r13, r0, r6, r4, r1)     // Catch:{ all -> 0x00d7 }
        L_0x00b3:
            android.view.SurfaceControl$Transaction r12 = r11.getTransaction()     // Catch:{ all -> 0x00d7 }
            android.view.SurfaceControl r13 = r11.mPrimarySurface     // Catch:{ all -> 0x00d7 }
            java.lang.String r0 = "Primary Divider Dim"
            android.view.SurfaceSession r1 = r11.mSurfaceSession     // Catch:{ all -> 0x00d7 }
            android.view.SurfaceControl r13 = com.android.p011wm.shell.common.SurfaceUtils.makeDimLayer(r12, r13, r0, r1)     // Catch:{ all -> 0x00d7 }
            r11.mPrimaryDim = r13     // Catch:{ all -> 0x00d7 }
            android.view.SurfaceControl r13 = r11.mSecondarySurface     // Catch:{ all -> 0x00d7 }
            java.lang.String r0 = "Secondary Divider Dim"
            android.view.SurfaceSession r1 = r11.mSurfaceSession     // Catch:{ all -> 0x00d7 }
            android.view.SurfaceControl r13 = com.android.p011wm.shell.common.SurfaceUtils.makeDimLayer(r12, r13, r0, r1)     // Catch:{ all -> 0x00d7 }
            r11.mSecondaryDim = r13     // Catch:{ all -> 0x00d7 }
            r12.apply()     // Catch:{ all -> 0x00d7 }
            r11.releaseTransaction(r12)     // Catch:{ all -> 0x00d7 }
        L_0x00d5:
            monitor-exit(r11)     // Catch:{ all -> 0x00d7 }
            return
        L_0x00d7:
            r12 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00d7 }
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenTaskListener.onTaskAppeared(android.app.ActivityManager$RunningTaskInfo, android.view.SurfaceControl):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0068, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTaskVanished(android.app.ActivityManager.RunningTaskInfo r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            android.util.SparseArray<android.graphics.Point> r0 = r4.mPositionByTaskId     // Catch:{ all -> 0x0069 }
            int r1 = r5.taskId     // Catch:{ all -> 0x0069 }
            r0.remove(r1)     // Catch:{ all -> 0x0069 }
            boolean r0 = r5.hasParentTask()     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x0017
            android.util.SparseArray<android.view.SurfaceControl> r0 = r4.mLeashByTaskId     // Catch:{ all -> 0x0069 }
            int r5 = r5.taskId     // Catch:{ all -> 0x0069 }
            r0.remove(r5)     // Catch:{ all -> 0x0069 }
            monitor-exit(r4)     // Catch:{ all -> 0x0069 }
            return
        L_0x0017:
            android.app.ActivityManager$RunningTaskInfo r0 = r4.mPrimary     // Catch:{ all -> 0x0069 }
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0029
            android.window.WindowContainerToken r3 = r5.token     // Catch:{ all -> 0x0069 }
            android.window.WindowContainerToken r0 = r0.token     // Catch:{ all -> 0x0069 }
            boolean r0 = r3.equals(r0)     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x0029
            r0 = r1
            goto L_0x002a
        L_0x0029:
            r0 = r2
        L_0x002a:
            android.app.ActivityManager$RunningTaskInfo r3 = r4.mSecondary     // Catch:{ all -> 0x0069 }
            if (r3 == 0) goto L_0x0039
            android.window.WindowContainerToken r5 = r5.token     // Catch:{ all -> 0x0069 }
            android.window.WindowContainerToken r3 = r3.token     // Catch:{ all -> 0x0069 }
            boolean r5 = r5.equals(r3)     // Catch:{ all -> 0x0069 }
            if (r5 == 0) goto L_0x0039
            goto L_0x003a
        L_0x0039:
            r1 = r2
        L_0x003a:
            boolean r5 = r4.mSplitScreenSupported     // Catch:{ all -> 0x0069 }
            if (r5 == 0) goto L_0x0067
            if (r0 != 0) goto L_0x0042
            if (r1 == 0) goto L_0x0067
        L_0x0042:
            r4.mSplitScreenSupported = r2     // Catch:{ all -> 0x0069 }
            android.view.SurfaceControl$Transaction r5 = r4.getTransaction()     // Catch:{ all -> 0x0069 }
            android.view.SurfaceControl r0 = r4.mPrimaryDim     // Catch:{ all -> 0x0069 }
            r5.remove(r0)     // Catch:{ all -> 0x0069 }
            android.view.SurfaceControl r0 = r4.mSecondaryDim     // Catch:{ all -> 0x0069 }
            r5.remove(r0)     // Catch:{ all -> 0x0069 }
            android.view.SurfaceControl r0 = r4.mPrimarySurface     // Catch:{ all -> 0x0069 }
            r5.remove(r0)     // Catch:{ all -> 0x0069 }
            android.view.SurfaceControl r0 = r4.mSecondarySurface     // Catch:{ all -> 0x0069 }
            r5.remove(r0)     // Catch:{ all -> 0x0069 }
            r5.apply()     // Catch:{ all -> 0x0069 }
            r4.releaseTransaction(r5)     // Catch:{ all -> 0x0069 }
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenController r5 = r4.mSplitScreenController     // Catch:{ all -> 0x0069 }
            r5.onTaskVanished()     // Catch:{ all -> 0x0069 }
        L_0x0067:
            monitor-exit(r4)     // Catch:{ all -> 0x0069 }
            return
        L_0x0069:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0069 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenTaskListener.onTaskVanished(android.app.ActivityManager$RunningTaskInfo):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0034, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTaskInfoChanged(android.app.ActivityManager.RunningTaskInfo r4) {
        /*
            r3 = this;
            int r0 = r4.displayId
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            monitor-enter(r3)
            boolean r0 = r4.supportsMultiWindow     // Catch:{ all -> 0x0064 }
            if (r0 != 0) goto L_0x0035
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenController r0 = r3.mSplitScreenController     // Catch:{ all -> 0x0064 }
            boolean r0 = r0.isDividerVisible()     // Catch:{ all -> 0x0064 }
            if (r0 == 0) goto L_0x0033
            int r0 = r4.taskId     // Catch:{ all -> 0x0064 }
            android.app.ActivityManager$RunningTaskInfo r1 = r3.mPrimary     // Catch:{ all -> 0x0064 }
            int r1 = r1.taskId     // Catch:{ all -> 0x0064 }
            if (r0 == r1) goto L_0x002c
            int r0 = r4.parentTaskId     // Catch:{ all -> 0x0064 }
            if (r0 != r1) goto L_0x001f
            goto L_0x002c
        L_0x001f:
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenController r0 = r3.mSplitScreenController     // Catch:{ all -> 0x0064 }
            boolean r4 = r4.isFocused     // Catch:{ all -> 0x0064 }
            if (r4 != 0) goto L_0x0027
            r4 = 1
            goto L_0x0028
        L_0x0027:
            r4 = 0
        L_0x0028:
            r0.startDismissSplit(r4)     // Catch:{ all -> 0x0064 }
            goto L_0x0033
        L_0x002c:
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenController r0 = r3.mSplitScreenController     // Catch:{ all -> 0x0064 }
            boolean r4 = r4.isFocused     // Catch:{ all -> 0x0064 }
            r0.startDismissSplit(r4)     // Catch:{ all -> 0x0064 }
        L_0x0033:
            monitor-exit(r3)     // Catch:{ all -> 0x0064 }
            return
        L_0x0035:
            boolean r0 = r4.hasParentTask()     // Catch:{ all -> 0x0064 }
            if (r0 == 0) goto L_0x0051
            android.graphics.Point r0 = r4.positionInParent     // Catch:{ all -> 0x0064 }
            android.util.SparseArray<android.graphics.Point> r1 = r3.mPositionByTaskId     // Catch:{ all -> 0x0064 }
            int r2 = r4.taskId     // Catch:{ all -> 0x0064 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0064 }
            boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x0064 }
            if (r0 == 0) goto L_0x004d
            monitor-exit(r3)     // Catch:{ all -> 0x0064 }
            return
        L_0x004d:
            r3.handleChildTaskChanged(r4)     // Catch:{ all -> 0x0064 }
            goto L_0x0054
        L_0x0051:
            r3.handleTaskInfoChanged(r4)     // Catch:{ all -> 0x0064 }
        L_0x0054:
            android.util.SparseArray<android.graphics.Point> r0 = r3.mPositionByTaskId     // Catch:{ all -> 0x0064 }
            int r1 = r4.taskId     // Catch:{ all -> 0x0064 }
            android.graphics.Point r2 = new android.graphics.Point     // Catch:{ all -> 0x0064 }
            android.graphics.Point r4 = r4.positionInParent     // Catch:{ all -> 0x0064 }
            r2.<init>(r4)     // Catch:{ all -> 0x0064 }
            r0.put(r1, r2)     // Catch:{ all -> 0x0064 }
            monitor-exit(r3)     // Catch:{ all -> 0x0064 }
            return
        L_0x0064:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0064 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenTaskListener.onTaskInfoChanged(android.app.ActivityManager$RunningTaskInfo):void");
    }

    private void handleChildTaskAppeared(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl) {
        this.mLeashByTaskId.put(runningTaskInfo.taskId, surfaceControl);
        this.mPositionByTaskId.put(runningTaskInfo.taskId, new Point(runningTaskInfo.positionInParent));
        if (!Transitions.ENABLE_SHELL_TRANSITIONS) {
            updateChildTaskSurface(runningTaskInfo, surfaceControl, true);
        }
    }

    private void handleChildTaskChanged(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!Transitions.ENABLE_SHELL_TRANSITIONS) {
            updateChildTaskSurface(runningTaskInfo, this.mLeashByTaskId.get(runningTaskInfo.taskId), false);
        }
    }

    private void updateChildTaskSurface(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl, boolean z) {
        this.mSyncQueue.runInSync(new LegacySplitScreenTaskListener$$ExternalSyntheticLambda0(surfaceControl, runningTaskInfo.positionInParent, z));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateChildTaskSurface$0(SurfaceControl surfaceControl, Point point, boolean z, SurfaceControl.Transaction transaction) {
        transaction.setWindowCrop(surfaceControl, (Rect) null);
        transaction.setPosition(surfaceControl, (float) point.x, (float) point.y);
        if (z && !Transitions.ENABLE_SHELL_TRANSITIONS) {
            transaction.setAlpha(surfaceControl, 1.0f);
            transaction.setMatrix(surfaceControl, 1.0f, 0.0f, 0.0f, 1.0f);
            transaction.show(surfaceControl);
        }
    }

    private void handleTaskInfoChanged(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!this.mSplitScreenSupported) {
            Log.e(TAG, "Got handleTaskInfoChanged when not initialized: " + runningTaskInfo);
            return;
        }
        int i = this.mSecondary.topActivityType;
        boolean z = true;
        boolean z2 = i == 2 || (i == 3 && this.mSplitScreenController.isHomeStackResizable());
        boolean z3 = this.mPrimary.topActivityType == 0;
        boolean z4 = this.mSecondary.topActivityType == 0;
        if (runningTaskInfo.token.asBinder() == this.mPrimary.token.asBinder()) {
            this.mPrimary = runningTaskInfo;
        } else if (runningTaskInfo.token.asBinder() == this.mSecondary.token.asBinder()) {
            this.mSecondary = runningTaskInfo;
        }
        if (!Transitions.ENABLE_SHELL_TRANSITIONS) {
            boolean z5 = this.mPrimary.topActivityType == 0;
            int i2 = this.mSecondary.topActivityType;
            boolean z6 = i2 == 0;
            if (i2 != 2 && (i2 != 3 || !this.mSplitScreenController.isHomeStackResizable())) {
                z = false;
            }
            if (z5 != z3 || z4 != z6 || z2 != z) {
                if (z5 || z6) {
                    if (this.mSplitScreenController.isDividerVisible()) {
                        this.mSplitScreenController.startDismissSplit(false);
                    } else if (!z5 && z3 && z4) {
                        this.mSplitScreenController.startEnterSplit();
                    }
                } else if (z) {
                    ArrayList arrayList = new ArrayList();
                    this.mSplitScreenController.getWmProxy().getHomeAndRecentsTasks(arrayList, this.mSplitScreenController.getSecondaryRoot());
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        ActivityManager.RunningTaskInfo runningTaskInfo2 = (ActivityManager.RunningTaskInfo) arrayList.get(i3);
                        SurfaceControl surfaceControl = this.mLeashByTaskId.get(runningTaskInfo2.taskId);
                        if (surfaceControl != null) {
                            updateChildTaskSurface(runningTaskInfo2, surfaceControl, false);
                        }
                    }
                    this.mSplitScreenController.ensureMinimizedSplit();
                } else {
                    this.mSplitScreenController.ensureNormalSplit();
                }
            }
        }
    }

    public void attachChildSurfaceToTask(int i, SurfaceControl.Builder builder) {
        if (this.mLeashByTaskId.contains(i)) {
            builder.setParent(this.mLeashByTaskId.get(i));
            return;
        }
        throw new IllegalArgumentException("There is no surface for taskId=" + i);
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.println(str + this);
        printWriter.println(str2 + "mSplitScreenSupported=" + this.mSplitScreenSupported);
        if (this.mPrimary != null) {
            printWriter.println(str2 + "mPrimary.taskId=" + this.mPrimary.taskId);
        }
        if (this.mSecondary != null) {
            printWriter.println(str2 + "mSecondary.taskId=" + this.mSecondary.taskId);
        }
    }

    public String toString() {
        return TAG;
    }
}
