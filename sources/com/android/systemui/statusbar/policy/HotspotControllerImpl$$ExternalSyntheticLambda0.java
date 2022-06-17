package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.HotspotController;

public final /* synthetic */ class HotspotControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ HotspotControllerImpl f$0;
    public final /* synthetic */ HotspotController.Callback f$1;

    public /* synthetic */ HotspotControllerImpl$$ExternalSyntheticLambda0(HotspotControllerImpl hotspotControllerImpl, HotspotController.Callback callback) {
        this.f$0 = hotspotControllerImpl;
        this.f$1 = callback;
    }

    public final void run() {
        this.f$0.lambda$addCallback$0(this.f$1);
    }
}
