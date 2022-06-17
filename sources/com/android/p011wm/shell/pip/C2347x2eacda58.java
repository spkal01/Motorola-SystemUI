package com.android.p011wm.shell.pip;

import android.content.pm.ParceledListSlice;
import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.pip.PinnedStackListenerForwarder$PinnedTaskListenerImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C2347x2eacda58 implements Runnable {
    public final /* synthetic */ PinnedStackListenerForwarder.PinnedTaskListenerImpl f$0;
    public final /* synthetic */ ParceledListSlice f$1;

    public /* synthetic */ C2347x2eacda58(PinnedStackListenerForwarder.PinnedTaskListenerImpl pinnedTaskListenerImpl, ParceledListSlice parceledListSlice) {
        this.f$0 = pinnedTaskListenerImpl;
        this.f$1 = parceledListSlice;
    }

    public final void run() {
        this.f$0.lambda$onActionsChanged$2(this.f$1);
    }
}
