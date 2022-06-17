package com.motorola.systemui.desktop;

import android.app.PendingIntent;

public final /* synthetic */ class DesktopStatusBar$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ DesktopStatusBar f$0;
    public final /* synthetic */ PendingIntent f$1;

    public /* synthetic */ DesktopStatusBar$$ExternalSyntheticLambda5(DesktopStatusBar desktopStatusBar, PendingIntent pendingIntent) {
        this.f$0 = desktopStatusBar;
        this.f$1 = pendingIntent;
    }

    public final void run() {
        this.f$0.lambda$postStartActivityDismissingKeyguard$6(this.f$1);
    }
}
