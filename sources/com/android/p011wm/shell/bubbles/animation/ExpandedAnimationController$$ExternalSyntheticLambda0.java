package com.android.p011wm.shell.bubbles.animation;

import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;

/* renamed from: com.android.wm.shell.bubbles.animation.ExpandedAnimationController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class ExpandedAnimationController$$ExternalSyntheticLambda0 implements PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator {
    public final /* synthetic */ ExpandedAnimationController f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ExpandedAnimationController$$ExternalSyntheticLambda0(ExpandedAnimationController expandedAnimationController, boolean z) {
        this.f$0 = expandedAnimationController;
        this.f$1 = z;
    }

    public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
        this.f$0.lambda$startOrUpdatePathAnimation$3(this.f$1, i, physicsPropertyAnimator);
    }
}
