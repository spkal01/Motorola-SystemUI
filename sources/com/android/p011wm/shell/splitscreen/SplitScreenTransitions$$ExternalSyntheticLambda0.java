package com.android.p011wm.shell.splitscreen;

import android.animation.ValueAnimator;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenTransitions$$ExternalSyntheticLambda0 */
public final /* synthetic */ class SplitScreenTransitions$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SurfaceControl.Transaction f$0;
    public final /* synthetic */ SurfaceControl f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ SplitScreenTransitions$$ExternalSyntheticLambda0(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, float f, float f2) {
        this.f$0 = transaction;
        this.f$1 = surfaceControl;
        this.f$2 = f;
        this.f$3 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplitScreenTransitions.lambda$startExampleAnimation$1(this.f$0, this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}
