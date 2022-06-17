package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayController;

/* renamed from: com.android.wm.shell.common.DisplayController$DisplayWindowListenerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2285xd1ef1ecf implements Runnable {
    public final /* synthetic */ DisplayController.DisplayWindowListenerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C2285xd1ef1ecf(DisplayController.DisplayWindowListenerImpl displayWindowListenerImpl, int i) {
        this.f$0 = displayWindowListenerImpl;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onDisplayAdded$0(this.f$1);
    }
}
