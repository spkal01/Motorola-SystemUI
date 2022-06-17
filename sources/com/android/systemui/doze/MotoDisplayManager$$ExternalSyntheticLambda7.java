package com.android.systemui.doze;

import android.app.PendingIntent;
import android.content.Intent;

public final /* synthetic */ class MotoDisplayManager$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ MotoDisplayManager f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ PendingIntent f$2;
    public final /* synthetic */ Intent f$3;

    public /* synthetic */ MotoDisplayManager$$ExternalSyntheticLambda7(MotoDisplayManager motoDisplayManager, String str, PendingIntent pendingIntent, Intent intent) {
        this.f$0 = motoDisplayManager;
        this.f$1 = str;
        this.f$2 = pendingIntent;
        this.f$3 = intent;
    }

    public final void run() {
        this.f$0.lambda$triggerNotificationClickAndRequestUnlockInternal$3(this.f$1, this.f$2, this.f$3);
    }
}
