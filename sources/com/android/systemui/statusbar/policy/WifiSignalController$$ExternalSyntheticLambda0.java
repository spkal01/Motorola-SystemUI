package com.android.systemui.statusbar.policy;

public final /* synthetic */ class WifiSignalController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ WifiSignalController f$0;

    public /* synthetic */ WifiSignalController$$ExternalSyntheticLambda0(WifiSignalController wifiSignalController) {
        this.f$0 = wifiSignalController;
    }

    public final void run() {
        this.f$0.handleStatusUpdated();
    }
}
