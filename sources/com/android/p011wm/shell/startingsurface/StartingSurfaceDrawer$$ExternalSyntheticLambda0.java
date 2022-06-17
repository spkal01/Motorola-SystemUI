package com.android.p011wm.shell.startingsurface;

import android.os.Bundle;
import android.os.RemoteCallback;

/* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class StartingSurfaceDrawer$$ExternalSyntheticLambda0 implements RemoteCallback.OnResultListener {
    public final /* synthetic */ StartingSurfaceDrawer f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ StartingSurfaceDrawer$$ExternalSyntheticLambda0(StartingSurfaceDrawer startingSurfaceDrawer, int i) {
        this.f$0 = startingSurfaceDrawer;
        this.f$1 = i;
    }

    public final void onResult(Bundle bundle) {
        this.f$0.lambda$copySplashScreenView$4(this.f$1, bundle);
    }
}
