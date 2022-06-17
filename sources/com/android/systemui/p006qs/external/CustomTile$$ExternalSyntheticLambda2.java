package com.android.systemui.p006qs.external;

import android.service.quicksettings.Tile;

/* renamed from: com.android.systemui.qs.external.CustomTile$$ExternalSyntheticLambda2 */
public final /* synthetic */ class CustomTile$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CustomTile f$0;
    public final /* synthetic */ Tile f$1;

    public /* synthetic */ CustomTile$$ExternalSyntheticLambda2(CustomTile customTile, Tile tile) {
        this.f$0 = customTile;
        this.f$1 = tile;
    }

    public final void run() {
        this.f$0.lambda$updateTileState$0(this.f$1);
    }
}
