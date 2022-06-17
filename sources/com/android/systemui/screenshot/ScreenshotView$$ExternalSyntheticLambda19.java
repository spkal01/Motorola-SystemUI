package com.android.systemui.screenshot;

import android.view.View;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda19 implements View.OnClickListener {
    public final /* synthetic */ ScreenshotView f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda19(ScreenshotView screenshotView, Runnable runnable) {
        this.f$0 = screenshotView;
        this.f$1 = runnable;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showScrollChip$0(this.f$1, view);
    }
}
