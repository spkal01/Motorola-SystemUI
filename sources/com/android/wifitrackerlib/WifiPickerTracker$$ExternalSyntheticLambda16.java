package com.android.wifitrackerlib;

import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda16 implements Predicate {
    public final /* synthetic */ WifiPickerTracker f$0;

    public /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda16(WifiPickerTracker wifiPickerTracker) {
        this.f$0 = wifiPickerTracker;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$applyAdminRestrictions$0((WifiEntry) obj);
    }
}
