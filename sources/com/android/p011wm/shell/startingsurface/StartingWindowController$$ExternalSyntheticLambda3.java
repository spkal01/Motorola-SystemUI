package com.android.p011wm.shell.startingsurface;

import android.graphics.Rect;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class StartingWindowController$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ StartingWindowController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ SurfaceControl f$2;
    public final /* synthetic */ Rect f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ StartingWindowController$$ExternalSyntheticLambda3(StartingWindowController startingWindowController, int i, SurfaceControl surfaceControl, Rect rect, boolean z) {
        this.f$0 = startingWindowController;
        this.f$1 = i;
        this.f$2 = surfaceControl;
        this.f$3 = rect;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$removeStartingWindow$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
