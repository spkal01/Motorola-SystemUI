package com.android.p011wm.shell.pip;

import com.android.p011wm.shell.pip.PipAnimationController;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda4 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PipAnimationController.PipTransitionAnimator f$0;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda4(PipAnimationController.PipTransitionAnimator pipTransitionAnimator) {
        this.f$0 = pipTransitionAnimator;
    }

    public final void run() {
        this.f$0.clearContentOverlay();
    }
}
