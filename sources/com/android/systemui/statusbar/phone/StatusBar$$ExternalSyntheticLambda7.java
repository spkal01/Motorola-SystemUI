package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.DozeParameters;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda7 implements DozeParameters.Callback {
    public final /* synthetic */ StatusBar f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda7(StatusBar statusBar) {
        this.f$0 = statusBar;
    }

    public final void onAlwaysOnChange() {
        this.f$0.updateLightRevealScrimVisibility();
    }
}
