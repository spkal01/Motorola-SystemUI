package com.android.systemui.screenshot;

import com.android.systemui.screenshot.ScreenshotController;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ ScreenshotController f$0;
    public final /* synthetic */ ScreenshotController.SavedImageData f$1;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda16(ScreenshotController screenshotController, ScreenshotController.SavedImageData savedImageData) {
        this.f$0 = screenshotController;
        this.f$1 = savedImageData;
    }

    public final void run() {
        this.f$0.lambda$showUiOnActionsReady$12(this.f$1);
    }
}
