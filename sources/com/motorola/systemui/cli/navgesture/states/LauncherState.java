package com.motorola.systemui.cli.navgesture.states;

import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;
import com.motorola.systemui.cli.navgesture.util.ScaleTranslation;

public interface LauncherState {
    boolean disableRestore() {
        return true;
    }

    float getOverviewFullscreenProgress();

    void onBackPressed(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
    }

    void onStateDisabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher);

    void onStateEnabled(AbstractRecentGestureLauncher abstractRecentGestureLauncher);

    void onStateTransitionEnd(AbstractRecentGestureLauncher abstractRecentGestureLauncher);

    boolean overview();

    int transitionDuration();

    ScaleTranslation getOverviewScaleTranslation(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        return abstractRecentGestureLauncher.getOverviewPanel().getScaleTranslation(this);
    }

    String toShortString() {
        return getClass().getSimpleName();
    }
}
