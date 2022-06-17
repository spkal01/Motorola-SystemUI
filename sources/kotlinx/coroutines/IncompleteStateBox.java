package kotlinx.coroutines;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: JobSupport.kt */
final class IncompleteStateBox {
    @NotNull
    public final Incomplete state;

    public IncompleteStateBox(@NotNull Incomplete incomplete) {
        Intrinsics.checkParameterIsNotNull(incomplete, "state");
        this.state = incomplete;
    }
}
