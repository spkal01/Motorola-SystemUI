package com.motorola.systemui.p014qs;

import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.function.Consumer;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager$1$$ExternalSyntheticLambda0 */
public final /* synthetic */ class DynamicTileManager$1$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ DynamicTileManager$1$$ExternalSyntheticLambda0(String str, String str2, boolean z) {
        this.f$0 = str;
        this.f$1 = str2;
        this.f$2 = z;
    }

    public final void accept(Object obj) {
        ((DynamicTileManager.DynamicTile) obj).onPackageEvent(this.f$0, this.f$1, this.f$2);
    }
}
