package com.android.p011wm.shell.pip;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.pip.PipTaskOrganizer$$ExternalSyntheticLambda3 */
public final /* synthetic */ class PipTaskOrganizer$$ExternalSyntheticLambda3 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ PipTaskOrganizer$$ExternalSyntheticLambda3(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.run();
    }
}
