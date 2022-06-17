package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import java.util.function.Consumer;

public final /* synthetic */ class LocationControllerImpl$H$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ LocationControllerImpl.C2031H f$0;

    public /* synthetic */ LocationControllerImpl$H$$ExternalSyntheticLambda0(LocationControllerImpl.C2031H h) {
        this.f$0 = h;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$locationActiveChanged$0((LocationController.LocationChangeCallback) obj);
    }
}
