package com.android.systemui.doze;

import java.util.function.Consumer;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ DozeTriggers f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda8(DozeTriggers dozeTriggers, boolean z, Runnable runnable, int i) {
        this.f$0 = dozeTriggers;
        this.f$1 = z;
        this.f$2 = runnable;
        this.f$3 = i;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$requestPulse$4(this.f$1, this.f$2, this.f$3, (Boolean) obj);
    }
}
