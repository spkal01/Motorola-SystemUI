package kotlinx.coroutines.intrinsics;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlinx.coroutines.internal.ThreadContextKt;
import org.jetbrains.annotations.NotNull;

/* compiled from: Undispatched.kt */
public final class UndispatchedKt {
    public static final <R, T> void startCoroutineUndispatched(@NotNull Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2, R r, @NotNull Continuation<? super T> continuation) {
        CoroutineContext context;
        Object updateThreadContext;
        Intrinsics.checkParameterIsNotNull(function2, "$this$startCoroutineUndispatched");
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        Continuation<? super T> probeCoroutineCreated = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            context = continuation.getContext();
            updateThreadContext = ThreadContextKt.updateThreadContext(context, (Object) null);
            Object invoke = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(r, probeCoroutineCreated);
            ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            if (invoke != IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                Result.Companion companion = Result.Companion;
                probeCoroutineCreated.resumeWith(Result.m849constructorimpl(invoke));
            }
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            probeCoroutineCreated.resumeWith(Result.m849constructorimpl(ResultKt.createFailure(th)));
        }
    }
}
