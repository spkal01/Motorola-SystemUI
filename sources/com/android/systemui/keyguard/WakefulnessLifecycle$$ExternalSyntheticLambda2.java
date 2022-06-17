package com.android.systemui.keyguard;

import com.android.systemui.keyguard.WakefulnessLifecycle;
import java.util.function.Consumer;

public final /* synthetic */ class WakefulnessLifecycle$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ WakefulnessLifecycle$$ExternalSyntheticLambda2 INSTANCE = new WakefulnessLifecycle$$ExternalSyntheticLambda2();

    private /* synthetic */ WakefulnessLifecycle$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((WakefulnessLifecycle.Observer) obj).onStartedGoingToSleep();
    }
}
