package com.android.p011wm.shell.transition;

import android.window.IRemoteTransition;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.transition.Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ IRemoteTransition f$0;

    public /* synthetic */ Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda0(IRemoteTransition iRemoteTransition) {
        this.f$0 = iRemoteTransition;
    }

    public final void accept(Object obj) {
        ((Transitions) obj).mRemoteTransitionHandler.removeFiltered(this.f$0);
    }
}
