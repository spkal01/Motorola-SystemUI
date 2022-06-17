package com.motorola.systemui.cli.navgesture.util;

import android.hardware.display.DisplayManager;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import java.util.function.Supplier;

public final /* synthetic */ class DeviceProfileProvider$$ExternalSyntheticLambda3 implements Supplier {
    public final /* synthetic */ DisplayManager f$0;
    public final /* synthetic */ SecondaryDisplay f$1;

    public /* synthetic */ DeviceProfileProvider$$ExternalSyntheticLambda3(DisplayManager displayManager, SecondaryDisplay secondaryDisplay) {
        this.f$0 = displayManager;
        this.f$1 = secondaryDisplay;
    }

    public final Object get() {
        return this.f$0.getDisplay(this.f$1.getDisplayId());
    }
}
