package com.motorola.systemui.cli.navgesture.animation.remote;

import android.graphics.PointF;
import android.view.animation.Interpolator;
import com.motorola.systemui.cli.navgesture.animation.GestureState;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ WindowTransformHelper f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ Interpolator f$4;
    public final /* synthetic */ GestureState.GestureEndTarget f$5;
    public final /* synthetic */ PointF f$6;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda26(WindowTransformHelper windowTransformHelper, float f, float f2, long j, Interpolator interpolator, GestureState.GestureEndTarget gestureEndTarget, PointF pointF) {
        this.f$0 = windowTransformHelper;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = j;
        this.f$4 = interpolator;
        this.f$5 = gestureEndTarget;
        this.f$6 = pointF;
    }

    public final void run() {
        this.f$0.lambda$animateToProgress$10(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
