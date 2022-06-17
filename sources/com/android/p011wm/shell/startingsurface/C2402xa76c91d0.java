package com.android.p011wm.shell.startingsurface;

import android.graphics.drawable.Drawable;
import com.android.p011wm.shell.startingsurface.SplashscreenIconDrawableFactory;

/* renamed from: com.android.wm.shell.startingsurface.SplashscreenIconDrawableFactory$ImmobileIconDrawable$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2402xa76c91d0 implements Runnable {
    public final /* synthetic */ SplashscreenIconDrawableFactory.ImmobileIconDrawable f$0;
    public final /* synthetic */ Drawable f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ C2402xa76c91d0(SplashscreenIconDrawableFactory.ImmobileIconDrawable immobileIconDrawable, Drawable drawable, int i) {
        this.f$0 = immobileIconDrawable;
        this.f$1 = drawable;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2);
    }
}
