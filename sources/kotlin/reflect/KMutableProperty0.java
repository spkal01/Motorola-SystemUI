package kotlin.reflect;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

/* compiled from: KProperty.kt */
public interface KMutableProperty0<V> extends KProperty0<V>, KProperty {

    /* compiled from: KProperty.kt */
    public interface Setter<V> extends Function1<V, Unit>, Function1 {
    }

    @NotNull
    Setter<V> getSetter();

    void set(V v);
}
