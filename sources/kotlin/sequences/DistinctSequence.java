package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Sequences.kt */
public final class DistinctSequence<T, K> implements Sequence<T> {
    private final Function1<T, K> keySelector;
    private final Sequence<T> source;

    public DistinctSequence(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, ? extends K> function1) {
        Intrinsics.checkNotNullParameter(sequence, "source");
        Intrinsics.checkNotNullParameter(function1, "keySelector");
        this.source = sequence;
        this.keySelector = function1;
    }

    @NotNull
    public Iterator<T> iterator() {
        return new DistinctIterator(this.source.iterator(), this.keySelector);
    }
}
