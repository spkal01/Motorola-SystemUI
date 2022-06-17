package com.android.keyguard;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public final /* synthetic */ class KeyguardUpdateMonitor$$ExternalSyntheticLambda10 implements Predicate {
    public final /* synthetic */ KeyguardUpdateMonitorCallback f$0;

    public /* synthetic */ KeyguardUpdateMonitor$$ExternalSyntheticLambda10(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        this.f$0 = keyguardUpdateMonitorCallback;
    }

    public final boolean test(Object obj) {
        return KeyguardUpdateMonitor.lambda$removeCallback$14(this.f$0, (WeakReference) obj);
    }
}
