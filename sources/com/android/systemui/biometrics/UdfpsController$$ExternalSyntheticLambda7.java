package com.android.systemui.biometrics;

import com.android.systemui.biometrics.UdfpsController;

public final /* synthetic */ class UdfpsController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ UdfpsController f$0;
    public final /* synthetic */ UdfpsController.PointerInfo f$1;

    public /* synthetic */ UdfpsController$$ExternalSyntheticLambda7(UdfpsController udfpsController, UdfpsController.PointerInfo pointerInfo) {
        this.f$0 = udfpsController;
        this.f$1 = pointerInfo;
    }

    public final void run() {
        this.f$0.lambda$onFingerDown$6(this.f$1);
    }
}
