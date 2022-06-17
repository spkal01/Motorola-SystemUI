package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.NotificationListenerController;
import java.util.function.Consumer;

public final /* synthetic */ class NotificationListenerWithPlugins$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ NotificationListenerWithPlugins f$0;

    public /* synthetic */ NotificationListenerWithPlugins$$ExternalSyntheticLambda0(NotificationListenerWithPlugins notificationListenerWithPlugins) {
        this.f$0 = notificationListenerWithPlugins;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onPluginConnected$0((NotificationListenerController) obj);
    }
}
