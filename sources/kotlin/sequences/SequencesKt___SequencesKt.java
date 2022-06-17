package kotlin.sequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import kotlin.Pair;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: _Sequences.kt */
class SequencesKt___SequencesKt extends SequencesKt___SequencesJvmKt {
    @NotNull
    public static <T> Sequence<T> filter(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, Boolean> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$filter");
        Intrinsics.checkNotNullParameter(function1, "predicate");
        return new FilteringSequence(sequence, true, function1);
    }

    @NotNull
    public static <T> Sequence<T> filterNot(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, Boolean> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$filterNot");
        Intrinsics.checkNotNullParameter(function1, "predicate");
        return new FilteringSequence(sequence, false, function1);
    }

    @NotNull
    public static <T> Sequence<T> filterNotNull(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$filterNotNull");
        Sequence<T> filterNot = filterNot(sequence, SequencesKt___SequencesKt$filterNotNull$1.INSTANCE);
        Objects.requireNonNull(filterNot, "null cannot be cast to non-null type kotlin.sequences.Sequence<T>");
        return filterNot;
    }

    @NotNull
    public static <T> Sequence<T> take(@NotNull Sequence<? extends T> sequence, int i) {
        Intrinsics.checkNotNullParameter(sequence, "$this$take");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return SequencesKt__SequencesKt.emptySequence();
        } else {
            if (sequence instanceof DropTakeSequence) {
                return ((DropTakeSequence) sequence).take(i);
            }
            return new TakeSequence(sequence, i);
        }
    }

    @NotNull
    public static <T> Sequence<T> sortedWith(@NotNull Sequence<? extends T> sequence, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkNotNullParameter(sequence, "$this$sortedWith");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        return new SequencesKt___SequencesKt$sortedWith$1(sequence, comparator);
    }

    @NotNull
    public static final <T, C extends Collection<? super T>> C toCollection(@NotNull Sequence<? extends T> sequence, @NotNull C c) {
        Intrinsics.checkNotNullParameter(sequence, "$this$toCollection");
        Intrinsics.checkNotNullParameter(c, "destination");
        for (Object add : sequence) {
            c.add(add);
        }
        return c;
    }

    @NotNull
    public static <T> List<T> toList(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$toList");
        return CollectionsKt__CollectionsKt.optimizeReadOnlyList(toMutableList(sequence));
    }

    @NotNull
    public static final <T> List<T> toMutableList(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$toMutableList");
        return (List) toCollection(sequence, new ArrayList());
    }

    @NotNull
    public static <T, R> Sequence<R> flatMap(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, ? extends Sequence<? extends R>> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$flatMap");
        Intrinsics.checkNotNullParameter(function1, "transform");
        return new FlatteningSequence(sequence, function1, SequencesKt___SequencesKt$flatMap$2.INSTANCE);
    }

    @NotNull
    public static <T, R> Sequence<R> map(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, ? extends R> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$map");
        Intrinsics.checkNotNullParameter(function1, "transform");
        return new TransformingSequence(sequence, function1);
    }

    @NotNull
    public static <T, R> Sequence<R> mapIndexed(@NotNull Sequence<? extends T> sequence, @NotNull Function2<? super Integer, ? super T, ? extends R> function2) {
        Intrinsics.checkNotNullParameter(sequence, "$this$mapIndexed");
        Intrinsics.checkNotNullParameter(function2, "transform");
        return new TransformingIndexedSequence(sequence, function2);
    }

    @NotNull
    public static <T, R> Sequence<R> mapNotNull(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, ? extends R> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$mapNotNull");
        Intrinsics.checkNotNullParameter(function1, "transform");
        return filterNotNull(new TransformingSequence(sequence, function1));
    }

    @NotNull
    public static <T> Sequence<T> distinct(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$distinct");
        return distinctBy(sequence, SequencesKt___SequencesKt$distinct$1.INSTANCE);
    }

    @NotNull
    public static final <T, K> Sequence<T> distinctBy(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, ? extends K> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$distinctBy");
        Intrinsics.checkNotNullParameter(function1, "selector");
        return new DistinctSequence(sequence, function1);
    }

    @NotNull
    public static <T> Sequence<T> plus(@NotNull Sequence<? extends T> sequence, @NotNull Sequence<? extends T> sequence2) {
        Intrinsics.checkNotNullParameter(sequence, "$this$plus");
        Intrinsics.checkNotNullParameter(sequence2, "elements");
        return SequencesKt__SequencesKt.flatten(SequencesKt__SequencesKt.sequenceOf(sequence, sequence2));
    }

    @NotNull
    public static <T> Sequence<Pair<T, T>> zipWithNext(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$zipWithNext");
        return zipWithNext(sequence, SequencesKt___SequencesKt$zipWithNext$1.INSTANCE);
    }

    @NotNull
    public static final <T, R> Sequence<R> zipWithNext(@NotNull Sequence<? extends T> sequence, @NotNull Function2<? super T, ? super T, ? extends R> function2) {
        Intrinsics.checkNotNullParameter(sequence, "$this$zipWithNext");
        Intrinsics.checkNotNullParameter(function2, "transform");
        return SequencesKt__SequenceBuilderKt.sequence(new SequencesKt___SequencesKt$zipWithNext$2(sequence, function2, (Continuation) null));
    }

    @NotNull
    public static final <T, A extends Appendable> A joinTo(@NotNull Sequence<? extends T> sequence, @NotNull A a, @NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull CharSequence charSequence3, int i, @NotNull CharSequence charSequence4, @Nullable Function1<? super T, ? extends CharSequence> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$joinTo");
        Intrinsics.checkNotNullParameter(a, "buffer");
        Intrinsics.checkNotNullParameter(charSequence, "separator");
        Intrinsics.checkNotNullParameter(charSequence2, "prefix");
        Intrinsics.checkNotNullParameter(charSequence3, "postfix");
        Intrinsics.checkNotNullParameter(charSequence4, "truncated");
        a.append(charSequence2);
        int i2 = 0;
        for (Object next : sequence) {
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

    public static /* synthetic */ String joinToString$default(Sequence sequence, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
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
        return joinToString(sequence, charSequence, charSequence6, charSequence5, i3, charSequence7, function1);
    }

    @NotNull
    public static final <T> String joinToString(@NotNull Sequence<? extends T> sequence, @NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull CharSequence charSequence3, int i, @NotNull CharSequence charSequence4, @Nullable Function1<? super T, ? extends CharSequence> function1) {
        Intrinsics.checkNotNullParameter(sequence, "$this$joinToString");
        Intrinsics.checkNotNullParameter(charSequence, "separator");
        Intrinsics.checkNotNullParameter(charSequence2, "prefix");
        Intrinsics.checkNotNullParameter(charSequence3, "postfix");
        Intrinsics.checkNotNullParameter(charSequence4, "truncated");
        String sb = ((StringBuilder) joinTo(sequence, new StringBuilder(), charSequence, charSequence2, charSequence3, i, charSequence4, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    @NotNull
    public static <T> Iterable<T> asIterable(@NotNull Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this$asIterable");
        return new SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1(sequence);
    }
}
