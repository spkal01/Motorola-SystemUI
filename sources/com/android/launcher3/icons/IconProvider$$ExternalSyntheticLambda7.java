package com.android.launcher3.icons;

import android.content.pm.ActivityInfo;
import java.util.function.Supplier;

public final /* synthetic */ class IconProvider$$ExternalSyntheticLambda7 implements Supplier {
    public final /* synthetic */ IconProvider f$0;
    public final /* synthetic */ ActivityInfo f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ IconProvider$$ExternalSyntheticLambda7(IconProvider iconProvider, ActivityInfo activityInfo, int i) {
        this.f$0 = iconProvider;
        this.f$1 = activityInfo;
        this.f$2 = i;
    }

    public final Object get() {
        return this.f$0.lambda$getIcon$3(this.f$1, this.f$2);
    }
}
