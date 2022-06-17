package com.android.systemui.biometrics;

public final /* synthetic */ class UdfpsEnrollView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ UdfpsEnrollView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ UdfpsEnrollView$$ExternalSyntheticLambda1(UdfpsEnrollView udfpsEnrollView, int i, int i2) {
        this.f$0 = udfpsEnrollView;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$onEnrollmentProgress$0(this.f$1, this.f$2);
    }
}
