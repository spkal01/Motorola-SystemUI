package com.android.keyguard;

import com.android.systemui.biometrics.UdfpsView;
import java.util.function.Consumer;

public final /* synthetic */ class KeyguardUpdateMonitor$14$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ KeyguardUpdateMonitor$14$$ExternalSyntheticLambda2 INSTANCE = new KeyguardUpdateMonitor$14$$ExternalSyntheticLambda2();

    private /* synthetic */ KeyguardUpdateMonitor$14$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((UdfpsView.Callback) obj).onFailed();
    }
}
