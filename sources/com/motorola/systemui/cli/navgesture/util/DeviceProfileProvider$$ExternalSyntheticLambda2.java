package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.hardware.display.DisplayManager;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import java.util.function.Supplier;

public final /* synthetic */ class DeviceProfileProvider$$ExternalSyntheticLambda2 implements Supplier {
    public final /* synthetic */ DisplayManager f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ DeviceProfileProvider$$ExternalSyntheticLambda2(DisplayManager displayManager, Context context) {
        this.f$0 = displayManager;
        this.f$1 = context;
    }

    public final Object get() {
        return this.f$0.getDisplay(SecondaryDisplay.INSTANCE.lambda$get$0(this.f$1).getDisplayId());
    }
}
