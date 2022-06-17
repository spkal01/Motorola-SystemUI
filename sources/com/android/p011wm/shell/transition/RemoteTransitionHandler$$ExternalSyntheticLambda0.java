package com.android.p011wm.shell.transition;

import android.os.IBinder;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.RemoteTransitionHandler$$ExternalSyntheticLambda0 */
public final /* synthetic */ class RemoteTransitionHandler$$ExternalSyntheticLambda0 implements IBinder.DeathRecipient {
    public final /* synthetic */ RemoteTransitionHandler f$0;
    public final /* synthetic */ IBinder f$1;
    public final /* synthetic */ Transitions.TransitionFinishCallback f$2;

    public /* synthetic */ RemoteTransitionHandler$$ExternalSyntheticLambda0(RemoteTransitionHandler remoteTransitionHandler, IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback) {
        this.f$0 = remoteTransitionHandler;
        this.f$1 = iBinder;
        this.f$2 = transitionFinishCallback;
    }

    public final void binderDied() {
        this.f$0.lambda$startAnimation$1(this.f$1, this.f$2);
    }
}
