package com.android.systemui.statusbar.charging;

import com.android.systemui.statusbar.policy.BatteryController;

/* compiled from: WiredChargingRippleController.kt */
public final class WiredChargingRippleController$batteryStateChangeCallback$1 implements BatteryController.BatteryStateChangeCallback {
    final /* synthetic */ BatteryController $batteryController;
    final /* synthetic */ WiredChargingRippleController this$0;

    WiredChargingRippleController$batteryStateChangeCallback$1(WiredChargingRippleController wiredChargingRippleController, BatteryController batteryController) {
        this.this$0 = wiredChargingRippleController;
        this.$batteryController = batteryController;
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        if (this.this$0.rippleEnabled && !this.$batteryController.isPluggedInWireless()) {
            Boolean access$getPluggedIn$p = this.this$0.pluggedIn;
            this.this$0.pluggedIn = Boolean.valueOf(z);
            if ((access$getPluggedIn$p == null || !access$getPluggedIn$p.booleanValue()) && z) {
                this.this$0.mo18941x242f6c47();
            }
        }
    }
}
