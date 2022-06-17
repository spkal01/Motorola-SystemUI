package com.android.p011wm.shell.legacysplitscreen;

import android.graphics.Rect;
import java.lang.ref.WeakReference;
import java.util.function.Predicate;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$$ExternalSyntheticLambda4 */
public final /* synthetic */ class LegacySplitScreenController$$ExternalSyntheticLambda4 implements Predicate {
    public final /* synthetic */ Rect f$0;
    public final /* synthetic */ Rect f$1;

    public /* synthetic */ LegacySplitScreenController$$ExternalSyntheticLambda4(Rect rect, Rect rect2) {
        this.f$0 = rect;
        this.f$1 = rect2;
    }

    public final boolean test(Object obj) {
        return LegacySplitScreenController.lambda$notifyBoundsChanged$5(this.f$0, this.f$1, (WeakReference) obj);
    }
}
