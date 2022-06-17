package com.android.p011wm.shell.legacysplitscreen;

import android.graphics.Point;
import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTaskListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class LegacySplitScreenTaskListener$$ExternalSyntheticLambda0 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ SurfaceControl f$0;
    public final /* synthetic */ Point f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ LegacySplitScreenTaskListener$$ExternalSyntheticLambda0(SurfaceControl surfaceControl, Point point, boolean z) {
        this.f$0 = surfaceControl;
        this.f$1 = point;
        this.f$2 = z;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        LegacySplitScreenTaskListener.lambda$updateChildTaskSurface$0(this.f$0, this.f$1, this.f$2, transaction);
    }
}
