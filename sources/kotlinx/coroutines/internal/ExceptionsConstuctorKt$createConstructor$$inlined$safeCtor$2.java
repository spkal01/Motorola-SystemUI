package kotlinx.coroutines.internal;

import java.lang.reflect.Constructor;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ExceptionsConstuctor.kt */
public final class ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$2 extends Lambda implements Function1<Throwable, Throwable> {
    final /* synthetic */ Constructor $constructor$inlined;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$2(Constructor constructor) {
        super(1);
        this.$constructor$inlined = constructor;
    }

    @Nullable
    public final Throwable invoke(@NotNull Throwable th) {
        Object obj;
        Intrinsics.checkParameterIsNotNull(th, "e");
        try {
            Result.Companion companion = Result.Companion;
            Object newInstance = this.$constructor$inlined.newInstance(new Object[]{th});
            if (newInstance != null) {
                obj = Result.m849constructorimpl((Throwable) newInstance);
                if (Result.m853isFailureimpl(obj)) {
                    obj = null;
                }
                return (Throwable) obj;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Throwable");
        } catch (Throwable th2) {
            Result.Companion companion2 = Result.Companion;
            obj = Result.m849constructorimpl(ResultKt.createFailure(th2));
        }
    }
}
