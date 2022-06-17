package com.android.p011wm.shell.splitscreen;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.splitscreen.StageTaskListener$$ExternalSyntheticLambda2 */
public final /* synthetic */ class StageTaskListener$$ExternalSyntheticLambda2 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ StageTaskListener f$0;

    public /* synthetic */ StageTaskListener$$ExternalSyntheticLambda2(StageTaskListener stageTaskListener) {
        this.f$0 = stageTaskListener;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onTaskAppeared$0(transaction);
    }
}
