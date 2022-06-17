package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Sequences.kt */
public final class FilteringSequence<T> implements Sequence<T> {
    /* access modifiers changed from: private */
    public final Function1<T, Boolean> predicate;
    /* access modifiers changed from: private */
    public final boolean sendWhen;
    /* access modifiers changed from: private */
    public final Sequence<T> sequence;

    public FilteringSequence(@NotNull Sequence<? extends T> sequence2, boolean z, @NotNull Function1<? super T, Boolean> function1) {
        Intrinsics.checkNotNullParameter(sequence2, "sequence");
        Intrinsics.checkNotNullParameter(function1, "predicate");
        this.sequence = sequence2;
        this.sendWhen = z;
        this.predicate = function1;
    }

    @NotNull
    public Iterator<T> iterator() {
        return new FilteringSequence$iterator$1(this);
    }
}
