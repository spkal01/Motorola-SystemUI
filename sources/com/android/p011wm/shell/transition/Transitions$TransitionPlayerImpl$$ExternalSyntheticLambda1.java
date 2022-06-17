package com.android.p011wm.shell.transition;

import android.os.IBinder;
import android.window.TransitionRequestInfo;
import com.android.p011wm.shell.transition.Transitions;

/* renamed from: com.android.wm.shell.transition.Transitions$TransitionPlayerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class Transitions$TransitionPlayerImpl$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ Transitions.TransitionPlayerImpl f$0;
    public final /* synthetic */ IBinder f$1;
    public final /* synthetic */ TransitionRequestInfo f$2;

    public /* synthetic */ Transitions$TransitionPlayerImpl$$ExternalSyntheticLambda1(Transitions.TransitionPlayerImpl transitionPlayerImpl, IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        this.f$0 = transitionPlayerImpl;
        this.f$1 = iBinder;
        this.f$2 = transitionRequestInfo;
    }

    public final void run() {
        this.f$0.lambda$requestStartTransition$1(this.f$1, this.f$2);
    }
}
