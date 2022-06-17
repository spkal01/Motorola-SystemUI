package com.android.systemui.p006qs.external;

import android.content.ComponentName;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessTileServices$$ExternalSyntheticLambda1 */
public final /* synthetic */ class MotoDesktopProcessTileServices$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MotoDesktopProcessTileServices f$0;
    public final /* synthetic */ ComponentName f$1;

    public /* synthetic */ MotoDesktopProcessTileServices$$ExternalSyntheticLambda1(MotoDesktopProcessTileServices motoDesktopProcessTileServices, ComponentName componentName) {
        this.f$0 = motoDesktopProcessTileServices;
        this.f$1 = componentName;
    }

    public final void run() {
        this.f$0.lambda$onTileChanged$0(this.f$1);
    }
}
