package com.android.p011wm.shell.startingsurface;

import android.content.res.TypedArray;
import java.util.function.UnaryOperator;

/* renamed from: com.android.wm.shell.startingsurface.SplashscreenContentDrawer$$ExternalSyntheticLambda5 */
public final /* synthetic */ class SplashscreenContentDrawer$$ExternalSyntheticLambda5 implements UnaryOperator {
    public final /* synthetic */ TypedArray f$0;

    public /* synthetic */ SplashscreenContentDrawer$$ExternalSyntheticLambda5(TypedArray typedArray) {
        this.f$0 = typedArray;
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(this.f$0.getColor(60, ((Integer) obj).intValue()));
    }
}
