package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlinx.coroutines.DebugKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: LockFreeTaskQueue.kt */
public final class LockFreeTaskQueueCore<E> {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    public static final Symbol REMOVE_FROZEN = new Symbol("REMOVE_FROZEN");
    private static final AtomicReferenceFieldUpdater _next$FU;
    public static final /* synthetic */ AtomicLongFieldUpdater _state$FU$internal;
    private volatile Object _next = null;
    public volatile /* synthetic */ long _state$internal = 0;
    public /* synthetic */ AtomicReferenceArray array$internal;
    private final int capacity;
    /* access modifiers changed from: private */
    public final int mask;
    /* access modifiers changed from: private */
    public final boolean singleConsumer;

    public LockFreeTaskQueueCore(int i, boolean z) {
        this.capacity = i;
        this.singleConsumer = z;
        int i2 = i - 1;
        this.mask = i2;
        this.array$internal = new AtomicReferenceArray(i);
        boolean z2 = false;
        if (i2 <= 1073741823) {
            if (!((i & i2) == 0 ? true : z2)) {
                throw new IllegalStateException("Check failed.".toString());
            }
            return;
        }
        throw new IllegalStateException("Check failed.".toString());
    }

    public final boolean isEmpty() {
        long j = this._state$internal;
        return ((int) ((1073741823 & j) >> 0)) == ((int) ((j & 1152921503533105152L) >> 30));
    }

    public final int getSize() {
        long j = this._state$internal;
        return 1073741823 & (((int) ((j & 1152921503533105152L) >> 30)) - ((int) ((1073741823 & j) >> 0)));
    }

    private final LockFreeTaskQueueCore<E> fillPlaceholder(int i, E e) {
        Object obj = this.array$internal.get(this.mask & i);
        if (!(obj instanceof Placeholder) || ((Placeholder) obj).index != i) {
            return null;
        }
        this.array$internal.set(i & this.mask, e);
        return this;
    }

    @NotNull
    public final LockFreeTaskQueueCore<E> next() {
        return allocateOrGetNextCopy(markFrozen());
    }

    private final LockFreeTaskQueueCore<E> allocateNextCopy(long j) {
        LockFreeTaskQueueCore<E> lockFreeTaskQueueCore = new LockFreeTaskQueueCore<>(this.capacity * 2, this.singleConsumer);
        int i = (int) ((1073741823 & j) >> 0);
        int i2 = (int) ((1152921503533105152L & j) >> 30);
        while (true) {
            int i3 = this.mask;
            if ((i & i3) != (i2 & i3)) {
                Object obj = this.array$internal.get(i3 & i);
                if (obj == null) {
                    obj = new Placeholder(i);
                }
                lockFreeTaskQueueCore.array$internal.set(lockFreeTaskQueueCore.mask & i, obj);
                i++;
            } else {
                lockFreeTaskQueueCore._state$internal = Companion.mo31795wo(j, 1152921504606846976L);
                return lockFreeTaskQueueCore;
            }
        }
    }

    /* compiled from: LockFreeTaskQueue.kt */
    public static final class Placeholder {
        public final int index;

        public Placeholder(int i) {
            this.index = i;
        }
    }

    /* compiled from: LockFreeTaskQueue.kt */
    public static final class Companion {
        public final int addFailReason(long j) {
            return (j & 2305843009213693952L) != 0 ? 2 : 1;
        }

        /* renamed from: wo */
        public final long mo31795wo(long j, long j2) {
            return j & (~j2);
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final long updateHead(long j, int i) {
            return mo31795wo(j, 1073741823) | (((long) i) << 0);
        }

        public final long updateTail(long j, int i) {
            return mo31795wo(j, 1152921503533105152L) | (((long) i) << 30);
        }
    }

