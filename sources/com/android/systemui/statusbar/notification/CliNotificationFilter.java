package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;

public class CliNotificationFilter {
    private static final boolean DEBUG = (!"user".equals(Build.TYPE));

    public static boolean isNotificationFiltered(Context context, StatusBarNotification statusBarNotification) {
        if (!statusBarNotification.isClearable() && !isValidOnGoingNotification(statusBarNotification.getPackageName(), statusBarNotification.getId())) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: OnGoing notification=" + statusBarNotification);
            }
            return true;
        } else if (isMediaStyle(statusBarNotification)) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: Media style=" + statusBarNotification);
            }
            return true;
        } else if (shouldFilterLowPriorityNotification(statusBarNotification)) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: Low Priority notification=" + statusBarNotification);
            }
            return true;
        } else if (shouldFilterCalendarNotification(statusBarNotification)) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: Calendar quiet refresh=" + statusBarNotification);
            }
            return true;
        } else if (isAutoBundledNotification(statusBarNotification)) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: Auto bundled notification=" + statusBarNotification);
            }
            return true;
        } else if (!CliNotificationSettings.getInstance(context).isBlockedApp(statusBarNotification.getPackageName())) {
            return false;
        } else {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Filtered: blocked notification=" + statusBarNotification);
            }
            return true;
        }
    }

    public static boolean isNotificationCardFiltered(HighPriorityProvider highPriorityProvider, NotificationEntry notificationEntry) {
        if (isMediaStyle(notificationEntry.getSbn())) {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Card filtered: Media style=" + notificationEntry.getSbn());
            }
            return true;
        } else if (highPriorityProvider.isHighPriority(notificationEntry)) {
            return false;
        } else {
            if (DEBUG) {
                Log.d("Cli_NotificationFilter", "Card filtered: Low Priority notification" + notificationEntry.getSbn());
            }
            return true;
        }
    }

    private static boolean isAutoBundledNotification(StatusBarNotification statusBarNotification) {
        String groupKey = statusBarNotification.getGroupKey();
        return (statusBarNotification.getTag() == null || groupKey == null || (!groupKey.contains("ranker_bundle") && !groupKey.contains("ranker_group"))) ? false : true;
    }

    private static boolean shouldFilterCalendarNotification(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        return ("com.android.calendar".equals(statusBarNotification.getPackageName()) || "com.google.android.calendar".equals(statusBarNotification.getPackageName())) && notification.tickerText == null && notification.sound == null;
    }

    private static boolean hasValidGroupKey(StatusBarNotification statusBarNotification) {
        return statusBarNotification.getGroupKey().isEmpty() || statusBarNotification.getGroupKey().equals(statusBarNotification.getKey());
    }

    private static boolean shouldFilterLowPriorityNotification(StatusBarNotification statusBarNotification) {
        return isLowPriority(statusBarNotification) && hasValidGroupKey(statusBarNotification);
    }

    private static boolean isLowPriority(StatusBarNotification statusBarNotification) {
        return statusBarNotification.getNotification().priority <= -1;
    }

    private static boolean isValidOnGoingNotification(String str, int i) {
        return ("com.android.phone".equals(str) || "com.android.dialer".equals(str)) && (i == 3 || i == 53 || i == 103);
    }

    private static boolean isMediaStyle(StatusBarNotification statusBarNotification) {
        return "android.app.Notification$MediaStyle".equals(statusBarNotification.getNotification().extras.getString("android.template"));
    }
}
