package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda22 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda22 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda22();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda22() {
    }

    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$updateStandardWifiEntryScans$12((ScanResult) obj);
    }
}
