package com.android.systemui.keyguard;

import android.os.UserHandle;
import android.os.UserManager;

public final /* synthetic */ class KeyguardViewMediator$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ KeyguardViewMediator f$0;
    public final /* synthetic */ UserManager f$1;
    public final /* synthetic */ UserHandle f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ KeyguardViewMediator$$ExternalSyntheticLambda6(KeyguardViewMediator keyguardViewMediator, UserManager userManager, UserHandle userHandle, int i) {
        this.f$0 = keyguardViewMediator;
        this.f$1 = userManager;
        this.f$2 = userHandle;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$sendUserPresentBroadcast$2(this.f$1, this.f$2, this.f$3);
    }
}
