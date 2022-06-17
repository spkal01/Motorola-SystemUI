package com.android.p011wm.shell.bubbles.animation;

import android.view.View;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda4 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ View f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda4(View view, Runnable runnable) {
        this.f$0 = view;
        this.f$1 = runnable;
    }

    public final void run() {
        StackAnimationController.lambda$moveToFinalIndex$5(this.f$0, this.f$1);
    }
}
