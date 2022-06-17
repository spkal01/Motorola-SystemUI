package com.android.p011wm.shell.legacysplitscreen;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTransitions$$ExternalSyntheticLambda3 */
public final /* synthetic */ class LegacySplitScreenTransitions$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ LegacySplitScreenTransitions f$0;
    public final /* synthetic */ ValueAnimator f$1;

    public /* synthetic */ LegacySplitScreenTransitions$$ExternalSyntheticLambda3(LegacySplitScreenTransitions legacySplitScreenTransitions, ValueAnimator valueAnimator) {
        this.f$0 = legacySplitScreenTransitions;
        this.f$1 = valueAnimator;
    }

    public final void run() {
        this.f$0.lambda$startExampleResizeAnimation$4(this.f$1);
    }
}
