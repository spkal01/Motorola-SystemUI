package com.motorola.systemui.cli.navgesture.animation;

import android.os.CancellationSignal;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;

public final /* synthetic */ class GestureRecentsAppTransitionManager$$ExternalSyntheticLambda0 implements CancellationSignal.OnCancelListener {
    public final /* synthetic */ GestureRecentsAppTransitionManager f$0;
    public final /* synthetic */ RemoteAnimationProvider f$1;

    public /* synthetic */ GestureRecentsAppTransitionManager$$ExternalSyntheticLambda0(GestureRecentsAppTransitionManager gestureRecentsAppTransitionManager, RemoteAnimationProvider remoteAnimationProvider) {
        this.f$0 = gestureRecentsAppTransitionManager;
        this.f$1 = remoteAnimationProvider;
    }

    public final void onCancel() {
        this.f$0.lambda$setRemoteAnimationProvider$0(this.f$1);
    }
}
