package com.android.p011wm.shell.startingsurface;

import com.android.p011wm.shell.startingsurface.StartingSurfaceDrawer;

/* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer$$ExternalSyntheticLambda6 */
public final /* synthetic */ class StartingSurfaceDrawer$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ StartingSurfaceDrawer f$0;
    public final /* synthetic */ StartingSurfaceDrawer.StartingWindowRecord f$1;

    public /* synthetic */ StartingSurfaceDrawer$$ExternalSyntheticLambda6(StartingSurfaceDrawer startingSurfaceDrawer, StartingSurfaceDrawer.StartingWindowRecord startingWindowRecord) {
        this.f$0 = startingSurfaceDrawer;
        this.f$1 = startingWindowRecord;
    }

    public final void run() {
        this.f$0.lambda$removeWindowSynced$5(this.f$1);
    }
}
