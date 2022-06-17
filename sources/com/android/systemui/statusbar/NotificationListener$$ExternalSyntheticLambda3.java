package com.android.systemui.statusbar;

import android.app.NotificationChannel;
import android.os.UserHandle;

public final /* synthetic */ class NotificationListener$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ NotificationListener f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ UserHandle f$2;
    public final /* synthetic */ NotificationChannel f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ NotificationListener$$ExternalSyntheticLambda3(NotificationListener notificationListener, String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
        this.f$0 = notificationListener;
        this.f$1 = str;
        this.f$2 = userHandle;
        this.f$3 = notificationChannel;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.lambda$onNotificationChannelModified$4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
