package com.android.p011wm.shell.splitscreen;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.split.SplitLayout;

/* renamed from: com.android.wm.shell.splitscreen.StageCoordinator$$ExternalSyntheticLambda1 */
public final /* synthetic */ class StageCoordinator$$ExternalSyntheticLambda1 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ SplitLayout f$0;
    public final /* synthetic */ StageTaskListener f$1;
    public final /* synthetic */ StageTaskListener f$2;

    public /* synthetic */ StageCoordinator$$ExternalSyntheticLambda1(SplitLayout splitLayout, StageTaskListener stageTaskListener, StageTaskListener stageTaskListener2) {
        this.f$0 = splitLayout;
        this.f$1 = stageTaskListener;
        this.f$2 = stageTaskListener2;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.applySurfaceChanges(transaction, this.f$1.mRootLeash, this.f$2.mRootLeash, this.f$1.mDimLayer, this.f$2.mDimLayer);
    }
}
