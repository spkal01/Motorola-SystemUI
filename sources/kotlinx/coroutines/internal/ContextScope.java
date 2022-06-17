package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

/* compiled from: Scopes.kt */
public final class ContextScope implements CoroutineScope {
    @NotNull
    private final CoroutineContext coroutineContext;

    public ContextScope(@NotNull CoroutineContext coroutineContext2) {
        Intrinsics.checkParameterIsNotNull(coroutineContext2, "context");
        this.coroutineContext = coroutineContext2;
    }

    @NotNull
    public CoroutineContext getCoroutineContext() {
        return this.coroutineContext;
    }
}
