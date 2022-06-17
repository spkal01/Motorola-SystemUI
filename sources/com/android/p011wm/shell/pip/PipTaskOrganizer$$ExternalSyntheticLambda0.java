package com.android.p011wm.shell.pip;

import android.animation.ValueAnimator;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PipTaskOrganizer f$0;
    public final /* synthetic */ SurfaceControl f$1;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda0(PipTaskOrganizer pipTaskOrganizer, SurfaceControl surfaceControl) {
        this.f$0 = pipTaskOrganizer;
        this.f$1 = surfaceControl;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$fadeOutAndRemoveOverlay$8(this.f$1, valueAnimator);
    }
}
