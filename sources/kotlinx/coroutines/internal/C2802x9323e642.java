package kotlinx.coroutines.internal;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$$inlined$sortedByDescending$1 */
/* compiled from: Comparisons.kt */
public final class C2802x9323e642<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        Constructor constructor = (Constructor) t2;
        Intrinsics.checkExpressionValueIsNotNull(constructor, "it");
        Integer valueOf = Integer.valueOf(constructor.getParameterTypes().length);
        Constructor constructor2 = (Constructor) t;
        Intrinsics.checkExpressionValueIsNotNull(constructor2, "it");
        return ComparisonsKt__ComparisonsKt.compareValues(valueOf, Integer.valueOf(constructor2.getParameterTypes().length));
    }
}
