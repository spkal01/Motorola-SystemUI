package com.android.p011wm.shell.splitscreen;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenTransitions$$ExternalSyntheticLambda3 */
public final /* synthetic */ class SplitScreenTransitions$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SplitScreenTransitions f$0;
    public final /* synthetic */ ValueAnimator f$1;

    public /* synthetic */ SplitScreenTransitions$$ExternalSyntheticLambda3(SplitScreenTransitions splitScreenTransitions, ValueAnimator valueAnimator) {
        this.f$0 = splitScreenTransitions;
        this.f$1 = valueAnimator;
    }

    public final void run() {
        this.f$0.lambda$startExampleResizeAnimation$5(this.f$1);
    }
}
