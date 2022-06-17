package com.motorola.systemui.cli.navgesture.animation.remote;

import java.util.function.Consumer;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ RecentsAnimationCallbacks f$0;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda5(RecentsAnimationCallbacks recentsAnimationCallbacks) {
        this.f$0 = recentsAnimationCallbacks;
    }

    public final void accept(Object obj) {
        this.f$0.onAnimationFinished((RecentsAnimationTargetSetController) obj);
    }
}
