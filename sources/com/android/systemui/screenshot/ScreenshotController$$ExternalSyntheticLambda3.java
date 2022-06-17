package com.android.systemui.screenshot;

import com.android.systemui.screenshot.ScreenshotController;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda3 implements ScreenshotController.ActionsReadyListener {
    public final /* synthetic */ ScreenshotController f$0;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda3(ScreenshotController screenshotController) {
        this.f$0 = screenshotController;
    }

    public final void onActionsReady(ScreenshotController.SavedImageData savedImageData) {
        this.f$0.showUiOnActionsReady(savedImageData);
    }
}
