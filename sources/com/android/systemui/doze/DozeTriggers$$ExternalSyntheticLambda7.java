package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine;
import java.util.function.Consumer;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda7 implements Consumer {
    public final /* synthetic */ DozeTriggers f$0;
    public final /* synthetic */ DozeMachine.State f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda7(DozeTriggers dozeTriggers, DozeMachine.State state, int i) {
        this.f$0 = dozeTriggers;
        this.f$1 = state;
        this.f$2 = i;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onWakeScreen$3(this.f$1, this.f$2, (Boolean) obj);
    }
}
