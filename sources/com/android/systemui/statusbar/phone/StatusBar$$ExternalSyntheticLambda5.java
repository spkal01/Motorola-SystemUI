package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.content.Intent;
import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda5 implements ActivityStarter.OnDismissAction {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ PendingIntent f$2;
    public final /* synthetic */ Intent f$3;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda5(StatusBar statusBar, String str, PendingIntent pendingIntent, Intent intent) {
        this.f$0 = statusBar;
        this.f$1 = str;
        this.f$2 = pendingIntent;
        this.f$3 = intent;
    }

    public final boolean onDismiss() {
        return this.f$0.lambda$triggerNotificationClickAndRequestUnlockInternal$39(this.f$1, this.f$2, this.f$3);
    }
}
