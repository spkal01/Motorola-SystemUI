package com.motorola.systemui.cli.navgesture.states;

import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;

public class OverviewState extends NormalState {
    public float getOverviewFullscreenProgress() {
        return 0.0f;
    }

    public void onStateEnabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }

    public boolean overview() {
        return true;
    }

    public int transitionDuration() {
        return 250;
    }

    public void onBackPressed(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        if (!abstractRecentGestureLauncher.getOverviewPanel().onBackPressed()) {
            super.onBackPressed(abstractRecentGestureLauncher);
        }
    }
}
