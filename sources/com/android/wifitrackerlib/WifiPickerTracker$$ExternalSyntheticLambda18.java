package com.android.wifitrackerlib;

import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda18 implements Predicate {
    public final /* synthetic */ WifiPickerTracker f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda18(WifiPickerTracker wifiPickerTracker, String str) {
        this.f$0 = wifiPickerTracker;
        this.f$1 = str;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$applyAdminRestrictions$2(this.f$1, (WifiEntry) obj);
    }
}
