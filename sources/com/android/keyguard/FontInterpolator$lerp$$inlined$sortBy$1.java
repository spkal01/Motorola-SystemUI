package com.android.keyguard;

import android.graphics.fonts.FontVariationAxis;
import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class FontInterpolator$lerp$$inlined$sortBy$1<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        return ComparisonsKt__ComparisonsKt.compareValues(((FontVariationAxis) t).getTag(), ((FontVariationAxis) t2).getTag());
    }
}
