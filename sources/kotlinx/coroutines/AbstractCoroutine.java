package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: AbstractCoroutine.kt */
public abstract class AbstractCoroutine<T> extends JobSupport implements Job, Continuation<T> {
    @NotNull
    private final CoroutineContext context;
    @NotNull
    protected final CoroutineContext parentContext;

    public int getDefaultResumeMode$kotlinx_coroutines_core() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onCancelled(@NotNull Throwable th, boolean z) {
        Intrinsics.checkParameterIsNotNull(th, "cause");
    }

    /* access modifiers changed from: protected */
    public void onCompleted(T t) {
    }

    /* access modifiers changed from: protected */
    public void onStart() {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AbstractCoroutine(@NotNull CoroutineContext coroutineContext, boolean z) {
        super(z);
        Intrinsics.checkParameterIsNotNull(coroutineContext, "parentContext");
        this.parentContext = coroutineContext;
        this.context = coroutineContext.plus(this);
    }

    @NotNull
    public final CoroutineContext getContext() {
        return this.context;
    }

    @NotNull
    public CoroutineContext getCoroutineContext() {
        return this.context;
    }

    public boolean isActive() {
        return super.isActive();
    }

    public final void initParentJob$kotlinx_coroutines_core() {
        initParentJobInternal$kotlinx_coroutines_core((Job) this.parentContext.get(Job.Key));
    }

    public final void onStartInternal$kotlinx_coroutines_core() {
        onStart();
    }

    /* access modifiers changed from: protected */
    public final void onCompletionInternal(@Nullable Object obj) {
        if (obj instanceof CompletedExceptionally) {
            CompletedExceptionally completedExceptionally = (CompletedExceptionally) obj;
            onCancelled(completedExceptionally.cause, completedExceptionally.getHandled());
            return;
        }
        onCompleted(obj);
    }

    public final void resumeWith(@NotNull Object obj) {
        makeCompletingOnce$kotlinx_coroutines_core(CompletedExceptionallyKt.toState(obj), getDefaultResumeMode$kotlinx_coroutines_core());
    }

    public final void handleOnCompletionException$kotlinx_coroutines_core(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "exception");
        CoroutineExceptionHandlerKt.handleCoroutineException(this.context, th);
    }

    @NotNull
    public String nameString$kotlinx_coroutines_core() {
        String coroutineName = CoroutineContextKt.getCoroutineName(this.context);
        if (coroutineName == null) {
            return super.nameString$kotlinx_coroutines_core();
        }
        return '\"' + coroutineName + "\":" + super.nameString$kotlinx_coroutines_core();
    }

    public final <R> void start(@NotNull CoroutineStart coroutineStart, R r, @NotNull Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2) {
        Intrinsics.checkParameterIsNotNull(coroutineStart, "start");
        Intrinsics.checkParameterIsNotNull(function2, "block");
        initParentJob$kotlinx_coroutines_core();
        coroutineStart.invoke(function2, r, this);
    }
}
