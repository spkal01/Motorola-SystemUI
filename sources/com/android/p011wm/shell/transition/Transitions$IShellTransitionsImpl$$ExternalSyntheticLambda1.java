package com.android.p011wm.shell.transition;

import android.window.IRemoteTransition;
import android.window.TransitionFilter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.transition.Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ TransitionFilter f$0;
    public final /* synthetic */ IRemoteTransition f$1;

    public /* synthetic */ Transitions$IShellTransitionsImpl$$ExternalSyntheticLambda1(TransitionFilter transitionFilter, IRemoteTransition iRemoteTransition) {
        this.f$0 = transitionFilter;
        this.f$1 = iRemoteTransition;
    }

    public final void accept(Object obj) {
        ((Transitions) obj).mRemoteTransitionHandler.addFiltered(this.f$0, this.f$1);
    }
}
