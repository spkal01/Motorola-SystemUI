package com.motorola.android.systemui.safemode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.util.NotificationChannels;

public class SafeModeRebootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") && context.getPackageManager().isSafeMode()) {
            createAndPostNotification(context);
        }
    }

    private void createAndPostNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).notify(0, new Notification.Builder(context, NotificationChannels.HINTS).setSmallIcon(R$drawable.zz_moto_safe_mode).setContentTitle(context.getString(R$string.safemode_notification_title)).setContentText(context.getString(R$string.safemode_notification_message)).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, SafeModeRebootActivity.class), 67108864)).setOngoing(true).build());
    }
}
