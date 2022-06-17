package kotlinx.coroutines.internal;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ExceptionsConstuctor.kt */
final class ExceptionsConstuctorKt$tryCopyException$5$1 extends Lambda implements Function1 {
    public static final ExceptionsConstuctorKt$tryCopyException$5$1 INSTANCE = new ExceptionsConstuctorKt$tryCopyException$5$1();

    ExceptionsConstuctorKt$tryCopyException$5$1() {
        super(1);
    }

    @Nullable
    public final Void invoke(@NotNull Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "it");
        return null;
    }
}
