package kotlinx.coroutines.scheduling;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: WorkQueue.kt */
public final class WorkQueue {
    static final AtomicIntegerFieldUpdater consumerIndex$FU;
    private static final AtomicReferenceFieldUpdater lastScheduledTask$FU;
    static final AtomicIntegerFieldUpdater producerIndex$FU;
    /* access modifiers changed from: private */
    public final AtomicReferenceArray<Task> buffer = new AtomicReferenceArray<>(128);
    volatile int consumerIndex = 0;
    private volatile Object lastScheduledTask = null;
    volatile int producerIndex = 0;

    static {
        Class<WorkQueue> cls = WorkQueue.class;
        lastScheduledTask$FU = AtomicReferenceFieldUpdater.newUpdater(cls, Object.class, "lastScheduledTask");
        producerIndex$FU = AtomicIntegerFieldUpdater.newUpdater(cls, "producerIndex");
        consumerIndex$FU = AtomicIntegerFieldUpdater.newUpdater(cls, "consumerIndex");
    }

    public final int getBufferSize$kotlinx_coroutines_core() {
        return this.producerIndex - this.consumerIndex;
    }

    @Nullable
    public final Task poll() {
        Task task = (Task) lastScheduledTask$FU.getAndSet(this, (Object) null);
        if (task != null) {
            return task;
        }
        while (true) {
            int i = this.consumerIndex;
            if (i - this.producerIndex == 0) {
                return null;
            }
            int i2 = i & 127;
            if (((Task) this.buffer.get(i2)) != null && consumerIndex$FU.compareAndSet(this, i, i + 1)) {
                return (Task) this.buffer.getAndSet(i2, (Object) null);
            }
        }
    }

    public final boolean add(@NotNull Task task, @NotNull GlobalQueue globalQueue) {
        Intrinsics.checkParameterIsNotNull(task, "task");
        Intrinsics.checkParameterIsNotNull(globalQueue, "globalQueue");
        Task task2 = (Task) lastScheduledTask$FU.getAndSet(this, task);
        if (task2 != null) {
            return addLast(task2, globalQueue);
        }
        return true;
    }

