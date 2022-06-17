package com.android.systemui.p006qs.customize;

import java.util.ArrayList;

/* renamed from: com.android.systemui.qs.customize.TileQueryHelper$$ExternalSyntheticLambda1 */
public final /* synthetic */ class TileQueryHelper$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ TileQueryHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ TileQueryHelper$$ExternalSyntheticLambda1(TileQueryHelper tileQueryHelper, ArrayList arrayList, boolean z) {
        this.f$0 = tileQueryHelper;
        this.f$1 = arrayList;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$notifyTilesChanged$1(this.f$1, this.f$2);
    }
}
