package com.motorola.systemui.cli.navgesture.animation.remote;

import android.view.InputEvent;
import com.android.systemui.shared.system.InputConsumerController;

public final /* synthetic */ class RecentsAnimationTargetSetController$$ExternalSyntheticLambda0 implements InputConsumerController.InputListener {
    public final /* synthetic */ RecentsAnimationTargetSetController f$0;

    public /* synthetic */ RecentsAnimationTargetSetController$$ExternalSyntheticLambda0(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.f$0 = recentsAnimationTargetSetController;
    }

    public final boolean onInputEvent(InputEvent inputEvent) {
        return this.f$0.onInputConsumerEvent(inputEvent);
    }
}
