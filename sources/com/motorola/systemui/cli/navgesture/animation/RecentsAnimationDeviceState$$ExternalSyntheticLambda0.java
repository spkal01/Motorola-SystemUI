package com.motorola.systemui.cli.navgesture.animation;

import com.android.systemui.shared.system.SystemGestureExclusionListenerCompat;

public final /* synthetic */ class RecentsAnimationDeviceState$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SystemGestureExclusionListenerCompat f$0;

    public /* synthetic */ RecentsAnimationDeviceState$$ExternalSyntheticLambda0(SystemGestureExclusionListenerCompat systemGestureExclusionListenerCompat) {
        this.f$0 = systemGestureExclusionListenerCompat;
    }

    public final void run() {
        this.f$0.unregister();
    }
}
