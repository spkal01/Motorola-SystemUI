package com.android.p011wm.shell.pip;

import android.graphics.Rect;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda6 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PipTaskOrganizer f$0;
    public final /* synthetic */ Rect f$1;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda6(PipTaskOrganizer pipTaskOrganizer, Rect rect) {
        this.f$0 = pipTaskOrganizer;
        this.f$1 = rect;
    }

    public final void run() {
        this.f$0.lambda$onFixedRotationFinished$6(this.f$1);
    }
}
