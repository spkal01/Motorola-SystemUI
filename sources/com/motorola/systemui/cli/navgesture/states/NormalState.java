package com.motorola.systemui.cli.navgesture.states;

import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;

public class NormalState implements LauncherState {
    public float getOverviewFullscreenProgress() {
        return 0.0f;
    }

    public void onStateDisabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }

    public void onStateEnabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }

    public void onStateTransitionEnd(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }

    public boolean overview() {
        return false;
    }

    public int transitionDuration() {
        return 0;
    }

    public void onBackPressed(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        if (this != StateManager.NORMAL) {
            StateManager stateManager = abstractRecentGestureLauncher.getStateManager();
            stateManager.goToState(stateManager.getLastState());
        }
    }
}
