package com.android.keyguard;

import java.util.function.Predicate;

public final /* synthetic */ class KeyguardUpdateMonitor$$ExternalSyntheticLambda16 implements Predicate {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ KeyguardUpdateMonitor$$ExternalSyntheticLambda16(String str, String str2) {
        this.f$0 = str;
        this.f$1 = str2;
    }

    public final boolean test(Object obj) {
        return ((String) obj).equals(this.f$0 + this.f$1);
    }
}
