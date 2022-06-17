package com.android.systemui.screenshot;

import com.android.systemui.screenshot.MotoGlobalScreenshot;

/* renamed from: com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$9$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1360x4341b090 implements Runnable {
    public final /* synthetic */ MotoGlobalScreenshot.DisplayScreenshotSession.C13359 f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ C1360x4341b090(MotoGlobalScreenshot.DisplayScreenshotSession.C13359 r1, boolean z, boolean z2) {
        this.f$0 = r1;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void run() {
        this.f$0.lambda$onLongScreenshotReady$0(this.f$1, this.f$2);
    }
}
