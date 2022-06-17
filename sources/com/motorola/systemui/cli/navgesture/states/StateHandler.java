package com.motorola.systemui.cli.navgesture.states;

import com.motorola.systemui.cli.navgesture.animation.AnimatorSetBuilder;
import com.motorola.systemui.cli.navgesture.states.StateManager;

public interface StateHandler {
    void setState(LauncherState launcherState);

    void setStateWithAnimation(LauncherState launcherState, AnimatorSetBuilder animatorSetBuilder, StateManager.AnimationConfig animationConfig);
}
