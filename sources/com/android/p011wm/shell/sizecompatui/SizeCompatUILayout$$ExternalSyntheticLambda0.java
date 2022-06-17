package com.android.p011wm.shell.sizecompatui;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SyncTransactionQueue;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUILayout$$ExternalSyntheticLambda0 */
public final /* synthetic */ class SizeCompatUILayout$$ExternalSyntheticLambda0 implements SyncTransactionQueue.TransactionRunnable {
    public final /* synthetic */ SurfaceControl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SizeCompatUILayout$$ExternalSyntheticLambda0(SurfaceControl surfaceControl, int i, int i2) {
        this.f$0 = surfaceControl;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void runWithTransaction(SurfaceControl.Transaction transaction) {
        SizeCompatUILayout.lambda$updateSurfacePosition$0(this.f$0, this.f$1, this.f$2, transaction);
    }
}
