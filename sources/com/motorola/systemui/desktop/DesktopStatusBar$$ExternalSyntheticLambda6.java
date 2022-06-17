package com.motorola.systemui.desktop;

import android.content.Intent;

public final /* synthetic */ class DesktopStatusBar$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ DesktopStatusBar f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ DesktopStatusBar$$ExternalSyntheticLambda6(DesktopStatusBar desktopStatusBar, Intent intent) {
        this.f$0 = desktopStatusBar;
        this.f$1 = intent;
    }

    public final void run() {
        this.f$0.lambda$postStartActivityDismissingKeyguard$5(this.f$1);
    }
}
