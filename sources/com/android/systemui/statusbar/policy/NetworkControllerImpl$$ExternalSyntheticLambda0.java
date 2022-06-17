package com.android.systemui.statusbar.policy;

import android.telephony.TelephonyCallback;

public final /* synthetic */ class NetworkControllerImpl$$ExternalSyntheticLambda0 implements TelephonyCallback.ActiveDataSubscriptionIdListener {
    public final /* synthetic */ NetworkControllerImpl f$0;

    public /* synthetic */ NetworkControllerImpl$$ExternalSyntheticLambda0(NetworkControllerImpl networkControllerImpl) {
        this.f$0 = networkControllerImpl;
    }

    public final void onActiveDataSubscriptionIdChanged(int i) {
        this.f$0.lambda$new$1(i);
    }
}
