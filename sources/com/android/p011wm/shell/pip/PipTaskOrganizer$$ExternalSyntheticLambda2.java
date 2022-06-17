package com.android.p011wm.shell.pip;

import android.graphics.Rect;
import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda2 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda2 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ PipTaskOrganizer f$0;
    public final /* synthetic */ SurfaceControl f$1;
    public final /* synthetic */ Rect f$2;
    public final /* synthetic */ Rect f$3;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda2(PipTaskOrganizer pipTaskOrganizer, SurfaceControl surfaceControl, Rect rect, Rect rect2) {
        this.f$0 = pipTaskOrganizer;
        this.f$1 = surfaceControl;
        this.f$2 = rect;
        this.f$3 = rect2;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$finishResize$7(this.f$1, this.f$2, this.f$3, transaction);
    }
}
