package com.android.systemui.telephony;

import android.telephony.TelephonyCallback;
import java.util.function.Consumer;

public final /* synthetic */ class TelephonyCallback$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ TelephonyCallback$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((TelephonyCallback.CallStateListener) obj).onCallStateChanged(this.f$0);
    }
}
