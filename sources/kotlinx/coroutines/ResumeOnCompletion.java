package kotlinx.coroutines;

import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: JobSupport.kt */
final class ResumeOnCompletion extends JobNode<Job> {
    private final Continuation<Unit> continuation;

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ResumeOnCompletion(@NotNull Job job, @NotNull Continuation<? super Unit> continuation2) {
        super(job);
        Intrinsics.checkParameterIsNotNull(job, "job");
        Intrinsics.checkParameterIsNotNull(continuation2, "continuation");
        this.continuation = continuation2;
    }

    public void invoke(@Nullable Throwable th) {
        Continuation<Unit> continuation2 = this.continuation;
        Unit unit = Unit.INSTANCE;
        Result.Companion companion = Result.Companion;
        continuation2.resumeWith(Result.m849constructorimpl(unit));
    }

    @NotNull
    public String toString() {
        return "ResumeOnCompletion[" + this.continuation + ']';
    }
}
