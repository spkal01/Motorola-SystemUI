package com.android.p011wm.shell.bubbles.animation;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2275x4b8fea75 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ C2275x4b8fea75(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.run();
    }
}
