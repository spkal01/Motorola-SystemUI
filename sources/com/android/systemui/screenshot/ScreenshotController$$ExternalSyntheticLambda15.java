package com.android.systemui.screenshot;

import com.android.systemui.screenshot.ScreenshotController;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ ScreenshotController f$0;
    public final /* synthetic */ ScreenshotController.QuickShareData f$1;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda15(ScreenshotController screenshotController, ScreenshotController.QuickShareData quickShareData) {
        this.f$0 = screenshotController;
        this.f$1 = quickShareData;
    }

    public final void run() {
        this.f$0.lambda$showUiOnQuickShareActionReady$13(this.f$1);
    }
}
