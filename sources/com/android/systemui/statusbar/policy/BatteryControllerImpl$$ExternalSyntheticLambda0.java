package com.android.systemui.statusbar.policy;

public final /* synthetic */ class BatteryControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BatteryControllerImpl f$0;

    public /* synthetic */ BatteryControllerImpl$$ExternalSyntheticLambda0(BatteryControllerImpl batteryControllerImpl) {
        this.f$0 = batteryControllerImpl;
    }

    public final void run() {
        this.f$0.notifyEstimateFetchCallbacks();
    }
}
