package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.statusbar.phone.StatusBar;

public final /* synthetic */ class StatusBar$8$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StatusBar.C19338 f$0;
    public final /* synthetic */ OverlayPlugin f$1;

    public /* synthetic */ StatusBar$8$$ExternalSyntheticLambda0(StatusBar.C19338 r1, OverlayPlugin overlayPlugin) {
        this.f$0 = r1;
        this.f$1 = overlayPlugin;
    }

    public final void run() {
        this.f$0.lambda$onPluginConnected$0(this.f$1);
    }
}
