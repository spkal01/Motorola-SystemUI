package kotlin.comparisons;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: _ComparisonsJvm.kt */
class ComparisonsKt___ComparisonsJvmKt extends ComparisonsKt__ComparisonsKt {
    public static float maxOf(float f, @NotNull float... fArr) {
        Intrinsics.checkNotNullParameter(fArr, "other");
        for (float max : fArr) {
            f = Math.max(f, max);
        }
        return f;
    }
}
