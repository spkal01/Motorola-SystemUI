package com.android.keyguard;

import com.android.systemui.biometrics.UdfpsView;
import java.util.function.Consumer;

public final /* synthetic */ class KeyguardUpdateMonitor$14$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ KeyguardUpdateMonitor$14$$ExternalSyntheticLambda1 INSTANCE = new KeyguardUpdateMonitor$14$$ExternalSyntheticLambda1();

    private /* synthetic */ KeyguardUpdateMonitor$14$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        ((UdfpsView.Callback) obj).onSuccess();
    }
}
