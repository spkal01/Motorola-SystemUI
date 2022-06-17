package com.motorola.systemui.cli.navgesture.recents;

import android.app.ActivityManager;
import android.util.SparseBooleanArray;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.executors.LooperExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecentTasksList extends TaskStackChangeListener {
    private static final TaskLoadResult INVALID_RESULT = new TaskLoadResult(-1, false, 0);
    private final ActivityManagerWrapper mActivityManagerWrapper;
    private int mChangeId = 1;
    /* access modifiers changed from: private */
    public final KeyguardManagerCompat mKeyguardManager;
    private final LooperExecutor mMainThreadExecutor;
    private TaskLoadResult mResultsBg;
    private TaskLoadResult mResultsUi;

    public RecentTasksList(LooperExecutor looperExecutor, KeyguardManagerCompat keyguardManagerCompat, ActivityManagerWrapper activityManagerWrapper) {
        TaskLoadResult taskLoadResult = INVALID_RESULT;
        this.mResultsBg = taskLoadResult;
        this.mResultsUi = taskLoadResult;
        this.mMainThreadExecutor = looperExecutor;
        this.mKeyguardManager = keyguardManagerCompat;
        this.mActivityManagerWrapper = activityManagerWrapper;
        activityManagerWrapper.registerTaskStackListener(this);
    }

    public void getTaskKeys(int i, Consumer<ArrayList<Task>> consumer) {
        AppExecutors.background().execute(new RecentTasksList$$ExternalSyntheticLambda1(this, i, consumer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTaskKeys$1(int i, Consumer consumer) {
        this.mMainThreadExecutor.execute(new RecentTasksList$$ExternalSyntheticLambda4(consumer, loadTasksInBackground(i, -1, true)));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001e, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getTasks(boolean r4, java.util.function.Consumer<java.util.ArrayList<com.android.systemui.shared.recents.model.Task>> r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            int r0 = r3.mChangeId     // Catch:{ all -> 0x002d }
            com.motorola.systemui.cli.navgesture.recents.RecentTasksList$TaskLoadResult r1 = r3.mResultsUi     // Catch:{ all -> 0x002d }
            boolean r1 = r1.isValidForRequest(r0, r4)     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x001f
            if (r5 == 0) goto L_0x001d
            com.motorola.systemui.cli.navgesture.recents.RecentTasksList$TaskLoadResult r4 = r3.mResultsUi     // Catch:{ all -> 0x002d }
            java.util.ArrayList r4 = r3.copyOf(r4)     // Catch:{ all -> 0x002d }
            com.motorola.systemui.cli.navgesture.executors.LooperExecutor r1 = r3.mMainThreadExecutor     // Catch:{ all -> 0x002d }
            com.motorola.systemui.cli.navgesture.recents.RecentTasksList$$ExternalSyntheticLambda5 r2 = new com.motorola.systemui.cli.navgesture.recents.RecentTasksList$$ExternalSyntheticLambda5     // Catch:{ all -> 0x002d }
            r2.<init>(r5, r4)     // Catch:{ all -> 0x002d }
            r1.executeNext(r2)     // Catch:{ all -> 0x002d }
        L_0x001d:
            monitor-exit(r3)
            return r0
        L_0x001f:
            com.motorola.systemui.cli.navgesture.executors.LooperExecutor r1 = com.motorola.systemui.cli.navgesture.executors.AppExecutors.background()     // Catch:{ all -> 0x002d }
            com.motorola.systemui.cli.navgesture.recents.RecentTasksList$$ExternalSyntheticLambda2 r2 = new com.motorola.systemui.cli.navgesture.recents.RecentTasksList$$ExternalSyntheticLambda2     // Catch:{ all -> 0x002d }
            r2.<init>(r3, r0, r4, r5)     // Catch:{ all -> 0x002d }
            r1.execute(r2)     // Catch:{ all -> 0x002d }
            monitor-exit(r3)
            return r0
        L_0x002d:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.recents.RecentTasksList.getTasks(boolean, java.util.function.Consumer):int");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTasks$4(int i, boolean z, Consumer consumer) {
        if (!this.mResultsBg.isValidForRequest(i, z)) {
            this.mResultsBg = loadTasksInBackground(Integer.MAX_VALUE, i, z);
        }
        this.mMainThreadExecutor.execute(new RecentTasksList$$ExternalSyntheticLambda3(this, this.mResultsBg, consumer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTasks$3(TaskLoadResult taskLoadResult, Consumer consumer) {
        this.mResultsUi = taskLoadResult;
        if (consumer != null) {
            consumer.accept(copyOf(taskLoadResult));
        }
    }

    public synchronized boolean isTaskListValid(int i) {
        return this.mChangeId == i;
    }

    public void onTaskStackChanged() {
        invalidateLoadedTasks();
    }

    public void onRecentTaskListUpdated() {
        invalidateLoadedTasks();
    }

    public void onTaskRemoved(int i) {
        invalidateLoadedTasks();
    }

    public void onActivityPinned(String str, int i, int i2, int i3) {
        invalidateLoadedTasks();
    }

    public synchronized void onActivityUnpinned() {
        invalidateLoadedTasks();
    }

    private synchronized void invalidateLoadedTasks() {
        AppExecutors.background().execute(new RecentTasksList$$ExternalSyntheticLambda0(this));
        this.mResultsUi = INVALID_RESULT;
        this.mChangeId++;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$invalidateLoadedTasks$5() {
        this.mResultsBg = INVALID_RESULT;
    }

    /* access modifiers changed from: package-private */
    public TaskLoadResult loadTasksInBackground(int i, int i2, boolean z) {
        Task task;
        List<ActivityManager.RecentTaskInfo> recentTasks = this.mActivityManagerWrapper.getRecentTasks(i, ActivityManagerWrapper.getInstance().getCurrentUserId());
        C27191 r0 = new SparseBooleanArray() {
            public boolean get(int i) {
                if (indexOfKey(i) < 0) {
                    put(i, RecentTasksList.this.mKeyguardManager.isDeviceLocked(i));
                }
                return super.get(i);
            }
        };
        TaskLoadResult taskLoadResult = new TaskLoadResult(i2, z, recentTasks.size());
        for (ActivityManager.RecentTaskInfo next : recentTasks) {
            Task.TaskKey taskKey = new Task.TaskKey(next);
            if (!z) {
                task = Task.from(taskKey, next, r0.get(taskKey.userId));
            } else {
                task = new Task(taskKey);
            }
            taskLoadResult.add(task);
        }
        return taskLoadResult;
    }

    private ArrayList<Task> copyOf(ArrayList<Task> arrayList) {
        ArrayList<Task> arrayList2 = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            Task task = arrayList.get(i);
            arrayList2.add(new Task(task.key, task.colorPrimary, task.colorBackground, task.isDockable, task.isLocked, task.taskDescription, task.topActivity));
        }
        return arrayList2;
    }

    private static class TaskLoadResult extends ArrayList<Task> {
        final int mId;
        final boolean mKeysOnly;

        TaskLoadResult(int i, boolean z, int i2) {
            super(i2);
            this.mId = i;
            this.mKeysOnly = z;
        }

        /* access modifiers changed from: package-private */
        public boolean isValidForRequest(int i, boolean z) {
            return this.mId == i && (!this.mKeysOnly || z);
        }
    }
}
