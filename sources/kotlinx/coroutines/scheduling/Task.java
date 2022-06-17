package kotlinx.coroutines.scheduling;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Tasks.kt */
public abstract class Task implements Runnable {
    public long submissionTime;
    @NotNull
    public TaskContext taskContext;

    public Task(long j, @NotNull TaskContext taskContext2) {
        Intrinsics.checkParameterIsNotNull(taskContext2, "taskContext");
        this.submissionTime = j;
        this.taskContext = taskContext2;
    }

    public Task() {
        this(0, NonBlockingContext.INSTANCE);
    }

    @NotNull
    public final TaskMode getMode() {
        return this.taskContext.getTaskMode();
    }
}
