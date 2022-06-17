package kotlinx.coroutines;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;
import org.jetbrains.annotations.NotNull;

/* compiled from: ResumeMode.kt */
public final class ResumeModeKt {
    public static final boolean isCancellableMode(int i) {
        return i == 1;
    }

    public static final boolean isDispatchedMode(int i) {
        return i == 0 || i == 1;
    }

    public static final <T> void resumeMode(@NotNull Continuation<? super T> continuation, T t, int i) {
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeMode");
        if (i == 0) {
            Result.Companion companion = Result.Companion;
            continuation.resumeWith(Result.m849constructorimpl(t));
        } else if (i == 1) {
            DispatchedKt.resumeCancellable(continuation, t);
        } else if (i == 2) {
            DispatchedKt.resumeDirect(continuation, t);
        } else if (i == 3) {
            DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) continuation;
            CoroutineContext context = dispatchedContinuation.getContext();
            Object updateThreadContext = ThreadContextKt.updateThreadContext(context, dispatchedContinuation.countOrElement);
            try {
                Continuation<T> continuation2 = dispatchedContinuation.continuation;
                Result.Companion companion2 = Result.Companion;
                continuation2.resumeWith(Result.m849constructorimpl(t));
                Unit unit = Unit.INSTANCE;
            } finally {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
        } else if (i != 4) {
            throw new IllegalStateException(("Invalid mode " + i).toString());
        }
    }

    public static final <T> void resumeWithExceptionMode(@NotNull Continuation<? super T> continuation, @NotNull Throwable th, int i) {
        Intrinsics.checkParameterIsNotNull(continuation, "$this$resumeWithExceptionMode");
        Intrinsics.checkParameterIsNotNull(th, "exception");
        if (i == 0) {
            Result.Companion companion = Result.Companion;
            continuation.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(th)));
        } else if (i == 1) {
            DispatchedKt.resumeCancellableWithException(continuation, th);
        } else if (i == 2) {
            DispatchedKt.resumeDirectWithException(continuation, th);
        } else if (i == 3) {
            DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) continuation;
            CoroutineContext context = dispatchedContinuation.getContext();
            Object updateThreadContext = ThreadContextKt.updateThreadContext(context, dispatchedContinuation.countOrElement);
            try {
                Continuation<T> continuation2 = dispatchedContinuation.continuation;
                Result.Companion companion2 = Result.Companion;
                continuation2.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(th, continuation2))));
                Unit unit = Unit.INSTANCE;
            } finally {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
        } else if (i != 4) {
            throw new IllegalStateException(("Invalid mode " + i).toString());
        }
    }
}
