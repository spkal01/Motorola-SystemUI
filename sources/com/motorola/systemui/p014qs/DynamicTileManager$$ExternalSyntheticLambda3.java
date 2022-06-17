package com.motorola.systemui.p014qs;

import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.function.Predicate;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$$ExternalSyntheticLambda3 */
public final /* synthetic */ class DynamicTileManager$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ DynamicTileManager$$ExternalSyntheticLambda3 INSTANCE = new DynamicTileManager$$ExternalSyntheticLambda3();

    private /* synthetic */ DynamicTileManager$$ExternalSyntheticLambda3() {
    }

    public final boolean test(Object obj) {
        return ((DynamicTileManager.DynamicTile) obj).needsStock();
    }
}
