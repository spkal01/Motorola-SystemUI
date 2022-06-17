package com.android.p011wm.shell.pip;

import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.pip.PinnedStackListenerForwarder$PinnedTaskListenerImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class C2349x2eacda5a implements Runnable {
    public final /* synthetic */ PinnedStackListenerForwarder.PinnedTaskListenerImpl f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ C2349x2eacda5a(PinnedStackListenerForwarder.PinnedTaskListenerImpl pinnedTaskListenerImpl, boolean z, int i) {
        this.f$0 = pinnedTaskListenerImpl;
        this.f$1 = z;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onImeVisibilityChanged$1(this.f$1, this.f$2);
    }
}
