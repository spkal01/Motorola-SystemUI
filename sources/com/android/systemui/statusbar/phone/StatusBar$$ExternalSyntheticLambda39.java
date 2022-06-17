package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.NotificationShadeWindowController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda39 implements Consumer {
    public final /* synthetic */ NotificationShadeWindowController f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda39(NotificationShadeWindowController notificationShadeWindowController) {
        this.f$0 = notificationShadeWindowController;
    }

    public final void accept(Object obj) {
        this.f$0.setLightRevealScrimAmount(((Float) obj).floatValue());
    }
}
