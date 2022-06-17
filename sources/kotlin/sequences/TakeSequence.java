package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Sequences.kt */
public final class TakeSequence<T> implements Sequence<T>, DropTakeSequence<T> {
    /* access modifiers changed from: private */
    public final int count;
    /* access modifiers changed from: private */
    public final Sequence<T> sequence;

    public TakeSequence(@NotNull Sequence<? extends T> sequence2, int i) {
        Intrinsics.checkNotNullParameter(sequence2, "sequence");
        this.sequence = sequence2;
        this.count = i;
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("count must be non-negative, but was " + i + '.').toString());
        }
    }

    @NotNull
    public Sequence<T> take(int i) {
        return i >= this.count ? this : new TakeSequence(this.sequence, i);
    }

    @NotNull
    public Iterator<T> iterator() {
        return new TakeSequence$iterator$1(this);
    }
}
