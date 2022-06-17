package com.android.systemui.appops;

public final /* synthetic */ class AppOpsControllerImpl$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AppOpsControllerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ AppOpsControllerImpl$$ExternalSyntheticLambda1(AppOpsControllerImpl appOpsControllerImpl, int i, int i2, String str, boolean z) {
        this.f$0 = appOpsControllerImpl;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = str;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$notifySuscribers$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
