package com.motorola.systemui.cli.media;

import android.service.notification.StatusBarNotification;
import com.motorola.systemui.prc.media.IMediaNotification;

public final /* synthetic */ class CliMediaNotificationController$$ExternalSyntheticLambda0 implements IMediaNotification {
    public final /* synthetic */ CliMediaNotificationController f$0;

    public /* synthetic */ CliMediaNotificationController$$ExternalSyntheticLambda0(CliMediaNotificationController cliMediaNotificationController) {
        this.f$0 = cliMediaNotificationController;
    }

    public final void onNotificationPosted(StatusBarNotification statusBarNotification) {
        this.f$0.onNotificationPosted(statusBarNotification);
    }
}
