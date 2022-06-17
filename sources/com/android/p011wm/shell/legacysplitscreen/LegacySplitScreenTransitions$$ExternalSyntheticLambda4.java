package com.android.p011wm.shell.legacysplitscreen;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTransitions$$ExternalSyntheticLambda4 */
public final /* synthetic */ class LegacySplitScreenTransitions$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ LegacySplitScreenTransitions f$0;
    public final /* synthetic */ ValueAnimator f$1;

    public /* synthetic */ LegacySplitScreenTransitions$$ExternalSyntheticLambda4(LegacySplitScreenTransitions legacySplitScreenTransitions, ValueAnimator valueAnimator) {
        this.f$0 = legacySplitScreenTransitions;
        this.f$1 = valueAnimator;
    }

    public final void run() {
        this.f$0.lambda$startExampleAnimation$1(this.f$1);
    }
}
