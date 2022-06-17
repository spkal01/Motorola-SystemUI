package kotlin.collections;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: _Sets.kt */
class SetsKt___SetsKt extends SetsKt__SetsKt {
    @NotNull
    public static <T> Set<T> minus(@NotNull Set<? extends T> set, @NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(set, "$this$minus");
        Intrinsics.checkNotNullParameter(iterable, "elements");
        Collection<? extends T> convertToSetForSetOperationWith = CollectionsKt__IterablesKt.convertToSetForSetOperationWith(iterable, set);
        if (convertToSetForSetOperationWith.isEmpty()) {
            return CollectionsKt___CollectionsKt.toSet(set);
        }
        if (convertToSetForSetOperationWith instanceof Set) {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            for (T next : set) {
                if (!convertToSetForSetOperationWith.contains(next)) {
                    linkedHashSet.add(next);
                }
            }
            return linkedHashSet;
        }
        LinkedHashSet linkedHashSet2 = new LinkedHashSet(set);
        linkedHashSet2.removeAll(convertToSetForSetOperationWith);
        return linkedHashSet2;
    }
}
