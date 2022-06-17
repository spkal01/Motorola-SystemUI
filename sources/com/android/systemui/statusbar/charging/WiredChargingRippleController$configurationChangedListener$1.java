package com.android.systemui.statusbar.charging;

import android.content.res.Configuration;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.policy.ConfigurationController;
import org.jetbrains.annotations.Nullable;

/* compiled from: WiredChargingRippleController.kt */
public final class WiredChargingRippleController$configurationChangedListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ WiredChargingRippleController this$0;

    WiredChargingRippleController$configurationChangedListener$1(WiredChargingRippleController wiredChargingRippleController) {
        this.this$0 = wiredChargingRippleController;
    }

    public void onUiModeChanged() {
        this.this$0.updateRippleColor();
    }

    public void onThemeChanged() {
        this.this$0.updateRippleColor();
    }

    public void onOverlayChanged() {
        this.this$0.updateRippleColor();
    }

    public void onConfigChanged(@Nullable Configuration configuration) {
        WiredChargingRippleController wiredChargingRippleController = this.this$0;
        wiredChargingRippleController.normalizedPortPosX = wiredChargingRippleController.context.getResources().getFloat(R$dimen.physical_charger_port_location_normalized_x);
        WiredChargingRippleController wiredChargingRippleController2 = this.this$0;
        wiredChargingRippleController2.normalizedPortPosY = wiredChargingRippleController2.context.getResources().getFloat(R$dimen.physical_charger_port_location_normalized_y);
    }
}
