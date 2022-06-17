package com.android.systemui.statusbar.notification.logging;

import com.android.internal.statusbar.NotificationVisibility;

public final /* synthetic */ class NotificationLogger$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ NotificationLogger f$0;
    public final /* synthetic */ NotificationVisibility[] f$1;
    public final /* synthetic */ NotificationVisibility[] f$2;

    public /* synthetic */ NotificationLogger$$ExternalSyntheticLambda0(NotificationLogger notificationLogger, NotificationVisibility[] notificationVisibilityArr, NotificationVisibility[] notificationVisibilityArr2) {
        this.f$0 = notificationLogger;
        this.f$1 = notificationVisibilityArr;
        this.f$2 = notificationVisibilityArr2;
    }

    public final void run() {
        this.f$0.lambda$logNotificationVisibilityChanges$0(this.f$1, this.f$2);
    }
}
