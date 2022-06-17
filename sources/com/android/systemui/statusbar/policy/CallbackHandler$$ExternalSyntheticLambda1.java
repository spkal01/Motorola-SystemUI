package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.NetworkController;

public final /* synthetic */ class CallbackHandler$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ CallbackHandler f$0;
    public final /* synthetic */ NetworkController.MobileDataIndicators f$1;

    public /* synthetic */ CallbackHandler$$ExternalSyntheticLambda1(CallbackHandler callbackHandler, NetworkController.MobileDataIndicators mobileDataIndicators) {
        this.f$0 = callbackHandler;
        this.f$1 = mobileDataIndicators;
    }

    public final void run() {
        this.f$0.lambda$setMobileDataIndicators$1(this.f$1);
    }
}
