package com.android.p011wm.shell.common;

import android.graphics.Rect;
import java.util.Comparator;

/* renamed from: com.android.wm.shell.common.FloatingContentCoordinator$Companion$findAreaForContentAboveOrBelow$$inlined$sortedBy$1 */
/* compiled from: Comparisons.kt */
public final class C2296x8b489ee0<T> implements Comparator<T> {
    final /* synthetic */ boolean $findAbove$inlined;

    public C2296x8b489ee0(boolean z) {
        this.$findAbove$inlined = z;
    }

    public final int compare(T t, T t2) {
        boolean z = this.$findAbove$inlined;
        int i = ((Rect) t).top;
        if (z) {
            i = -i;
        }
        Rect rect = (Rect) t2;
        return ComparisonsKt__ComparisonsKt.compareValues(Integer.valueOf(i), Integer.valueOf(this.$findAbove$inlined ? -rect.top : rect.top));
    }
}
