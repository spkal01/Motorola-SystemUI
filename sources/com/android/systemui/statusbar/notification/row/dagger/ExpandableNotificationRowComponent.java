package com.android.systemui.statusbar.notification.row.dagger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;

public interface ExpandableNotificationRowComponent {

    public interface Builder {
        ExpandableNotificationRowComponent build();

        Builder expandableNotificationRow(ExpandableNotificationRow expandableNotificationRow);

        Builder listContainer(NotificationListContainer notificationListContainer);

        Builder notificationEntry(NotificationEntry notificationEntry);

        Builder onExpandClickListener(ExpandableNotificationRow.OnExpandClickListener onExpandClickListener);
    }

    ExpandableNotificationRowController getExpandableNotificationRowController();

    public static abstract class ExpandableNotificationRowModule {
        static StatusBarNotification provideStatusBarNotification(NotificationEntry notificationEntry) {
            return notificationEntry.getSbn();
        }

        static String provideNotificationKey(StatusBarNotification statusBarNotification) {
            return statusBarNotification.getKey();
        }

        static String provideAppName(Context context, StatusBarNotification statusBarNotification) {
            PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(context, statusBarNotification.getUser().getIdentifier());
            String packageName = statusBarNotification.getPackageName();
            try {
                ApplicationInfo applicationInfo = packageManagerForUser.getApplicationInfo(packageName, 8704);
                if (applicationInfo != null) {
                    return String.valueOf(packageManagerForUser.getApplicationLabel(applicationInfo));
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            return packageName;
        }
    }
}
