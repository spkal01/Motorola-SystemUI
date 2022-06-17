package com.android.p011wm.shell.transition;

import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.OneShotRemoteHandler$1$$ExternalSyntheticLambda0 */
public final /* synthetic */ class OneShotRemoteHandler$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Transitions.TransitionFinishCallback f$0;
    public final /* synthetic */ WindowContainerTransaction f$1;

    public /* synthetic */ OneShotRemoteHandler$1$$ExternalSyntheticLambda0(Transitions.TransitionFinishCallback transitionFinishCallback, WindowContainerTransaction windowContainerTransaction) {
        this.f$0 = transitionFinishCallback;
        this.f$1 = windowContainerTransaction;
    }

    public final void run() {
        this.f$0.onTransitionFinished(this.f$1, (WindowContainerTransactionCallback) null);
    }
}
