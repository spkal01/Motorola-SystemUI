package com.android.keyguard;

import android.util.SparseArray;
import kotlin.jvm.functions.Function0;

/* compiled from: TextAnimator.kt */
public final class TextAnimatorKt {
    /* access modifiers changed from: private */
    public static final <V> V getOrElse(SparseArray<V> sparseArray, int i, Function0<? extends V> function0) {
        V v = sparseArray.get(i);
        if (v != null) {
            return v;
        }
        V invoke = function0.invoke();
        sparseArray.put(i, invoke);
        return invoke;
    }
}
