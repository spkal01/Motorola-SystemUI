package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayController;

/* renamed from: com.android.wm.shell.common.DisplayController$DisplayWindowListenerImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C2287xd1ef1ed1 implements Runnable {
    public final /* synthetic */ DisplayController.DisplayWindowListenerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C2287xd1ef1ed1(DisplayController.DisplayWindowListenerImpl displayWindowListenerImpl, int i) {
        this.f$0 = displayWindowListenerImpl;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onFixedRotationFinished$4(this.f$1);
    }
}
