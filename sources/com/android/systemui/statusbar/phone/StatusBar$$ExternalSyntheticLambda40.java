package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda40 implements Consumer {
    public final /* synthetic */ StatusBar f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda40(StatusBar statusBar) {
        this.f$0 = statusBar;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$maybeEscalateHeadsUp$17((NotificationEntry) obj);
    }
}
