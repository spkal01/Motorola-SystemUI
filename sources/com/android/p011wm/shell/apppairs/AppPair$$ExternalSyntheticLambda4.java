package com.android.p011wm.shell.apppairs;

import android.app.ActivityManager;
import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.apppairs.AppPair$$ExternalSyntheticLambda4 */
public final /* synthetic */ class AppPair$$ExternalSyntheticLambda4 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ AppPair f$0;
    public final /* synthetic */ ActivityManager.RunningTaskInfo f$1;

    public /* synthetic */ AppPair$$ExternalSyntheticLambda4(AppPair appPair, ActivityManager.RunningTaskInfo runningTaskInfo) {
        this.f$0 = appPair;
        this.f$1 = runningTaskInfo;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        this.f$0.lambda$onTaskInfoChanged$4(this.f$1, transaction);
    }
}
