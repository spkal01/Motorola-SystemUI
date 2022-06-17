package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import java.util.function.Function;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda8 implements Function {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda8 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda8();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda8() {
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(((WifiConfiguration) obj).networkId);
    }
}
