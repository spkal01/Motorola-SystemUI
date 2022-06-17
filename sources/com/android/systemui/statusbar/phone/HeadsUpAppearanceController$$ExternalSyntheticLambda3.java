package com.android.systemui.statusbar.phone;

import java.util.function.BiConsumer;

public final /* synthetic */ class HeadsUpAppearanceController$$ExternalSyntheticLambda3 implements BiConsumer {
    public final /* synthetic */ HeadsUpAppearanceController f$0;

    public /* synthetic */ HeadsUpAppearanceController$$ExternalSyntheticLambda3(HeadsUpAppearanceController headsUpAppearanceController) {
        this.f$0 = headsUpAppearanceController;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.setAppearFraction(((Float) obj).floatValue(), ((Float) obj2).floatValue());
    }
}
