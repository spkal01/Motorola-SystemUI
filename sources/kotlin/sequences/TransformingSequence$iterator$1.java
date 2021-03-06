package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.jetbrains.annotations.NotNull;

/* compiled from: Sequences.kt */
public final class TransformingSequence$iterator$1 implements Iterator<R>, KMappedMarker {
    @NotNull
    private final Iterator<T> iterator;
    final /* synthetic */ TransformingSequence this$0;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    TransformingSequence$iterator$1(TransformingSequence transformingSequence) {
        this.this$0 = transformingSequence;
        this.iterator = transformingSequence.sequence.iterator();
    }

    public R next() {
        return this.this$0.transformer.invoke(this.iterator.next());
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
