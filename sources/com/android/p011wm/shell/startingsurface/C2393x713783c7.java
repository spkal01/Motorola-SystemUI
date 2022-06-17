package com.android.p011wm.shell.startingsurface;

import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.startingsurface.SplashScreenExitAnimation$ShiftUpAnimation$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2393x713783c7 implements Runnable {
    public final /* synthetic */ SurfaceControl f$0;

    public /* synthetic */ C2393x713783c7(SurfaceControl surfaceControl) {
        this.f$0 = surfaceControl;
    }

    public final void run() {
        this.f$0.release();
    }
}
