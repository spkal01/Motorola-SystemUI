package com.motorola.systemui.prc.media;

import android.service.notification.StatusBarNotification;

public interface IMediaNotification {
    void onNotificationPosted(StatusBarNotification statusBarNotification);
}
