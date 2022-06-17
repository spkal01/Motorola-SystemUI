package com.android.systemui.doze;

import java.util.function.Consumer;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda6 implements Consumer {
    public final /* synthetic */ DozeTriggers f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Consumer f$3;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda6(DozeTriggers dozeTriggers, long j, int i, Consumer consumer) {
        this.f$0 = dozeTriggers;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = consumer;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$proximityCheckThenCall$0(this.f$1, this.f$2, this.f$3, (Boolean) obj);
    }
}
