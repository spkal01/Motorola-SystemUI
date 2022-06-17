package com.android.systemui.screenshot;

import com.android.systemui.screenshot.LongScreenshotActivity;
import com.google.common.util.concurrent.ListenableFuture;

public final /* synthetic */ class LongScreenshotActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ LongScreenshotActivity f$0;
    public final /* synthetic */ LongScreenshotActivity.PendingAction f$1;
    public final /* synthetic */ ListenableFuture f$2;

    public /* synthetic */ LongScreenshotActivity$$ExternalSyntheticLambda4(LongScreenshotActivity longScreenshotActivity, LongScreenshotActivity.PendingAction pendingAction, ListenableFuture listenableFuture) {
        this.f$0 = longScreenshotActivity;
        this.f$1 = pendingAction;
        this.f$2 = listenableFuture;
    }

    public final void run() {
        this.f$0.lambda$startExport$4(this.f$1, this.f$2);
    }
}
