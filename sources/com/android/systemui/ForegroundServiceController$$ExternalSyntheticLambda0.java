package com.android.systemui;

import com.android.systemui.appops.AppOpsController;

public final /* synthetic */ class ForegroundServiceController$$ExternalSyntheticLambda0 implements AppOpsController.Callback {
    public final /* synthetic */ ForegroundServiceController f$0;

    public /* synthetic */ ForegroundServiceController$$ExternalSyntheticLambda0(ForegroundServiceController foregroundServiceController) {
        this.f$0 = foregroundServiceController;
    }

    public final void onActiveStateChanged(int i, int i2, String str, boolean z) {
        this.f$0.lambda$new$1(i, i2, str, z);
    }
}
