package com.android.keyguard;

import com.android.keyguard.clock.ClockManager;
import com.android.systemui.plugins.ClockPlugin;

public final /* synthetic */ class KeyguardClockSwitchController$$ExternalSyntheticLambda1 implements ClockManager.ClockChangedListener {
    public final /* synthetic */ KeyguardClockSwitchController f$0;

    public /* synthetic */ KeyguardClockSwitchController$$ExternalSyntheticLambda1(KeyguardClockSwitchController keyguardClockSwitchController) {
        this.f$0 = keyguardClockSwitchController;
    }

    public final void onClockChanged(ClockPlugin clockPlugin) {
        this.f$0.setClockPlugin(clockPlugin);
    }
}
