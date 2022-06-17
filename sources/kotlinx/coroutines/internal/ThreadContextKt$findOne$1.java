package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlinx.coroutines.ThreadContextElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ThreadContext.kt */
final class ThreadContextKt$findOne$1 extends Lambda implements Function2<ThreadContextElement<?>, CoroutineContext.Element, ThreadContextElement<?>> {
    public static final ThreadContextKt$findOne$1 INSTANCE = new ThreadContextKt$findOne$1();

    ThreadContextKt$findOne$1() {
        super(2);
    }

    @Nullable
    public final ThreadContextElement<?> invoke(@Nullable ThreadContextElement<?> threadContextElement, @NotNull CoroutineContext.Element element) {
        Intrinsics.checkParameterIsNotNull(element, "element");
        if (threadContextElement != null) {
            return threadContextElement;
        }
        if (!(element instanceof ThreadContextElement)) {
            element = null;
        }
        return (ThreadContextElement) element;
    }
}
