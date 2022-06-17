package com.android.systemui.screenshot;

import android.content.res.Configuration;
import android.view.ViewRootImpl;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda2 implements ViewRootImpl.ActivityConfigCallback {
    public final /* synthetic */ ScreenshotController f$0;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda2(ScreenshotController screenshotController) {
        this.f$0 = screenshotController;
    }

    public final void onConfigurationChanged(Configuration configuration, int i) {
        this.f$0.lambda$saveScreenshot$2(configuration, i);
    }
}
