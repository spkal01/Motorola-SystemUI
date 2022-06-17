package com.android.p011wm.shell.bubbles;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.JobKt;
import kotlinx.coroutines.YieldKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(mo31517c = "com.android.wm.shell.bubbles.BubbleDataRepository$persistToDisk$1", mo31518f = "BubbleDataRepository.kt", mo31519l = {110, 112}, mo31520m = "invokeSuspend")
/* renamed from: com.android.wm.shell.bubbles.BubbleDataRepository$persistToDisk$1 */
/* compiled from: BubbleDataRepository.kt */
final class BubbleDataRepository$persistToDisk$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Job $prev;
    int label;

    /* renamed from: p$ */
    private /* synthetic */ CoroutineScope f179p$;
    final /* synthetic */ BubbleDataRepository this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    BubbleDataRepository$persistToDisk$1(Job job, BubbleDataRepository bubbleDataRepository, Continuation<? super BubbleDataRepository$persistToDisk$1> continuation) {
        super(2, continuation);
        this.$prev = job;
        this.this$0 = bubbleDataRepository;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object obj, @NotNull Continuation<?> continuation) {
        BubbleDataRepository$persistToDisk$1 bubbleDataRepository$persistToDisk$1 = new BubbleDataRepository$persistToDisk$1(this.$prev, this.this$0, continuation);
        bubbleDataRepository$persistToDisk$1.f179p$ = (CoroutineScope) obj;
        return bubbleDataRepository$persistToDisk$1;
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope coroutineScope, @Nullable Continuation<? super Unit> continuation) {
        return ((BubbleDataRepository$persistToDisk$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object obj) {
        Object coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            Job job = this.$prev;
            if (job != null) {
                this.label = 1;
                if (JobKt.cancelAndJoin(job, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            }
        } else if (i == 1) {
            ResultKt.throwOnFailure(obj);
        } else if (i == 2) {
            ResultKt.throwOnFailure(obj);
            this.this$0.persistentRepository.persistsToDisk(this.this$0.volatileRepository.getBubbles());
            return Unit.INSTANCE;
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        this.label = 2;
        if (YieldKt.yield(this) == coroutine_suspended) {
            return coroutine_suspended;
        }
        this.this$0.persistentRepository.persistsToDisk(this.this$0.volatileRepository.getBubbles());
        return Unit.INSTANCE;
    }
}
