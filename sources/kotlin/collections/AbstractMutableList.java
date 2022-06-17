package kotlin.collections;

import java.util.AbstractList;
import java.util.List;

/* compiled from: AbstractMutableList.kt */
public abstract class AbstractMutableList<E> extends AbstractList<E> implements List<E> {
    public abstract int getSize();

    public abstract E removeAt(int i);

    protected AbstractMutableList() {
    }

    public final /* bridge */ E remove(int i) {
        return removeAt(i);
    }

    public final /* bridge */ int size() {
        return getSize();
    }
}
