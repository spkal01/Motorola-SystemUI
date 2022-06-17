package kotlin.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: _Arrays.kt */
class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt {
    public static <T> boolean contains(@NotNull T[] tArr, T t) {
        Intrinsics.checkNotNullParameter(tArr, "$this$contains");
        return indexOf(tArr, t) >= 0;
    }

    public static boolean contains(@NotNull int[] iArr, int i) {
        Intrinsics.checkNotNullParameter(iArr, "$this$contains");
        return indexOf(iArr, i) >= 0;
    }

    public static final <T> int indexOf(@NotNull T[] tArr, T t) {
        Intrinsics.checkNotNullParameter(tArr, "$this$indexOf");
        int i = 0;
        if (t == null) {
            int length = tArr.length;
            while (i < length) {
                if (tArr[i] == null) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        int length2 = tArr.length;
        while (i < length2) {
            if (Intrinsics.areEqual((Object) t, (Object) tArr[i])) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static final int indexOf(@NotNull int[] iArr, int i) {
        Intrinsics.checkNotNullParameter(iArr, "$this$indexOf");
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i == iArr[i2]) {
                return i2;
            }
        }
        return -1;
    }

    public static char single(@NotNull char[] cArr) {
        Intrinsics.checkNotNullParameter(cArr, "$this$single");
        int length = cArr.length;
        if (length == 0) {
            throw new NoSuchElementException("Array is empty.");
        } else if (length == 1) {
            return cArr[0];
        } else {
            throw new IllegalArgumentException("Array has more than one element.");
        }
    }

    @Nullable
    public static <T> T singleOrNull(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$singleOrNull");
        if (tArr.length == 1) {
            return tArr[0];
        }
        return null;
    }

    @NotNull
    public static <T> List<T> drop(@NotNull T[] tArr, int i) {
        Intrinsics.checkNotNullParameter(tArr, "$this$drop");
        if (i >= 0) {
            return takeLast(tArr, RangesKt___RangesKt.coerceAtLeast(tArr.length - i, 0));
        }
        throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
    }

    @NotNull
    public static <T> List<T> filterNotNull(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$filterNotNull");
        return (List) filterNotNullTo(tArr, new ArrayList());
    }

    @NotNull
    public static final <C extends Collection<? super T>, T> C filterNotNullTo(@NotNull T[] tArr, @NotNull C c) {
        Intrinsics.checkNotNullParameter(tArr, "$this$filterNotNullTo");
        Intrinsics.checkNotNullParameter(c, "destination");
        for (T t : tArr) {
            if (t != null) {
                c.add(t);
            }
        }
        return c;
    }

    @NotNull
    public static final <T> List<T> takeLast(@NotNull T[] tArr, int i) {
        Intrinsics.checkNotNullParameter(tArr, "$this$takeLast");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        } else {
            int length = tArr.length;
            if (i >= length) {
                return toList(tArr);
            }
            if (i == 1) {
                return CollectionsKt__CollectionsJVMKt.listOf(tArr[length - 1]);
            }
            ArrayList arrayList = new ArrayList(i);
            for (int i2 = length - i; i2 < length; i2++) {
                arrayList.add(tArr[i2]);
            }
            return arrayList;
        }
    }

    @NotNull
    public static final <T> T[] sortedArrayWith(@NotNull T[] tArr, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkNotNullParameter(tArr, "$this$sortedArrayWith");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        if (tArr.length == 0) {
            return tArr;
        }
        T[] copyOf = Arrays.copyOf(tArr, tArr.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
        ArraysKt___ArraysJvmKt.sortWith(copyOf, comparator);
        return copyOf;
    }

    @NotNull
    public static <T> List<T> sortedWith(@NotNull T[] tArr, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkNotNullParameter(tArr, "$this$sortedWith");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        return ArraysKt___ArraysJvmKt.asList(sortedArrayWith(tArr, comparator));
    }

    public static final <T> int getLastIndex(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$lastIndex");
        return tArr.length - 1;
    }

    @NotNull
    public static final <T, C extends Collection<? super T>> C toCollection(@NotNull T[] tArr, @NotNull C c) {
        Intrinsics.checkNotNullParameter(tArr, "$this$toCollection");
        Intrinsics.checkNotNullParameter(c, "destination");
        for (T add : tArr) {
            c.add(add);
        }
        return c;
    }

    @NotNull
    public static final <T> List<T> toList(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$toList");
        int length = tArr.length;
        if (length == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        }
        if (length != 1) {
            return toMutableList(tArr);
        }
        return CollectionsKt__CollectionsJVMKt.listOf(tArr[0]);
    }

    @NotNull
    public static List<Integer> toList(@NotNull int[] iArr) {
        Intrinsics.checkNotNullParameter(iArr, "$this$toList");
        int length = iArr.length;
        if (length == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        }
        if (length != 1) {
            return toMutableList(iArr);
        }
        return CollectionsKt__CollectionsJVMKt.listOf(Integer.valueOf(iArr[0]));
    }

    @NotNull
    public static <T> List<T> toMutableList(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$toMutableList");
        return new ArrayList(CollectionsKt__CollectionsKt.asCollection(tArr));
    }

    @NotNull
    public static List<Integer> toMutableList(@NotNull int[] iArr) {
        Intrinsics.checkNotNullParameter(iArr, "$this$toMutableList");
        ArrayList arrayList = new ArrayList(iArr.length);
        for (int valueOf : iArr) {
            arrayList.add(Integer.valueOf(valueOf));
        }
        return arrayList;
    }

    @NotNull
    public static <T> Set<T> toSet(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$toSet");
        int length = tArr.length;
        if (length == 0) {
            return SetsKt__SetsKt.emptySet();
        }
        if (length != 1) {
            return (Set) toCollection(tArr, new LinkedHashSet(MapsKt__MapsJVMKt.mapCapacity(tArr.length)));
        }
        return SetsKt__SetsJVMKt.setOf(tArr[0]);
    }

    @NotNull
    public static <T> Sequence<T> asSequence(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "$this$asSequence");
        if (tArr.length == 0) {
            return SequencesKt__SequencesKt.emptySequence();
        }
        return new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1(tArr);
    }
}
