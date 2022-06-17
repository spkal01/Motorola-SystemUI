package com.android.systemui.biometrics;

public final /* synthetic */ class UdfpsEnrollView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ UdfpsEnrollDrawable f$0;

    public /* synthetic */ UdfpsEnrollView$$ExternalSyntheticLambda0(UdfpsEnrollDrawable udfpsEnrollDrawable) {
        this.f$0 = udfpsEnrollDrawable;
    }

    public final void run() {
        this.f$0.onLastStepAcquired();
    }
}
