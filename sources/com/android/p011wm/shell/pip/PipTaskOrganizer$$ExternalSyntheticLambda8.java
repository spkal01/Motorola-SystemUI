package com.android.p011wm.shell.pip;

import android.graphics.Rect;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda8 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ PipTaskOrganizer f$0;
    public final /* synthetic */ Rect f$1;
    public final /* synthetic */ SurfaceControl f$2;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda8(PipTaskOrganizer pipTaskOrganizer, Rect rect, SurfaceControl surfaceControl) {
        this.f$0 = pipTaskOrganizer;
        this.f$1 = rect;
        this.f$2 = surfaceControl;
    }

    public final void run() {
        this.f$0.lambda$onEndOfSwipePipToHomeTransition$4(this.f$1, this.f$2);
    }
}
