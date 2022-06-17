package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;

public final /* synthetic */ class StatusBarNotificationPresenter$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ StatusBarNotificationPresenter f$0;
    public final /* synthetic */ NotificationStackScrollLayoutController f$1;
    public final /* synthetic */ NotificationRemoteInputManager f$2;
    public final /* synthetic */ NotificationInterruptStateProvider f$3;

    public /* synthetic */ StatusBarNotificationPresenter$$ExternalSyntheticLambda1(StatusBarNotificationPresenter statusBarNotificationPresenter, NotificationStackScrollLayoutController notificationStackScrollLayoutController, NotificationRemoteInputManager notificationRemoteInputManager, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        this.f$0 = statusBarNotificationPresenter;
        this.f$1 = notificationStackScrollLayoutController;
        this.f$2 = notificationRemoteInputManager;
        this.f$3 = notificationInterruptStateProvider;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2, this.f$3);
    }
}
