package com.android.systemui.screenshot;

import android.graphics.Rect;
import java.util.function.Consumer;

public final /* synthetic */ class ScreenshotController$$ExternalSyntheticLambda18 implements Consumer {
    public final /* synthetic */ ScreenshotController f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ ScreenshotController$$ExternalSyntheticLambda18(ScreenshotController screenshotController, Consumer consumer) {
        this.f$0 = screenshotController;
        this.f$1 = consumer;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$takeScreenshotPartial$0(this.f$1, (Rect) obj);
    }
}
