package com.android.systemui;

import com.android.systemui.ImageWallpaper;

public final /* synthetic */ class ImageWallpaper$GLEngine$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ImageWallpaper.GLEngine f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ImageWallpaper$GLEngine$$ExternalSyntheticLambda4(ImageWallpaper.GLEngine gLEngine, int i, int i2) {
        this.f$0 = gLEngine;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$onSurfaceChanged$5(this.f$1, this.f$2);
    }
}
