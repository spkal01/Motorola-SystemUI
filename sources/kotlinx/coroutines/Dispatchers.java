package kotlinx.coroutines;

import kotlinx.coroutines.scheduling.DefaultScheduler;
import org.jetbrains.annotations.NotNull;

/* compiled from: Dispatchers.kt */
public final class Dispatchers {
    @NotNull
    private static final CoroutineDispatcher Default = CoroutineContextKt.createDefaultDispatcher();
    public static final Dispatchers INSTANCE = new Dispatchers();
    @NotNull

    /* renamed from: IO */
    private static final CoroutineDispatcher f196IO = DefaultScheduler.INSTANCE.getIO();
    @NotNull
    private static final CoroutineDispatcher Unconfined = Unconfined.INSTANCE;

    private Dispatchers() {
    }

    @NotNull
    public static final CoroutineDispatcher getDefault() {
        return Default;
    }

    @NotNull
    public static final CoroutineDispatcher getIO() {
        return f196IO;
    }
}
