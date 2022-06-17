package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.BatteryController;
import java.util.function.Consumer;

public final /* synthetic */ class BatteryControllerImpl$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ BatteryControllerImpl f$0;

    public /* synthetic */ BatteryControllerImpl$$ExternalSyntheticLambda2(BatteryControllerImpl batteryControllerImpl) {
        this.f$0 = batteryControllerImpl;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$fireWirelessChargingChanged$0((BatteryController.BatteryStateChangeCallback) obj);
    }
}
