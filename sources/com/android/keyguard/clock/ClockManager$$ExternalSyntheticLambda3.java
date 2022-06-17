package com.android.keyguard.clock;

import android.content.res.Resources;
import android.view.LayoutInflater;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import java.util.function.Supplier;

public final /* synthetic */ class ClockManager$$ExternalSyntheticLambda3 implements Supplier {
    public final /* synthetic */ Resources f$0;
    public final /* synthetic */ LayoutInflater f$1;
    public final /* synthetic */ SysuiColorExtractor f$2;

    public /* synthetic */ ClockManager$$ExternalSyntheticLambda3(Resources resources, LayoutInflater layoutInflater, SysuiColorExtractor sysuiColorExtractor) {
        this.f$0 = resources;
        this.f$1 = layoutInflater;
        this.f$2 = sysuiColorExtractor;
    }

    public final Object get() {
        return ClockManager.lambda$new$1(this.f$0, this.f$1, this.f$2);
    }
}
