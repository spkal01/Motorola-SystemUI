package com.android.p011wm.shell.pip;

import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.pip.PinnedStackListenerForwarder$PinnedTaskListenerImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C2348x2eacda59 implements Runnable {
    public final /* synthetic */ PinnedStackListenerForwarder.PinnedTaskListenerImpl f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ C2348x2eacda59(PinnedStackListenerForwarder.PinnedTaskListenerImpl pinnedTaskListenerImpl, boolean z) {
        this.f$0 = pinnedTaskListenerImpl;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$onMovementBoundsChanged$0(this.f$1);
    }
}
