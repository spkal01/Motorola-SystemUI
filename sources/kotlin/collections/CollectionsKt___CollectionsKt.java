package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: _Collections.kt */
class CollectionsKt___CollectionsKt extends CollectionsKt___CollectionsJvmKt {
    public static <T> boolean contains(@NotNull Iterable<? extends T> iterable, T t) {
        Intrinsics.checkNotNullParameter(iterable, "$this$contains");
        if (iterable instanceof Collection) {
            return ((Collection) iterable).contains(t);
        }
        return indexOf(iterable, t) >= 0;
    }

    public static <T> T elementAt(@NotNull Iterable<? extends T> iterable, int i) {
        Intrinsics.checkNotNullParameter(iterable, "$this$elementAt");
        if (iterable instanceof List) {
            return ((List) iterable).get(i);
        }
        return elementAtOrElse(iterable, i, new CollectionsKt___CollectionsKt$elementAt$1(i));
    }

    public static final <T> T elementAtOrElse(@NotNull Iterable<? extends T> iterable, int i, @NotNull Function1<? super Integer, ? extends T> function1) {
        Intrinsics.checkNotNullParameter(iterable, "$this$elementAtOrElse");
        Intrinsics.checkNotNullParameter(function1, "defaultValue");
        if (iterable instanceof List) {
            List list = (List) iterable;
            return (i < 0 || i > CollectionsKt__CollectionsKt.getLastIndex(list)) ? function1.invoke(Integer.valueOf(i)) : list.get(i);
        } else if (i < 0) {
            return function1.invoke(Integer.valueOf(i));
        } else {
            int i2 = 0;
            for (T next : iterable) {
                int i3 = i2 + 1;
                if (i == i2) {
                    return next;
                }
                i2 = i3;
            }
            return function1.invoke(Integer.valueOf(i));
        }
    }

    @Nullable
    public static <T> T elementAtOrNull(@NotNull Iterable<? extends T> iterable, int i) {
        Intrinsics.checkNotNullParameter(iterable, "$this$elementAtOrNull");
        if (iterable instanceof List) {
            return getOrNull((List) iterable, i);
        }
        if (i < 0) {
            return null;
        }
        int i2 = 0;
        for (T next : iterable) {
            int i3 = i2 + 1;
            if (i == i2) {
                return next;
            }
            i2 = i3;
        }
        return null;
    }

    public static final <T> T first(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$first");
        if (iterable instanceof List) {
            return first((List) iterable);
        }
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        throw new NoSuchElementException("Collection is empty.");
    }

