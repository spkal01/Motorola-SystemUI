package com.android.systemui.screenshot;

import android.view.ScrollCaptureResponse;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ScreenshotController f$0;
    public final /* synthetic */ ScrollCaptureResponse f$1;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda13(ScreenshotController screenshotController, ScrollCaptureResponse scrollCaptureResponse) {
        this.f$0 = screenshotController;
        this.f$1 = scrollCaptureResponse;
    }

    public final void run() {
        this.f$0.lambda$onScrollCaptureResponseReady$8(this.f$1);
    }
}
