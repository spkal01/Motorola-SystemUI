package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.ThreadContextKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: Dispatched.kt */
public final class DispatchedContinuation<T> extends DispatchedTask<T> implements CoroutineStackFrame, Continuation<T> {
    @Nullable
    public Object _state = DispatchedKt.UNDEFINED;
    @Nullable
    private final CoroutineStackFrame callerFrame;
    @NotNull
    public final Continuation<T> continuation;
    @NotNull
    public final Object countOrElement;
    @NotNull
    public final CoroutineDispatcher dispatcher;

    @NotNull
    public CoroutineContext getContext() {
        return this.continuation.getContext();
    }

    @NotNull
    public Continuation<T> getDelegate$kotlinx_coroutines_core() {
        return this;
    }

    @Nullable
    public StackTraceElement getStackTraceElement() {
        return null;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DispatchedContinuation(@NotNull CoroutineDispatcher coroutineDispatcher, @NotNull Continuation<? super T> continuation2) {
        super(0);
        Intrinsics.checkParameterIsNotNull(coroutineDispatcher, "dispatcher");
        Intrinsics.checkParameterIsNotNull(continuation2, "continuation");
        this.dispatcher = coroutineDispatcher;
        this.continuation = continuation2;
        this.callerFrame = (CoroutineStackFrame) (!(continuation2 instanceof CoroutineStackFrame) ? null : continuation2);
        this.countOrElement = ThreadContextKt.threadContextElements(getContext());
    }

    @Nullable
    public CoroutineStackFrame getCallerFrame() {
        return this.callerFrame;
    }

    @Nullable
    public Object takeState$kotlinx_coroutines_core() {
        Object obj = this._state;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(obj != DispatchedKt.UNDEFINED)) {
                throw new AssertionError();
            }
        }
        this._state = DispatchedKt.UNDEFINED;
        return obj;
    }

    public void resumeWith(@NotNull Object obj) {
        CoroutineContext context;
        Object updateThreadContext;
        CoroutineContext context2 = this.continuation.getContext();
        Object state = CompletedExceptionallyKt.toState(obj);
        if (this.dispatcher.isDispatchNeeded(context2)) {
            this._state = state;
            this.resumeMode = 0;
            this.dispatcher.dispatch(context2, this);
            return;
        }
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            this._state = state;
            this.resumeMode = 0;
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(this);
            return;
        }
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            context = getContext();
            updateThreadContext = ThreadContextKt.updateThreadContext(context, this.countOrElement);
            this.continuation.resumeWith(obj);
            Unit unit = Unit.INSTANCE;
            ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            do {
            } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
        } catch (Throwable th) {
            try {
                handleFatalException$kotlinx_coroutines_core(th, (Throwable) null);
            } catch (Throwable th2) {
                eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
                throw th2;
            }
        }
        eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
    }

    public final void dispatchYield$kotlinx_coroutines_core(T t) {
        CoroutineContext context = this.continuation.getContext();
        this._state = t;
        this.resumeMode = 1;
        this.dispatcher.dispatchYield(context, this);
    }

    @NotNull
    public String toString() {
        return "DispatchedContinuation[" + this.dispatcher + ", " + DebugStringsKt.toDebugString(this.continuation) + ']';
    }
}
