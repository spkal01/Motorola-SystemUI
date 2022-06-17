package com.android.p011wm.shell;

import android.graphics.Point;
import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.FullscreenTaskListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class FullscreenTaskListener$$ExternalSyntheticLambda0 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ SurfaceControl f$0;
    public final /* synthetic */ Point f$1;

    public /* synthetic */ FullscreenTaskListener$$ExternalSyntheticLambda0(SurfaceControl surfaceControl, Point point) {
        this.f$0 = surfaceControl;
        this.f$1 = point;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        FullscreenTaskListener.lambda$onTaskAppeared$0(this.f$0, this.f$1, transaction);
    }
}
