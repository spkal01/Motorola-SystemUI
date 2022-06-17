package com.android.p011wm.shell.bubbles.animation;

import java.util.List;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda7 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ StackAnimationController f$0;
    public final /* synthetic */ List f$1;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda7(StackAnimationController stackAnimationController, List list) {
        this.f$0 = stackAnimationController;
        this.f$1 = list;
    }

    public final void run() {
        this.f$0.lambda$animateReorder$3(this.f$1);
    }
}
