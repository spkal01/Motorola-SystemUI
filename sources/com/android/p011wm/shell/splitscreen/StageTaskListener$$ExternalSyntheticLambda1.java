package com.android.p011wm.shell.splitscreen;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.splitscreen.StageTaskListener$$ExternalSyntheticLambda1 */
public final /* synthetic */ class StageTaskListener$$ExternalSyntheticLambda1 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ StageTaskListener f$0;

    public /* synthetic */ StageTaskListener$$ExternalSyntheticLambda1(StageTaskListener stageTaskListener) {
        this.f$0 = stageTaskListener;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onTaskVanished$1(transaction);
    }
}
