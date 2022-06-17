package com.android.systemui.statusbar.policy;

public final /* synthetic */ class LocationControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ LocationControllerImpl f$0;

    public /* synthetic */ LocationControllerImpl$$ExternalSyntheticLambda0(LocationControllerImpl locationControllerImpl) {
        this.f$0 = locationControllerImpl;
    }

    public final void run() {
        this.f$0.updateActiveLocationRequests();
    }
}
