package com.android.systemui.keyguard;

import com.android.systemui.keyguard.ScreenLifecycle;
import java.util.function.Consumer;

public final /* synthetic */ class ScreenLifecycle$$ExternalSyntheticLambda4 implements Consumer {
    public static final /* synthetic */ ScreenLifecycle$$ExternalSyntheticLambda4 INSTANCE = new ScreenLifecycle$$ExternalSyntheticLambda4();

    private /* synthetic */ ScreenLifecycle$$ExternalSyntheticLambda4() {
    }

    public final void accept(Object obj) {
        ((ScreenLifecycle.Observer) obj).onScreenTurningOff();
    }
}
