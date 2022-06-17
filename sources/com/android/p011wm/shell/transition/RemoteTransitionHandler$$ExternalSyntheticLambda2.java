package com.android.p011wm.shell.transition;

import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.RemoteTransitionHandler$$ExternalSyntheticLambda2 */
public final /* synthetic */ class RemoteTransitionHandler$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ Transitions.TransitionFinishCallback f$0;

    public /* synthetic */ RemoteTransitionHandler$$ExternalSyntheticLambda2(Transitions.TransitionFinishCallback transitionFinishCallback) {
        this.f$0 = transitionFinishCallback;
    }

    public final void run() {
        this.f$0.onTransitionFinished((WindowContainerTransaction) null, (WindowContainerTransactionCallback) null);
    }
}
