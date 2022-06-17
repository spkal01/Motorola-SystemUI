package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlinx.coroutines.ThreadContextElement;
import org.jetbrains.annotations.NotNull;

/* compiled from: ThreadContext.kt */
final class ThreadContextKt$updateState$1 extends Lambda implements Function2<ThreadState, CoroutineContext.Element, ThreadState> {
    public static final ThreadContextKt$updateState$1 INSTANCE = new ThreadContextKt$updateState$1();

    ThreadContextKt$updateState$1() {
        super(2);
    }

    @NotNull
    public final ThreadState invoke(@NotNull ThreadState threadState, @NotNull CoroutineContext.Element element) {
        Intrinsics.checkParameterIsNotNull(threadState, "state");
        Intrinsics.checkParameterIsNotNull(element, "element");
        if (element instanceof ThreadContextElement) {
            threadState.append(((ThreadContextElement) element).updateThreadContext(threadState.getContext()));
        }
        return threadState;
    }
}
