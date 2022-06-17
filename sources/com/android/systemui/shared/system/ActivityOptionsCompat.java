package com.android.systemui.shared.system;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Handler;
import com.android.systemui.shared.recents.model.Task;

public abstract class ActivityOptionsCompat {
    public static ActivityOptions makeRemoteAnimation(RemoteAnimationAdapterCompat remoteAnimationAdapterCompat) {
        return ActivityOptions.makeRemoteAnimation(remoteAnimationAdapterCompat.getWrapped(), remoteAnimationAdapterCompat.getRemoteTransition().getTransition());
    }

    public static ActivityOptions makeCustomAnimation(Context context, int i, int i2, final Runnable runnable, final Handler handler) {
        return ActivityOptions.makeCustomTaskAnimation(context, i, i2, handler, new ActivityOptions.OnAnimationStartedListener() {
            public void onAnimationStarted() {
                Runnable runnable = runnable;
                if (runnable != null) {
                    handler.post(runnable);
                }
            }
        }, (ActivityOptions.OnAnimationFinishedListener) null);
    }

    public static ActivityOptions setFreezeRecentTasksList(ActivityOptions activityOptions) {
        activityOptions.setFreezeRecentTasksReordering();
        return activityOptions;
    }

    public static void addTaskInfo(ActivityOptions activityOptions, Task.TaskKey taskKey) {
        if (taskKey.windowingMode == 3) {
            activityOptions.setLaunchWindowingMode(4);
        }
    }
}
