package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.NetworkController;

public final /* synthetic */ class CallbackHandler$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CallbackHandler f$0;
    public final /* synthetic */ NetworkController.WifiIndicators f$1;

    public /* synthetic */ CallbackHandler$$ExternalSyntheticLambda2(CallbackHandler callbackHandler, NetworkController.WifiIndicators wifiIndicators) {
        this.f$0 = callbackHandler;
        this.f$1 = wifiIndicators;
    }

    public final void run() {
        this.f$0.lambda$setWifiIndicators$0(this.f$1);
    }
}
