package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: JobSupport.kt */
public final class ChildContinuation extends JobCancellingNode<Job> {
    @NotNull
    public final CancellableContinuationImpl<?> child;

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChildContinuation(@NotNull Job job, @NotNull CancellableContinuationImpl<?> cancellableContinuationImpl) {
        super(job);
        Intrinsics.checkParameterIsNotNull(job, "parent");
        Intrinsics.checkParameterIsNotNull(cancellableContinuationImpl, "child");
        this.child = cancellableContinuationImpl;
    }

    public void invoke(@Nullable Throwable th) {
        CancellableContinuationImpl<?> cancellableContinuationImpl = this.child;
        cancellableContinuationImpl.cancel(cancellableContinuationImpl.getContinuationCancellationCause(this.job));
    }

    @NotNull
    public String toString() {
        return "ChildContinuation[" + this.child + ']';
    }
}
