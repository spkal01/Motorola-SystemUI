package com.android.systemui.screenshot;

import com.android.systemui.screenshot.MotoGlobalScreenshot;
import com.android.systemui.screenshot.ScreenshotController;

/* renamed from: com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$6$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1359x974e7e4d implements Runnable {
    public final /* synthetic */ MotoGlobalScreenshot.DisplayScreenshotSession.C13316 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ScreenshotController.SavedImageData f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ C1359x974e7e4d(MotoGlobalScreenshot.DisplayScreenshotSession.C13316 r1, int i, ScreenshotController.SavedImageData savedImageData, String str) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = savedImageData;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$onActionsReady$0(this.f$1, this.f$2, this.f$3);
    }
}
