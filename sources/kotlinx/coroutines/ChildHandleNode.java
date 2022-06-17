package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: JobSupport.kt */
public final class ChildHandleNode extends JobCancellingNode<JobSupport> implements ChildHandle {
    @NotNull
    public final ChildJob childJob;

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChildHandleNode(@NotNull JobSupport jobSupport, @NotNull ChildJob childJob2) {
        super(jobSupport);
        Intrinsics.checkParameterIsNotNull(jobSupport, "parent");
        Intrinsics.checkParameterIsNotNull(childJob2, "childJob");
        this.childJob = childJob2;
    }

    public void invoke(@Nullable Throwable th) {
        this.childJob.parentCancelled((ParentJob) this.job);
    }

    public boolean childCancelled(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "cause");
        return ((JobSupport) this.job).childCancelled(th);
    }

    @NotNull
    public String toString() {
        return "ChildHandle[" + this.childJob + ']';
    }
}
