package com.android.systemui.statusbar.policy;

public final /* synthetic */ class NetworkControllerImpl$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ NetworkControllerImpl f$0;

    public /* synthetic */ NetworkControllerImpl$$ExternalSyntheticLambda3(NetworkControllerImpl networkControllerImpl) {
        this.f$0 = networkControllerImpl;
    }

    public final void run() {
        this.f$0.updateConnectivity();
    }
}
