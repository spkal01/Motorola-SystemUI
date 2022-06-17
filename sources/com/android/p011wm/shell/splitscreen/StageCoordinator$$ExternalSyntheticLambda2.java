package com.android.p011wm.shell.splitscreen;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.splitscreen.StageCoordinator$$ExternalSyntheticLambda2 */
public final /* synthetic */ class StageCoordinator$$ExternalSyntheticLambda2 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ StageCoordinator f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ StageCoordinator$$ExternalSyntheticLambda2(StageCoordinator stageCoordinator, boolean z, boolean z2) {
        this.f$0 = stageCoordinator;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onStageVisibilityChanged$1(this.f$1, this.f$2, transaction);
    }
}
