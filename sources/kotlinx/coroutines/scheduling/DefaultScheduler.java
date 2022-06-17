package kotlinx.coroutines.scheduling;

import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.internal.SystemPropsKt;
import org.jetbrains.annotations.NotNull;

/* compiled from: Dispatcher.kt */
public final class DefaultScheduler extends ExperimentalCoroutineDispatcher {
    public static final DefaultScheduler INSTANCE;
    @NotNull

    /* renamed from: IO */
    private static final CoroutineDispatcher f200IO;

    @NotNull
    public String toString() {
        return "DefaultDispatcher";
    }

    static {
        DefaultScheduler defaultScheduler = new DefaultScheduler();
        INSTANCE = defaultScheduler;
        f200IO = defaultScheduler.blocking(SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.io.parallelism", RangesKt___RangesKt.coerceAtLeast(64, SystemPropsKt.getAVAILABLE_PROCESSORS()), 0, 0, 12, (Object) null));
    }

    private DefaultScheduler() {
        super(0, 0, (String) null, 7, (DefaultConstructorMarker) null);
    }

    @NotNull
    public final CoroutineDispatcher getIO() {
        return f200IO;
    }

    public void close() {
        throw new UnsupportedOperationException("DefaultDispatcher cannot be closed");
    }
}
