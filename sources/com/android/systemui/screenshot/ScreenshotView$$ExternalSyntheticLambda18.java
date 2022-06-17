package com.android.systemui.screenshot;

import android.view.View;
import com.android.systemui.screenshot.ScreenshotController;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda18 implements View.OnClickListener {
    public final /* synthetic */ ScreenshotView f$0;
    public final /* synthetic */ ScreenshotController.SavedImageData f$1;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda18(ScreenshotView screenshotView, ScreenshotController.SavedImageData savedImageData) {
        this.f$0 = screenshotView;
        this.f$1 = savedImageData;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setChipIntents$10(this.f$1, view);
    }
}
