package com.android.systemui.power;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;

public class PowerSharingNotification {
    private static final boolean DEBUG = (Build.IS_USERDEBUG || Build.IS_ENG);
    private Context mContext;
    private NotificationManager mNotificationManager;
    private Resources mRes = this.mContext.getResources();

    public PowerSharingNotification(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (DEBUG) {
            Log.d("PowerSharingNotification", "create the channel");
        }
        if (Build.VERSION.SDK_INT >= 26) {
            String string = this.mRes.getString(R$string.wireless_power_sharing_feature_name);
            NotificationChannel notificationChannel = new NotificationChannel("wireless_power_sharing_notif_error", string, 3);
            notificationChannel.setDescription(string);
            this.mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /* access modifiers changed from: protected */
    public void notify(int i) {
        if (DEBUG) {
            Log.d("PowerSharingNotification", "send the notification with reason: " + i);
        }
        String fetchTextBasedOnErrorCode = fetchTextBasedOnErrorCode(i);
        if (!TextUtils.isEmpty(fetchTextBasedOnErrorCode)) {
            this.mNotificationManager.notify(4004, new NotificationCompat.Builder(this.mContext, "wireless_power_sharing_notif_error").setSmallIcon(R$drawable.ic_wireless_power_sharing_notification).setContentTitle(this.mRes.getString(R$string.wireless_power_sharing_off)).setContentText(fetchTextBasedOnErrorCode).setStyle(new NotificationCompat.BigTextStyle().bigText(fetchTextBasedOnErrorCode)).setOngoing(true).setAutoCancel(false).build());
        }
    }

    /* access modifiers changed from: protected */
    public void cancel() {
        if (DEBUG) {
            Log.d("PowerSharingNotification", "cancel the notification");
        }
        this.mNotificationManager.cancel(4004);
    }

    private String fetchTextBasedOnErrorCode(int i) {
        int i2;
        if (i == 1) {
            i2 = R$string.wireless_power_sharing_off_battery_save_reason;
        } else if (i == 2) {
            i2 = R$string.wireless_power_sharing_off_low_battery_reason;
        } else if (i != 3) {
            return null;
        } else {
            i2 = R$string.wireless_power_sharing_off_warm_reason;
        }
        return this.mRes.getString(i2);
    }
}
