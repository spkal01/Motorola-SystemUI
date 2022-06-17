package com.android.systemui.statusbar.policy;

public final /* synthetic */ class NetworkSpeedControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ NetworkSpeedControllerImpl f$0;
    public final /* synthetic */ NetworkSpeedController$Callback f$1;

    public /* synthetic */ NetworkSpeedControllerImpl$$ExternalSyntheticLambda0(NetworkSpeedControllerImpl networkSpeedControllerImpl, NetworkSpeedController$Callback networkSpeedController$Callback) {
        this.f$0 = networkSpeedControllerImpl;
        this.f$1 = networkSpeedController$Callback;
    }

    public final void run() {
        this.f$0.lambda$addCallback$0(this.f$1);
    }
}
