package com.android.p011wm.shell.apppairs;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.split.SplitLayout;

/* renamed from: com.android.wm.shell.apppairs.AppPair$$ExternalSyntheticLambda7 */
public final /* synthetic */ class AppPair$$ExternalSyntheticLambda7 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ AppPair f$0;
    public final /* synthetic */ SplitLayout f$1;

    public /* synthetic */ AppPair$$ExternalSyntheticLambda7(AppPair appPair, SplitLayout splitLayout) {
        this.f$0 = appPair;
        this.f$1 = splitLayout;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onBoundsChanging$7(this.f$1, transaction);
    }
}
