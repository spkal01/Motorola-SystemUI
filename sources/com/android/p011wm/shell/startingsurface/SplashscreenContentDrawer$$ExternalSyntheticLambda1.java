package com.android.p011wm.shell.startingsurface;

import android.graphics.drawable.Drawable;
import java.util.function.IntSupplier;

/* renamed from: com.android.wm.shell.startingsurface.SplashscreenContentDrawer$$ExternalSyntheticLambda1 */
public final /* synthetic */ class SplashscreenContentDrawer$$ExternalSyntheticLambda1 implements IntSupplier {
    public final /* synthetic */ Drawable f$0;

    public /* synthetic */ SplashscreenContentDrawer$$ExternalSyntheticLambda1(Drawable drawable) {
        this.f$0 = drawable;
    }

    public final int getAsInt() {
        return SplashscreenContentDrawer.estimateWindowBGColor(this.f$0);
    }
}
