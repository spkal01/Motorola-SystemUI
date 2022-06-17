package com.android.systemui.statusbar.notification.row.wrapper;

import android.widget.Button;

public final /* synthetic */ class NotificationTemplateViewWrapper$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ NotificationTemplateViewWrapper f$0;
    public final /* synthetic */ Button f$1;

    public /* synthetic */ NotificationTemplateViewWrapper$$ExternalSyntheticLambda3(NotificationTemplateViewWrapper notificationTemplateViewWrapper, Button button) {
        this.f$0 = notificationTemplateViewWrapper;
        this.f$1 = button;
    }

    public final void run() {
        this.f$0.lambda$updatePendingIntentCancellations$0(this.f$1);
    }
}
