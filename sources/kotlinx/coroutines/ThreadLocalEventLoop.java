package kotlinx.coroutines;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: EventLoop.common.kt */
public final class ThreadLocalEventLoop {
    public static final ThreadLocalEventLoop INSTANCE = new ThreadLocalEventLoop();
    private static final ThreadLocal<EventLoop> ref = new ThreadLocal<>();

    private ThreadLocalEventLoop() {
    }

    @NotNull
    public final EventLoop getEventLoop$kotlinx_coroutines_core() {
        ThreadLocal<EventLoop> threadLocal = ref;
        EventLoop eventLoop = threadLocal.get();
        if (eventLoop != null) {
            return eventLoop;
        }
        EventLoop createEventLoop = EventLoopKt.createEventLoop();
        threadLocal.set(createEventLoop);
        return createEventLoop;
    }

    public final void resetEventLoop$kotlinx_coroutines_core() {
        ref.set((Object) null);
    }

    public final void setEventLoop$kotlinx_coroutines_core(@NotNull EventLoop eventLoop) {
        Intrinsics.checkParameterIsNotNull(eventLoop, "eventLoop");
        ref.set(eventLoop);
    }
}
