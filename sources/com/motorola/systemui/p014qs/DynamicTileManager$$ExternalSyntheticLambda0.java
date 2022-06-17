package com.motorola.systemui.p014qs;

import android.content.IntentFilter;
import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.function.Consumer;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$$ExternalSyntheticLambda0 */
public final /* synthetic */ class DynamicTileManager$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ IntentFilter f$0;

    public /* synthetic */ DynamicTileManager$$ExternalSyntheticLambda0(IntentFilter intentFilter) {
        this.f$0 = intentFilter;
    }

    public final void accept(Object obj) {
        DynamicTileManager.lambda$loadTiles$0(this.f$0, (DynamicTileManager.DynamicTile) obj);
    }
}
