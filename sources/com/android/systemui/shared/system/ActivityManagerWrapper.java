package com.android.systemui.shared.system;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.IRecentsAnimationController;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationTarget;
import android.window.TaskSnapshot;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.List;
import java.util.function.Consumer;

public class ActivityManagerWrapper {
    private static final ActivityManagerWrapper sInstance = new ActivityManagerWrapper();
    private final ActivityTaskManager mAtm = ActivityTaskManager.getInstance();

    private ActivityManagerWrapper() {
    }

    public static ActivityManagerWrapper getInstance() {
        return sInstance;
    }

    public int getCurrentUserId() {
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            if (currentUser != null) {
                return currentUser.id;
            }
            return 0;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ActivityManager.RunningTaskInfo getRunningTask() {
        return getRunningTask(false);
    }

    public ActivityManager.RunningTaskInfo getRunningTask(boolean z) {
        List tasks = this.mAtm.getTasks(1, z);
        if (tasks.isEmpty()) {
            return null;
        }
        return (ActivityManager.RunningTaskInfo) tasks.get(0);
    }

    public List<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2) {
        return this.mAtm.getRecentTasks(i, 2, i2);
    }

    public ThumbnailData getTaskThumbnail(int i, boolean z) {
        TaskSnapshot taskSnapshot;
        try {
            taskSnapshot = ActivityTaskManager.getService().getTaskSnapshot(i, z);
        } catch (RemoteException e) {
            Log.w("ActivityManagerWrapper", "Failed to retrieve task snapshot", e);
            taskSnapshot = null;
        }
        if (taskSnapshot != null) {
            return new ThumbnailData(taskSnapshot);
        }
        return new ThumbnailData();
    }

    public void startRecentsActivity(Intent intent, long j, RecentsAnimationListener recentsAnimationListener, final Consumer<Boolean> consumer, Handler handler) {
        final boolean startRecentsActivity = startRecentsActivity(intent, j, recentsAnimationListener);
        if (consumer != null) {
            handler.post(new Runnable() {
                public void run() {
                    consumer.accept(Boolean.valueOf(startRecentsActivity));
                }
            });
        }
    }

    public boolean startRecentsActivity(Intent intent, long j, final RecentsAnimationListener recentsAnimationListener) {
        C14062 r0 = null;
        if (recentsAnimationListener != null) {
            try {
                r0 = new IRecentsAnimationRunner.Stub() {
                    public void onAnimationStart(IRecentsAnimationController iRecentsAnimationController, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, Rect rect, Rect rect2) {
                        recentsAnimationListener.onAnimationStart(new RecentsAnimationControllerCompat(iRecentsAnimationController), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr2), rect, rect2);
                    }

                    public void onAnimationCanceled(TaskSnapshot taskSnapshot) {
                        recentsAnimationListener.onAnimationCanceled(taskSnapshot != null ? new ThumbnailData(taskSnapshot) : null);
                    }

                    public void onTaskAppeared(RemoteAnimationTarget remoteAnimationTarget) {
                        recentsAnimationListener.onTaskAppeared(new RemoteAnimationTargetCompat(remoteAnimationTarget));
                    }
                };
            } catch (Exception unused) {
                return false;
            }
        }
        ActivityTaskManager.getService().startRecentsActivity(intent, j, r0);
        return true;
    }

    public void cancelRecentsAnimation(boolean z) {
        try {
            ActivityTaskManager.getService().cancelRecentsAnimation(z);
        } catch (RemoteException e) {
            Log.e("ActivityManagerWrapper", "Failed to cancel recents animation", e);
        }
    }

    public void startActivityFromRecentsAsync(Task.TaskKey taskKey, ActivityOptions activityOptions, final Consumer<Boolean> consumer, Handler handler) {
        final boolean startActivityFromRecents = startActivityFromRecents(taskKey, activityOptions);
        if (consumer != null) {
            handler.post(new Runnable() {
                public void run() {
                    consumer.accept(Boolean.valueOf(startActivityFromRecents));
                }
            });
        }
    }

    public boolean startActivityFromRecents(Task.TaskKey taskKey, ActivityOptions activityOptions) {
        ActivityOptionsCompat.addTaskInfo(activityOptions, taskKey);
        return startActivityFromRecents(taskKey.f124id, activityOptions);
    }

    public boolean startActivityFromRecents(int i, ActivityOptions activityOptions) {
        Bundle bundle;
        if (activityOptions == null) {
            bundle = null;
        } else {
            try {
                bundle = activityOptions.toBundle();
            } catch (Exception unused) {
                return false;
            }
        }
        ActivityTaskManager.getService().startActivityFromRecents(i, bundle);
        return true;
    }

    public void registerTaskStackListener(TaskStackChangeListener taskStackChangeListener) {
        TaskStackChangeListeners.getInstance().registerTaskStackListener(taskStackChangeListener);
    }

    public void unregisterTaskStackListener(TaskStackChangeListener taskStackChangeListener) {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(taskStackChangeListener);
    }

    public void closeSystemWindows(String str) {
        try {
            ActivityManager.getService().closeSystemDialogs(str);
        } catch (RemoteException e) {
            Log.w("ActivityManagerWrapper", "Failed to close system windows", e);
        }
    }

    public void removeTask(int i) {
        try {
            ActivityTaskManager.getService().removeTask(i);
        } catch (RemoteException e) {
            Log.w("ActivityManagerWrapper", "Failed to remove task=" + i, e);
        }
    }

    public boolean isScreenPinningActive() {
        try {
            return ActivityTaskManager.getService().getLockTaskModeState() == 2;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isLockTaskKioskModeActive() {
        try {
            return ActivityTaskManager.getService().getLockTaskModeState() == 1;
        } catch (RemoteException unused) {
            return false;
        }
    }
}
