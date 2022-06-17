package com.android.p011wm.shell.bubbles.animation;

import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;

/* renamed from: com.android.wm.shell.bubbles.animation.ExpandedAnimationController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class ExpandedAnimationController$$ExternalSyntheticLambda1 implements PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator {
    public static final /* synthetic */ ExpandedAnimationController$$ExternalSyntheticLambda1 INSTANCE = new ExpandedAnimationController$$ExternalSyntheticLambda1();

    private /* synthetic */ ExpandedAnimationController$$ExternalSyntheticLambda1() {
    }

    public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
        physicsPropertyAnimator.scaleX(1.0f, new Runnable[0]).scaleY(1.0f, new Runnable[0]).alpha(1.0f, new Runnable[0]);
    }
}
