package com.android.systemui.keyguard;

import com.android.systemui.keyguard.WakefulnessLifecycle;
import java.util.function.Consumer;

public final /* synthetic */ class WakefulnessLifecycle$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ WakefulnessLifecycle$$ExternalSyntheticLambda0 INSTANCE = new WakefulnessLifecycle$$ExternalSyntheticLambda0();

    private /* synthetic */ WakefulnessLifecycle$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        ((WakefulnessLifecycle.Observer) obj).onFinishedGoingToSleep();
    }
}
