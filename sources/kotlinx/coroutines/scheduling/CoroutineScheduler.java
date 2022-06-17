package kotlinx.coroutines.scheduling;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.DebugStringsKt;
import kotlinx.coroutines.TimeSource;
import kotlinx.coroutines.TimeSourceKt;
import kotlinx.coroutines.internal.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CoroutineScheduler.kt */
public final class CoroutineScheduler implements Executor, Closeable {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final int MAX_PARK_TIME_NS;
    /* access modifiers changed from: private */
    public static final int MAX_SPINS;
    /* access modifiers changed from: private */
    public static final int MAX_YIELDS;
    /* access modifiers changed from: private */
    public static final int MIN_PARK_TIME_NS;
    /* access modifiers changed from: private */
    public static final Symbol NOT_IN_STACK = new Symbol("NOT_IN_STACK");
    private static final AtomicIntegerFieldUpdater _isTerminated$FU = AtomicIntegerFieldUpdater.newUpdater(CoroutineScheduler.class, "_isTerminated");
    static final AtomicLongFieldUpdater controlState$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "controlState");
    private static final AtomicLongFieldUpdater parkedWorkersStack$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "parkedWorkersStack");
    private volatile int _isTerminated;
    volatile long controlState;
    /* access modifiers changed from: private */
    public final int corePoolSize;
    /* access modifiers changed from: private */
    public final Semaphore cpuPermits;
    /* access modifiers changed from: private */
    public final GlobalQueue globalQueue;
    /* access modifiers changed from: private */
    public final long idleWorkerKeepAliveNs;
    private final int maxPoolSize;
    private volatile long parkedWorkersStack;
    /* access modifiers changed from: private */
    public final Random random;
    /* access modifiers changed from: private */
    public final String schedulerName;
    /* access modifiers changed from: private */
    public final Worker[] workers;

    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[WorkerState.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[WorkerState.PARKING.ordinal()] = 1;
            iArr[WorkerState.BLOCKING.ordinal()] = 2;
            iArr[WorkerState.CPU_ACQUIRED.ordinal()] = 3;
            iArr[WorkerState.RETIRING.ordinal()] = 4;
            iArr[WorkerState.TERMINATED.ordinal()] = 5;
        }
    }

    /* compiled from: CoroutineScheduler.kt */
    public enum WorkerState {
        CPU_ACQUIRED,
        BLOCKING,
        PARKING,
        RETIRING,
        TERMINATED
    }

    public CoroutineScheduler(int i, int i2, long j, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "schedulerName");
        this.corePoolSize = i;
        this.maxPoolSize = i2;
        this.idleWorkerKeepAliveNs = j;
        this.schedulerName = str;
        if (i >= 1) {
            if (i2 >= i) {
                if (i2 <= 2097150) {
                    if (j > 0) {
                        this.globalQueue = new GlobalQueue();
                        this.cpuPermits = new Semaphore(i, false);
                        this.parkedWorkersStack = 0;
                        this.workers = new Worker[(i2 + 1)];
                        this.controlState = 0;
                        this.random = new Random();
                        this._isTerminated = 0;
                        return;
                    }
                    throw new IllegalArgumentException(("Idle worker keep alive time " + j + " must be positive").toString());
                }
                throw new IllegalArgumentException(("Max pool size " + i2 + " should not exceed maximal supported number of threads 2097150").toString());
            }
            throw new IllegalArgumentException(("Max pool size " + i2 + " should be greater than or equals to core pool size " + i).toString());
        }
        throw new IllegalArgumentException(("Core pool size " + i + " should be at least 1").toString());
    }

    /* access modifiers changed from: private */
    public final void parkedWorkersStackPush(Worker worker) {
        long j;
        long j2;
        int indexInArray;
        if (worker.getNextParkedWorker() == NOT_IN_STACK) {
            do {
                j = this.parkedWorkersStack;
                int i = (int) (2097151 & j);
                j2 = (2097152 + j) & -2097152;
                indexInArray = worker.getIndexInArray();
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(indexInArray != 0)) {
                        throw new AssertionError();
                    }
                }
                worker.setNextParkedWorker(this.workers[i]);
            } while (!parkedWorkersStack$FU.compareAndSet(this, j, ((long) indexInArray) | j2));
        }
    }

    private final int parkedWorkersStackNextIndex(Worker worker) {
        Object nextParkedWorker = worker.getNextParkedWorker();
        while (nextParkedWorker != NOT_IN_STACK) {
            if (nextParkedWorker == null) {
                return 0;
            }
            Worker worker2 = (Worker) nextParkedWorker;
            int indexInArray = worker2.getIndexInArray();
            if (indexInArray != 0) {
                return indexInArray;
            }
            nextParkedWorker = worker2.getNextParkedWorker();
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public final int getCreatedWorkers() {
        return (int) (this.controlState & 2097151);
    }

    /* access modifiers changed from: private */
    public final boolean isTerminated() {
        return this._isTerminated != 0;
    }

    /* compiled from: CoroutineScheduler.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    static {
        int systemProp$default = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.spins", 1000, 1, 0, 8, (Object) null);
        MAX_SPINS = systemProp$default;
        MAX_YIELDS = systemProp$default + SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.yields", 0, 0, 0, 8, (Object) null);
        int nanos = (int) TimeUnit.SECONDS.toNanos(1);
        MAX_PARK_TIME_NS = nanos;
        MIN_PARK_TIME_NS = (int) RangesKt___RangesKt.coerceAtMost(RangesKt___RangesKt.coerceAtLeast(TasksKt.WORK_STEALING_TIME_RESOLUTION_NS / ((long) 4), 10), (long) nanos);
    }

    public void execute(@NotNull Runnable runnable) {
        Intrinsics.checkParameterIsNotNull(runnable, "command");
        dispatch$default(this, runnable, (TaskContext) null, false, 6, (Object) null);
    }

    public void close() {
        shutdown(10000);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0068, code lost:
        if (r9 != null) goto L_0x0073;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void shutdown(long r9) {
        /*
            r8 = this;
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r0 = _isTerminated$FU
            r1 = 0
            r2 = 1
            boolean r0 = r0.compareAndSet(r8, r1, r2)
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker r0 = r8.currentWorker()
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r3 = r8.workers
            monitor-enter(r3)
            long r4 = r8.controlState     // Catch:{ all -> 0x00a1 }
            r6 = 2097151(0x1fffff, double:1.0361303E-317)
            long r4 = r4 & r6
            int r4 = (int) r4
            monitor-exit(r3)
            if (r2 > r4) goto L_0x005d
            r3 = r2
        L_0x001d:
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r5 = r8.workers
            r5 = r5[r3]
            if (r5 != 0) goto L_0x0026
            kotlin.jvm.internal.Intrinsics.throwNpe()
        L_0x0026:
            if (r5 == r0) goto L_0x0058
        L_0x0028:
            boolean r6 = r5.isAlive()
            if (r6 == 0) goto L_0x0035
            java.util.concurrent.locks.LockSupport.unpark(r5)
            r5.join(r9)
            goto L_0x0028
        L_0x0035:
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r6 = r5.getState()
            boolean r7 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r7 == 0) goto L_0x004f
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r7 = kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState.TERMINATED
            if (r6 != r7) goto L_0x0045
            r6 = r2
            goto L_0x0046
        L_0x0045:
            r6 = r1
        L_0x0046:
            if (r6 == 0) goto L_0x0049
            goto L_0x004f
        L_0x0049:
            java.lang.AssertionError r8 = new java.lang.AssertionError
            r8.<init>()
            throw r8
        L_0x004f:
            kotlinx.coroutines.scheduling.WorkQueue r5 = r5.getLocalQueue()
            kotlinx.coroutines.scheduling.GlobalQueue r6 = r8.globalQueue
            r5.offloadAllWork$kotlinx_coroutines_core(r6)
        L_0x0058:
            if (r3 == r4) goto L_0x005d
            int r3 = r3 + 1
            goto L_0x001d
        L_0x005d:
            kotlinx.coroutines.scheduling.GlobalQueue r9 = r8.globalQueue
            r9.close()
        L_0x0062:
            if (r0 == 0) goto L_0x006b
            kotlinx.coroutines.scheduling.Task r9 = r0.findTask$kotlinx_coroutines_core()
            if (r9 == 0) goto L_0x006b
            goto L_0x0073
        L_0x006b:
            kotlinx.coroutines.scheduling.GlobalQueue r9 = r8.globalQueue
            java.lang.Object r9 = r9.removeFirstOrNull()
            kotlinx.coroutines.scheduling.Task r9 = (kotlinx.coroutines.scheduling.Task) r9
        L_0x0073:
            if (r9 == 0) goto L_0x0079
            r8.runSafely(r9)
            goto L_0x0062
        L_0x0079:
            if (r0 == 0) goto L_0x0080
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r9 = kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState.TERMINATED
            r0.tryReleaseCpu$kotlinx_coroutines_core(r9)
        L_0x0080:
            boolean r9 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r9 == 0) goto L_0x009a
            java.util.concurrent.Semaphore r9 = r8.cpuPermits
            int r9 = r9.availablePermits()
            int r10 = r8.corePoolSize
            if (r9 != r10) goto L_0x0091
            r1 = r2
        L_0x0091:
            if (r1 == 0) goto L_0x0094
            goto L_0x009a
        L_0x0094:
            java.lang.AssertionError r8 = new java.lang.AssertionError
            r8.<init>()
            throw r8
        L_0x009a:
            r9 = 0
            r8.parkedWorkersStack = r9
            r8.controlState = r9
            return
        L_0x00a1:
            r8 = move-exception
            monitor-exit(r3)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.scheduling.CoroutineScheduler.shutdown(long):void");
    }

    public static /* synthetic */ void dispatch$default(CoroutineScheduler coroutineScheduler, Runnable runnable, TaskContext taskContext, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            taskContext = NonBlockingContext.INSTANCE;
        }
        if ((i & 4) != 0) {
            z = false;
        }
        coroutineScheduler.dispatch(runnable, taskContext, z);
    }

    public final void dispatch(@NotNull Runnable runnable, @NotNull TaskContext taskContext, boolean z) {
        Intrinsics.checkParameterIsNotNull(runnable, "block");
        Intrinsics.checkParameterIsNotNull(taskContext, "taskContext");
        TimeSource timeSource = TimeSourceKt.getTimeSource();
        if (timeSource != null) {
            timeSource.trackTask();
        }
        Task createTask$kotlinx_coroutines_core = createTask$kotlinx_coroutines_core(runnable, taskContext);
        int submitToLocalQueue = submitToLocalQueue(createTask$kotlinx_coroutines_core, z);
        if (submitToLocalQueue == -1) {
            return;
        }
        if (submitToLocalQueue != 1) {
            requestCpuWorker();
        } else if (this.globalQueue.addLast(createTask$kotlinx_coroutines_core)) {
            requestCpuWorker();
        } else {
            throw new RejectedExecutionException(this.schedulerName + " was terminated");
        }
    }

    @NotNull
    public final Task createTask$kotlinx_coroutines_core(@NotNull Runnable runnable, @NotNull TaskContext taskContext) {
        Intrinsics.checkParameterIsNotNull(runnable, "block");
        Intrinsics.checkParameterIsNotNull(taskContext, "taskContext");
        long nanoTime = TasksKt.schedulerTimeSource.nanoTime();
        if (!(runnable instanceof Task)) {
            return new TaskImpl(runnable, nanoTime, taskContext);
        }
        Task task = (Task) runnable;
        task.submissionTime = nanoTime;
        task.taskContext = taskContext;
        return task;
    }

    /* access modifiers changed from: private */
    public final void requestCpuWorker() {
        if (this.cpuPermits.availablePermits() == 0) {
            tryUnpark();
        } else if (!tryUnpark()) {
            long j = this.controlState;
            if (((int) (2097151 & j)) - ((int) ((j & 4398044413952L) >> 21)) < this.corePoolSize) {
                int createNewWorker = createNewWorker();
                if (createNewWorker == 1 && this.corePoolSize > 1) {
                    createNewWorker();
                }
                if (createNewWorker > 0) {
                    return;
                }
            }
            tryUnpark();
        }
    }

    private final boolean tryUnpark() {
        while (true) {
            Worker parkedWorkersStackPop = parkedWorkersStackPop();
            if (parkedWorkersStackPop == null) {
                return false;
            }
            parkedWorkersStackPop.idleResetBeforeUnpark();
            boolean isParking = parkedWorkersStackPop.isParking();
            LockSupport.unpark(parkedWorkersStackPop);
            if (isParking && parkedWorkersStackPop.tryForbidTermination()) {
                return true;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007c, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int createNewWorker() {
        /*
            r10 = this;
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r0 = r10.workers
            monitor-enter(r0)
            boolean r1 = r10.isTerminated()     // Catch:{ all -> 0x007d }
            if (r1 == 0) goto L_0x000c
            r10 = -1
            monitor-exit(r0)
            return r10
        L_0x000c:
            long r1 = r10.controlState     // Catch:{ all -> 0x007d }
            r3 = 2097151(0x1fffff, double:1.0361303E-317)
            long r5 = r1 & r3
            int r5 = (int) r5     // Catch:{ all -> 0x007d }
            r6 = 4398044413952(0x3ffffe00000, double:2.1729226538177E-311)
            long r1 = r1 & r6
            r6 = 21
            long r1 = r1 >> r6
            int r1 = (int) r1     // Catch:{ all -> 0x007d }
            int r1 = r5 - r1
            int r2 = r10.corePoolSize     // Catch:{ all -> 0x007d }
            r6 = 0
            if (r1 < r2) goto L_0x0027
            monitor-exit(r0)
            return r6
        L_0x0027:
            int r2 = r10.maxPoolSize     // Catch:{ all -> 0x007d }
            if (r5 >= r2) goto L_0x007b
            java.util.concurrent.Semaphore r2 = r10.cpuPermits     // Catch:{ all -> 0x007d }
            int r2 = r2.availablePermits()     // Catch:{ all -> 0x007d }
            if (r2 != 0) goto L_0x0034
            goto L_0x007b
        L_0x0034:
            long r7 = r10.controlState     // Catch:{ all -> 0x007d }
            long r7 = r7 & r3
            int r2 = (int) r7     // Catch:{ all -> 0x007d }
            r5 = 1
            int r2 = r2 + r5
            if (r2 <= 0) goto L_0x0044
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r7 = r10.workers     // Catch:{ all -> 0x007d }
            r7 = r7[r2]     // Catch:{ all -> 0x007d }
            if (r7 != 0) goto L_0x0044
            r7 = r5
            goto L_0x0045
        L_0x0044:
            r7 = r6
        L_0x0045:
            if (r7 == 0) goto L_0x006f
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker r7 = new kotlinx.coroutines.scheduling.CoroutineScheduler$Worker     // Catch:{ all -> 0x007d }
            r7.<init>(r10, r2)     // Catch:{ all -> 0x007d }
            r7.start()     // Catch:{ all -> 0x007d }
            java.util.concurrent.atomic.AtomicLongFieldUpdater r8 = controlState$FU     // Catch:{ all -> 0x007d }
            long r8 = r8.incrementAndGet(r10)     // Catch:{ all -> 0x007d }
            long r3 = r3 & r8
            int r3 = (int) r3     // Catch:{ all -> 0x007d }
            if (r2 != r3) goto L_0x005a
            r6 = r5
        L_0x005a:
            if (r6 == 0) goto L_0x0063
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r10 = r10.workers     // Catch:{ all -> 0x007d }
            r10[r2] = r7     // Catch:{ all -> 0x007d }
            int r1 = r1 + r5
            monitor-exit(r0)
            return r1
        L_0x0063:
            java.lang.String r10 = "Failed requirement."
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x007d }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x007d }
            r1.<init>(r10)     // Catch:{ all -> 0x007d }
            throw r1     // Catch:{ all -> 0x007d }
        L_0x006f:
            java.lang.String r10 = "Failed requirement."
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x007d }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x007d }
            r1.<init>(r10)     // Catch:{ all -> 0x007d }
            throw r1     // Catch:{ all -> 0x007d }
        L_0x007b:
            monitor-exit(r0)
            return r6
        L_0x007d:
            r10 = move-exception
            monitor-exit(r0)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.scheduling.CoroutineScheduler.createNewWorker():int");
    }

    private final int submitToLocalQueue(Task task, boolean z) {
        boolean z2;
        Worker currentWorker = currentWorker();
        if (currentWorker == null || currentWorker.getState() == WorkerState.TERMINATED) {
            return 1;
        }
        int i = -1;
        if (task.getMode() == TaskMode.NON_BLOCKING) {
            if (currentWorker.isBlocking()) {
                i = 0;
            } else if (!currentWorker.tryAcquireCpuPermit()) {
                return 1;
            }
        }
        if (z) {
            z2 = currentWorker.getLocalQueue().addLast(task, this.globalQueue);
        } else {
            z2 = currentWorker.getLocalQueue().add(task, this.globalQueue);
        }
        if (!z2 || currentWorker.getLocalQueue().getBufferSize$kotlinx_coroutines_core() > TasksKt.QUEUE_SIZE_OFFLOAD_THRESHOLD) {
            return 0;
        }
        return i;
    }

    private final Worker currentWorker() {
        Thread currentThread = Thread.currentThread();
        if (!(currentThread instanceof Worker)) {
            currentThread = null;
        }
        Worker worker = (Worker) currentThread;
        if (worker == null || !Intrinsics.areEqual((Object) worker.getScheduler(), (Object) this)) {
            return null;
        }
        return worker;
    }

    @NotNull
    public String toString() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (Worker worker : this.workers) {
            if (worker != null) {
                int size$kotlinx_coroutines_core = worker.getLocalQueue().size$kotlinx_coroutines_core();
                int i6 = WhenMappings.$EnumSwitchMapping$0[worker.getState().ordinal()];
                if (i6 == 1) {
                    i3++;
                } else if (i6 == 2) {
                    i2++;
                    arrayList.add(String.valueOf(size$kotlinx_coroutines_core) + "b");
                } else if (i6 == 3) {
                    i++;
                    arrayList.add(String.valueOf(size$kotlinx_coroutines_core) + "c");
                } else if (i6 == 4) {
                    i4++;
                    if (size$kotlinx_coroutines_core > 0) {
                        arrayList.add(String.valueOf(size$kotlinx_coroutines_core) + "r");
                    }
                } else if (i6 == 5) {
                    i5++;
                }
            }
        }
        long j = this.controlState;
        return this.schedulerName + '@' + DebugStringsKt.getHexAddress(this) + '[' + "Pool Size {" + "core = " + this.corePoolSize + ", " + "max = " + this.maxPoolSize + "}, " + "Worker States {" + "CPU = " + i + ", " + "blocking = " + i2 + ", " + "parked = " + i3 + ", " + "retired = " + i4 + ", " + "terminated = " + i5 + "}, " + "running workers queues = " + arrayList + ", " + "global queue size = " + this.globalQueue.getSize() + ", " + "Control State Workers {" + "created = " + ((int) (2097151 & j)) + ", " + "blocking = " + ((int) ((j & 4398044413952L) >> 21)) + '}' + "]";
    }

    /* access modifiers changed from: private */
    public final void runSafely(Task task) {
        TimeSource timeSource;
        try {
            task.run();
            timeSource = TimeSourceKt.getTimeSource();
            if (timeSource == null) {
                return;
            }
        } catch (Throwable th) {
            TimeSource timeSource2 = TimeSourceKt.getTimeSource();
            if (timeSource2 != null) {
                timeSource2.unTrackTask();
            }
            throw th;
        }
        timeSource.unTrackTask();
    }

    /* compiled from: CoroutineScheduler.kt */
    public final class Worker extends Thread {
        private static final AtomicIntegerFieldUpdater terminationState$FU = AtomicIntegerFieldUpdater.newUpdater(Worker.class, "terminationState");
        private volatile int indexInArray;
        private long lastExhaustionTime;
        private int lastStealIndex;
        @NotNull
        private final WorkQueue localQueue;
        @Nullable
        private volatile Object nextParkedWorker;
        private int parkTimeNs;
        private int rngState;
        private volatile int spins;
        @NotNull
        private volatile WorkerState state;
        private long terminationDeadline;
        private volatile int terminationState;

        private Worker() {
            setDaemon(true);
            this.localQueue = new WorkQueue();
            this.state = WorkerState.RETIRING;
            this.terminationState = 0;
            this.nextParkedWorker = CoroutineScheduler.NOT_IN_STACK;
            this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            this.rngState = CoroutineScheduler.this.random.nextInt();
        }

        public final int getIndexInArray() {
            return this.indexInArray;
        }

        public final void setIndexInArray(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append(CoroutineScheduler.this.schedulerName);
            sb.append("-worker-");
            sb.append(i == 0 ? "TERMINATED" : String.valueOf(i));
            setName(sb.toString());
            this.indexInArray = i;
        }

        public Worker(CoroutineScheduler coroutineScheduler, int i) {
            this();
            setIndexInArray(i);
        }

        @NotNull
        public final CoroutineScheduler getScheduler() {
            return CoroutineScheduler.this;
        }

        @NotNull
        public final WorkQueue getLocalQueue() {
            return this.localQueue;
        }

        @NotNull
        public final WorkerState getState() {
            return this.state;
        }

        public final boolean isParking() {
            return this.state == WorkerState.PARKING;
        }

        public final boolean isBlocking() {
            return this.state == WorkerState.BLOCKING;
        }

        @Nullable
        public final Object getNextParkedWorker() {
            return this.nextParkedWorker;
        }

        public final void setNextParkedWorker(@Nullable Object obj) {
            this.nextParkedWorker = obj;
        }

        public final boolean tryForbidTermination() {
            int i = this.terminationState;
            if (i == 1 || i == -1) {
                return false;
            }
            if (i == 0) {
                return terminationState$FU.compareAndSet(this, 0, -1);
            }
            throw new IllegalStateException(("Invalid terminationState = " + i).toString());
        }

        public final boolean tryAcquireCpuPermit() {
            WorkerState workerState = this.state;
            WorkerState workerState2 = WorkerState.CPU_ACQUIRED;
            if (workerState == workerState2) {
                return true;
            }
            if (!CoroutineScheduler.this.cpuPermits.tryAcquire()) {
                return false;
            }
            this.state = workerState2;
            return true;
        }

        public final boolean tryReleaseCpu$kotlinx_coroutines_core(@NotNull WorkerState workerState) {
            Intrinsics.checkParameterIsNotNull(workerState, "newState");
            WorkerState workerState2 = this.state;
            boolean z = workerState2 == WorkerState.CPU_ACQUIRED;
            if (z) {
                CoroutineScheduler.this.cpuPermits.release();
            }
            if (workerState2 != workerState) {
                this.state = workerState;
            }
            return z;
        }

        public void run() {
            boolean z = false;
            while (!CoroutineScheduler.this.isTerminated() && this.state != WorkerState.TERMINATED) {
                Task findTask$kotlinx_coroutines_core = findTask$kotlinx_coroutines_core();
                if (findTask$kotlinx_coroutines_core == null) {
                    if (this.state == WorkerState.CPU_ACQUIRED) {
                        cpuWorkerIdle();
                    } else {
                        blockingWorkerIdle();
                    }
                    z = true;
                } else {
                    TaskMode mode = findTask$kotlinx_coroutines_core.getMode();
                    if (z) {
                        idleReset(mode);
                        z = false;
                    }
                    beforeTask(mode, findTask$kotlinx_coroutines_core.submissionTime);
                    CoroutineScheduler.this.runSafely(findTask$kotlinx_coroutines_core);
                    afterTask(mode);
                }
            }
            tryReleaseCpu$kotlinx_coroutines_core(WorkerState.TERMINATED);
        }

        private final void beforeTask(TaskMode taskMode, long j) {
            if (taskMode != TaskMode.NON_BLOCKING) {
                CoroutineScheduler.controlState$FU.addAndGet(CoroutineScheduler.this, 2097152);
                if (tryReleaseCpu$kotlinx_coroutines_core(WorkerState.BLOCKING)) {
                    CoroutineScheduler.this.requestCpuWorker();
                }
            } else if (CoroutineScheduler.this.cpuPermits.availablePermits() != 0) {
                long nanoTime = TasksKt.schedulerTimeSource.nanoTime();
                long j2 = TasksKt.WORK_STEALING_TIME_RESOLUTION_NS;
                if (nanoTime - j >= j2 && nanoTime - this.lastExhaustionTime >= j2 * ((long) 5)) {
                    this.lastExhaustionTime = nanoTime;
                    CoroutineScheduler.this.requestCpuWorker();
                }
            }
        }

        private final void afterTask(TaskMode taskMode) {
            if (taskMode != TaskMode.NON_BLOCKING) {
                CoroutineScheduler.controlState$FU.addAndGet(CoroutineScheduler.this, -2097152);
                WorkerState workerState = this.state;
                if (workerState != WorkerState.TERMINATED) {
                    if (DebugKt.getASSERTIONS_ENABLED()) {
                        if (!(workerState == WorkerState.BLOCKING)) {
                            throw new AssertionError();
                        }
                    }
                    this.state = WorkerState.RETIRING;
                }
            }
        }

        public final int nextInt$kotlinx_coroutines_core(int i) {
            int i2 = this.rngState;
            int i3 = i2 ^ (i2 << 13);
            this.rngState = i3;
            int i4 = i3 ^ (i3 >> 17);
            this.rngState = i4;
            int i5 = i4 ^ (i4 << 5);
            this.rngState = i5;
            int i6 = i - 1;
            if ((i6 & i) == 0) {
                return i6 & i5;
            }
            return (Integer.MAX_VALUE & i5) % i;
        }

        private final void cpuWorkerIdle() {
            int i = this.spins;
            if (i <= CoroutineScheduler.MAX_YIELDS) {
                this.spins = i + 1;
                if (i >= CoroutineScheduler.MAX_SPINS) {
                    Thread.yield();
                    return;
                }
                return;
            }
            if (this.parkTimeNs < CoroutineScheduler.MAX_PARK_TIME_NS) {
                this.parkTimeNs = RangesKt___RangesKt.coerceAtMost((this.parkTimeNs * 3) >>> 1, CoroutineScheduler.MAX_PARK_TIME_NS);
            }
            tryReleaseCpu$kotlinx_coroutines_core(WorkerState.PARKING);
            doPark((long) this.parkTimeNs);
        }

        private final void blockingWorkerIdle() {
            tryReleaseCpu$kotlinx_coroutines_core(WorkerState.PARKING);
            if (blockingQuiescence()) {
                this.terminationState = 0;
                if (this.terminationDeadline == 0) {
                    this.terminationDeadline = System.nanoTime() + CoroutineScheduler.this.idleWorkerKeepAliveNs;
                }
                if (doPark(CoroutineScheduler.this.idleWorkerKeepAliveNs) && System.nanoTime() - this.terminationDeadline >= 0) {
                    this.terminationDeadline = 0;
                    tryTerminateWorker();
                }
            }
        }

        private final boolean doPark(long j) {
            CoroutineScheduler.this.parkedWorkersStackPush(this);
            if (!blockingQuiescence()) {
                return false;
            }
            LockSupport.parkNanos(j);
            return true;
        }

        private final void tryTerminateWorker() {
            synchronized (CoroutineScheduler.this.workers) {
                if (!CoroutineScheduler.this.isTerminated()) {
                    if (CoroutineScheduler.this.getCreatedWorkers() > CoroutineScheduler.this.corePoolSize) {
                        if (blockingQuiescence()) {
                            if (terminationState$FU.compareAndSet(this, 0, 1)) {
                                int i = this.indexInArray;
                                setIndexInArray(0);
                                CoroutineScheduler.this.parkedWorkersStackTopUpdate(this, i, 0);
                                int andDecrement = (int) (CoroutineScheduler.controlState$FU.getAndDecrement(CoroutineScheduler.this) & 2097151);
                                if (andDecrement != i) {
                                    Worker worker = CoroutineScheduler.this.workers[andDecrement];
                                    if (worker == null) {
                                        Intrinsics.throwNpe();
                                    }
                                    CoroutineScheduler.this.workers[i] = worker;
                                    worker.setIndexInArray(i);
                                    CoroutineScheduler.this.parkedWorkersStackTopUpdate(worker, andDecrement, i);
                                }
                                CoroutineScheduler.this.workers[andDecrement] = null;
                                Unit unit = Unit.INSTANCE;
                                this.state = WorkerState.TERMINATED;
                            }
                        }
                    }
                }
            }
        }

        private final boolean blockingQuiescence() {
            Task removeFirstWithModeOrNull = CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.PROBABLY_BLOCKING);
            if (removeFirstWithModeOrNull == null) {
                return true;
            }
            this.localQueue.add(removeFirstWithModeOrNull, CoroutineScheduler.this.globalQueue);
            return false;
        }

        private final void idleReset(TaskMode taskMode) {
            this.terminationDeadline = 0;
            this.lastStealIndex = 0;
            if (this.state == WorkerState.PARKING) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(taskMode == TaskMode.PROBABLY_BLOCKING)) {
                        throw new AssertionError();
                    }
                }
                this.state = WorkerState.BLOCKING;
                this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            }
            this.spins = 0;
        }

        public final void idleResetBeforeUnpark() {
            this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            this.spins = 0;
        }

        @Nullable
        public final Task findTask$kotlinx_coroutines_core() {
            if (tryAcquireCpuPermit()) {
                return findTaskWithCpuPermit();
            }
            Task poll = this.localQueue.poll();
            return poll != null ? poll : CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.PROBABLY_BLOCKING);
        }

        private final Task findTaskWithCpuPermit() {
            Task task;
            Task removeFirstWithModeOrNull;
            boolean z = nextInt$kotlinx_coroutines_core(CoroutineScheduler.this.corePoolSize * 2) == 0;
            if (z && (removeFirstWithModeOrNull = CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.NON_BLOCKING)) != null) {
                return removeFirstWithModeOrNull;
            }
            Task poll = this.localQueue.poll();
            if (poll != null) {
                return poll;
            }
            if (z || (task = (Task) CoroutineScheduler.this.globalQueue.removeFirstOrNull()) == null) {
                return trySteal();
            }
            return task;
        }

        private final Task trySteal() {
            int access$getCreatedWorkers$p = CoroutineScheduler.this.getCreatedWorkers();
            if (access$getCreatedWorkers$p < 2) {
                return null;
            }
            int i = this.lastStealIndex;
            if (i == 0) {
                i = nextInt$kotlinx_coroutines_core(access$getCreatedWorkers$p);
            }
            int i2 = 1;
            int i3 = i + 1;
            if (i3 <= access$getCreatedWorkers$p) {
                i2 = i3;
            }
            this.lastStealIndex = i2;
            Worker worker = CoroutineScheduler.this.workers[i2];
            if (worker == null || worker == this || !this.localQueue.trySteal(worker.localQueue, CoroutineScheduler.this.globalQueue)) {
                return null;
            }
            return this.localQueue.poll();
        }
    }

    /* access modifiers changed from: private */
    public final void parkedWorkersStackTopUpdate(Worker worker, int i, int i2) {
        while (true) {
            long j = this.parkedWorkersStack;
            int i3 = (int) (2097151 & j);
            long j2 = (2097152 + j) & -2097152;
            if (i3 == i) {
                i3 = i2 == 0 ? parkedWorkersStackNextIndex(worker) : i2;
            }
            if (i3 >= 0) {
                if (parkedWorkersStack$FU.compareAndSet(this, j, j2 | ((long) i3))) {
                    return;
                }
            }
        }
    }

    private final Worker parkedWorkersStackPop() {
        while (true) {
            long j = this.parkedWorkersStack;
            Worker worker = this.workers[(int) (2097151 & j)];
            if (worker == null) {
                return null;
            }
            long j2 = (2097152 + j) & -2097152;
            int parkedWorkersStackNextIndex = parkedWorkersStackNextIndex(worker);
            if (parkedWorkersStackNextIndex >= 0) {
                if (parkedWorkersStack$FU.compareAndSet(this, j, ((long) parkedWorkersStackNextIndex) | j2)) {
                    worker.setNextParkedWorker(NOT_IN_STACK);
                    return worker;
                }
            }
        }
    }
}
