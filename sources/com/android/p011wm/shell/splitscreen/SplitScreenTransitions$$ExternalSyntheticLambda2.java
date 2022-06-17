package com.android.p011wm.shell.splitscreen;

import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenTransitions$$ExternalSyntheticLambda2 */
public final /* synthetic */ class SplitScreenTransitions$$ExternalSyntheticLambda2 implements Transitions.TransitionFinishCallback {
    public final /* synthetic */ SplitScreenTransitions f$0;

    public /* synthetic */ SplitScreenTransitions$$ExternalSyntheticLambda2(SplitScreenTransitions splitScreenTransitions) {
        this.f$0 = splitScreenTransitions;
    }

    public final void onTransitionFinished(WindowContainerTransaction windowContainerTransaction, WindowContainerTransactionCallback windowContainerTransactionCallback) {
        this.f$0.lambda$new$0(windowContainerTransaction, windowContainerTransactionCallback);
    }
}
