package kotlin.coroutines;

import java.util.Objects;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ContinuationInterceptor.kt */
public interface ContinuationInterceptor extends CoroutineContext.Element {
    @NotNull
    public static final Key Key = Key.$$INSTANCE;

    @NotNull
    <T> Continuation<T> interceptContinuation(@NotNull Continuation<? super T> continuation);

    void releaseInterceptedContinuation(@NotNull Continuation<?> continuation);

    /* compiled from: ContinuationInterceptor.kt */
    public static final class Key implements CoroutineContext.Key<ContinuationInterceptor> {
        static final /* synthetic */ Key $$INSTANCE = new Key();

        private Key() {
        }
    }

    /* compiled from: ContinuationInterceptor.kt */
    public static final class DefaultImpls {
        public static void releaseInterceptedContinuation(@NotNull ContinuationInterceptor continuationInterceptor, @NotNull Continuation<?> continuation) {
            Intrinsics.checkNotNullParameter(continuation, "continuation");
        }

        @Nullable
        public static <E extends CoroutineContext.Element> E get(@NotNull ContinuationInterceptor continuationInterceptor, @NotNull CoroutineContext.Key<E> key) {
            Intrinsics.checkNotNullParameter(key, "key");
            if (key instanceof AbstractCoroutineContextKey) {
                AbstractCoroutineContextKey abstractCoroutineContextKey = (AbstractCoroutineContextKey) key;
                if (!abstractCoroutineContextKey.isSubKey$kotlin_stdlib(continuationInterceptor.getKey())) {
                    return null;
                }
                E tryCast$kotlin_stdlib = abstractCoroutineContextKey.tryCast$kotlin_stdlib(continuationInterceptor);
                if (!(tryCast$kotlin_stdlib instanceof CoroutineContext.Element)) {
                    return null;
                }
                return tryCast$kotlin_stdlib;
            } else if (ContinuationInterceptor.Key != key) {
                return null;
            } else {
                Objects.requireNonNull(continuationInterceptor, "null cannot be cast to non-null type E");
                return continuationInterceptor;
            }
        }

        @NotNull
        public static CoroutineContext minusKey(@NotNull ContinuationInterceptor continuationInterceptor, @NotNull CoroutineContext.Key<?> key) {
            Intrinsics.checkNotNullParameter(key, "key");
            if (!(key instanceof AbstractCoroutineContextKey)) {
                return ContinuationInterceptor.Key == key ? EmptyCoroutineContext.INSTANCE : continuationInterceptor;
            }
            AbstractCoroutineContextKey abstractCoroutineContextKey = (AbstractCoroutineContextKey) key;
            return (!abstractCoroutineContextKey.isSubKey$kotlin_stdlib(continuationInterceptor.getKey()) || abstractCoroutineContextKey.tryCast$kotlin_stdlib(continuationInterceptor) == null) ? continuationInterceptor : EmptyCoroutineContext.INSTANCE;
        }
    }
}
