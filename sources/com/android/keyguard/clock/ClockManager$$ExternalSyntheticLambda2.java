package com.android.keyguard.clock;

import com.android.keyguard.clock.ClockManager;
import java.util.function.BiConsumer;

public final /* synthetic */ class ClockManager$$ExternalSyntheticLambda2 implements BiConsumer {
    public final /* synthetic */ ClockManager f$0;

    public /* synthetic */ ClockManager$$ExternalSyntheticLambda2(ClockManager clockManager) {
        this.f$0 = clockManager;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.lambda$reload$3((ClockManager.ClockChangedListener) obj, (ClockManager.AvailableClocks) obj2);
    }
}