    static {
        Class<LockFreeTaskQueueCore> cls = LockFreeTaskQueueCore.class;
        _next$FU = AtomicReferenceFieldUpdater.newUpdater(cls, Object.class, "_next");
        _state$FU$internal = AtomicLongFieldUpdater.newUpdater(cls, "_state$internal");
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x006c A[LOOP:1: B:20:0x006c->B:23:0x007e, LOOP_START, PHI: r13 
      PHI: (r13v3 'this' kotlinx.coroutines.internal.LockFreeTaskQueueCore) = (r13v0 'this' kotlinx.coroutines.internal.LockFreeTaskQueueCore A[THIS]), (r13v5 'this' kotlinx.coroutines.internal.LockFreeTaskQueueCore) binds: [B:19:0x0065, B:23:0x007e] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int addLast(@org.jetbrains.annotations.NotNull E r14) {
        /*
            r13 = this;
            java.lang.String r0 = "element"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r14, r0)
        L_0x0005:
            long r3 = r13._state$internal
            r0 = 3458764513820540928(0x3000000000000000, double:1.727233711018889E-77)
            long r0 = r0 & r3
            r7 = 0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x0017
            kotlinx.coroutines.internal.LockFreeTaskQueueCore$Companion r13 = Companion
            int r13 = r13.addFailReason(r3)
            return r13
        L_0x0017:
            kotlinx.coroutines.internal.LockFreeTaskQueueCore$Companion r0 = Companion
            r1 = 1073741823(0x3fffffff, double:5.304989472E-315)
            long r1 = r1 & r3
            r9 = 0
            long r1 = r1 >> r9
            int r1 = (int) r1
            r5 = 1152921503533105152(0xfffffffc0000000, double:1.2882296003504729E-231)
            long r5 = r5 & r3
            r2 = 30
            long r5 = r5 >> r2
            int r10 = (int) r5
            int r11 = r13.mask
            int r2 = r10 + 2
            r2 = r2 & r11
            r5 = r1 & r11
            r6 = 1
            if (r2 != r5) goto L_0x0035
            return r6
        L_0x0035:
            boolean r2 = r13.singleConsumer
            r5 = 1073741823(0x3fffffff, float:1.9999999)
            if (r2 != 0) goto L_0x0054
            java.util.concurrent.atomic.AtomicReferenceArray r2 = r13.array$internal
            r12 = r10 & r11
            java.lang.Object r2 = r2.get(r12)
            if (r2 == 0) goto L_0x0054
            int r0 = r13.capacity
            r2 = 1024(0x400, float:1.435E-42)
            if (r0 < r2) goto L_0x0053
            int r10 = r10 - r1
            r1 = r10 & r5
            int r0 = r0 >> 1
            if (r1 <= r0) goto L_0x0005
        L_0x0053:
            return r6
        L_0x0054:
            int r1 = r10 + 1
            r1 = r1 & r5
            java.util.concurrent.atomic.AtomicLongFieldUpdater r2 = _state$FU$internal
            long r5 = r0.updateTail(r3, r1)
            r1 = r2
            r2 = r13
            boolean r0 = r1.compareAndSet(r2, r3, r5)
            if (r0 == 0) goto L_0x0005
            java.util.concurrent.atomic.AtomicReferenceArray r0 = r13.array$internal
            r1 = r10 & r11
            r0.set(r1, r14)
        L_0x006c:
            long r0 = r13._state$internal
            r2 = 1152921504606846976(0x1000000000000000, double:1.2882297539194267E-231)
            long r0 = r0 & r2
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0076
            goto L_0x0081
        L_0x0076:
            kotlinx.coroutines.internal.LockFreeTaskQueueCore r13 = r13.next()
            kotlinx.coroutines.internal.LockFreeTaskQueueCore r13 = r13.fillPlaceholder(r10, r14)
            if (r13 == 0) goto L_0x0081
            goto L_0x006c
        L_0x0081:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.LockFreeTaskQueueCore.addLast(java.lang.Object):int");
    }

    @Nullable
    public final Object removeFirstOrNull() {
        Object obj;
        while (true) {
            long j = this._state$internal;
            if ((1152921504606846976L & j) == 0) {
                Companion companion = Companion;
                int i = (int) ((1073741823 & j) >> 0);
                if ((((int) ((1152921503533105152L & j) >> 30)) & this.mask) != (this.mask & i)) {
                    obj = this.array$internal.get(this.mask & i);
                    if (obj != null) {
                        if (!(obj instanceof Placeholder)) {
                            int i2 = (i + 1) & 1073741823;
                            if (!_state$FU$internal.compareAndSet(this, j, companion.updateHead(j, i2))) {
                                if (this.singleConsumer) {
                                    do {
                                        this = this.removeSlowPath(i, i2);
                                    } while (this != null);
                                    break;
                                }
                            } else {
                                this.array$internal.set(this.mask & i, (Object) null);
                                break;
                            }
                        } else {
                            return null;
                        }
                    } else if (this.singleConsumer) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return REMOVE_FROZEN;
            }
        }
        return obj;
    }

    /* access modifiers changed from: private */
    public final LockFreeTaskQueueCore<E> removeSlowPath(int i, int i2) {
        long j;
        Companion companion;
        int i3;
        do {
            j = this._state$internal;
            companion = Companion;
            boolean z = false;
            i3 = (int) ((1073741823 & j) >> 0);
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (i3 == i) {
                    z = true;
                }
                if (!z) {
                    throw new AssertionError();
                }
            }
            if ((1152921504606846976L & j) != 0) {
                return next();
            }
        } while (!_state$FU$internal.compareAndSet(this, j, companion.updateHead(j, i2)));
        this.array$internal.set(this.mask & i3, (Object) null);
        return null;
    }

    private final LockFreeTaskQueueCore<E> allocateOrGetNextCopy(long j) {
        while (true) {
            LockFreeTaskQueueCore<E> lockFreeTaskQueueCore = (LockFreeTaskQueueCore) this._next;
            if (lockFreeTaskQueueCore != null) {
                return lockFreeTaskQueueCore;
            }
            _next$FU.compareAndSet(this, (Object) null, allocateNextCopy(j));
        }
    }

    public final boolean close() {
        long j;
        do {
            j = this._state$internal;
            if ((j & 2305843009213693952L) != 0) {
                return true;
            }
            if ((1152921504606846976L & j) != 0) {
                return false;
            }
        } while (!_state$FU$internal.compareAndSet(this, j, j | 2305843009213693952L));
        return true;
    }

    private final long markFrozen() {
        long j;
        long j2;
        do {
            j = this._state$internal;
            if ((j & 1152921504606846976L) != 0) {
                return j;
            }
            j2 = j | 1152921504606846976L;
        } while (!_state$FU$internal.compareAndSet(this, j, j2));
        return j2;
    }
}
