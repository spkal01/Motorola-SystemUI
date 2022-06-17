package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ThreadContext.kt */
final class ThreadState {

    /* renamed from: a */
    private Object[] f198a;
    @NotNull
    private final CoroutineContext context;

    /* renamed from: i */
    private int f199i;

    public ThreadState(@NotNull CoroutineContext coroutineContext, int i) {
        Intrinsics.checkParameterIsNotNull(coroutineContext, "context");
        this.context = coroutineContext;
        this.f198a = new Object[i];
    }

    @NotNull
    public final CoroutineContext getContext() {
        return this.context;
    }

    public final void append(@Nullable Object obj) {
        Object[] objArr = this.f198a;
        int i = this.f199i;
        this.f199i = i + 1;
        objArr[i] = obj;
    }

    @Nullable
    public final Object take() {
        Object[] objArr = this.f198a;
        int i = this.f199i;
        this.f199i = i + 1;
        return objArr[i];
    }

    public final void start() {
        this.f199i = 0;
    }
}
