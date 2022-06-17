package com.android.p011wm.shell.apppairs;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.apppairs.AppPair$$ExternalSyntheticLambda3 */
public final /* synthetic */ class AppPair$$ExternalSyntheticLambda3 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ AppPair f$0;

    public /* synthetic */ AppPair$$ExternalSyntheticLambda3(AppPair appPair) {
        this.f$0 = appPair;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onTaskAppeared$1(transaction);
    }
}
