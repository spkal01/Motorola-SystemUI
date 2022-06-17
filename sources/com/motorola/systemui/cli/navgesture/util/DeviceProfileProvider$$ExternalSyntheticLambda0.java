package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import java.util.function.Consumer;

public final /* synthetic */ class DeviceProfileProvider$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ DeviceProfileProvider f$0;

    public /* synthetic */ DeviceProfileProvider$$ExternalSyntheticLambda0(DeviceProfileProvider deviceProfileProvider) {
        this.f$0 = deviceProfileProvider;
    }

    public final void accept(Object obj) {
        this.f$0.onConfigChanged((Context) obj);
    }
}
