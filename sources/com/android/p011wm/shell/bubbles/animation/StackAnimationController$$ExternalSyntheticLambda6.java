package com.android.p011wm.shell.bubbles.animation;

import android.view.View;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda6 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ StackAnimationController f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda6(StackAnimationController stackAnimationController, Runnable runnable, View view, Runnable runnable2) {
        this.f$0 = stackAnimationController;
        this.f$1 = runnable;
        this.f$2 = view;
        this.f$3 = runnable2;
    }

    public final void run() {
        this.f$0.lambda$animateToFrontThenUpdateIcons$4(this.f$1, this.f$2, this.f$3);
    }
}
