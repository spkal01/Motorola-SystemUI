package kotlin.collections;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: Maps.kt */
class MapsKt__MapsKt extends MapsKt__MapsJVMKt {
    @NotNull
    public static <K, V> Map<K, V> emptyMap() {
        EmptyMap emptyMap = EmptyMap.INSTANCE;
        Objects.requireNonNull(emptyMap, "null cannot be cast to non-null type kotlin.collections.Map<K, V>");
        return emptyMap;
    }

    @NotNull
    public static <K, V> Map<K, V> mapOf(@NotNull Pair<? extends K, ? extends V>... pairArr) {
        Intrinsics.checkNotNullParameter(pairArr, "pairs");
        return pairArr.length > 0 ? toMap(pairArr, new LinkedHashMap(MapsKt__MapsJVMKt.mapCapacity(pairArr.length))) : emptyMap();
    }

    @NotNull
    public static <K, V> Map<K, V> mutableMapOf(@NotNull Pair<? extends K, ? extends V>... pairArr) {
        Intrinsics.checkNotNullParameter(pairArr, "pairs");
        LinkedHashMap linkedHashMap = new LinkedHashMap(MapsKt__MapsJVMKt.mapCapacity(pairArr.length));
        putAll(linkedHashMap, pairArr);
        return linkedHashMap;
    }

    public static <K, V> V getValue(@NotNull Map<K, ? extends V> map, K k) {
        Intrinsics.checkNotNullParameter(map, "$this$getValue");
        return MapsKt__MapWithDefaultKt.getOrImplicitDefaultNullable(map, k);
    }

    public static final <K, V> void putAll(@NotNull Map<? super K, ? super V> map, @NotNull Pair<? extends K, ? extends V>[] pairArr) {
        Intrinsics.checkNotNullParameter(map, "$this$putAll");
        Intrinsics.checkNotNullParameter(pairArr, "pairs");
        for (Pair<? extends K, ? extends V> pair : pairArr) {
            map.put(pair.component1(), pair.component2());
        }
    }

    public static final <K, V> void putAll(@NotNull Map<? super K, ? super V> map, @NotNull Iterable<? extends Pair<? extends K, ? extends V>> iterable) {
        Intrinsics.checkNotNullParameter(map, "$this$putAll");
        Intrinsics.checkNotNullParameter(iterable, "pairs");
        for (Pair pair : iterable) {
            map.put(pair.component1(), pair.component2());
        }
    }

    public static final <K, V> void putAll(@NotNull Map<? super K, ? super V> map, @NotNull Sequence<? extends Pair<? extends K, ? extends V>> sequence) {
        Intrinsics.checkNotNullParameter(map, "$this$putAll");
        Intrinsics.checkNotNullParameter(sequence, "pairs");
        for (Pair pair : sequence) {
            map.put(pair.component1(), pair.component2());
        }
    }

    @NotNull
    public static <K, V> Map<K, V> toMap(@NotNull Iterable<? extends Pair<? extends K, ? extends V>> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toMap");
        if (!(iterable instanceof Collection)) {
            return optimizeReadOnlyMap(toMap(iterable, new LinkedHashMap()));
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            return emptyMap();
        }
        if (size != 1) {
            return toMap(iterable, new LinkedHashMap(MapsKt__MapsJVMKt.mapCapacity(collection.size())));
        }
        return MapsKt__MapsJVMKt.mapOf((Pair) (iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next()));
    }

    @NotNull
    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(@NotNull Iterable<? extends Pair<? extends K, ? extends V>> iterable, @NotNull M m) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toMap");
        Intrinsics.checkNotNullParameter(m, "destination");
        putAll(m, iterable);
        return m;
    }

    @NotNull
    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(@NotNull Pair<? extends K, ? extends V>[] pairArr, @NotNull M m) {
        Intrinsics.checkNotNullParameter(pairArr, "$this$toMap");
        Intrinsics.checkNotNullParameter(m, "destination");
        putAll(m, pairArr);
        return m;
    }

    @NotNull
    public static <K, V> Map<K, V> toMap(@NotNull Sequence<? extends Pair<? extends K, ? extends V>> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$toMap");
        return optimizeReadOnlyMap(toMap(sequence, new LinkedHashMap()));
    }

    @NotNull
    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(@NotNull Sequence<? extends Pair<? extends K, ? extends V>> sequence, @NotNull M m) {
        Intrinsics.checkNotNullParameter(sequence, "$this$toMap");
        Intrinsics.checkNotNullParameter(m, "destination");
        putAll(m, sequence);
        return m;
    }

    @NotNull
    public static <K, V> Map<K, V> toMutableMap(@NotNull Map<? extends K, ? extends V> map) {
        Intrinsics.checkNotNullParameter(map, "$this$toMutableMap");
        return new LinkedHashMap(map);
    }

    @NotNull
    public static final <K, V> Map<K, V> optimizeReadOnlyMap(@NotNull Map<K, ? extends V> map) {
        Intrinsics.checkNotNullParameter(map, "$this$optimizeReadOnlyMap");
        int size = map.size();
        if (size == 0) {
            return emptyMap();
        }
        if (size != 1) {
            return map;
        }
        return MapsKt__MapsJVMKt.toSingletonMap(map);
    }
}
