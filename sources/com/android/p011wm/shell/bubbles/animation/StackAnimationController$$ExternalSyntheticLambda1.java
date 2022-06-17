package com.android.p011wm.shell.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;

/* renamed from: com.android.wm.shell.bubbles.animation.StackAnimationController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class StackAnimationController$$ExternalSyntheticLambda1 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ StackAnimationController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable[] f$2;

    public /* synthetic */ StackAnimationController$$ExternalSyntheticLambda1(StackAnimationController stackAnimationController, boolean z, Runnable[] runnableArr) {
        this.f$0 = stackAnimationController;
        this.f$1 = z;
        this.f$2 = runnableArr;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$springFirstBubbleWithStackFollowing$2(this.f$1, this.f$2, dynamicAnimation, z, f, f2);
    }
}
