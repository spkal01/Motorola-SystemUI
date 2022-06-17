package com.android.systemui.theme;

import android.content.om.OverlayInfo;
import java.util.function.Predicate;

public final /* synthetic */ class ThemeOverlayApplier$$ExternalSyntheticLambda4 implements Predicate {
    public final /* synthetic */ ThemeOverlayApplier f$0;

    public /* synthetic */ ThemeOverlayApplier$$ExternalSyntheticLambda4(ThemeOverlayApplier themeOverlayApplier) {
        this.f$0 = themeOverlayApplier;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$applyCurrentUserOverlays$2((OverlayInfo) obj);
    }
}
