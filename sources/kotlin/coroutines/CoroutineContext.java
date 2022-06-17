package kotlin.coroutines;

import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CoroutineContext.kt */
public interface CoroutineContext {

    /* compiled from: CoroutineContext.kt */
    public interface Key<E extends Element> {
    }

    <R> R fold(R r, @NotNull Function2<? super R, ? super Element, ? extends R> function2);

    @Nullable
    <E extends Element> E get(@NotNull Key<E> key);

    @NotNull
    CoroutineContext minusKey(@NotNull Key<?> key);

    @NotNull
    CoroutineContext plus(@NotNull CoroutineContext coroutineContext);

    /* compiled from: CoroutineContext.kt */
    public static final class DefaultImpls {
        @NotNull
        public static CoroutineContext plus(@NotNull CoroutineContext coroutineContext, @NotNull CoroutineContext coroutineContext2) {
            Intrinsics.checkNotNullParameter(coroutineContext2, "context");
            return coroutineContext2 == EmptyCoroutineContext.INSTANCE ? coroutineContext : (CoroutineContext) coroutineContext2.fold(coroutineContext, CoroutineContext$plus$1.INSTANCE);
        }
    }

    /* compiled from: CoroutineContext.kt */
    public interface Element extends CoroutineContext {
        @Nullable
        <E extends Element> E get(@NotNull Key<E> key);

        @NotNull
        Key<?> getKey();

        /* compiled from: CoroutineContext.kt */
        public static final class DefaultImpls {
            @NotNull
            public static CoroutineContext plus(@NotNull Element element, @NotNull CoroutineContext coroutineContext) {
                Intrinsics.checkNotNullParameter(coroutineContext, "context");
                return DefaultImpls.plus(element, coroutineContext);
            }

            @Nullable
            public static <E extends Element> E get(@NotNull Element element, @NotNull Key<E> key) {
                Intrinsics.checkNotNullParameter(key, "key");
                if (Intrinsics.areEqual((Object) element.getKey(), (Object) key)) {
                    return element;
                }
                return null;
            }

            public static <R> R fold(@NotNull Element element, R r, @NotNull Function2<? super R, ? super Element, ? extends R> function2) {
                Intrinsics.checkNotNullParameter(function2, "operation");
                return function2.invoke(r, element);
            }

            @NotNull
            public static CoroutineContext minusKey(@NotNull Element element, @NotNull Key<?> key) {
                Intrinsics.checkNotNullParameter(key, "key");
                return Intrinsics.areEqual((Object) element.getKey(), (Object) key) ? EmptyCoroutineContext.INSTANCE : element;
            }
        }
    }
}
