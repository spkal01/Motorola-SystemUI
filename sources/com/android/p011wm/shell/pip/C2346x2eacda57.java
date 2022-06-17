package com.android.p011wm.shell.pip;

import android.content.ComponentName;
import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.pip.PinnedStackListenerForwarder$PinnedTaskListenerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C2346x2eacda57 implements Runnable {
    public final /* synthetic */ PinnedStackListenerForwarder.PinnedTaskListenerImpl f$0;
    public final /* synthetic */ ComponentName f$1;

    public /* synthetic */ C2346x2eacda57(PinnedStackListenerForwarder.PinnedTaskListenerImpl pinnedTaskListenerImpl, ComponentName componentName) {
        this.f$0 = pinnedTaskListenerImpl;
        this.f$1 = componentName;
    }

    public final void run() {
        this.f$0.lambda$onActivityHidden$3(this.f$1);
    }
}
