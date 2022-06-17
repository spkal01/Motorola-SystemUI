package com.android.systemui.screenshot;

import com.android.systemui.screenshot.MotoGlobalScreenshot;

/* renamed from: com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$$ExternalSyntheticLambda17 */
public final /* synthetic */ class C1347xcf89107b implements Runnable {
    public final /* synthetic */ MotoGlobalScreenshot.DisplayScreenshotSession f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C1347xcf89107b(MotoGlobalScreenshot.DisplayScreenshotSession displayScreenshotSession, int i) {
        this.f$0 = displayScreenshotSession;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onFirstScreenshotSaved$13(this.f$1);
    }
}
