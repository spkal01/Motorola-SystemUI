package com.motorola.systemui.cli.navgesture;

import com.motorola.systemui.cli.navgesture.LauncherActivityControllerHelper;
import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import com.motorola.systemui.cli.navgesture.states.LauncherState;

public final /* synthetic */ class LauncherActivityControllerHelper$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AbstractRecentGestureLauncher f$0;
    public final /* synthetic */ AnimatorPlaybackController f$1;
    public final /* synthetic */ LauncherState f$2;
    public final /* synthetic */ LauncherState f$3;

    public /* synthetic */ LauncherActivityControllerHelper$1$$ExternalSyntheticLambda0(AbstractRecentGestureLauncher abstractRecentGestureLauncher, AnimatorPlaybackController animatorPlaybackController, LauncherState launcherState, LauncherState launcherState2) {
        this.f$0 = abstractRecentGestureLauncher;
        this.f$1 = animatorPlaybackController;
        this.f$2 = launcherState;
        this.f$3 = launcherState2;
    }

    public final void run() {
        LauncherActivityControllerHelper.C26881.lambda$createActivityController$0(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
