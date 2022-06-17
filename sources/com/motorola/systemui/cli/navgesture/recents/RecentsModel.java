package com.motorola.systemui.cli.navgesture.recents;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.motorola.systemui.cli.navgesture.MainThreadInitializedObject;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class RecentsModel extends TaskStackChangeListener {
    public static final MainThreadInitializedObject<RecentsModel> INSTANCE = new MainThreadInitializedObject<>(RecentsModel$$ExternalSyntheticLambda1.INSTANCE);
    private final Context mContext;
    private final RecentTasksList mTaskList;
    private final TaskThumbnailCache mThumbnailCache;
    private final List<TaskVisualsChangeListener> mThumbnailChangeListeners = new ArrayList();

    public interface TaskVisualsChangeListener {
        void onTaskThumbnailChanged(int i, ThumbnailData thumbnailData);
    }

    public static /* synthetic */ RecentsModel $r8$lambda$XYxWTjtbccDLqjVJ83rqEHeVUxU(Context context) {
        return new RecentsModel(context);
    }

    private RecentsModel(Context context) {
        this.mContext = context;
        Looper createAndStartNewLooper = AppExecutors.createAndStartNewLooper("TaskThumbnailIconCache", 10);
        this.mTaskList = new RecentTasksList(AppExecutors.m97ui(), new KeyguardManagerCompat(context), ActivityManagerWrapper.getInstance());
        this.mThumbnailCache = new TaskThumbnailCache(context, createAndStartNewLooper);
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this);
    }

    public TaskThumbnailCache getThumbnailCache() {
        return this.mThumbnailCache;
    }

    public int getTasks(Consumer<ArrayList<Task>> consumer) {
        return this.mTaskList.getTasks(false, consumer);
    }

    public static int getRunningTaskId() {
        ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
        if (runningTask != null) {
            return runningTask.taskId;
        }
        return -1;
    }

    public boolean isTaskListValid(int i) {
        return this.mTaskList.isTaskListValid(i);
    }

    public void onTaskStackChangedBackground() {
        if (this.mThumbnailCache.isPreloadingEnabled()) {
            this.mTaskList.getTaskKeys(this.mThumbnailCache.getCacheSize(), new RecentsModel$$ExternalSyntheticLambda0(this, getRunningTaskId()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskStackChangedBackground$1(int i, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.key.f124id != i) {
                this.mThumbnailCache.updateThumbnailInCache(task);
            }
        }
    }

    public void onTaskSnapshotChanged(int i, ThumbnailData thumbnailData) {
        this.mThumbnailCache.updateTaskSnapShot(i, thumbnailData);
        for (int size = this.mThumbnailChangeListeners.size() - 1; size >= 0; size--) {
            this.mThumbnailChangeListeners.get(size).onTaskThumbnailChanged(i, thumbnailData);
        }
    }

    public void onTaskRemoved(int i) {
        DebugLog.m100v("RecentsModel", "onTaskRemoved: task id = " + i);
        this.mThumbnailCache.remove(new Task.TaskKey(i, 0, (Intent) null, (ComponentName) null, 0, 0));
    }

    public void onTrimMemory(int i) {
        if (i == 20) {
            this.mThumbnailCache.getHighResLoadingState().setVisible(false);
        }
        if (i == 15) {
            this.mThumbnailCache.clear();
        }
    }

    public void addThumbnailChangeListener(TaskVisualsChangeListener taskVisualsChangeListener) {
        this.mThumbnailChangeListeners.add(taskVisualsChangeListener);
    }

    public void removeThumbnailChangeListener(TaskVisualsChangeListener taskVisualsChangeListener) {
        this.mThumbnailChangeListeners.remove(taskVisualsChangeListener);
    }
}
