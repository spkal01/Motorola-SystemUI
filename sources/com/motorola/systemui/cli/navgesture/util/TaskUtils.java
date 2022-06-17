package com.motorola.systemui.cli.navgesture.util;

import android.app.Activity;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public final class TaskUtils {
    public static boolean taskIsATargetWithMode(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i, int i2) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == i2 && remoteAnimationTargetCompat.taskId == i) {
                return true;
            }
        }
        return false;
    }

    public static boolean activityIsATargetWithMode(Activity activity, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i) {
        return taskIsATargetWithMode(remoteAnimationTargetCompatArr, activity.getTaskId(), i);
    }
}
