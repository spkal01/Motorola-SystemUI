package com.android.systemui.biometrics;

public final /* synthetic */ class UdfpsController$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ UdfpsController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ UdfpsController$$ExternalSyntheticLambda6(UdfpsController udfpsController, int i, int i2, float f, float f2) {
        this.f$0 = udfpsController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = f;
        this.f$4 = f2;
    }

    public final void run() {
        this.f$0.lambda$onAodInterrupt$4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
