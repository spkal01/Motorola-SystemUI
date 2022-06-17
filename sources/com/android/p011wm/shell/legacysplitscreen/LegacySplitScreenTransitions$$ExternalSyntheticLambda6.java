package com.android.p011wm.shell.legacysplitscreen;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTransitions$$ExternalSyntheticLambda6 */
public final /* synthetic */ class LegacySplitScreenTransitions$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ LegacySplitScreenTransitions f$0;
    public final /* synthetic */ SurfaceControl.Transaction f$1;
    public final /* synthetic */ SurfaceControl f$2;
    public final /* synthetic */ Rect f$3;
    public final /* synthetic */ ValueAnimator f$4;

    public /* synthetic */ LegacySplitScreenTransitions$$ExternalSyntheticLambda6(LegacySplitScreenTransitions legacySplitScreenTransitions, SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, ValueAnimator valueAnimator) {
        this.f$0 = legacySplitScreenTransitions;
        this.f$1 = transaction;
        this.f$2 = surfaceControl;
        this.f$3 = rect;
        this.f$4 = valueAnimator;
    }

    public final void run() {
        this.f$0.lambda$startExampleResizeAnimation$5(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
