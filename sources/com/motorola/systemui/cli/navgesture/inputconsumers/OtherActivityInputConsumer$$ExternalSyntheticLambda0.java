package com.motorola.systemui.cli.navgesture.inputconsumers;

import com.motorola.systemui.cli.navgesture.animation.remote.WindowTransformHelper;
import com.motorola.systemui.cli.navgesture.util.MotionPauseDetector;

public final /* synthetic */ class OtherActivityInputConsumer$$ExternalSyntheticLambda0 implements MotionPauseDetector.OnMotionPauseListener {
    public final /* synthetic */ WindowTransformHelper f$0;

    public /* synthetic */ OtherActivityInputConsumer$$ExternalSyntheticLambda0(WindowTransformHelper windowTransformHelper) {
        this.f$0 = windowTransformHelper;
    }

    public final void onMotionPauseChanged(boolean z) {
        this.f$0.onMotionPauseChanged(z);
    }
}
