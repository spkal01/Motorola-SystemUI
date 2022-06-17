package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.service.quicksettings.Tile;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessTileServices$$ExternalSyntheticLambda2 */
public final /* synthetic */ class MotoDesktopProcessTileServices$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MotoDesktopProcessTileServices f$0;
    public final /* synthetic */ ComponentName f$1;
    public final /* synthetic */ Tile f$2;

    public /* synthetic */ MotoDesktopProcessTileServices$$ExternalSyntheticLambda2(MotoDesktopProcessTileServices motoDesktopProcessTileServices, ComponentName componentName, Tile tile) {
        this.f$0 = motoDesktopProcessTileServices;
        this.f$1 = componentName;
        this.f$2 = tile;
    }

    public final void run() {
        this.f$0.lambda$updateQsTile$1(this.f$1, this.f$2);
    }
}
