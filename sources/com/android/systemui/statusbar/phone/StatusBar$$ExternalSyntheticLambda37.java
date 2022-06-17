package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.content.Intent;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ PendingIntent f$2;
    public final /* synthetic */ Intent f$3;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda37(StatusBar statusBar, String str, PendingIntent pendingIntent, Intent intent) {
        this.f$0 = statusBar;
        this.f$1 = str;
        this.f$2 = pendingIntent;
        this.f$3 = intent;
    }

    public final void run() {
        this.f$0.lambda$triggerNotificationClickAndRequestUnlockInternal$38(this.f$1, this.f$2, this.f$3);
    }
}
