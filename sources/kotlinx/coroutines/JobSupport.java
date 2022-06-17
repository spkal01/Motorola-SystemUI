package kotlinx.coroutines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.internal.ConcurrentKt;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;
import kotlinx.coroutines.internal.OpDescriptor;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: JobSupport.kt */
public class JobSupport implements Job, ChildJob, ParentJob {
    private static final AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.class, Object.class, "_state");
    private volatile Object _state;
    @Nullable
    public volatile ChildHandle parentHandle;

    /* access modifiers changed from: protected */
    public void afterCompletionInternal(@Nullable Object obj, int i) {
    }

    public boolean getHandlesException$kotlinx_coroutines_core() {
        return true;
    }

    public boolean getOnCancelComplete$kotlinx_coroutines_core() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean handleJobException(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "exception");
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isScopedCoroutine() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCancelling(@Nullable Throwable th) {
    }

    /* access modifiers changed from: protected */
    public void onCompletionInternal(@Nullable Object obj) {
    }

    public void onStartInternal$kotlinx_coroutines_core() {
    }

    public JobSupport(boolean z) {
        this._state = z ? JobSupportKt.EMPTY_ACTIVE : JobSupportKt.EMPTY_NEW;
    }

    public <R> R fold(R r, @NotNull Function2<? super R, ? super CoroutineContext.Element, ? extends R> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "operation");
        return Job.DefaultImpls.fold(this, r, function2);
    }

    @Nullable
    public <E extends CoroutineContext.Element> E get(@NotNull CoroutineContext.Key<E> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        return Job.DefaultImpls.get(this, key);
    }

    @NotNull
    public CoroutineContext minusKey(@NotNull CoroutineContext.Key<?> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        return Job.DefaultImpls.minusKey(this, key);
    }

    @NotNull
    public CoroutineContext plus(@NotNull CoroutineContext coroutineContext) {
        Intrinsics.checkParameterIsNotNull(coroutineContext, "context");
        return Job.DefaultImpls.plus(this, coroutineContext);
    }

    @NotNull
    public final CoroutineContext.Key<?> getKey() {
        return Job.Key;
    }

    private final boolean addLastAtomic(Object obj, NodeList nodeList, JobNode<?> jobNode) {
        int tryCondAddNext;
        JobSupport$addLastAtomic$$inlined$addLastIf$1 jobSupport$addLastAtomic$$inlined$addLastIf$1 = new JobSupport$addLastAtomic$$inlined$addLastIf$1(jobNode, jobNode, this, obj);
        do {
            Object prev = nodeList.getPrev();
            if (prev != null) {
                tryCondAddNext = ((LockFreeLinkedListNode) prev).tryCondAddNext(jobNode, nodeList, jobSupport$addLastAtomic$$inlined$addLastIf$1);
                if (tryCondAddNext == 1) {
                    return true;
                }
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
        } while (tryCondAddNext != 2);
        return false;
    }

    public final void initParentJobInternal$kotlinx_coroutines_core(@Nullable Job job) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.parentHandle == null)) {
                throw new AssertionError();
            }
        }
        if (job == null) {
            this.parentHandle = NonDisposableHandle.INSTANCE;
            return;
        }
        job.start();
        ChildHandle attachChild = job.attachChild(this);
        this.parentHandle = attachChild;
        if (isCompleted()) {
            attachChild.dispose();
            this.parentHandle = NonDisposableHandle.INSTANCE;
        }
    }

    private final boolean cancelMakeCompleting(Object obj) {
        int tryMakeCompleting;
        do {
            Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof Incomplete) || (((state$kotlinx_coroutines_core instanceof Finishing) && ((Finishing) state$kotlinx_coroutines_core).isCompleting) || (tryMakeCompleting = tryMakeCompleting(state$kotlinx_coroutines_core, new CompletedExceptionally(createCauseException(obj), false, 2, (DefaultConstructorMarker) null), 0)) == 0)) {
                return false;
            }
            if (tryMakeCompleting == 1 || tryMakeCompleting == 2) {
                return true;
            }
        } while (tryMakeCompleting == 3);
        throw new IllegalStateException("unexpected result".toString());
    }

    private final boolean joinInternal() {
        Object state$kotlinx_coroutines_core;
        do {
            state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
                return false;
            }
        } while (startInternal(state$kotlinx_coroutines_core) < 0);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003b, code lost:
        if (r0 == null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003d, code lost:
        notifyCancelling(((kotlinx.coroutines.JobSupport.Finishing) r2).getList(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0046, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean makeCancelling(java.lang.Object r8) {
        /*
            r7 = this;
            r0 = 0
            r1 = r0
        L_0x0002:
            java.lang.Object r2 = r7.getState$kotlinx_coroutines_core()
            boolean r3 = r2 instanceof kotlinx.coroutines.JobSupport.Finishing
            r4 = 0
            r5 = 1
            if (r3 == 0) goto L_0x004a
            monitor-enter(r2)
            r3 = r2
            kotlinx.coroutines.JobSupport$Finishing r3 = (kotlinx.coroutines.JobSupport.Finishing) r3     // Catch:{ all -> 0x0047 }
            boolean r3 = r3.isSealed()     // Catch:{ all -> 0x0047 }
            if (r3 == 0) goto L_0x0018
            monitor-exit(r2)
            return r4
        L_0x0018:
            r3 = r2
            kotlinx.coroutines.JobSupport$Finishing r3 = (kotlinx.coroutines.JobSupport.Finishing) r3     // Catch:{ all -> 0x0047 }
            boolean r3 = r3.isCancelling()     // Catch:{ all -> 0x0047 }
            if (r8 != 0) goto L_0x0023
            if (r3 != 0) goto L_0x0030
        L_0x0023:
            if (r1 == 0) goto L_0x0026
            goto L_0x002a
        L_0x0026:
            java.lang.Throwable r1 = r7.createCauseException(r8)     // Catch:{ all -> 0x0047 }
        L_0x002a:
            r8 = r2
            kotlinx.coroutines.JobSupport$Finishing r8 = (kotlinx.coroutines.JobSupport.Finishing) r8     // Catch:{ all -> 0x0047 }
            r8.addExceptionLocked(r1)     // Catch:{ all -> 0x0047 }
        L_0x0030:
            r8 = r2
            kotlinx.coroutines.JobSupport$Finishing r8 = (kotlinx.coroutines.JobSupport.Finishing) r8     // Catch:{ all -> 0x0047 }
            java.lang.Throwable r8 = r8.rootCause     // Catch:{ all -> 0x0047 }
            r1 = r3 ^ 1
            if (r1 == 0) goto L_0x003a
            r0 = r8
        L_0x003a:
            monitor-exit(r2)
            if (r0 == 0) goto L_0x0046
            kotlinx.coroutines.JobSupport$Finishing r2 = (kotlinx.coroutines.JobSupport.Finishing) r2
            kotlinx.coroutines.NodeList r8 = r2.getList()
            r7.notifyCancelling(r8, r0)
        L_0x0046:
            return r5
        L_0x0047:
            r7 = move-exception
            monitor-exit(r2)
            throw r7
        L_0x004a:
            boolean r3 = r2 instanceof kotlinx.coroutines.Incomplete
            if (r3 == 0) goto L_0x00a1
            if (r1 == 0) goto L_0x0051
            goto L_0x0055
        L_0x0051:
            java.lang.Throwable r1 = r7.createCauseException(r8)
        L_0x0055:
            r3 = r2
            kotlinx.coroutines.Incomplete r3 = (kotlinx.coroutines.Incomplete) r3
            boolean r6 = r3.isActive()
            if (r6 == 0) goto L_0x0065
            boolean r2 = r7.tryMakeCancelling(r3, r1)
            if (r2 == 0) goto L_0x0002
            return r5
        L_0x0065:
            kotlinx.coroutines.CompletedExceptionally r3 = new kotlinx.coroutines.CompletedExceptionally
            r6 = 2
            r3.<init>(r1, r4, r6, r0)
            int r3 = r7.tryMakeCompleting(r2, r3, r4)
            if (r3 == 0) goto L_0x0086
            if (r3 == r5) goto L_0x0085
            if (r3 == r6) goto L_0x0085
            r2 = 3
            if (r3 != r2) goto L_0x0079
            goto L_0x0002
        L_0x0079:
            java.lang.String r7 = "unexpected result"
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r7 = r7.toString()
            r8.<init>(r7)
            throw r8
        L_0x0085:
            return r5
        L_0x0086:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Cannot happen in "
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r7 = r7.toString()
            r8.<init>(r7)
            throw r8
        L_0x00a1:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobSupport.makeCancelling(java.lang.Object):boolean");
    }

    @NotNull
    public final DisposableHandle invokeOnCompletion(boolean z, boolean z2, @NotNull Function1<? super Throwable, Unit> function1) {
        Throwable th;
        Intrinsics.checkParameterIsNotNull(function1, "handler");
        Throwable th2 = null;
        JobNode<?> jobNode = null;
        while (true) {
            Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (state$kotlinx_coroutines_core instanceof Empty) {
                Empty empty = (Empty) state$kotlinx_coroutines_core;
                if (empty.isActive()) {
                    if (jobNode == null) {
                        jobNode = makeNode(function1, z);
                    }
                    if (_state$FU.compareAndSet(this, state$kotlinx_coroutines_core, jobNode)) {
                        return jobNode;
                    }
                } else {
                    promoteEmptyToNodeList(empty);
                }
            } else if (state$kotlinx_coroutines_core instanceof Incomplete) {
                NodeList list = ((Incomplete) state$kotlinx_coroutines_core).getList();
                if (list != null) {
                    DisposableHandle disposableHandle = NonDisposableHandle.INSTANCE;
                    if (!z || !(state$kotlinx_coroutines_core instanceof Finishing)) {
                        th = null;
                    } else {
                        synchronized (state$kotlinx_coroutines_core) {
                            th = ((Finishing) state$kotlinx_coroutines_core).rootCause;
                            if (th == null || ((function1 instanceof ChildHandleNode) && !((Finishing) state$kotlinx_coroutines_core).isCompleting)) {
                                if (jobNode == null) {
                                    jobNode = makeNode(function1, z);
                                }
                                if (addLastAtomic(state$kotlinx_coroutines_core, list, jobNode)) {
                                    if (th == null) {
                                        return jobNode;
                                    }
                                    disposableHandle = jobNode;
                                }
                            }
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                    if (th != null) {
                        if (z2) {
                            function1.invoke(th);
                        }
                        return disposableHandle;
                    }
                    if (jobNode == null) {
                        jobNode = makeNode(function1, z);
                    }
                    if (addLastAtomic(state$kotlinx_coroutines_core, list, jobNode)) {
                        return jobNode;
                    }
                } else if (state$kotlinx_coroutines_core != null) {
                    promoteSingleToNodeList((JobNode) state$kotlinx_coroutines_core);
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.JobNode<*>");
                }
            } else {
                if (z2) {
                    if (!(state$kotlinx_coroutines_core instanceof CompletedExceptionally)) {
                        state$kotlinx_coroutines_core = null;
                    }
                    CompletedExceptionally completedExceptionally = (CompletedExceptionally) state$kotlinx_coroutines_core;
                    if (completedExceptionally != null) {
                        th2 = completedExceptionally.cause;
                    }
                    function1.invoke(th2);
                }
                return NonDisposableHandle.INSTANCE;
            }
        }
    }

    public final boolean makeCompletingOnce$kotlinx_coroutines_core(@Nullable Object obj, int i) {
        int tryMakeCompleting;
        do {
            tryMakeCompleting = tryMakeCompleting(getState$kotlinx_coroutines_core(), obj, i);
            if (tryMakeCompleting == 0) {
                throw new IllegalStateException("Job " + this + " is already complete or completing, " + "but is being completed with " + obj, getExceptionOrNull(obj));
            } else if (tryMakeCompleting == 1) {
                return true;
            } else {
                if (tryMakeCompleting == 2) {
                    return false;
                }
            }
        } while (tryMakeCompleting == 3);
        throw new IllegalStateException("unexpected result".toString());
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x001d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x000d A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void removeNode$kotlinx_coroutines_core(@org.jetbrains.annotations.NotNull kotlinx.coroutines.JobNode<?> r4) {
        /*
            r3 = this;
            java.lang.String r0 = "node"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
        L_0x0005:
            java.lang.Object r0 = r3.getState$kotlinx_coroutines_core()
            boolean r1 = r0 instanceof kotlinx.coroutines.JobNode
            if (r1 == 0) goto L_0x001d
            if (r0 == r4) goto L_0x0010
            return
        L_0x0010:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r1 = _state$FU
            kotlinx.coroutines.Empty r2 = kotlinx.coroutines.JobSupportKt.EMPTY_ACTIVE
            boolean r0 = r1.compareAndSet(r3, r0, r2)
            if (r0 == 0) goto L_0x0005
            return
        L_0x001d:
            boolean r3 = r0 instanceof kotlinx.coroutines.Incomplete
            if (r3 == 0) goto L_0x002c
            kotlinx.coroutines.Incomplete r0 = (kotlinx.coroutines.Incomplete) r0
            kotlinx.coroutines.NodeList r3 = r0.getList()
            if (r3 == 0) goto L_0x002c
            r4.remove()
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobSupport.removeNode$kotlinx_coroutines_core(kotlinx.coroutines.JobNode):void");
    }

    public final boolean start() {
        int startInternal;
        do {
            startInternal = startInternal(getState$kotlinx_coroutines_core());
            if (startInternal == 0) {
                return false;
            }
        } while (startInternal != 1);
        return true;
    }

    public boolean isActive() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        return (state$kotlinx_coroutines_core instanceof Incomplete) && ((Incomplete) state$kotlinx_coroutines_core).isActive();
    }

    public final boolean isCompleted() {
        return !(getState$kotlinx_coroutines_core() instanceof Incomplete);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public final /* synthetic */ Object joinSuspend(@NotNull Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation), 1);
        CancellableContinuationKt.disposeOnCancellation(cancellableContinuationImpl, invokeOnCompletion(new ResumeOnCompletion(this, cancellableContinuationImpl)));
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }

    private final boolean tryFinalizeFinishingState(Finishing finishing, Object obj, int i) {
        boolean isCancelling;
        Throwable finalRootCause;
        boolean z = false;
        if (!(getState$kotlinx_coroutines_core() == finishing)) {
            throw new IllegalArgumentException("Failed requirement.".toString());
        } else if (!(!finishing.isSealed())) {
            throw new IllegalArgumentException("Failed requirement.".toString());
        } else if (finishing.isCompleting) {
            CompletedExceptionally completedExceptionally = (CompletedExceptionally) (!(obj instanceof CompletedExceptionally) ? null : obj);
            Throwable th = completedExceptionally != null ? completedExceptionally.cause : null;
            synchronized (finishing) {
                isCancelling = finishing.isCancelling();
                List<Throwable> sealLocked = finishing.sealLocked(th);
                finalRootCause = getFinalRootCause(finishing, sealLocked);
                if (finalRootCause != null) {
                    addSuppressedExceptions(finalRootCause, sealLocked);
                }
            }
            if (!(finalRootCause == null || finalRootCause == th)) {
                obj = new CompletedExceptionally(finalRootCause, false, 2, (DefaultConstructorMarker) null);
            }
            if (finalRootCause != null) {
                if (cancelParent(finalRootCause) || handleJobException(finalRootCause)) {
                    z = true;
                }
                if (z) {
                    if (obj != null) {
                        ((CompletedExceptionally) obj).makeHandled();
                    } else {
                        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.CompletedExceptionally");
                    }
                }
            }
            if (!isCancelling) {
                onCancelling(finalRootCause);
            }
            onCompletionInternal(obj);
            if (_state$FU.compareAndSet(this, finishing, JobSupportKt.boxIncomplete(obj))) {
                completeStateFinalization(finishing, obj, i);
                return true;
            }
            throw new IllegalArgumentException(("Unexpected state: " + this._state + ", expected: " + finishing + ", update: " + obj).toString());
        } else {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
    }

    private final Throwable getFinalRootCause(Finishing finishing, List<? extends Throwable> list) {
        T t = null;
        if (!list.isEmpty()) {
            Iterator<T> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                T next = it.next();
                if (!(((Throwable) next) instanceof CancellationException)) {
                    t = next;
                    break;
                }
            }
            Throwable th = (Throwable) t;
            return th != null ? th : (Throwable) list.get(0);
        } else if (finishing.isCancelling()) {
            return createJobCancellationException();
        } else {
            return null;
        }
    }

    private final void addSuppressedExceptions(Throwable th, List<? extends Throwable> list) {
        if (list.size() > 1) {
            Set identitySet = ConcurrentKt.identitySet(list.size());
            Throwable unwrap = StackTraceRecoveryKt.unwrap(th);
            for (Throwable unwrap2 : list) {
                Throwable unwrap3 = StackTraceRecoveryKt.unwrap(unwrap2);
                if (unwrap3 != th && unwrap3 != unwrap && !(unwrap3 instanceof CancellationException) && identitySet.add(unwrap3)) {
                    ExceptionsKt__ExceptionsKt.addSuppressed(th, unwrap3);
                }
            }
        }
    }

    private final boolean tryFinalizeSimpleState(Incomplete incomplete, Object obj, int i) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!((incomplete instanceof Empty) || (incomplete instanceof JobNode))) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED() && !(!(obj instanceof CompletedExceptionally))) {
            throw new AssertionError();
        } else if (!_state$FU.compareAndSet(this, incomplete, JobSupportKt.boxIncomplete(obj))) {
            return false;
        } else {
            onCancelling((Throwable) null);
            onCompletionInternal(obj);
            completeStateFinalization(incomplete, obj, i);
            return true;
        }
    }

    private final void completeStateFinalization(Incomplete incomplete, Object obj, int i) {
        ChildHandle childHandle = this.parentHandle;
        if (childHandle != null) {
            childHandle.dispose();
            this.parentHandle = NonDisposableHandle.INSTANCE;
        }
        Throwable th = null;
        CompletedExceptionally completedExceptionally = (CompletedExceptionally) (!(obj instanceof CompletedExceptionally) ? null : obj);
        if (completedExceptionally != null) {
            th = completedExceptionally.cause;
        }
        if (incomplete instanceof JobNode) {
            try {
                ((JobNode) incomplete).invoke(th);
            } catch (Throwable th2) {
                handleOnCompletionException$kotlinx_coroutines_core(new CompletionHandlerException("Exception in completion handler " + incomplete + " for " + this, th2));
            }
        } else {
            NodeList list = incomplete.getList();
            if (list != null) {
                notifyCompletion(list, th);
            }
        }
        afterCompletionInternal(obj, i);
    }

    private final void notifyCancelling(NodeList nodeList, Throwable th) {
        onCancelling(th);
        Object next = nodeList.getNext();
        if (next != null) {
            CompletionHandlerException completionHandlerException = null;
            for (LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) next; !Intrinsics.areEqual((Object) lockFreeLinkedListNode, (Object) nodeList); lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode()) {
                if (lockFreeLinkedListNode instanceof JobCancellingNode) {
                    JobNode jobNode = (JobNode) lockFreeLinkedListNode;
                    try {
                        jobNode.invoke(th);
                    } catch (Throwable th2) {
                        if (completionHandlerException != null) {
                            ExceptionsKt__ExceptionsKt.addSuppressed(completionHandlerException, th2);
                        } else {
                            completionHandlerException = new CompletionHandlerException("Exception in completion handler " + jobNode + " for " + this, th2);
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }
            }
            if (completionHandlerException != null) {
                handleOnCompletionException$kotlinx_coroutines_core(completionHandlerException);
            }
            cancelParent(th);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
    }

    private final boolean cancelParent(Throwable th) {
        if (isScopedCoroutine()) {
            return true;
        }
        boolean z = th instanceof CancellationException;
        ChildHandle childHandle = this.parentHandle;
        if (childHandle == null || childHandle == NonDisposableHandle.INSTANCE) {
            return z;
        }
        if (childHandle.childCancelled(th) || z) {
            return true;
        }
        return false;
    }

    private final int startInternal(Object obj) {
        if (obj instanceof Empty) {
            if (((Empty) obj).isActive()) {
                return 0;
            }
            if (!_state$FU.compareAndSet(this, obj, JobSupportKt.EMPTY_ACTIVE)) {
                return -1;
            }
            onStartInternal$kotlinx_coroutines_core();
            return 1;
        } else if (!(obj instanceof InactiveNodeList)) {
            return 0;
        } else {
            if (!_state$FU.compareAndSet(this, obj, ((InactiveNodeList) obj).getList())) {
                return -1;
            }
            onStartInternal$kotlinx_coroutines_core();
            return 1;
        }
    }

    @NotNull
    public final CancellationException getCancellationException() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        if (state$kotlinx_coroutines_core instanceof Finishing) {
            Throwable th = ((Finishing) state$kotlinx_coroutines_core).rootCause;
            if (th != null) {
                CancellationException cancellationException = toCancellationException(th, DebugStringsKt.getClassSimpleName(this) + " is cancelling");
                if (cancellationException != null) {
                    return cancellationException;
                }
            }
            throw new IllegalStateException(("Job is still new or active: " + this).toString());
        } else if (state$kotlinx_coroutines_core instanceof Incomplete) {
            throw new IllegalStateException(("Job is still new or active: " + this).toString());
        } else if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            return toCancellationException$default(this, ((CompletedExceptionally) state$kotlinx_coroutines_core).cause, (String) null, 1, (Object) null);
        } else {
            return new JobCancellationException(DebugStringsKt.getClassSimpleName(this) + " has completed normally", (Throwable) null, this);
        }
    }

    public static /* synthetic */ CancellationException toCancellationException$default(JobSupport jobSupport, Throwable th, String str, int i, Object obj) {
        if (obj == null) {
            if ((i & 1) != 0) {
                str = null;
            }
            return jobSupport.toCancellationException(th, str);
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: toCancellationException");
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final CancellationException toCancellationException(@NotNull Throwable th, @Nullable String str) {
        Intrinsics.checkParameterIsNotNull(th, "$this$toCancellationException");
        CancellationException cancellationException = (CancellationException) (!(th instanceof CancellationException) ? null : th);
        if (cancellationException == null) {
            if (str == null) {
                str = DebugStringsKt.getClassSimpleName(th) + " was cancelled";
            }
            cancellationException = new JobCancellationException(str, th, this);
        }
        return cancellationException;
    }

    @NotNull
    public final DisposableHandle invokeOnCompletion(@NotNull Function1<? super Throwable, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "handler");
        return invokeOnCompletion(false, true, function1);
    }

    private final JobNode<?> makeNode(Function1<? super Throwable, Unit> function1, boolean z) {
        boolean z2 = true;
        JobCancellingNode jobCancellingNode = null;
        if (z) {
            if (function1 instanceof JobCancellingNode) {
                jobCancellingNode = function1;
            }
            JobCancellingNode jobCancellingNode2 = jobCancellingNode;
            if (jobCancellingNode2 == null) {
                return new InvokeOnCancelling(this, function1);
            }
            if (jobCancellingNode2.job != this) {
                z2 = false;
            }
            if (z2) {
                return jobCancellingNode2;
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
        if (function1 instanceof JobNode) {
            jobCancellingNode = function1;
        }
        JobNode<?> jobNode = jobCancellingNode;
        if (jobNode == null) {
            return new InvokeOnCompletion(this, function1);
        }
        if (jobNode.job != this || (jobNode instanceof JobCancellingNode)) {
            z2 = false;
        }
        if (z2) {
            return jobNode;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    /* JADX WARNING: type inference failed for: r1v2, types: [kotlinx.coroutines.InactiveNodeList] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void promoteEmptyToNodeList(kotlinx.coroutines.Empty r3) {
        /*
            r2 = this;
            kotlinx.coroutines.NodeList r0 = new kotlinx.coroutines.NodeList
            r0.<init>()
            boolean r1 = r3.isActive()
            if (r1 == 0) goto L_0x000c
            goto L_0x0012
        L_0x000c:
            kotlinx.coroutines.InactiveNodeList r1 = new kotlinx.coroutines.InactiveNodeList
            r1.<init>(r0)
            r0 = r1
        L_0x0012:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r1 = _state$FU
            r1.compareAndSet(r2, r3, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobSupport.promoteEmptyToNodeList(kotlinx.coroutines.Empty):void");
    }

    private final void promoteSingleToNodeList(JobNode<?> jobNode) {
        jobNode.addOneIfEmpty(new NodeList());
        _state$FU.compareAndSet(this, jobNode, jobNode.getNextNode());
    }

    @Nullable
    public final Object join(@NotNull Continuation<? super Unit> continuation) {
        if (joinInternal()) {
            return joinSuspend(continuation);
        }
        YieldKt.checkCompletion(continuation.getContext());
        return Unit.INSTANCE;
    }

    public void cancel(@Nullable CancellationException cancellationException) {
        cancelInternal(cancellationException);
    }

    public boolean cancelInternal(@Nullable Throwable th) {
        return cancelImpl$kotlinx_coroutines_core(th) && getHandlesException$kotlinx_coroutines_core();
    }

    public final void parentCancelled(@NotNull ParentJob parentJob) {
        Intrinsics.checkParameterIsNotNull(parentJob, "parentJob");
        cancelImpl$kotlinx_coroutines_core(parentJob);
    }

    public boolean childCancelled(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "cause");
        if (th instanceof CancellationException) {
            return true;
        }
        if (!cancelImpl$kotlinx_coroutines_core(th) || !getHandlesException$kotlinx_coroutines_core()) {
            return false;
        }
        return true;
    }

    public final boolean cancelImpl$kotlinx_coroutines_core(@Nullable Object obj) {
        if (!getOnCancelComplete$kotlinx_coroutines_core() || !cancelMakeCompleting(obj)) {
            return makeCancelling(obj);
        }
        return true;
    }

    private final void notifyCompletion(@NotNull NodeList nodeList, Throwable th) {
        Object next = nodeList.getNext();
        if (next != null) {
            CompletionHandlerException completionHandlerException = null;
            for (LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) next; !Intrinsics.areEqual((Object) lockFreeLinkedListNode, (Object) nodeList); lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode()) {
                if (lockFreeLinkedListNode instanceof JobNode) {
                    JobNode jobNode = (JobNode) lockFreeLinkedListNode;
                    try {
                        jobNode.invoke(th);
                    } catch (Throwable th2) {
                        if (completionHandlerException != null) {
                            ExceptionsKt__ExceptionsKt.addSuppressed(completionHandlerException, th2);
                        } else {
                            completionHandlerException = new CompletionHandlerException("Exception in completion handler " + jobNode + " for " + this, th2);
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }
            }
            if (completionHandlerException != null) {
                handleOnCompletionException$kotlinx_coroutines_core(completionHandlerException);
                return;
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
    }

    private final JobCancellationException createJobCancellationException() {
        return new JobCancellationException("Job was cancelled", (Throwable) null, this);
    }

    @NotNull
    public CancellationException getChildJobCancellationCause() {
        Throwable th;
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        CancellationException cancellationException = null;
        if (state$kotlinx_coroutines_core instanceof Finishing) {
            th = ((Finishing) state$kotlinx_coroutines_core).rootCause;
        } else if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            th = ((CompletedExceptionally) state$kotlinx_coroutines_core).cause;
        } else if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
            th = null;
        } else {
            throw new IllegalStateException(("Cannot be cancelling child in this state: " + state$kotlinx_coroutines_core).toString());
        }
        if (th instanceof CancellationException) {
            cancellationException = th;
        }
        CancellationException cancellationException2 = cancellationException;
        if (cancellationException2 != null) {
            return cancellationException2;
        }
        return new JobCancellationException("Parent job is " + stateString(state$kotlinx_coroutines_core), th, this);
    }

    private final Throwable createCauseException(Object obj) {
        if (obj != null ? obj instanceof Throwable : true) {
            return obj != null ? (Throwable) obj : createJobCancellationException();
        }
        if (obj != null) {
            return ((ParentJob) obj).getChildJobCancellationCause();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.ParentJob");
    }

    private final NodeList getOrPromoteCancellingList(Incomplete incomplete) {
        NodeList list = incomplete.getList();
        if (list != null) {
            return list;
        }
        if (incomplete instanceof Empty) {
            return new NodeList();
        }
        if (incomplete instanceof JobNode) {
            promoteSingleToNodeList((JobNode) incomplete);
            return null;
        }
        throw new IllegalStateException(("State should have list: " + incomplete).toString());
    }

    private final boolean tryMakeCancelling(Incomplete incomplete, Throwable th) {
        if (DebugKt.getASSERTIONS_ENABLED() && !(!(incomplete instanceof Finishing))) {
            throw new AssertionError();
        } else if (!DebugKt.getASSERTIONS_ENABLED() || incomplete.isActive()) {
            NodeList orPromoteCancellingList = getOrPromoteCancellingList(incomplete);
            if (orPromoteCancellingList == null) {
                return false;
            }
            if (!_state$FU.compareAndSet(this, incomplete, new Finishing(orPromoteCancellingList, false, th))) {
                return false;
            }
            notifyCancelling(orPromoteCancellingList, th);
            return true;
        } else {
            throw new AssertionError();
        }
    }

    private final int tryMakeCompleting(Object obj, Object obj2, int i) {
        if (!(obj instanceof Incomplete)) {
            return 0;
        }
        if (((obj instanceof Empty) || (obj instanceof JobNode)) && !(obj instanceof ChildHandleNode) && !(obj2 instanceof CompletedExceptionally)) {
            return !tryFinalizeSimpleState((Incomplete) obj, obj2, i) ? 3 : 1;
        }
        return tryMakeCompletingSlowPath((Incomplete) obj, obj2, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0054, code lost:
        if (r3 == null) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0056, code lost:
        notifyCancelling(r0, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0059, code lost:
        r8 = firstChild(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x005d, code lost:
        if (r8 == null) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0063, code lost:
        if (tryWaitForChild(r2, r8, r9) == false) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0065, code lost:
        return 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x006b, code lost:
        if (tryFinalizeFinishingState(r2, r9, r10) == false) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x006d, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x006e, code lost:
        return 3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int tryMakeCompletingSlowPath(kotlinx.coroutines.Incomplete r8, java.lang.Object r9, int r10) {
        /*
            r7 = this;
            kotlinx.coroutines.NodeList r0 = r7.getOrPromoteCancellingList(r8)
            r1 = 3
            if (r0 == 0) goto L_0x007e
            boolean r2 = r8 instanceof kotlinx.coroutines.JobSupport.Finishing
            r3 = 0
            if (r2 != 0) goto L_0x000e
            r2 = r3
            goto L_0x000f
        L_0x000e:
            r2 = r8
        L_0x000f:
            kotlinx.coroutines.JobSupport$Finishing r2 = (kotlinx.coroutines.JobSupport.Finishing) r2
            r4 = 0
            if (r2 == 0) goto L_0x0015
            goto L_0x001a
        L_0x0015:
            kotlinx.coroutines.JobSupport$Finishing r2 = new kotlinx.coroutines.JobSupport$Finishing
            r2.<init>(r0, r4, r3)
        L_0x001a:
            monitor-enter(r2)
            boolean r5 = r2.isCompleting     // Catch:{ all -> 0x007b }
            if (r5 == 0) goto L_0x0021
            monitor-exit(r2)
            return r4
        L_0x0021:
            r4 = 1
            r2.isCompleting = r4     // Catch:{ all -> 0x007b }
            if (r2 == r8) goto L_0x0030
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r5 = _state$FU     // Catch:{ all -> 0x007b }
            boolean r5 = r5.compareAndSet(r7, r8, r2)     // Catch:{ all -> 0x007b }
            if (r5 != 0) goto L_0x0030
            monitor-exit(r2)
            return r1
        L_0x0030:
            boolean r5 = r2.isSealed()     // Catch:{ all -> 0x007b }
            r5 = r5 ^ r4
            if (r5 == 0) goto L_0x006f
            boolean r5 = r2.isCancelling()     // Catch:{ all -> 0x007b }
            boolean r6 = r9 instanceof kotlinx.coroutines.CompletedExceptionally     // Catch:{ all -> 0x007b }
            if (r6 != 0) goto L_0x0041
            r6 = r3
            goto L_0x0042
        L_0x0041:
            r6 = r9
        L_0x0042:
            kotlinx.coroutines.CompletedExceptionally r6 = (kotlinx.coroutines.CompletedExceptionally) r6     // Catch:{ all -> 0x007b }
            if (r6 == 0) goto L_0x004b
            java.lang.Throwable r6 = r6.cause     // Catch:{ all -> 0x007b }
            r2.addExceptionLocked(r6)     // Catch:{ all -> 0x007b }
        L_0x004b:
            java.lang.Throwable r6 = r2.rootCause     // Catch:{ all -> 0x007b }
            r5 = r5 ^ r4
            if (r5 == 0) goto L_0x0051
            r3 = r6
        L_0x0051:
            kotlin.Unit r5 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x007b }
            monitor-exit(r2)
            if (r3 == 0) goto L_0x0059
            r7.notifyCancelling(r0, r3)
        L_0x0059:
            kotlinx.coroutines.ChildHandleNode r8 = r7.firstChild(r8)
            if (r8 == 0) goto L_0x0067
            boolean r8 = r7.tryWaitForChild(r2, r8, r9)
            if (r8 == 0) goto L_0x0067
            r7 = 2
            return r7
        L_0x0067:
            boolean r7 = r7.tryFinalizeFinishingState(r2, r9, r10)
            if (r7 == 0) goto L_0x006e
            return r4
        L_0x006e:
            return r1
        L_0x006f:
            java.lang.String r7 = "Failed requirement."
            java.lang.IllegalArgumentException r8 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x007b }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x007b }
            r8.<init>(r7)     // Catch:{ all -> 0x007b }
            throw r8     // Catch:{ all -> 0x007b }
        L_0x007b:
            r7 = move-exception
            monitor-exit(r2)
            throw r7
        L_0x007e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobSupport.tryMakeCompletingSlowPath(kotlinx.coroutines.Incomplete, java.lang.Object, int):int");
    }

    private final Throwable getExceptionOrNull(@Nullable Object obj) {
        if (!(obj instanceof CompletedExceptionally)) {
            obj = null;
        }
        CompletedExceptionally completedExceptionally = (CompletedExceptionally) obj;
        if (completedExceptionally != null) {
            return completedExceptionally.cause;
        }
        return null;
    }

    private final ChildHandleNode firstChild(Incomplete incomplete) {
        ChildHandleNode childHandleNode = (ChildHandleNode) (!(incomplete instanceof ChildHandleNode) ? null : incomplete);
        if (childHandleNode != null) {
            return childHandleNode;
        }
        NodeList list = incomplete.getList();
        if (list != null) {
            return nextChild(list);
        }
        return null;
    }

    private final boolean tryWaitForChild(Finishing finishing, ChildHandleNode childHandleNode, Object obj) {
        while (Job.DefaultImpls.invokeOnCompletion$default(childHandleNode.childJob, false, false, new ChildCompletion(this, finishing, childHandleNode, obj), 1, (Object) null) == NonDisposableHandle.INSTANCE) {
            childHandleNode = nextChild(childHandleNode);
            if (childHandleNode == null) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public final void continueCompleting(Finishing finishing, ChildHandleNode childHandleNode, Object obj) {
        if (getState$kotlinx_coroutines_core() == finishing) {
            ChildHandleNode nextChild = nextChild(childHandleNode);
            if (nextChild == null || !tryWaitForChild(finishing, nextChild, obj)) {
                tryFinalizeFinishingState(finishing, obj, 0);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    private final ChildHandleNode nextChild(@NotNull LockFreeLinkedListNode lockFreeLinkedListNode) {
        while (lockFreeLinkedListNode.isRemoved()) {
            lockFreeLinkedListNode = lockFreeLinkedListNode.getPrevNode();
        }
        while (true) {
            lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode();
            if (!lockFreeLinkedListNode.isRemoved()) {
                if (lockFreeLinkedListNode instanceof ChildHandleNode) {
                    return (ChildHandleNode) lockFreeLinkedListNode;
                }
                if (lockFreeLinkedListNode instanceof NodeList) {
                    return null;
                }
            }
        }
    }

    @NotNull
    public final ChildHandle attachChild(@NotNull ChildJob childJob) {
        Intrinsics.checkParameterIsNotNull(childJob, "child");
        DisposableHandle invokeOnCompletion$default = Job.DefaultImpls.invokeOnCompletion$default(this, true, false, new ChildHandleNode(this, childJob), 2, (Object) null);
        if (invokeOnCompletion$default != null) {
            return (ChildHandle) invokeOnCompletion$default;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.ChildHandle");
    }

    public void handleOnCompletionException$kotlinx_coroutines_core(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "exception");
        throw th;
    }

    @NotNull
    public String toString() {
        return toDebugString() + '@' + DebugStringsKt.getHexAddress(this);
    }

    @NotNull
    public final String toDebugString() {
        return nameString$kotlinx_coroutines_core() + '{' + stateString(getState$kotlinx_coroutines_core()) + '}';
    }

    @NotNull
    public String nameString$kotlinx_coroutines_core() {
        return DebugStringsKt.getClassSimpleName(this);
    }

    private final String stateString(Object obj) {
        if (obj instanceof Finishing) {
            Finishing finishing = (Finishing) obj;
            if (finishing.isCancelling()) {
                return "Cancelling";
            }
            if (finishing.isCompleting) {
                return "Completing";
            }
            return "Active";
        } else if (!(obj instanceof Incomplete)) {
            return obj instanceof CompletedExceptionally ? "Cancelled" : "Completed";
        } else {
            if (((Incomplete) obj).isActive()) {
                return "Active";
            }
            return "New";
        }
    }

    /* compiled from: JobSupport.kt */
    private static final class Finishing implements Incomplete {
        private volatile Object _exceptionsHolder;
        public volatile boolean isCompleting;
        @NotNull
        private final NodeList list;
        @Nullable
        public volatile Throwable rootCause;

        @NotNull
        public NodeList getList() {
            return this.list;
        }

        public Finishing(@NotNull NodeList nodeList, boolean z, @Nullable Throwable th) {
            Intrinsics.checkParameterIsNotNull(nodeList, "list");
            this.list = nodeList;
            this.isCompleting = z;
            this.rootCause = th;
        }

        public final boolean isSealed() {
            return this._exceptionsHolder == JobSupportKt.SEALED;
        }

        public final boolean isCancelling() {
            return this.rootCause != null;
        }

        public boolean isActive() {
            return this.rootCause == null;
        }

        @NotNull
        public final List<Throwable> sealLocked(@Nullable Throwable th) {
            ArrayList<Throwable> arrayList;
            Object obj = this._exceptionsHolder;
            if (obj == null) {
                arrayList = allocateList();
            } else if (obj instanceof Throwable) {
                ArrayList<Throwable> allocateList = allocateList();
                allocateList.add(obj);
                arrayList = allocateList;
            } else if (obj instanceof ArrayList) {
                arrayList = (ArrayList) obj;
            } else {
                throw new IllegalStateException(("State is " + obj).toString());
            }
            Throwable th2 = this.rootCause;
            if (th2 != null) {
                arrayList.add(0, th2);
            }
            if (th != null && (!Intrinsics.areEqual((Object) th, (Object) th2))) {
                arrayList.add(th);
            }
            this._exceptionsHolder = JobSupportKt.SEALED;
            return arrayList;
        }

        public final void addExceptionLocked(@NotNull Throwable th) {
            Intrinsics.checkParameterIsNotNull(th, "exception");
            Throwable th2 = this.rootCause;
            if (th2 == null) {
                this.rootCause = th;
            } else if (th != th2) {
                Object obj = this._exceptionsHolder;
                if (obj == null) {
                    this._exceptionsHolder = th;
                } else if (obj instanceof Throwable) {
                    if (th != obj) {
                        ArrayList<Throwable> allocateList = allocateList();
                        allocateList.add(obj);
                        allocateList.add(th);
                        this._exceptionsHolder = allocateList;
                    }
                } else if (obj instanceof ArrayList) {
                    ((ArrayList) obj).add(th);
                } else {
                    throw new IllegalStateException(("State is " + obj).toString());
                }
            }
        }

        private final ArrayList<Throwable> allocateList() {
            return new ArrayList<>(4);
        }

        @NotNull
        public String toString() {
            return "Finishing[cancelling=" + isCancelling() + ", completing=" + this.isCompleting + ", rootCause=" + this.rootCause + ", exceptions=" + this._exceptionsHolder + ", list=" + getList() + ']';
        }
    }

    /* compiled from: JobSupport.kt */
    private static final class ChildCompletion extends JobNode<Job> {
        private final ChildHandleNode child;
        private final JobSupport parent;
        private final Object proposedUpdate;
        private final Finishing state;

        public /* bridge */ /* synthetic */ Object invoke(Object obj) {
            invoke((Throwable) obj);
            return Unit.INSTANCE;
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public ChildCompletion(@NotNull JobSupport jobSupport, @NotNull Finishing finishing, @NotNull ChildHandleNode childHandleNode, @Nullable Object obj) {
            super(childHandleNode.childJob);
            Intrinsics.checkParameterIsNotNull(jobSupport, "parent");
            Intrinsics.checkParameterIsNotNull(finishing, "state");
            Intrinsics.checkParameterIsNotNull(childHandleNode, "child");
            this.parent = jobSupport;
            this.state = finishing;
            this.child = childHandleNode;
            this.proposedUpdate = obj;
        }

        public void invoke(@Nullable Throwable th) {
            this.parent.continueCompleting(this.state, this.child, this.proposedUpdate);
        }

        @NotNull
        public String toString() {
            return "ChildCompletion[" + this.child + ", " + this.proposedUpdate + ']';
        }
    }

    @Nullable
    public final Object getState$kotlinx_coroutines_core() {
        while (true) {
            Object obj = this._state;
            if (!(obj instanceof OpDescriptor)) {
                return obj;
            }
            ((OpDescriptor) obj).perform(this);
        }
    }
}
