package kotlin.sequences;

import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: _Sequences.kt */
final class SequencesKt___SequencesKt$zipWithNext$1 extends Lambda implements Function2<T, T, Pair<? extends T, ? extends T>> {
    public static final SequencesKt___SequencesKt$zipWithNext$1 INSTANCE = new SequencesKt___SequencesKt$zipWithNext$1();

    SequencesKt___SequencesKt$zipWithNext$1() {
        super(2);
    }

    @NotNull
    public final Pair<T, T> invoke(T t, T t2) {
        return TuplesKt.m104to(t, t2);
    }
}
