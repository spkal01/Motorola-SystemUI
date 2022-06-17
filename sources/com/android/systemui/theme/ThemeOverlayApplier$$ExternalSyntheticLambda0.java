package com.android.systemui.theme;

import android.content.om.FabricatedOverlay;
import java.util.Map;
import java.util.Set;

public final /* synthetic */ class ThemeOverlayApplier$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ThemeOverlayApplier f$0;
    public final /* synthetic */ Map f$1;
    public final /* synthetic */ FabricatedOverlay[] f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ Set f$4;

    public /* synthetic */ ThemeOverlayApplier$$ExternalSyntheticLambda0(ThemeOverlayApplier themeOverlayApplier, Map map, FabricatedOverlay[] fabricatedOverlayArr, int i, Set set) {
        this.f$0 = themeOverlayApplier;
        this.f$1 = map;
        this.f$2 = fabricatedOverlayArr;
        this.f$3 = i;
        this.f$4 = set;
    }

    public final void run() {
        this.f$0.lambda$applyCurrentUserOverlays$7(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
