package kotlin.coroutines.jvm.internal;

import java.io.Serializable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ContinuationImpl.kt */
public abstract class BaseContinuationImpl implements Continuation<Object>, CoroutineStackFrame, Serializable {
    @Nullable
    private final Continuation<Object> completion;

    /* access modifiers changed from: protected */
    @Nullable
    public abstract Object invokeSuspend(@NotNull Object obj);

    /* access modifiers changed from: protected */
    public void releaseIntercepted() {
    }

    public BaseContinuationImpl(@Nullable Continuation<Object> continuation) {
        this.completion = continuation;
    }

    @Nullable
    public final Continuation<Object> getCompletion() {
        return this.completion;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [kotlin.coroutines.Continuation, java.lang.Object, kotlin.coroutines.Continuation<java.lang.Object>] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void resumeWith(@org.jetbrains.annotations.NotNull java.lang.Object r3) {
        /*
            r2 = this;
        L_0x0000:
            kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineResumed(r2)
            kotlin.coroutines.Continuation<java.lang.Object> r0 = r2.completion
            kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
            java.lang.Object r3 = r2.invokeSuspend(r3)     // Catch:{ all -> 0x001a }
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()     // Catch:{ all -> 0x001a }
            if (r3 != r1) goto L_0x0013
            return
        L_0x0013:
            kotlin.Result$Companion r1 = kotlin.Result.Companion     // Catch:{ all -> 0x001a }
            java.lang.Object r3 = kotlin.Result.m849constructorimpl(r3)     // Catch:{ all -> 0x001a }
            goto L_0x0025
        L_0x001a:
            r3 = move-exception
            kotlin.Result$Companion r1 = kotlin.Result.Companion
            java.lang.Object r3 = kotlin.ResultKt.createFailure(r3)
            java.lang.Object r3 = kotlin.Result.m849constructorimpl(r3)
        L_0x0025:
            r2.releaseIntercepted()
            boolean r2 = r0 instanceof kotlin.coroutines.jvm.internal.BaseContinuationImpl
            if (r2 == 0) goto L_0x0030
            r2 = r0
            kotlin.coroutines.jvm.internal.BaseContinuationImpl r2 = (kotlin.coroutines.jvm.internal.BaseContinuationImpl) r2
            goto L_0x0000
        L_0x0030:
            r0.resumeWith(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(java.lang.Object):void");
    }

    @NotNull
    public Continuation<Unit> create(@Nullable Object obj, @NotNull Continuation<?> continuation) {
        Intrinsics.checkNotNullParameter(continuation, "completion");
        throw new UnsupportedOperationException("create(Any?;Continuation) has not been overridden");
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Continuation at ");
        Object stackTraceElement = getStackTraceElement();
        if (stackTraceElement == null) {
            stackTraceElement = getClass().getName();
        }
        sb.append(stackTraceElement);
        return sb.toString();
    }

    @Nullable
    public CoroutineStackFrame getCallerFrame() {
        Continuation<Object> continuation = this.completion;
        if (!(continuation instanceof CoroutineStackFrame)) {
            continuation = null;
        }
        return (CoroutineStackFrame) continuation;
    }

    @Nullable
    public StackTraceElement getStackTraceElement() {
        return DebugMetadataKt.getStackTraceElement(this);
    }
}
