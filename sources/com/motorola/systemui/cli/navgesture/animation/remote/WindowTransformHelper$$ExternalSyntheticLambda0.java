package com.motorola.systemui.cli.navgesture.animation.remote;

import android.animation.TimeInterpolator;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda0 implements TimeInterpolator {
    public final /* synthetic */ float f$0;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda0(float f) {
        this.f$0 = f;
    }

    public final float getInterpolation(float f) {
        return WindowTransformHelper.lambda$animateToProgressInternal$13(this.f$0, f);
    }
}
