package com.android.systemui.telephony;

import android.telephony.TelephonyCallback;
import java.util.function.Consumer;

public final /* synthetic */ class TelephonyCallback$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ TelephonyCallback$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((TelephonyCallback.ActiveDataSubscriptionIdListener) obj).onActiveDataSubscriptionIdChanged(this.f$0);
    }
}
