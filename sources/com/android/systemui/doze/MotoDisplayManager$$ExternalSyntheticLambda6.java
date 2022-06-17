package com.android.systemui.doze;

import android.os.IBinder;

public final /* synthetic */ class MotoDisplayManager$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ MotoDisplayManager f$0;
    public final /* synthetic */ IBinder f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MotoDisplayManager$$ExternalSyntheticLambda6(MotoDisplayManager motoDisplayManager, IBinder iBinder, boolean z, boolean z2) {
        this.f$0 = motoDisplayManager;
        this.f$1 = iBinder;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$requestUnlockInternal$2(this.f$1, this.f$2, this.f$3);
    }
}
