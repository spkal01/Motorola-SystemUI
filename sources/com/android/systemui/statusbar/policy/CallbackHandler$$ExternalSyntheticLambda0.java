package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.NetworkController;

public final /* synthetic */ class CallbackHandler$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ CallbackHandler f$0;
    public final /* synthetic */ NetworkController.IconState f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ CallbackHandler$$ExternalSyntheticLambda0(CallbackHandler callbackHandler, NetworkController.IconState iconState, int i) {
        this.f$0 = callbackHandler;
        this.f$1 = iconState;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$setCallIndicator$3(this.f$1, this.f$2);
    }
}
