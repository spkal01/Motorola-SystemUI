package com.android.systemui.screenshot;

import com.google.common.util.concurrent.ListenableFuture;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ ScreenshotController f$0;
    public final /* synthetic */ ListenableFuture f$1;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda17(ScreenshotController screenshotController, ListenableFuture listenableFuture) {
        this.f$0 = screenshotController;
        this.f$1 = listenableFuture;
    }

    public final void run() {
        this.f$0.lambda$onScrollCaptureResponseReady$7(this.f$1);
    }
}
