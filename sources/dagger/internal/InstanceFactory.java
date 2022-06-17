package dagger.internal;

import dagger.Lazy;

public final class InstanceFactory<T> implements Factory<T>, Lazy<T> {
    private static final InstanceFactory<Object> NULL_INSTANCE_FACTORY = new InstanceFactory<>((Object) null);
    private final T instance;

    public static <T> Factory<T> create(T t) {
        return new InstanceFactory(Preconditions.checkNotNull(t, "instance cannot be null"));
    }

    private InstanceFactory(T t) {
        this.instance = t;
    }

    public T get() {
        return this.instance;
    }
}
