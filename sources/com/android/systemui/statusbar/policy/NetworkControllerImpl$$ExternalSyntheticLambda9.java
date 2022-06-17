package com.android.systemui.statusbar.policy;

public final /* synthetic */ class NetworkControllerImpl$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ WifiSignalController f$0;

    public /* synthetic */ NetworkControllerImpl$$ExternalSyntheticLambda9(WifiSignalController wifiSignalController) {
        this.f$0 = wifiSignalController;
    }

    public final void run() {
        this.f$0.fetchInitialState();
    }
}
