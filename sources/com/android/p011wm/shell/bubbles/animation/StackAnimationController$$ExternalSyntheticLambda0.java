package com.android.p011wm.shell.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringForce;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ StackAnimationController f$0;
    public final /* synthetic */ DynamicAnimation.ViewProperty f$1;
    public final /* synthetic */ SpringForce f$2;
    public final /* synthetic */ Float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda0(StackAnimationController stackAnimationController, DynamicAnimation.ViewProperty viewProperty, SpringForce springForce, Float f, float f2, float f3) {
        this.f$0 = stackAnimationController;
        this.f$1 = viewProperty;
        this.f$2 = springForce;
        this.f$3 = f;
        this.f$4 = f2;
        this.f$5 = f3;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$flingThenSpringFirstBubbleWithStackFollowing$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dynamicAnimation, z, f, f2);
    }
}
