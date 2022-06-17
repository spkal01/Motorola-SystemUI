package com.motorola.systemui.cli.navgesture.animation;

import com.motorola.systemui.cli.navgesture.SysUINavigationMode;

public final /* synthetic */ class RecentsAnimationDeviceState$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ RecentsAnimationDeviceState f$0;
    public final /* synthetic */ SysUINavigationMode.NavigationModeChangeListener f$1;

    public /* synthetic */ RecentsAnimationDeviceState$$ExternalSyntheticLambda5(RecentsAnimationDeviceState recentsAnimationDeviceState, SysUINavigationMode.NavigationModeChangeListener navigationModeChangeListener) {
        this.f$0 = recentsAnimationDeviceState;
        this.f$1 = navigationModeChangeListener;
    }

    public final void run() {
        this.f$0.lambda$addNavigationModeChangedCallback$4(this.f$1);
    }
}
