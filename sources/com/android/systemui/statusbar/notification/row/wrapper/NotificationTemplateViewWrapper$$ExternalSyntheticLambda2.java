package com.android.systemui.statusbar.notification.row.wrapper;

import android.app.PendingIntent;

public final /* synthetic */ class NotificationTemplateViewWrapper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ NotificationTemplateViewWrapper f$0;
    public final /* synthetic */ PendingIntent f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ NotificationTemplateViewWrapper$$ExternalSyntheticLambda2(NotificationTemplateViewWrapper notificationTemplateViewWrapper, PendingIntent pendingIntent, Runnable runnable) {
        this.f$0 = notificationTemplateViewWrapper;
        this.f$1 = pendingIntent;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$performOnPendingIntentCancellation$1(this.f$1, this.f$2);
    }
}
