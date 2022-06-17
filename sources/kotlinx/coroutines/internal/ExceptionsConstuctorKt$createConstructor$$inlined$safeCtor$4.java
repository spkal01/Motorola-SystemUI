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
public final class ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$4 extends Lambda implements Function1<Throwable, Throwable> {
    final /* synthetic */ Constructor $constructor$inlined;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$4(Constructor constructor) {
        super(1);
        this.$constructor$inlined = constructor;
    }

    @Nullable
    public final Throwable invoke(@NotNull Throwable th) {
        Object obj;
        Intrinsics.checkParameterIsNotNull(th, "e");
        try {
            Result.Companion companion = Result.Companion;
            Object newInstance = this.$constructor$inlined.newInstance(new Object[0]);
            if (newInstance != null) {
                Throwable th2 = (Throwable) newInstance;
                th2.initCause(th);
                obj = Result.m849constructorimpl(th2);
                if (Result.m853isFailureimpl(obj)) {
                    obj = null;
                }
                return (Throwable) obj;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Throwable");
        } catch (Throwable th3) {
            Result.Companion companion2 = Result.Companion;
            obj = Result.m849constructorimpl(ResultKt.createFailure(th3));
        }
    }
}
