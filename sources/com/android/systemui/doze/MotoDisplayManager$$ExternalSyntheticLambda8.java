package com.android.systemui.doze;

import android.os.Bundle;

public final /* synthetic */ class MotoDisplayManager$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ MotoDisplayManager f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ Bundle f$5;

    public /* synthetic */ MotoDisplayManager$$ExternalSyntheticLambda8(MotoDisplayManager motoDisplayManager, String str, boolean z, String str2, String str3, Bundle bundle) {
        this.f$0 = motoDisplayManager;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = str2;
        this.f$4 = str3;
        this.f$5 = bundle;
    }

    public final void run() {
        this.f$0.lambda$notifyEvent$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
