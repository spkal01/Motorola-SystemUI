package com.android.p011wm.shell.transition;

import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.Transitions$$ExternalSyntheticLambda0 */
public final /* synthetic */ class Transitions$$ExternalSyntheticLambda0 implements Transitions.TransitionFinishCallback {
    public final /* synthetic */ Transitions f$0;
    public final /* synthetic */ Transitions.ActiveTransition f$1;

    public /* synthetic */ Transitions$$ExternalSyntheticLambda0(Transitions transitions, Transitions.ActiveTransition activeTransition) {
        this.f$0 = transitions;
        this.f$1 = activeTransition;
    }

    public final void onTransitionFinished(WindowContainerTransaction windowContainerTransaction, WindowContainerTransactionCallback windowContainerTransactionCallback) {
        this.f$0.lambda$attemptMergeTransition$1(this.f$1, windowContainerTransaction, windowContainerTransactionCallback);
    }
}
