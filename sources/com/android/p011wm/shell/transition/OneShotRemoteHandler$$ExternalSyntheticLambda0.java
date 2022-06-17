package com.android.p011wm.shell.transition;

import android.os.IBinder;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.OneShotRemoteHandler$$ExternalSyntheticLambda0 */
public final /* synthetic */ class OneShotRemoteHandler$$ExternalSyntheticLambda0 implements IBinder.DeathRecipient {
    public final /* synthetic */ OneShotRemoteHandler f$0;
    public final /* synthetic */ Transitions.TransitionFinishCallback f$1;

    public /* synthetic */ OneShotRemoteHandler$$ExternalSyntheticLambda0(OneShotRemoteHandler oneShotRemoteHandler, Transitions.TransitionFinishCallback transitionFinishCallback) {
        this.f$0 = oneShotRemoteHandler;
        this.f$1 = transitionFinishCallback;
    }

    public final void binderDied() {
        this.f$0.lambda$startAnimation$1(this.f$1);
    }
}
