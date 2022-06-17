package com.android.keyguard.clock;

import com.android.keyguard.clock.ClockManager;
import com.android.systemui.plugins.ClockPlugin;

public final /* synthetic */ class ClockManager$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ClockManager.ClockChangedListener f$0;
    public final /* synthetic */ ClockPlugin f$1;

    public /* synthetic */ ClockManager$$ExternalSyntheticLambda1(ClockManager.ClockChangedListener clockChangedListener, ClockPlugin clockPlugin) {
        this.f$0 = clockChangedListener;
        this.f$1 = clockPlugin;
    }

    public final void run() {
        ClockManager.lambda$reload$2(this.f$0, this.f$1);
    }
}
