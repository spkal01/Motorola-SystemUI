package com.android.systemui.appops;

public final /* synthetic */ class AppOpsControllerImpl$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ AppOpsControllerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ AppOpsControllerImpl$$ExternalSyntheticLambda2(AppOpsControllerImpl appOpsControllerImpl, int i, boolean z) {
        this.f$0 = appOpsControllerImpl;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$onSensorBlockedChanged$2(this.f$1, this.f$2);
    }
}
