package com.android.systemui.theme;

import android.content.om.OverlayInfo;
import java.util.function.Predicate;

public final /* synthetic */ class ThemeOverlayApplier$$ExternalSyntheticLambda7 implements Predicate {
    public static final /* synthetic */ ThemeOverlayApplier$$ExternalSyntheticLambda7 INSTANCE = new ThemeOverlayApplier$$ExternalSyntheticLambda7();

    private /* synthetic */ ThemeOverlayApplier$$ExternalSyntheticLambda7() {
    }

    public final boolean test(Object obj) {
        return ((OverlayInfo) obj).isEnabled();
    }
}
