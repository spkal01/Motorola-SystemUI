package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayController;

/* renamed from: com.android.wm.shell.common.DisplayController$DisplayWindowListenerImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C2288xd1ef1ed2 implements Runnable {
    public final /* synthetic */ DisplayController.DisplayWindowListenerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ C2288xd1ef1ed2(DisplayController.DisplayWindowListenerImpl displayWindowListenerImpl, int i, int i2) {
        this.f$0 = displayWindowListenerImpl;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$onFixedRotationStarted$3(this.f$1, this.f$2);
    }
}
