package com.android.settingslib.applications.appclone;

import android.content.pm.ApplicationInfo;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.applications.ApplicationsState;
import java.util.HashMap;

public class AppCloneUtils {
    public static void addCloneUserToEntryMap(UserManager userManager, SparseArray<HashMap<String, ApplicationsState.AppEntry>> sparseArray) {
        if (Build.IS_PRC_PRODUCT && userManager != null && sparseArray != null) {
            try {
                for (UserInfo userInfo : userManager.getAppCloneProfiles()) {
                    if (!sparseArray.contains(userInfo.id)) {
                        sparseArray.put(userInfo.id, new HashMap());
                    }
                }
            } catch (Exception e) {
                Log.w("AppCloneUtils", "Get app clone profiles exception: " + e.getMessage());
            }
        }
    }

    public static boolean isCloneAppInWorkProfileSetting(UserManager userManager, ApplicationInfo applicationInfo) {
        return userManager != null && applicationInfo != null && isCloneApp(applicationInfo) && userManager.isManagedProfile();
    }

    public static boolean isCloneApp(ApplicationInfo applicationInfo) {
        if (!Build.IS_PRC_PRODUCT || applicationInfo == null) {
            return false;
        }
        return UserHandle.isAppCloneUser(UserHandle.getUserId(applicationInfo.uid));
    }
}
