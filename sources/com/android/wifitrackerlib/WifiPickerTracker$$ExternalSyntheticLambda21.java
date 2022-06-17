package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda21 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda21 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda21();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda21() {
    }

    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$15((ScanResult) obj);
    }
}
