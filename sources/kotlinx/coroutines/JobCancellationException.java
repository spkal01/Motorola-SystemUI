package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: Exceptions.kt */
public final class JobCancellationException extends CancellationException implements CopyableThrowable<JobCancellationException> {
    @NotNull
    public final Job job;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public JobCancellationException(@NotNull String str, @Nullable Throwable th, @NotNull Job job2) {
        super(str);
        Intrinsics.checkParameterIsNotNull(str, "message");
        Intrinsics.checkParameterIsNotNull(job2, "job");
        this.job = job2;
        if (th != null) {
            initCause(th);
        }
    }

    @NotNull
    public Throwable fillInStackTrace() {
        if (!DebugKt.getDEBUG()) {
            return this;
        }
        Throwable fillInStackTrace = super.fillInStackTrace();
        Intrinsics.checkExpressionValueIsNotNull(fillInStackTrace, "super.fillInStackTrace()");
        return fillInStackTrace;
    }

    @Nullable
    public JobCancellationException createCopy() {
        if (!DebugKt.getDEBUG()) {
            return null;
        }
        String message = getMessage();
        if (message == null) {
            Intrinsics.throwNpe();
        }
        return new JobCancellationException(message, this, this.job);
    }

    @NotNull
    public String toString() {
        return super.toString() + "; job=" + this.job;
    }

    public boolean equals(@Nullable Object obj) {
        if (obj != this) {
            if (obj instanceof JobCancellationException) {
                JobCancellationException jobCancellationException = (JobCancellationException) obj;
                if (!Intrinsics.areEqual((Object) jobCancellationException.getMessage(), (Object) getMessage()) || !Intrinsics.areEqual((Object) jobCancellationException.job, (Object) this.job) || !Intrinsics.areEqual((Object) jobCancellationException.getCause(), (Object) getCause())) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        String message = getMessage();
        if (message == null) {
            Intrinsics.throwNpe();
        }
        int hashCode = ((message.hashCode() * 31) + this.job.hashCode()) * 31;
        Throwable cause = getCause();
        return hashCode + (cause != null ? cause.hashCode() : 0);
    }
}
