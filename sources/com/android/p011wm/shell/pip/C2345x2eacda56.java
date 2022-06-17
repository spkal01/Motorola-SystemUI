package com.android.p011wm.shell.pip;

import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.pip.PinnedStackListenerForwarder$PinnedTaskListenerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2345x2eacda56 implements Runnable {
    public final /* synthetic */ PinnedStackListenerForwarder.PinnedTaskListenerImpl f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ C2345x2eacda56(PinnedStackListenerForwarder.PinnedTaskListenerImpl pinnedTaskListenerImpl, float f) {
        this.f$0 = pinnedTaskListenerImpl;
        this.f$1 = f;
    }

    public final void run() {
        this.f$0.lambda$onAspectRatioChanged$4(this.f$1);
    }
}
