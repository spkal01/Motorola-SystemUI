package com.android.p011wm.shell.startingsurface;

import android.content.res.TypedArray;
import java.util.function.UnaryOperator;

/* renamed from: com.android.wm.shell.startingsurface.SplashscreenContentDrawer$$ExternalSyntheticLambda6 */
public final /* synthetic */ class SplashscreenContentDrawer$$ExternalSyntheticLambda6 implements UnaryOperator {
    public final /* synthetic */ TypedArray f$0;

    public /* synthetic */ SplashscreenContentDrawer$$ExternalSyntheticLambda6(TypedArray typedArray) {
        this.f$0 = typedArray;
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(this.f$0.getColor(56, ((Integer) obj).intValue()));
    }
}
