package com.android.systemui.theme;

import java.util.List;
import java.util.function.Consumer;

public final /* synthetic */ class ThemeOverlayApplier$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ ThemeOverlayApplier f$0;
    public final /* synthetic */ List f$1;

    public /* synthetic */ ThemeOverlayApplier$$ExternalSyntheticLambda1(ThemeOverlayApplier themeOverlayApplier, List list) {
        this.f$0 = themeOverlayApplier;
        this.f$1 = list;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$applyCurrentUserOverlays$1(this.f$1, (String) obj);
    }
}
