package com.android.keyguard;

import com.android.systemui.biometrics.UdfpsView;
import java.util.function.Consumer;

public final /* synthetic */ class KeyguardUpdateMonitor$14$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ KeyguardUpdateMonitor$14$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((UdfpsView.Callback) obj).onAcquired(this.f$0);
    }
}
