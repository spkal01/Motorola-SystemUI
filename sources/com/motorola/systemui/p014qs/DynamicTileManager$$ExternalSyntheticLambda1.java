package com.motorola.systemui.p014qs;

import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.ArrayList;
import java.util.function.Consumer;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$$ExternalSyntheticLambda1 */
public final /* synthetic */ class DynamicTileManager$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ DynamicTileManager$$ExternalSyntheticLambda1(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void accept(Object obj) {
        this.f$0.add(((DynamicTileManager.DynamicTile) obj).getTileSpec());
    }
}
