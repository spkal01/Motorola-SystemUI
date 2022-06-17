package com.android.p011wm.shell.startingsurface;

import android.content.Context;
import android.content.pm.ActivityInfo;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.startingsurface.SplashscreenContentDrawer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class SplashscreenContentDrawer$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SplashscreenContentDrawer f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ ActivityInfo f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ Consumer f$5;

    public /* synthetic */ SplashscreenContentDrawer$$ExternalSyntheticLambda0(SplashscreenContentDrawer splashscreenContentDrawer, Context context, ActivityInfo activityInfo, int i, int i2, Consumer consumer) {
        this.f$0 = splashscreenContentDrawer;
        this.f$1 = context;
        this.f$2 = activityInfo;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = consumer;
    }

    public final void run() {
        this.f$0.lambda$createContentView$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
