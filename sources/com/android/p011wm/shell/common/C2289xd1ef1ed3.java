package com.android.p011wm.shell.common;

import android.content.res.Configuration;
import com.android.p011wm.shell.common.DisplayController;

/* renamed from: com.android.wm.shell.common.DisplayController$DisplayWindowListenerImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class C2289xd1ef1ed3 implements Runnable {
    public final /* synthetic */ DisplayController.DisplayWindowListenerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Configuration f$2;

    public /* synthetic */ C2289xd1ef1ed3(DisplayController.DisplayWindowListenerImpl displayWindowListenerImpl, int i, Configuration configuration) {
        this.f$0 = displayWindowListenerImpl;
        this.f$1 = i;
        this.f$2 = configuration;
    }

    public final void run() {
        this.f$0.lambda$onDisplayConfigurationChanged$1(this.f$1, this.f$2);
    }
}
