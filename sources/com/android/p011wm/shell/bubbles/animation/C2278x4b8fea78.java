package com.android.p011wm.shell.bubbles.animation;

import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;

/* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C2278x4b8fea78 implements Runnable {
    public final /* synthetic */ PhysicsAnimationLayout.PhysicsPropertyAnimator f$0;
    public final /* synthetic */ SpringAnimation f$1;
    public final /* synthetic */ SpringAnimation f$2;

    public /* synthetic */ C2278x4b8fea78(PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator, SpringAnimation springAnimation, SpringAnimation springAnimation2) {
        this.f$0 = physicsPropertyAnimator;
        this.f$1 = springAnimation;
        this.f$2 = springAnimation2;
    }

    public final void run() {
        this.f$0.lambda$start$1(this.f$1, this.f$2);
    }
}
