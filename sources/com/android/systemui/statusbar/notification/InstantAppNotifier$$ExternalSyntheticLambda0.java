package com.android.systemui.statusbar.notification;

import android.app.NotificationManager;
import android.content.pm.IPackageManager;

public final /* synthetic */ class InstantAppNotifier$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ InstantAppNotifier f$0;
    public final /* synthetic */ NotificationManager f$1;
    public final /* synthetic */ IPackageManager f$2;

    public /* synthetic */ InstantAppNotifier$$ExternalSyntheticLambda0(InstantAppNotifier instantAppNotifier, NotificationManager notificationManager, IPackageManager iPackageManager) {
        this.f$0 = instantAppNotifier;
        this.f$1 = notificationManager;
        this.f$2 = iPackageManager;
    }

    public final void run() {
        this.f$0.lambda$updateForegroundInstantApps$3(this.f$1, this.f$2);
    }
}
