package com.android.launcher3.icons;

import android.content.ComponentName;
import java.util.function.Function;

public final /* synthetic */ class IconProvider$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ IconProvider$$ExternalSyntheticLambda1 INSTANCE = new IconProvider$$ExternalSyntheticLambda1();

    private /* synthetic */ IconProvider$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj) {
        return ComponentName.unflattenFromString((String) obj);
    }
}
