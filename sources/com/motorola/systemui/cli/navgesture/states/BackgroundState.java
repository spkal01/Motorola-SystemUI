package com.motorola.systemui.cli.navgesture.states;

import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;

public class BackgroundState extends OverviewState {
    public float getOverviewFullscreenProgress() {
        return 1.0f;
    }

    public void onStateEnabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }
}
