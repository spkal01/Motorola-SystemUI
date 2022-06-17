package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;

public final /* synthetic */ class StandardWifiEntry$$ExternalSyntheticLambda4 implements ToIntFunction {
    public static final /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda4 INSTANCE = new StandardWifiEntry$$ExternalSyntheticLambda4();

    private /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda4() {
    }

    public final int applyAsInt(Object obj) {
        return ((ScanResult) obj).level;
    }
}
