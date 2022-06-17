package kotlin.coroutines.intrinsics;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: IntrinsicsJvm.kt */
class IntrinsicsKt__IntrinsicsJvmKt {
    /* JADX WARNING: type inference failed for: r10v5, types: [kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3] */
    /* JADX WARNING: Multi-variable type inference failed */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <R, T> kotlin.coroutines.Continuation<kotlin.Unit> createCoroutineUnintercepted(@org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function2<? super R, ? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> r8, R r9, @org.jetbrains.annotations.NotNull kotlin.coroutines.Continuation<? super T> r10) {
        /*
            java.lang.String r0 = "$this$createCoroutineUnintercepted"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "completion"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)
            kotlin.coroutines.Continuation r4 = kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineCreated(r10)
            boolean r10 = r8 instanceof kotlin.coroutines.jvm.internal.BaseContinuationImpl
            if (r10 == 0) goto L_0x0019
            kotlin.coroutines.jvm.internal.BaseContinuationImpl r8 = (kotlin.coroutines.jvm.internal.BaseContinuationImpl) r8
            kotlin.coroutines.Continuation r8 = r8.create(r9, r4)
            goto L_0x0032
        L_0x0019:
            kotlin.coroutines.CoroutineContext r5 = r4.getContext()
            kotlin.coroutines.EmptyCoroutineContext r10 = kotlin.coroutines.EmptyCoroutineContext.INSTANCE
            if (r5 != r10) goto L_0x0027
            kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3 r10 = new kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3
            r10.<init>(r4, r4, r8, r9)
            goto L_0x0031
        L_0x0027:
            kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4 r10 = new kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4
            r1 = r10
            r2 = r4
            r3 = r5
            r6 = r8
            r7 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7)
        L_0x0031:
            r8 = r10
        L_0x0032:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(kotlin.jvm.functions.Function2, java.lang.Object, kotlin.coroutines.Continuation):kotlin.coroutines.Continuation");
    }

    @NotNull
    public static <T> Continuation<T> intercepted(@NotNull Continuation<? super T> continuation) {
        Continuation<Object> intercepted;
        Intrinsics.checkNotNullParameter(continuation, "$this$intercepted");
        ContinuationImpl continuationImpl = (ContinuationImpl) (!(continuation instanceof ContinuationImpl) ? null : continuation);
        return (continuationImpl == null || (intercepted = continuationImpl.intercepted()) == null) ? continuation : intercepted;
    }
}
