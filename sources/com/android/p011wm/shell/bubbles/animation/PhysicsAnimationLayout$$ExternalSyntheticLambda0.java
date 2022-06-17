package com.android.p011wm.shell.bubbles.animation;

import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;

/* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$$ExternalSyntheticLambda0 */
public final /* synthetic */ class PhysicsAnimationLayout$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationUpdateListener {
    public final /* synthetic */ PhysicsAnimationLayout f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ DynamicAnimation.ViewProperty f$2;

    public /* synthetic */ PhysicsAnimationLayout$$ExternalSyntheticLambda0(PhysicsAnimationLayout physicsAnimationLayout, View view, DynamicAnimation.ViewProperty viewProperty) {
        this.f$0 = physicsAnimationLayout;
        this.f$1 = view;
        this.f$2 = viewProperty;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.f$0.lambda$setUpAnimationForChild$1(this.f$1, this.f$2, dynamicAnimation, f, f2);
    }
}
