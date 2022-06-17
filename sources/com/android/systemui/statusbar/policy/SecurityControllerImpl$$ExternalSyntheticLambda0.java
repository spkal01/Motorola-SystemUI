package com.android.systemui.statusbar.policy;

public final /* synthetic */ class SecurityControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SecurityControllerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SecurityControllerImpl$$ExternalSyntheticLambda0(SecurityControllerImpl securityControllerImpl, int i) {
        this.f$0 = securityControllerImpl;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$refreshCACerts$0(this.f$1);
    }
}
