package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class DozeServiceHost$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DozeServiceHost f$0;
    public final /* synthetic */ NotificationEntry f$1;

    public /* synthetic */ DozeServiceHost$$ExternalSyntheticLambda0(DozeServiceHost dozeServiceHost, NotificationEntry notificationEntry) {
        this.f$0 = dozeServiceHost;
        this.f$1 = notificationEntry;
    }

    public final void run() {
        this.f$0.lambda$fireNotificationPulse$0(this.f$1);
    }
}
