package kotlinx.coroutines.internal;

import java.lang.Comparable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.internal.ThreadSafeHeapNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ThreadSafeHeap.common.kt */
public class ThreadSafeHeap<T extends ThreadSafeHeapNode & Comparable<? super T>> {
    private static final AtomicIntegerFieldUpdater _size$FU = AtomicIntegerFieldUpdater.newUpdater(ThreadSafeHeap.class, "_size");
    private volatile int _size = 0;

    /* renamed from: a */
    private T[] f197a;

    @Nullable
    public final T peek() {
        T firstImpl;
        synchronized (this) {
            firstImpl = firstImpl();
        }
        return firstImpl;
    }

    public final boolean remove(@NotNull T t) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(t, "node");
        synchronized (this) {
            z = true;
            boolean z2 = false;
            if (t.getHeap() == null) {
                z = false;
            } else {
                int index = t.getIndex();
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (index >= 0) {
                        z2 = true;
                    }
                    if (!z2) {
                        throw new AssertionError();
                    }
                }
                removeAtImpl(index);
            }
        }
        return z;
    }

    @Nullable
    public final T removeFirstOrNull() {
        T removeAtImpl;
        synchronized (this) {
            removeAtImpl = getSize() > 0 ? removeAtImpl(0) : null;
        }
        return removeAtImpl;
    }

    public final int getSize() {
        return this._size;
    }

    private final void setSize(int i) {
        this._size = i;
    }

    public final boolean isEmpty() {
        return getSize() == 0;
    }

    @Nullable
    public final T firstImpl() {
        T[] tArr = this.f197a;
        if (tArr != null) {
            return tArr[0];
        }
        return null;
    }

    @NotNull
    public final T removeAtImpl(int i) {
        boolean z = false;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(getSize() > 0)) {
                throw new AssertionError();
            }
        }
        T[] tArr = this.f197a;
        if (tArr == null) {
            Intrinsics.throwNpe();
        }
        setSize(getSize() - 1);
        if (i < getSize()) {
            swap(i, getSize());
            int i2 = (i - 1) / 2;
            if (i > 0) {
                T t = tArr[i];
                if (t == null) {
                    Intrinsics.throwNpe();
                }
                Comparable comparable = (Comparable) t;
                T t2 = tArr[i2];
                if (t2 == null) {
                    Intrinsics.throwNpe();
                }
                if (comparable.compareTo(t2) < 0) {
                    swap(i, i2);
                    siftUpFrom(i2);
                }
            }
            siftDownFrom(i);
        }
        T t3 = tArr[getSize()];
        if (t3 == null) {
            Intrinsics.throwNpe();
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (t3.getHeap() == this) {
                z = true;
            }
            if (!z) {
                throw new AssertionError();
            }
        }
        t3.setHeap((ThreadSafeHeap<?>) null);
        t3.setIndex(-1);
        tArr[getSize()] = null;
        return t3;
    }

    public final void addImpl(@NotNull T t) {
        Intrinsics.checkParameterIsNotNull(t, "node");
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(t.getHeap() == null)) {
                throw new AssertionError();
            }
        }
        t.setHeap(this);
        ThreadSafeHeapNode[] realloc = realloc();
        int size = getSize();
        setSize(size + 1);
        realloc[size] = t;
        t.setIndex(size);
        siftUpFrom(size);
    }

    private final void siftUpFrom(int i) {
        while (i > 0) {
            T[] tArr = this.f197a;
            if (tArr == null) {
                Intrinsics.throwNpe();
            }
            int i2 = (i - 1) / 2;
            T t = tArr[i2];
            if (t == null) {
                Intrinsics.throwNpe();
            }
            Comparable comparable = (Comparable) t;
            T t2 = tArr[i];
            if (t2 == null) {
                Intrinsics.throwNpe();
            }
            if (comparable.compareTo(t2) > 0) {
                swap(i, i2);
                i = i2;
            } else {
                return;
            }
        }
    }

    private final void siftDownFrom(int i) {
        while (true) {
            int i2 = (i * 2) + 1;
            if (i2 < getSize()) {
                T[] tArr = this.f197a;
                if (tArr == null) {
                    Intrinsics.throwNpe();
                }
                int i3 = i2 + 1;
                if (i3 < getSize()) {
                    T t = tArr[i3];
                    if (t == null) {
                        Intrinsics.throwNpe();
                    }
                    Comparable comparable = (Comparable) t;
                    T t2 = tArr[i2];
                    if (t2 == null) {
                        Intrinsics.throwNpe();
                    }
                    if (comparable.compareTo(t2) < 0) {
                        i2 = i3;
                    }
                }
                T t3 = tArr[i];
                if (t3 == null) {
                    Intrinsics.throwNpe();
                }
                Comparable comparable2 = (Comparable) t3;
                T t4 = tArr[i2];
                if (t4 == null) {
                    Intrinsics.throwNpe();
                }
                if (comparable2.compareTo(t4) > 0) {
                    swap(i, i2);
                    i = i2;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private final T[] realloc() {
        T[] tArr = this.f197a;
        if (tArr == null) {
            T[] tArr2 = new ThreadSafeHeapNode[4];
            this.f197a = tArr2;
            return tArr2;
        } else if (getSize() < tArr.length) {
            return tArr;
        } else {
            T[] copyOf = Arrays.copyOf(tArr, getSize() * 2);
            Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
            T[] tArr3 = (ThreadSafeHeapNode[]) copyOf;
            this.f197a = tArr3;
            return tArr3;
        }
    }

    private final void swap(int i, int i2) {
        T[] tArr = this.f197a;
        if (tArr == null) {
            Intrinsics.throwNpe();
        }
        T t = tArr[i2];
        if (t == null) {
            Intrinsics.throwNpe();
        }
        T t2 = tArr[i];
        if (t2 == null) {
            Intrinsics.throwNpe();
        }
        tArr[i] = t;
        tArr[i2] = t2;
        t.setIndex(i);
        t2.setIndex(i2);
    }
}
