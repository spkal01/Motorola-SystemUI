package com.android.p011wm.shell.startingsurface;

import android.view.SurfaceControlViewHost;

/* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer$$ExternalSyntheticLambda1 */
public final /* synthetic */ class StartingSurfaceDrawer$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SurfaceControlViewHost f$0;

    public /* synthetic */ StartingSurfaceDrawer$$ExternalSyntheticLambda1(SurfaceControlViewHost surfaceControlViewHost) {
        this.f$0 = surfaceControlViewHost;
    }

    public final void run() {
        this.f$0.release();
    }
}
