package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayController;

/* renamed from: com.android.wm.shell.common.DisplayController$DisplayWindowListenerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C2286xd1ef1ed0 implements Runnable {
    public final /* synthetic */ DisplayController.DisplayWindowListenerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C2286xd1ef1ed0(DisplayController.DisplayWindowListenerImpl displayWindowListenerImpl, int i) {
        this.f$0 = displayWindowListenerImpl;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onDisplayRemoved$2(this.f$1);
    }
}
