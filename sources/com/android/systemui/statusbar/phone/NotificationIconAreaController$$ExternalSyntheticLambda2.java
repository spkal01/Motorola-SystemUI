package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.StatusBarIconView;

public final /* synthetic */ class NotificationIconAreaController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ NotificationIconAreaController f$0;
    public final /* synthetic */ StatusBarIconView f$1;

    public /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda2(NotificationIconAreaController notificationIconAreaController, StatusBarIconView statusBarIconView) {
        this.f$0 = notificationIconAreaController;
        this.f$1 = statusBarIconView;
    }

    public final void run() {
        this.f$0.lambda$applyNotificationIconsTint$5(this.f$1);
    }
}
