package com.android.systemui;

import com.android.systemui.statusbar.policy.BatteryController;

public final /* synthetic */ class BatteryMeterView$$ExternalSyntheticLambda0 implements BatteryController.EstimateFetchCompletion {
    public final /* synthetic */ BatteryMeterView f$0;

    public /* synthetic */ BatteryMeterView$$ExternalSyntheticLambda0(BatteryMeterView batteryMeterView) {
        this.f$0 = batteryMeterView;
    }

    public final void onBatteryRemainingEstimateRetrieved(String str) {
        this.f$0.lambda$updatePercentText$0(str);
    }
}
