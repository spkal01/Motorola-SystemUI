package com.android.systemui.statusbar.phone;

import java.util.function.Consumer;

public final /* synthetic */ class NotificationPanelViewController$$ExternalSyntheticLambda14 implements Consumer {
    public final /* synthetic */ NotificationPanelViewController f$0;

    public /* synthetic */ NotificationPanelViewController$$ExternalSyntheticLambda14(NotificationPanelViewController notificationPanelViewController) {
        this.f$0 = notificationPanelViewController;
    }

    public final void accept(Object obj) {
        this.f$0.onStackYChanged(((Boolean) obj).booleanValue());
    }
}
