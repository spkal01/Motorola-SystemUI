package com.android.wifitrackerlib;

import java.util.function.Function;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda10 implements Function {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda10 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda10();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda10() {
    }

    public final Object apply(Object obj) {
        return ((StandardWifiEntry) obj).getStandardWifiEntryKey().getScanResultKey();
    }
}
