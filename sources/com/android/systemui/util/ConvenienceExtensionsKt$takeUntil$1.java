package com.android.systemui.util;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(mo31517c = "com.android.systemui.util.ConvenienceExtensionsKt$takeUntil$1", mo31518f = "ConvenienceExtensions.kt", mo31519l = {32}, mo31520m = "invokeSuspend")
/* compiled from: ConvenienceExtensions.kt */
final class ConvenienceExtensionsKt$takeUntil$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super T>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function1<T, Boolean> $pred;
    final /* synthetic */ Sequence<T> $this_takeUntil;
    Object L$0;
    Object L$1;
    int label;

    /* renamed from: p$ */
    private /* synthetic */ SequenceScope<T> f139p$;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ConvenienceExtensionsKt$takeUntil$1(Sequence<? extends T> sequence, Function1<? super T, Boolean> function1, Continuation<? super ConvenienceExtensionsKt$takeUntil$1> continuation) {
        super(2, continuation);
        this.$this_takeUntil = sequence;
        this.$pred = function1;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object obj, @NotNull Continuation<?> continuation) {
        ConvenienceExtensionsKt$takeUntil$1 convenienceExtensionsKt$takeUntil$1 = new ConvenienceExtensionsKt$takeUntil$1(this.$this_takeUntil, this.$pred, continuation);
        convenienceExtensionsKt$takeUntil$1.f139p$ = (SequenceScope) obj;
        return convenienceExtensionsKt$takeUntil$1;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceScope<? super T> sequenceScope, @Nullable Continuation<? super Unit> continuation) {
        return ((ConvenienceExtensionsKt$takeUntil$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004c, code lost:
        if (r4.$pred.invoke(r1).booleanValue() == false) goto L_0x0027;
     */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r5) {
        /*
            r4 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r4.label
            r2 = 1
            if (r1 == 0) goto L_0x001d
            if (r1 != r2) goto L_0x0015
            java.lang.Object r1 = r4.L$1
            java.lang.Object r3 = r4.L$0
            java.util.Iterator r3 = (java.util.Iterator) r3
            kotlin.ResultKt.throwOnFailure(r5)
            goto L_0x0040
        L_0x0015:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException
            java.lang.String r5 = "call to 'resume' before 'invoke' with coroutine"
            r4.<init>(r5)
            throw r4
        L_0x001d:
            kotlin.ResultKt.throwOnFailure(r5)
            kotlin.sequences.Sequence<T> r5 = r4.$this_takeUntil
            java.util.Iterator r5 = r5.iterator()
            r3 = r5
        L_0x0027:
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L_0x004e
            java.lang.Object r1 = r3.next()
            kotlin.sequences.SequenceScope<T> r5 = r4.f139p$
            r4.L$0 = r3
            r4.L$1 = r1
            r4.label = r2
            java.lang.Object r5 = r5.yield(r1, r4)
            if (r5 != r0) goto L_0x0040
            return r0
        L_0x0040:
            kotlin.jvm.functions.Function1<T, java.lang.Boolean> r5 = r4.$pred
            java.lang.Object r5 = r5.invoke(r1)
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            if (r5 == 0) goto L_0x0027
        L_0x004e:
            kotlin.Unit r4 = kotlin.Unit.INSTANCE
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.util.ConvenienceExtensionsKt$takeUntil$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
