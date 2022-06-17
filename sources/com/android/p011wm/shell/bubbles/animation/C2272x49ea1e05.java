package com.android.p011wm.shell.bubbles.animation;

import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;
import java.util.List;
import java.util.Set;

/* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2272x49ea1e05 implements PhysicsAnimationLayout.PhysicsAnimationController.MultiAnimationStarter {
    public final /* synthetic */ PhysicsAnimationLayout.PhysicsAnimationController f$0;
    public final /* synthetic */ Set f$1;
    public final /* synthetic */ List f$2;

    public /* synthetic */ C2272x49ea1e05(PhysicsAnimationLayout.PhysicsAnimationController physicsAnimationController, Set set, List list) {
        this.f$0 = physicsAnimationController;
        this.f$1 = set;
        this.f$2 = list;
    }

    public final void startAll(Runnable[] runnableArr) {
        this.f$0.lambda$animationsForChildrenFromIndex$1(this.f$1, this.f$2, runnableArr);
    }
}
