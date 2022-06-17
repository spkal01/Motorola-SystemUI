package kotlinx.coroutines;

import java.util.Iterator;
import java.util.List;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: CoroutineExceptionHandlerImpl.kt */
public final class CoroutineExceptionHandlerImplKt {
    private static final List<CoroutineExceptionHandler> handlers;

    static {
        Iterator m = CoroutineExceptionHandlerImplKt$$ExternalSyntheticServiceLoad0.m110m();
        Intrinsics.checkExpressionValueIsNotNull(m, "ServiceLoader.load(\n    ….classLoader\n).iterator()");
        handlers = SequencesKt___SequencesKt.toList(SequencesKt__SequencesKt.asSequence(m));
    }

    public static final void handleCoroutineExceptionImpl(@NotNull CoroutineContext coroutineContext, @NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(coroutineContext, "context");
        Intrinsics.checkParameterIsNotNull(th, "exception");
        for (CoroutineExceptionHandler handleException : handlers) {
            try {
                handleException.handleException(coroutineContext, th);
            } catch (Throwable th2) {
                Thread currentThread = Thread.currentThread();
                Intrinsics.checkExpressionValueIsNotNull(currentThread, "currentThread");
                currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, CoroutineExceptionHandlerKt.handlerException(th, th2));
            }
        }
        Thread currentThread2 = Thread.currentThread();
        Intrinsics.checkExpressionValueIsNotNull(currentThread2, "currentThread");
        currentThread2.getUncaughtExceptionHandler().uncaughtException(currentThread2, th);
    }
}
