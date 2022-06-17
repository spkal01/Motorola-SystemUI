package com.android.p011wm.shell.pip;

import android.graphics.Rect;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda7 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ PipTaskOrganizer f$0;
    public final /* synthetic */ Rect f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda7(PipTaskOrganizer pipTaskOrganizer, Rect rect, long j) {
        this.f$0 = pipTaskOrganizer;
        this.f$1 = rect;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$enterPipWithAlphaAnimation$3(this.f$1, this.f$2);
    }
}
