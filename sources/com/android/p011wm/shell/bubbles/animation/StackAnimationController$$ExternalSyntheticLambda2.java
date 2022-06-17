package com.android.p011wm.shell.bubbles.animation;

import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda2 implements PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator {
    public final /* synthetic */ StackAnimationController f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda2(StackAnimationController stackAnimationController, float f) {
        this.f$0 = stackAnimationController;
        this.f$1 = f;
    }

    public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
        this.f$0.lambda$animateStackDismissal$1(this.f$1, i, physicsPropertyAnimator);
    }
}