    public final boolean addLast(@NotNull Task task, @NotNull GlobalQueue globalQueue) {
        Intrinsics.checkParameterIsNotNull(task, "task");
        Intrinsics.checkParameterIsNotNull(globalQueue, "globalQueue");
        boolean z = true;
        while (!tryAddLast(task)) {
            offloadWork(globalQueue);
            z = false;
        }
        return z;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: kotlinx.coroutines.scheduling.Task} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean trySteal(@org.jetbrains.annotations.NotNull kotlinx.coroutines.scheduling.WorkQueue r18, @org.jetbrains.annotations.NotNull kotlinx.coroutines.scheduling.GlobalQueue r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            java.lang.String r3 = "victim"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r1, r3)
            java.lang.String r3 = "globalQueue"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r2, r3)
            kotlinx.coroutines.scheduling.TimeSource r3 = kotlinx.coroutines.scheduling.TasksKt.schedulerTimeSource
            long r3 = r3.nanoTime()
            int r5 = r18.getBufferSize$kotlinx_coroutines_core()
            if (r5 != 0) goto L_0x0021
            boolean r0 = r0.tryStealLastScheduled(r3, r1, r2)
            return r0
        L_0x0021:
            int r5 = r5 / 2
            r6 = 1
            int r5 = kotlin.ranges.RangesKt___RangesKt.coerceAtLeast((int) r5, (int) r6)
            r7 = 0
            r8 = r7
            r9 = r8
        L_0x002b:
            if (r8 >= r5) goto L_0x007c
        L_0x002d:
            int r10 = r1.consumerIndex
            int r11 = r1.producerIndex
            int r11 = r10 - r11
            r12 = 0
            if (r11 != 0) goto L_0x0037
            goto L_0x0073
        L_0x0037:
            r11 = r10 & 127(0x7f, float:1.78E-43)
            java.util.concurrent.atomic.AtomicReferenceArray r13 = r18.buffer
            java.lang.Object r13 = r13.get(r11)
            kotlinx.coroutines.scheduling.Task r13 = (kotlinx.coroutines.scheduling.Task) r13
            if (r13 == 0) goto L_0x002d
            long r13 = r13.submissionTime
            long r13 = r3 - r13
            long r15 = kotlinx.coroutines.scheduling.TasksKt.WORK_STEALING_TIME_RESOLUTION_NS
            int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r13 >= 0) goto L_0x005a
            int r13 = r18.getBufferSize$kotlinx_coroutines_core()
            int r14 = kotlinx.coroutines.scheduling.TasksKt.QUEUE_SIZE_OFFLOAD_THRESHOLD
            if (r13 <= r14) goto L_0x0058
            goto L_0x005a
        L_0x0058:
            r13 = r7
            goto L_0x005b
        L_0x005a:
            r13 = r6
        L_0x005b:
            if (r13 != 0) goto L_0x005e
            goto L_0x0073
        L_0x005e:
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r13 = consumerIndex$FU
            int r14 = r10 + 1
            boolean r10 = r13.compareAndSet(r1, r10, r14)
            if (r10 == 0) goto L_0x002d
            java.util.concurrent.atomic.AtomicReferenceArray r10 = r18.buffer
            java.lang.Object r10 = r10.getAndSet(r11, r12)
            r12 = r10
            kotlinx.coroutines.scheduling.Task r12 = (kotlinx.coroutines.scheduling.Task) r12
        L_0x0073:
            if (r12 == 0) goto L_0x007c
            r0.add(r12, r2)
            int r8 = r8 + 1
            r9 = r6
            goto L_0x002b
        L_0x007c:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.scheduling.WorkQueue.trySteal(kotlinx.coroutines.scheduling.WorkQueue, kotlinx.coroutines.scheduling.GlobalQueue):boolean");
    }

    private final boolean tryStealLastScheduled(long j, WorkQueue workQueue, GlobalQueue globalQueue) {
        Task task = (Task) workQueue.lastScheduledTask;
        if (task == null || j - task.submissionTime < TasksKt.WORK_STEALING_TIME_RESOLUTION_NS || !lastScheduledTask$FU.compareAndSet(workQueue, task, (Object) null)) {
            return false;
        }
        add(task, globalQueue);
        return true;
    }

    public final int size$kotlinx_coroutines_core() {
        Object obj = this.lastScheduledTask;
        int bufferSize$kotlinx_coroutines_core = getBufferSize$kotlinx_coroutines_core();
        return obj != null ? bufferSize$kotlinx_coroutines_core + 1 : bufferSize$kotlinx_coroutines_core;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: kotlinx.coroutines.scheduling.Task} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void offloadWork(kotlinx.coroutines.scheduling.GlobalQueue r8) {
        /*
            r7 = this;
            int r0 = r7.getBufferSize$kotlinx_coroutines_core()
            int r0 = r0 / 2
            r1 = 1
            int r0 = kotlin.ranges.RangesKt___RangesKt.coerceAtLeast((int) r0, (int) r1)
            r1 = 0
        L_0x000c:
            if (r1 >= r0) goto L_0x0043
        L_0x000e:
            int r2 = r7.consumerIndex
            int r3 = r7.producerIndex
            int r3 = r2 - r3
            r4 = 0
            if (r3 != 0) goto L_0x0018
            goto L_0x003b
        L_0x0018:
            r3 = r2 & 127(0x7f, float:1.78E-43)
            java.util.concurrent.atomic.AtomicReferenceArray r5 = r7.buffer
            java.lang.Object r5 = r5.get(r3)
            kotlinx.coroutines.scheduling.Task r5 = (kotlinx.coroutines.scheduling.Task) r5
            if (r5 == 0) goto L_0x000e
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r5 = consumerIndex$FU
            int r6 = r2 + 1
            boolean r2 = r5.compareAndSet(r7, r2, r6)
            if (r2 == 0) goto L_0x000e
            java.util.concurrent.atomic.AtomicReferenceArray r2 = r7.buffer
            java.lang.Object r2 = r2.getAndSet(r3, r4)
            r4 = r2
            kotlinx.coroutines.scheduling.Task r4 = (kotlinx.coroutines.scheduling.Task) r4
        L_0x003b:
            if (r4 == 0) goto L_0x0043
            r7.addToGlobalQueue(r8, r4)
            int r1 = r1 + 1
            goto L_0x000c
        L_0x0043:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.scheduling.WorkQueue.offloadWork(kotlinx.coroutines.scheduling.GlobalQueue):void");
    }

    private final void addToGlobalQueue(GlobalQueue globalQueue, Task task) {
        if (!globalQueue.addLast(task)) {
            throw new IllegalStateException("GlobalQueue could not be closed yet".toString());
        }
    }

    public final void offloadAllWork$kotlinx_coroutines_core(@NotNull GlobalQueue globalQueue) {
        Task task;
        Intrinsics.checkParameterIsNotNull(globalQueue, "globalQueue");
        Task task2 = (Task) lastScheduledTask$FU.getAndSet(this, (Object) null);
        if (task2 != null) {
            addToGlobalQueue(globalQueue, task2);
        }
        while (true) {
            int i = this.consumerIndex;
            if (i - this.producerIndex == 0) {
                task = null;
            } else {
                int i2 = i & 127;
                if (((Task) this.buffer.get(i2)) != null && consumerIndex$FU.compareAndSet(this, i, i + 1)) {
                    task = (Task) this.buffer.getAndSet(i2, (Object) null);
                }
            }
            if (task != null) {
                addToGlobalQueue(globalQueue, task);
            } else {
                return;
            }
        }
    }

    private final boolean tryAddLast(Task task) {
        if (getBufferSize$kotlinx_coroutines_core() == 127) {
            return false;
        }
        int i = this.producerIndex & 127;
        if (this.buffer.get(i) != null) {
            return false;
        }
        this.buffer.lazySet(i, task);
        producerIndex$FU.incrementAndGet(this);
        return true;
    }
}
