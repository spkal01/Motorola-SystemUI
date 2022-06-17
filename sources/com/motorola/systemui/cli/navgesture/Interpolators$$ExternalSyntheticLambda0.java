package com.motorola.systemui.cli.navgesture;

import android.view.animation.Interpolator;

public final /* synthetic */ class Interpolators$$ExternalSyntheticLambda0 implements Interpolator {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ Interpolator f$2;

    public /* synthetic */ Interpolators$$ExternalSyntheticLambda0(float f, float f2, Interpolator interpolator) {
        this.f$0 = f;
        this.f$1 = f2;
        this.f$2 = interpolator;
    }

    public final float getInterpolation(float f) {
        return Interpolators.lambda$clampToProgress$2(this.f$0, this.f$1, this.f$2, f);
    }
}
