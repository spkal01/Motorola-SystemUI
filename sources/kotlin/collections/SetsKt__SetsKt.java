package kotlin.collections;

import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Sets.kt */
class SetsKt__SetsKt extends SetsKt__SetsJVMKt {
    @NotNull
    public static <T> Set<T> emptySet() {
        return EmptySet.INSTANCE;
    }

    @NotNull
    public static <T> Set<T> setOf(@NotNull T... tArr) {
        Intrinsics.checkNotNullParameter(tArr, "elements");
        return tArr.length > 0 ? ArraysKt___ArraysKt.toSet(tArr) : emptySet();
    }

    @NotNull
    public static <T> Set<T> mutableSetOf(@NotNull T... tArr) {
        Intrinsics.checkNotNullParameter(tArr, "elements");
        return (Set) ArraysKt___ArraysKt.toCollection(tArr, new LinkedHashSet(MapsKt__MapsJVMKt.mapCapacity(tArr.length)));
    }

    @NotNull
    public static final <T> Set<T> optimizeReadOnlySet(@NotNull Set<? extends T> set) {
        Intrinsics.checkNotNullParameter(set, "$this$optimizeReadOnlySet");
        int size = set.size();
        if (size == 0) {
            return emptySet();
        }
        if (size != 1) {
            return set;
        }
        return SetsKt__SetsJVMKt.setOf(set.iterator().next());
    }
}
