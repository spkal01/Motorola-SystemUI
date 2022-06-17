package com.android.keyguard;

import android.telephony.PinResult;
import com.android.keyguard.KeyguardSimPukViewController;

public final /* synthetic */ class KeyguardSimPukViewController$3$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ KeyguardSimPukViewController.C06233 f$0;
    public final /* synthetic */ PinResult f$1;

    public /* synthetic */ KeyguardSimPukViewController$3$$ExternalSyntheticLambda0(KeyguardSimPukViewController.C06233 r1, PinResult pinResult) {
        this.f$0 = r1;
        this.f$1 = pinResult;
    }

    public final void run() {
        this.f$0.lambda$onSimLockChangedResponse$0(this.f$1);
    }
}
