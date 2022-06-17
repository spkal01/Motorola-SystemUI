package com.motorola.systemui.p014qs;

import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.function.Predicate;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$1$$ExternalSyntheticLambda1 */
public final /* synthetic */ class DynamicTileManager$1$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ DynamicTileManager$1$$ExternalSyntheticLambda1 INSTANCE = new DynamicTileManager$1$$ExternalSyntheticLambda1();

    private /* synthetic */ DynamicTileManager$1$$ExternalSyntheticLambda1() {
    }

    public final boolean test(Object obj) {
        return ((DynamicTileManager.DynamicTile) obj).needsPackageEvent();
    }
}
