package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.ThreadContextKt;
import org.jetbrains.annotations.NotNull;

/* compiled from: Dispatched.kt */
public final class DispatchedKt {
    /* access modifiers changed from: private */
    public static final Symbol UNDEFINED = new Symbol("UNDEFINED");

    private static final void resumeUnconfined(@NotNull DispatchedTask<?> dispatchedTask) {
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(dispatchedTask);
            return;
        }
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            resume(dispatchedTask, dispatchedTask.getDelegate$kotlinx_coroutines_core(), 3);
            do {
            } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
        } catch (Throwable th) {
            eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
            throw th;
        }
        eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
    }

    public static final <T> void resumeCancellable(@NotNull Continuation<? super T> continuation, T t) {
        boolean z;
        CoroutineContext context;
        Object updateThreadContext;
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeCancellable");
        if (continuation instanceof DispatchedContinuation) {
            DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) continuation;
            if (dispatchedContinuation.dispatcher.isDispatchNeeded(dispatchedContinuation.getContext())) {
                dispatchedContinuation._state = t;
                dispatchedContinuation.resumeMode = 1;
                dispatchedContinuation.dispatcher.dispatch(dispatchedContinuation.getContext(), dispatchedContinuation);
                return;
            }
            EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
            if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
                dispatchedContinuation._state = t;
                dispatchedContinuation.resumeMode = 1;
                eventLoop$kotlinx_coroutines_core.dispatchUnconfined(dispatchedContinuation);
                return;
            }
            eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
            try {
                Job job = (Job) dispatchedContinuation.getContext().get(Job.Key);
                if (job == null || job.isActive()) {
                    z = false;
                } else {
                    CancellationException cancellationException = job.getCancellationException();
                    Result.Companion companion = Result.Companion;
                    dispatchedContinuation.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(cancellationException)));
                    z = true;
                }
                if (!z) {
                    context = dispatchedContinuation.getContext();
                    updateThreadContext = ThreadContextKt.updateThreadContext(context, dispatchedContinuation.countOrElement);
                    Continuation<T> continuation2 = dispatchedContinuation.continuation;
                    Result.Companion companion2 = Result.Companion;
                    continuation2.resumeWith(Result.m849constructorimpl(t));
                    Unit unit = Unit.INSTANCE;
                    ThreadContextKt.restoreThreadContext(context, updateThreadContext);
                }
                do {
                } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
            } catch (Throwable th) {
                try {
                    dispatchedContinuation.handleFatalException$kotlinx_coroutines_core(th, (Throwable) null);
                } catch (Throwable th2) {
                    eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
                    throw th2;
                }
            }
            eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
            return;
        }
        Result.Companion companion3 = Result.Companion;
        continuation.resumeWith(Result.m849constructorimpl(t));
    }

    public static final <T> void resumeCancellableWithException(@NotNull Continuation<? super T> continuation, @NotNull Throwable th) {
        CoroutineContext context;
        Object updateThreadContext;
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeCancellableWithException");
        Intrinsics.checkParameterIsNotNull(th, "exception");
        if (continuation instanceof DispatchedContinuation) {
            DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) continuation;
            CoroutineContext context2 = dispatchedContinuation.continuation.getContext();
            boolean z = false;
            CompletedExceptionally completedExceptionally = new CompletedExceptionally(th, false, 2, (DefaultConstructorMarker) null);
            if (dispatchedContinuation.dispatcher.isDispatchNeeded(context2)) {
                dispatchedContinuation._state = new CompletedExceptionally(th, false, 2, (DefaultConstructorMarker) null);
                dispatchedContinuation.resumeMode = 1;
                dispatchedContinuation.dispatcher.dispatch(context2, dispatchedContinuation);
                return;
            }
            EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
            if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
                dispatchedContinuation._state = completedExceptionally;
                dispatchedContinuation.resumeMode = 1;
                eventLoop$kotlinx_coroutines_core.dispatchUnconfined(dispatchedContinuation);
                return;
            }
            eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
            try {
                Job job = (Job) dispatchedContinuation.getContext().get(Job.Key);
                if (job != null && !job.isActive()) {
                    CancellationException cancellationException = job.getCancellationException();
                    Result.Companion companion = Result.Companion;
                    dispatchedContinuation.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(cancellationException)));
                    z = true;
                }
                if (!z) {
                    context = dispatchedContinuation.getContext();
                    updateThreadContext = ThreadContextKt.updateThreadContext(context, dispatchedContinuation.countOrElement);
                    Continuation<T> continuation2 = dispatchedContinuation.continuation;
                    Result.Companion companion2 = Result.Companion;
                    continuation2.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(th, continuation2))));
                    Unit unit = Unit.INSTANCE;
                    ThreadContextKt.restoreThreadContext(context, updateThreadContext);
                }
                do {
                } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
            } catch (Throwable th2) {
                try {
                    dispatchedContinuation.handleFatalException$kotlinx_coroutines_core(th2, (Throwable) null);
                } catch (Throwable th3) {
                    eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
                    throw th3;
                }
            }
            eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
            return;
        }
        Result.Companion companion3 = Result.Companion;
        continuation.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(th, continuation))));
    }

    public static final <T> void resumeDirect(@NotNull Continuation<? super T> continuation, T t) {
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeDirect");
        if (continuation instanceof DispatchedContinuation) {
            Continuation<T> continuation2 = ((DispatchedContinuation) continuation).continuation;
            Result.Companion companion = Result.Companion;
            continuation2.resumeWith(Result.m849constructorimpl(t));
            return;
        }
        Result.Companion companion2 = Result.Companion;
        continuation.resumeWith(Result.m849constructorimpl(t));
    }

    public static final <T> void resumeDirectWithException(@NotNull Continuation<? super T> continuation, @NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeDirectWithException");
        Intrinsics.checkParameterIsNotNull(th, "exception");
        if (continuation instanceof DispatchedContinuation) {
            Continuation<T> continuation2 = ((DispatchedContinuation) continuation).continuation;
            Result.Companion companion = Result.Companion;
            continuation2.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(th, continuation2))));
            return;
        }
        Result.Companion companion2 = Result.Companion;
        continuation.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(th, continuation))));
    }

    public static final boolean yieldUndispatched(@NotNull DispatchedContinuation<? super Unit> dispatchedContinuation) {
        Intrinsics.checkParameterIsNotNull(dispatchedContinuation, "$this$yieldUndispatched");
        Unit unit = Unit.INSTANCE;
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedQueueEmpty()) {
            return false;
        }
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            dispatchedContinuation._state = unit;
            dispatchedContinuation.resumeMode = 1;
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(dispatchedContinuation);
            return true;
        }
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            dispatchedContinuation.run();
            do {
            } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
        } catch (Throwable th) {
            eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
            throw th;
        }
        eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
        return false;
    }

    public static final <T> void dispatch(@NotNull DispatchedTask<? super T> dispatchedTask, int i) {
        Intrinsics.checkParameterIsNotNull(dispatchedTask, "$this$dispatch");
        Continuation<? super T> delegate$kotlinx_coroutines_core = dispatchedTask.getDelegate$kotlinx_coroutines_core();
        if (!ResumeModeKt.isDispatchedMode(i) || !(delegate$kotlinx_coroutines_core instanceof DispatchedContinuation) || ResumeModeKt.isCancellableMode(i) != ResumeModeKt.isCancellableMode(dispatchedTask.resumeMode)) {
            resume(dispatchedTask, delegate$kotlinx_coroutines_core, i);
            return;
        }
        CoroutineDispatcher coroutineDispatcher = ((DispatchedContinuation) delegate$kotlinx_coroutines_core).dispatcher;
        CoroutineContext context = delegate$kotlinx_coroutines_core.getContext();
        if (coroutineDispatcher.isDispatchNeeded(context)) {
            coroutineDispatcher.dispatch(context, dispatchedTask);
        } else {
            resumeUnconfined(dispatchedTask);
        }
    }

    public static final <T> void resume(@NotNull DispatchedTask<? super T> dispatchedTask, @NotNull Continuation<? super T> continuation, int i) {
        Intrinsics.checkParameterIsNotNull(dispatchedTask, "$this$resume");
        Intrinsics.checkParameterIsNotNull(continuation, "delegate");
        Object takeState$kotlinx_coroutines_core = dispatchedTask.takeState$kotlinx_coroutines_core();
        Throwable exceptionalResult$kotlinx_coroutines_core = dispatchedTask.getExceptionalResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core);
        if (exceptionalResult$kotlinx_coroutines_core != null) {
            if (!(continuation instanceof DispatchedTask)) {
                exceptionalResult$kotlinx_coroutines_core = StackTraceRecoveryKt.recoverStackTrace(exceptionalResult$kotlinx_coroutines_core, continuation);
            }
            ResumeModeKt.resumeWithExceptionMode(continuation, exceptionalResult$kotlinx_coroutines_core, i);
            return;
        }
        ResumeModeKt.resumeMode(continuation, dispatchedTask.getSuccessfulResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core), i);
    }
}