    public static <T> T first(@NotNull List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, "$this$first");
        if (!list.isEmpty()) {
            return list.get(0);
        }
        throw new NoSuchElementException("List is empty.");
    }

    @Nullable
    public static <T> T firstOrNull(@NotNull List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, "$this$firstOrNull");
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Nullable
    public static final <T> T getOrNull(@NotNull List<? extends T> list, int i) {
        Intrinsics.checkNotNullParameter(list, "$this$getOrNull");
        if (i < 0 || i > CollectionsKt__CollectionsKt.getLastIndex(list)) {
            return null;
        }
        return list.get(i);
    }

    public static final <T> int indexOf(@NotNull Iterable<? extends T> iterable, T t) {
        Intrinsics.checkNotNullParameter(iterable, "$this$indexOf");
        if (iterable instanceof List) {
            return ((List) iterable).indexOf(t);
        }
        int i = 0;
        for (Object next : iterable) {
            if (i < 0) {
                CollectionsKt__CollectionsKt.throwIndexOverflow();
            }
            if (Intrinsics.areEqual((Object) t, (Object) next)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static final <T> T last(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$last");
        if (iterable instanceof List) {
            return last((List) iterable);
        }
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T next = it.next();
            while (it.hasNext()) {
                next = it.next();
            }
            return next;
        }
        throw new NoSuchElementException("Collection is empty.");
    }

    public static <T> T last(@NotNull List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, "$this$last");
        if (!list.isEmpty()) {
            return list.get(CollectionsKt__CollectionsKt.getLastIndex(list));
        }
        throw new NoSuchElementException("List is empty.");
    }

    public static <T> T single(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$single");
        if (iterable instanceof List) {
            return single((List) iterable);
        }
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T next = it.next();
            if (!it.hasNext()) {
                return next;
            }
            throw new IllegalArgumentException("Collection has more than one element.");
        }
        throw new NoSuchElementException("Collection is empty.");
    }

    public static final <T> T single(@NotNull List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, "$this$single");
        int size = list.size();
        if (size == 0) {
            throw new NoSuchElementException("List is empty.");
        } else if (size == 1) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("List has more than one element.");
        }
    }

    @NotNull
    public static <T> List<T> drop(@NotNull Iterable<? extends T> iterable, int i) {
        ArrayList arrayList;
        Intrinsics.checkNotNullParameter(iterable, "$this$drop");
        int i2 = 0;
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return toList(iterable);
        } else {
            if (iterable instanceof Collection) {
                Collection collection = (Collection) iterable;
                int size = collection.size() - i;
                if (size <= 0) {
                    return CollectionsKt__CollectionsKt.emptyList();
                }
                if (size == 1) {
                    return CollectionsKt__CollectionsJVMKt.listOf(last(iterable));
                }
                arrayList = new ArrayList(size);
                if (iterable instanceof List) {
                    if (iterable instanceof RandomAccess) {
                        int size2 = collection.size();
                        while (i < size2) {
                            arrayList.add(((List) iterable).get(i));
                            i++;
                        }
                    } else {
                        ListIterator listIterator = ((List) iterable).listIterator(i);
                        while (listIterator.hasNext()) {
                            arrayList.add(listIterator.next());
                        }
                    }
                    return arrayList;
                }
            } else {
                arrayList = new ArrayList();
            }
            for (Object next : iterable) {
                if (i2 >= i) {
                    arrayList.add(next);
                } else {
                    i2++;
                }
            }
            return CollectionsKt__CollectionsKt.optimizeReadOnlyList(arrayList);
        }
    }

    @NotNull
    public static <T> List<T> take(@NotNull Iterable<? extends T> iterable, int i) {
        Intrinsics.checkNotNullParameter(iterable, "$this$take");
        int i2 = 0;
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        } else {
            if (iterable instanceof Collection) {
                if (i >= ((Collection) iterable).size()) {
                    return toList(iterable);
                }
                if (i == 1) {
                    return CollectionsKt__CollectionsJVMKt.listOf(first(iterable));
                }
            }
            ArrayList arrayList = new ArrayList(i);
            for (Object add : iterable) {
                arrayList.add(add);
                i2++;
                if (i2 == i) {
                    break;
                }
            }
            return CollectionsKt__CollectionsKt.optimizeReadOnlyList(arrayList);
        }
    }

    @NotNull
    public static <T> List<T> takeLast(@NotNull List<? extends T> list, int i) {
        Intrinsics.checkNotNullParameter(list, "$this$takeLast");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        } else {
            int size = list.size();
            if (i >= size) {
                return toList(list);
            }
            if (i == 1) {
                return CollectionsKt__CollectionsJVMKt.listOf(last(list));
            }
            ArrayList arrayList = new ArrayList(i);
            if (list instanceof RandomAccess) {
                for (int i2 = size - i; i2 < size; i2++) {
                    arrayList.add(list.get(i2));
                }
            } else {
                ListIterator<? extends T> listIterator = list.listIterator(size - i);
                while (listIterator.hasNext()) {
                    arrayList.add(listIterator.next());
                }
            }
            return arrayList;
        }
    }

    @NotNull
    public static <T> List<T> reversed(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$reversed");
        if ((iterable instanceof Collection) && ((Collection) iterable).size() <= 1) {
            return toList(iterable);
        }
        List<T> mutableList = toMutableList(iterable);
        CollectionsKt___CollectionsJvmKt.reverse(mutableList);
        return mutableList;
    }

    @NotNull
    public static <T extends Comparable<? super T>> List<T> sorted(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$sorted");
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;
            if (collection.size() <= 1) {
                return toList(iterable);
            }
            Object[] array = collection.toArray(new Comparable[0]);
            Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T>");
            Comparable[] comparableArr = (Comparable[]) array;
            ArraysKt___ArraysJvmKt.sort(comparableArr);
            return ArraysKt___ArraysJvmKt.asList(comparableArr);
        }
        List<T> mutableList = toMutableList(iterable);
        CollectionsKt__MutableCollectionsJVMKt.sort(mutableList);
        return mutableList;
    }

    @NotNull
    public static <T> List<T> sortedWith(@NotNull Iterable<? extends T> iterable, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkNotNullParameter(iterable, "$this$sortedWith");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;
            if (collection.size() <= 1) {
                return toList(iterable);
            }
            Object[] array = collection.toArray(new Object[0]);
            Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T>");
            ArraysKt___ArraysJvmKt.sortWith(array, comparator);
            return ArraysKt___ArraysJvmKt.asList(array);
        }
        List<T> mutableList = toMutableList(iterable);
        CollectionsKt__MutableCollectionsJVMKt.sortWith(mutableList, comparator);
        return mutableList;
    }

    @NotNull
    public static final <T, C extends Collection<? super T>> C toCollection(@NotNull Iterable<? extends T> iterable, @NotNull C c) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toCollection");
        Intrinsics.checkNotNullParameter(c, "destination");
        for (Object add : iterable) {
            c.add(add);
        }
        return c;
    }

    @NotNull
    public static final <T> HashSet<T> toHashSet(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toHashSet");
        return (HashSet) toCollection(iterable, new HashSet(MapsKt__MapsJVMKt.mapCapacity(CollectionsKt__IterablesKt.collectionSizeOrDefault(iterable, 12))));
    }

    @NotNull
    public static <T> List<T> toList(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toList");
        if (!(iterable instanceof Collection)) {
            return CollectionsKt__CollectionsKt.optimizeReadOnlyList(toMutableList(iterable));
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        }
        if (size != 1) {
            return toMutableList(collection);
        }
        return CollectionsKt__CollectionsJVMKt.listOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
    }

    @NotNull
    public static final <T> List<T> toMutableList(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toMutableList");
        if (iterable instanceof Collection) {
            return toMutableList((Collection) iterable);
        }
        return (List) toCollection(iterable, new ArrayList());
    }

    @NotNull
    public static <T> List<T> toMutableList(@NotNull Collection<? extends T> collection) {
        Intrinsics.checkNotNullParameter(collection, "$this$toMutableList");
        return new ArrayList(collection);
    }

    @NotNull
    public static <T> Set<T> toSet(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toSet");
        if (!(iterable instanceof Collection)) {
            return SetsKt__SetsKt.optimizeReadOnlySet((Set) toCollection(iterable, new LinkedHashSet()));
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            return SetsKt__SetsKt.emptySet();
        }
        if (size != 1) {
            return (Set) toCollection(iterable, new LinkedHashSet(MapsKt__MapsJVMKt.mapCapacity(collection.size())));
        }
        return SetsKt__SetsJVMKt.setOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
    }

    @NotNull
    public static <T> List<T> distinct(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$distinct");
        return toList(toMutableSet(iterable));
    }

    @NotNull
    public static <T> Set<T> intersect(@NotNull Iterable<? extends T> iterable, @NotNull Iterable<? extends T> iterable2) {
        Intrinsics.checkNotNullParameter(iterable, "$this$intersect");
        Intrinsics.checkNotNullParameter(iterable2, "other");
        Set<T> mutableSet = toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.retainAll(mutableSet, iterable2);
        return mutableSet;
    }

    @NotNull
    public static <T> Set<T> subtract(@NotNull Iterable<? extends T> iterable, @NotNull Iterable<? extends T> iterable2) {
        Intrinsics.checkNotNullParameter(iterable, "$this$subtract");
        Intrinsics.checkNotNullParameter(iterable2, "other");
        Set<T> mutableSet = toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.removeAll(mutableSet, iterable2);
        return mutableSet;
    }

    @NotNull
    public static <T> Set<T> toMutableSet(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$toMutableSet");
        if (iterable instanceof Collection) {
            return new LinkedHashSet((Collection) iterable);
        }
        return (Set) toCollection(iterable, new LinkedHashSet());
    }

    @NotNull
    public static <T> Set<T> union(@NotNull Iterable<? extends T> iterable, @NotNull Iterable<? extends T> iterable2) {
        Intrinsics.checkNotNullParameter(iterable, "$this$union");
        Intrinsics.checkNotNullParameter(iterable2, "other");
        Set<T> mutableSet = toMutableSet(iterable);
        boolean unused = CollectionsKt__MutableCollectionsKt.addAll(mutableSet, iterable2);
        return mutableSet;
    }

    @Nullable
    public static <T extends Comparable<? super T>> T min(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$min");
        return minOrNull(iterable);
    }

    @Nullable
    public static <T extends Comparable<? super T>> T minOrNull(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$minOrNull");
        Iterator<? extends T> it = iterable.iterator();
        if (!it.hasNext()) {
            return null;
        }
        T t = (Comparable) it.next();
        while (it.hasNext()) {
            T t2 = (Comparable) it.next();
            if (t.compareTo(t2) > 0) {
                t = t2;
            }
        }
        return t;
    }

    @NotNull
    public static <T> List<T> minus(@NotNull Iterable<? extends T> iterable, T t) {
        Intrinsics.checkNotNullParameter(iterable, "$this$minus");
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(iterable, 10));
        boolean z = false;
        for (Object next : iterable) {
            boolean z2 = true;
            if (!z && Intrinsics.areEqual((Object) next, (Object) t)) {
                z = true;
                z2 = false;
            }
            if (z2) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    @NotNull
    public static <T> List<T> plus(@NotNull Collection<? extends T> collection, T t) {
        Intrinsics.checkNotNullParameter(collection, "$this$plus");
        ArrayList arrayList = new ArrayList(collection.size() + 1);
        arrayList.addAll(collection);
        arrayList.add(t);
        return arrayList;
    }

    @NotNull
    public static <T> List<T> plus(@NotNull Collection<? extends T> collection, @NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(collection, "$this$plus");
        Intrinsics.checkNotNullParameter(iterable, "elements");
        if (iterable instanceof Collection) {
            Collection collection2 = (Collection) iterable;
            ArrayList arrayList = new ArrayList(collection.size() + collection2.size());
            arrayList.addAll(collection);
            arrayList.addAll(collection2);
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList(collection);
        boolean unused = CollectionsKt__MutableCollectionsKt.addAll(arrayList2, iterable);
        return arrayList2;
    }

    public static /* synthetic */ Appendable joinTo$default(Iterable iterable, Appendable appendable, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        String str = (i2 & 2) != 0 ? ", " : charSequence;
        CharSequence charSequence5 = "";
        CharSequence charSequence6 = (i2 & 4) != 0 ? charSequence5 : charSequence2;
        if ((i2 & 8) == 0) {
            charSequence5 = charSequence3;
        }
        return joinTo(iterable, appendable, str, charSequence6, charSequence5, (i2 & 16) != 0 ? -1 : i, (i2 & 32) != 0 ? "..." : charSequence4, (i2 & 64) != 0 ? null : function1);
    }

    @NotNull
    public static final <T, A extends Appendable> A joinTo(@NotNull Iterable<? extends T> iterable, @NotNull A a, @NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull CharSequence charSequence3, int i, @NotNull CharSequence charSequence4, @Nullable Function1<? super T, ? extends CharSequence> function1) {
        Intrinsics.checkNotNullParameter(iterable, "$this$joinTo");
        Intrinsics.checkNotNullParameter(a, "buffer");
        Intrinsics.checkNotNullParameter(charSequence, "separator");
        Intrinsics.checkNotNullParameter(charSequence2, "prefix");
        Intrinsics.checkNotNullParameter(charSequence3, "postfix");
        Intrinsics.checkNotNullParameter(charSequence4, "truncated");
        a.append(charSequence2);
        int i2 = 0;
        for (Object next : iterable) {
            i2++;
            if (i2 > 1) {
                a.append(charSequence);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            StringsKt__AppendableKt.appendElement(a, next, function1);
        }
        if (i >= 0 && i2 > i) {
            a.append(charSequence4);
        }
        a.append(charSequence3);
        return a;
    }

    public static /* synthetic */ String joinToString$default(Iterable iterable, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = "";
        CharSequence charSequence6 = (i2 & 2) != 0 ? charSequence5 : charSequence2;
        if ((i2 & 4) == 0) {
            charSequence5 = charSequence3;
        }
        if ((i2 & 8) != 0) {
            i = -1;
        }
        int i3 = i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(iterable, charSequence, charSequence6, charSequence5, i3, charSequence7, function1);
    }

    @NotNull
    public static final <T> String joinToString(@NotNull Iterable<? extends T> iterable, @NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull CharSequence charSequence3, int i, @NotNull CharSequence charSequence4, @Nullable Function1<? super T, ? extends CharSequence> function1) {
        Intrinsics.checkNotNullParameter(iterable, "$this$joinToString");
        Intrinsics.checkNotNullParameter(charSequence, "separator");
        Intrinsics.checkNotNullParameter(charSequence2, "prefix");
        Intrinsics.checkNotNullParameter(charSequence3, "postfix");
        Intrinsics.checkNotNullParameter(charSequence4, "truncated");
        String sb = ((StringBuilder) joinTo(iterable, new StringBuilder(), charSequence, charSequence2, charSequence3, i, charSequence4, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    @NotNull
    public static <T> Sequence<T> asSequence(@NotNull Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "$this$asSequence");
        return new CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1(iterable);
    }
}
