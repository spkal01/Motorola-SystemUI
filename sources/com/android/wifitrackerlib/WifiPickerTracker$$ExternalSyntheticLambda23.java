package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda23 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda23 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda23();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda23() {
    }

    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$updateWifiConfigurations$22((WifiConfiguration) obj);
    }
}
