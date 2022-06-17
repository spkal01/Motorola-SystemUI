package com.android.launcher3.icons;

import android.content.res.Resources;
import java.util.function.IntFunction;

public final /* synthetic */ class ClockDrawableWrapper$$ExternalSyntheticLambda0 implements IntFunction {
    public final /* synthetic */ Resources f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ClockDrawableWrapper$$ExternalSyntheticLambda0(Resources resources, int i) {
        this.f$0 = resources;
        this.f$1 = i;
    }

    public final Object apply(int i) {
        return this.f$0.getDrawableForDensity(i, this.f$1);
    }
}
