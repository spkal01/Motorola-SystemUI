package com.android.wifitrackerlib;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda6 implements Consumer {
    public final /* synthetic */ Set f$0;
    public final /* synthetic */ Map f$1;

    public /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda6(Set set, Map map) {
        this.f$0 = set;
        this.f$1 = map;
    }

    public final void accept(Object obj) {
        WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$16(this.f$0, this.f$1, (StandardWifiEntry) obj);
    }
}
