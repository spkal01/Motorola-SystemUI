package com.android.keyguard;

import android.telephony.PinResult;
import com.android.keyguard.KeyguardSimPukViewController;

/* renamed from: com.android.keyguard.KeyguardSimPukViewController$CheckSimPuk$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0625x2e140b38 implements Runnable {
    public final /* synthetic */ KeyguardSimPukViewController.CheckSimPuk f$0;
    public final /* synthetic */ PinResult f$1;

    public /* synthetic */ C0625x2e140b38(KeyguardSimPukViewController.CheckSimPuk checkSimPuk, PinResult pinResult) {
        this.f$0 = checkSimPuk;
        this.f$1 = pinResult;
    }

    public final void run() {
        this.f$0.lambda$run$0(this.f$1);
    }
}
