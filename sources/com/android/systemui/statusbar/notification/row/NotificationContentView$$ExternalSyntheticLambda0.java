package com.android.systemui.statusbar.notification.row;

import java.util.function.Consumer;

public final /* synthetic */ class NotificationContentView$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ NotificationContentView f$0;

    public /* synthetic */ NotificationContentView$$ExternalSyntheticLambda0(NotificationContentView notificationContentView) {
        this.f$0 = notificationContentView;
    }

    public final void accept(Object obj) {
        this.f$0.setRemoteInputVisible(((Boolean) obj).booleanValue());
    }
}
