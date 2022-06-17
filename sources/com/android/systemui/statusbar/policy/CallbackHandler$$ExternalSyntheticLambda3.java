package com.android.systemui.statusbar.policy;

public final /* synthetic */ class CallbackHandler$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ CallbackHandler f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ CallbackHandler$$ExternalSyntheticLambda3(CallbackHandler callbackHandler, boolean z, boolean z2, boolean z3) {
        this.f$0 = callbackHandler;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = z3;
    }

    public final void run() {
        this.f$0.lambda$setConnectivityStatus$2(this.f$1, this.f$2, this.f$3);
    }
}
