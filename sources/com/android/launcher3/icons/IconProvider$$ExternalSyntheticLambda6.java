package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import java.util.function.Supplier;

public final /* synthetic */ class IconProvider$$ExternalSyntheticLambda6 implements Supplier {
    public final /* synthetic */ ActivityInfo f$0;

    public /* synthetic */ IconProvider$$ExternalSyntheticLambda6(ActivityInfo activityInfo) {
        this.f$0 = activityInfo;
    }

    public final Object get() {
        return ComponentName.createRelative(this.f$0.packageName, this.f$0.name);
    }
}
