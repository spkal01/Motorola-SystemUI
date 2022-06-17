package com.motorola.systemui.cli.navgesture.animation.remote;

import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import java.util.function.Consumer;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda30 implements Consumer {
    public final /* synthetic */ WindowTransformHelper f$0;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda30(WindowTransformHelper windowTransformHelper) {
        this.f$0 = windowTransformHelper;
    }

    public final void accept(Object obj) {
        this.f$0.onAnimatorPlaybackControllerCreated((AnimatorPlaybackController) obj);
    }
}
