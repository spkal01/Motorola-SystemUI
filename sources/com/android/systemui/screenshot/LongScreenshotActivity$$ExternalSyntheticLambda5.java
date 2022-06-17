package com.android.systemui.screenshot;

import com.google.common.util.concurrent.ListenableFuture;

public final /* synthetic */ class LongScreenshotActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ LongScreenshotActivity f$0;
    public final /* synthetic */ ListenableFuture f$1;

    public /* synthetic */ LongScreenshotActivity$$ExternalSyntheticLambda5(LongScreenshotActivity longScreenshotActivity, ListenableFuture listenableFuture) {
        this.f$0 = longScreenshotActivity;
        this.f$1 = listenableFuture;
    }

    public final void run() {
        this.f$0.lambda$onStart$2(this.f$1);
    }
}
