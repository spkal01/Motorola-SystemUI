package com.android.keyguard.clock;

import com.android.systemui.plugins.ClockPlugin;
import java.util.function.Supplier;

public final /* synthetic */ class ClockManager$AvailableClocks$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ ClockPlugin f$0;

    public /* synthetic */ ClockManager$AvailableClocks$$ExternalSyntheticLambda1(ClockPlugin clockPlugin) {
        this.f$0 = clockPlugin;
    }

    public final Object get() {
        return this.f$0.getThumbnail();
    }
}
