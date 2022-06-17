package com.motorola.systemui.cli.navgesture;

import android.view.animation.Interpolator;
import com.motorola.systemui.cli.navgesture.util.Utilities;

public final /* synthetic */ class Interpolators$$ExternalSyntheticLambda1 implements Interpolator {
    public final /* synthetic */ Interpolator f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ Interpolators$$ExternalSyntheticLambda1(Interpolator interpolator, float f, float f2) {
        this.f$0 = interpolator;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final float getInterpolation(float f) {
        return Utilities.mapRange(this.f$0.getInterpolation(f), this.f$1, this.f$2);
    }
}
