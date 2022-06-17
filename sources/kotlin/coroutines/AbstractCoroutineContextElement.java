package kotlin.coroutines;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CoroutineContextImpl.kt */
public abstract class AbstractCoroutineContextElement implements CoroutineContext.Element {
    @NotNull
    private final CoroutineContext.Key<?> key;

    public AbstractCoroutineContextElement(@NotNull CoroutineContext.Key<?> key2) {
        Intrinsics.checkNotNullParameter(key2, "key");
        this.key = key2;
    }

    public <R> R fold(R r, @NotNull Function2<? super R, ? super CoroutineContext.Element, ? extends R> function2) {
        Intrinsics.checkNotNullParameter(function2, "operation");
        return CoroutineContext.Element.DefaultImpls.fold(this, r, function2);
    }

    @Nullable
    public <E extends CoroutineContext.Element> E get(@NotNull CoroutineContext.Key<E> key2) {
        Intrinsics.checkNotNullParameter(key2, "key");
        return CoroutineContext.Element.DefaultImpls.get(this, key2);
    }

    @NotNull
    public CoroutineContext.Key<?> getKey() {
        return this.key;
    }

    @NotNull
    public CoroutineContext minusKey(@NotNull CoroutineContext.Key<?> key2) {
        Intrinsics.checkNotNullParameter(key2, "key");
        return CoroutineContext.Element.DefaultImpls.minusKey(this, key2);
    }

    @NotNull
    public CoroutineContext plus(@NotNull CoroutineContext coroutineContext) {
        Intrinsics.checkNotNullParameter(coroutineContext, "context");
        return CoroutineContext.Element.DefaultImpls.plus(this, coroutineContext);
    }
}
