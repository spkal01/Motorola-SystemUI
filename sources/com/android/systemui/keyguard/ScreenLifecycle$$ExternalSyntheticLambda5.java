package com.android.systemui.keyguard;

import com.android.systemui.keyguard.ScreenLifecycle;
import java.util.function.Consumer;

public final /* synthetic */ class ScreenLifecycle$$ExternalSyntheticLambda5 implements Consumer {
    public static final /* synthetic */ ScreenLifecycle$$ExternalSyntheticLambda5 INSTANCE = new ScreenLifecycle$$ExternalSyntheticLambda5();

    private /* synthetic */ ScreenLifecycle$$ExternalSyntheticLambda5() {
    }

    public final void accept(Object obj) {
        ((ScreenLifecycle.Observer) obj).onScreenTurningOn();
    }
}
