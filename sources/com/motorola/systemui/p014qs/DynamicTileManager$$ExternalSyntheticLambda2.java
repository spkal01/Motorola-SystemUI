package com.motorola.systemui.p014qs;

import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.List;
import java.util.function.Consumer;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$$ExternalSyntheticLambda2 */
public final /* synthetic */ class DynamicTileManager$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ List f$0;

    public /* synthetic */ DynamicTileManager$$ExternalSyntheticLambda2(List list) {
        this.f$0 = list;
    }

    public final void accept(Object obj) {
        ((DynamicTileManager.DynamicTile) obj).reload(this.f$0);
    }
}
