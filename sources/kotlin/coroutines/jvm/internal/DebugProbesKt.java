package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DebugProbes.kt */
public final class DebugProbesKt {
    @NotNull
    public static final <T> Continuation<T> probeCoroutineCreated(@NotNull Continuation<? super T> continuation) {
        Intrinsics.checkNotNullParameter(continuation, "completion");
        return continuation;
    }

    public static final void probeCoroutineResumed(@NotNull Continuation<?> continuation) {
        Intrinsics.checkNotNullParameter(continuation, "frame");
    }

    public static final void probeCoroutineSuspended(@NotNull Continuation<?> continuation) {
        Intrinsics.checkNotNullParameter(continuation, "frame");
    }
}
