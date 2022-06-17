package com.android.p011wm.shell.startingsurface;

import com.android.p011wm.shell.startingsurface.StartingWindowController;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$IStartingWindowImpl$1$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2408xf68ffddd implements Runnable {
    public final /* synthetic */ StartingWindowController.IStartingWindowImpl.C24051 f$0;
    public final /* synthetic */ StartingWindowController f$1;

    public /* synthetic */ C2408xf68ffddd(StartingWindowController.IStartingWindowImpl.C24051 r1, StartingWindowController startingWindowController) {
        this.f$0 = r1;
        this.f$1 = startingWindowController;
    }

    public final void run() {
        this.f$0.lambda$binderDied$0(this.f$1);
    }
}
