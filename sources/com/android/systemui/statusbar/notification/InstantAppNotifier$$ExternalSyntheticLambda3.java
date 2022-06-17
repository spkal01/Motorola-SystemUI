package com.android.systemui.statusbar.notification;

import android.app.NotificationManager;
import android.util.Pair;
import java.util.function.Consumer;

public final /* synthetic */ class InstantAppNotifier$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ InstantAppNotifier f$0;
    public final /* synthetic */ NotificationManager f$1;

    public /* synthetic */ InstantAppNotifier$$ExternalSyntheticLambda3(InstantAppNotifier instantAppNotifier, NotificationManager notificationManager) {
        this.f$0 = instantAppNotifier;
        this.f$1 = notificationManager;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$updateForegroundInstantApps$2(this.f$1, (Pair) obj);
    }
}
