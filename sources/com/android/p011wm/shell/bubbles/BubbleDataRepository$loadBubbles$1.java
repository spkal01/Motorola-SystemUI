package com.android.p011wm.shell.bubbles;

import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.p011wm.shell.bubbles.storage.BubbleEntity;
import com.android.p011wm.shell.common.ShellExecutor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(mo31517c = "com.android.wm.shell.bubbles.BubbleDataRepository$loadBubbles$1", mo31518f = "BubbleDataRepository.kt", mo31519l = {}, mo31520m = "invokeSuspend")
/* renamed from: com.android.wm.shell.bubbles.BubbleDataRepository$loadBubbles$1 */
/* compiled from: BubbleDataRepository.kt */
final class BubbleDataRepository$loadBubbles$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function1<List<? extends Bubble>, Unit> $cb;
    final /* synthetic */ int $userId;
    int label;

    /* renamed from: p$ */
    private /* synthetic */ CoroutineScope f178p$;
    final /* synthetic */ BubbleDataRepository this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    BubbleDataRepository$loadBubbles$1(BubbleDataRepository bubbleDataRepository, int i, Function1<? super List<? extends Bubble>, Unit> function1, Continuation<? super BubbleDataRepository$loadBubbles$1> continuation) {
        super(2, continuation);
        this.this$0 = bubbleDataRepository;
        this.$userId = i;
        this.$cb = function1;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object obj, @NotNull Continuation<?> continuation) {
        BubbleDataRepository$loadBubbles$1 bubbleDataRepository$loadBubbles$1 = new BubbleDataRepository$loadBubbles$1(this.this$0, this.$userId, this.$cb, continuation);
        bubbleDataRepository$loadBubbles$1.f178p$ = (CoroutineScope) obj;
        return bubbleDataRepository$loadBubbles$1;
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope coroutineScope, @Nullable Continuation<? super Unit> continuation) {
        return ((BubbleDataRepository$loadBubbles$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object obj) {
        Object obj2;
        Object unused = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (this.label == 0) {
            ResultKt.throwOnFailure(obj);
            List<BubbleEntity> list = this.this$0.persistentRepository.readFromDisk().get(this.$userId);
            if (list == null) {
                return Unit.INSTANCE;
            }
            this.this$0.volatileRepository.addBubbles(this.$userId, list);
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
            for (BubbleEntity bubbleEntity : list) {
                arrayList.add(new ShortcutKey(bubbleEntity.getUserId(), bubbleEntity.getPackageName()));
            }
            Set<ShortcutKey> set = CollectionsKt___CollectionsKt.toSet(arrayList);
            BubbleDataRepository bubbleDataRepository = this.this$0;
            ArrayList arrayList2 = new ArrayList();
            for (ShortcutKey shortcutKey : set) {
                List<ShortcutInfo> shortcuts = bubbleDataRepository.launcherApps.getShortcuts(new LauncherApps.ShortcutQuery().setPackage(shortcutKey.getPkg()).setQueryFlags(1041), UserHandle.of(shortcutKey.getUserId()));
                if (shortcuts == null) {
                    shortcuts = CollectionsKt__CollectionsKt.emptyList();
                }
                boolean unused2 = CollectionsKt__MutableCollectionsKt.addAll(arrayList2, shortcuts);
            }
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Object next : arrayList2) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) next;
                int userId = shortcutInfo.getUserId();
                String str = shortcutInfo.getPackage();
                Intrinsics.checkNotNullExpressionValue(str, "it.`package`");
                ShortcutKey shortcutKey2 = new ShortcutKey(userId, str);
                Object obj3 = linkedHashMap.get(shortcutKey2);
                if (obj3 == null) {
                    obj3 = new ArrayList();
                    linkedHashMap.put(shortcutKey2, obj3);
                }
                ((List) obj3).add(next);
            }
            BubbleDataRepository bubbleDataRepository2 = this.this$0;
            final ArrayList arrayList3 = new ArrayList();
            for (BubbleEntity bubbleEntity2 : list) {
                List list2 = (List) linkedHashMap.get(new ShortcutKey(bubbleEntity2.getUserId(), bubbleEntity2.getPackageName()));
                Bubble bubble = null;
                if (list2 != null) {
                    Iterator it = list2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            obj2 = null;
                            break;
                        }
                        obj2 = it.next();
                        if (Boxing.boxBoolean(Intrinsics.areEqual((Object) bubbleEntity2.getShortcutId(), (Object) ((ShortcutInfo) obj2).getId())).booleanValue()) {
                            break;
                        }
                    }
                    ShortcutInfo shortcutInfo2 = (ShortcutInfo) obj2;
                    if (shortcutInfo2 != null) {
                        bubble = new Bubble(bubbleEntity2.getKey(), shortcutInfo2, bubbleEntity2.getDesiredHeight(), bubbleEntity2.getDesiredHeightResId(), bubbleEntity2.getTitle(), bubbleEntity2.getTaskId(), bubbleEntity2.getLocus(), bubbleDataRepository2.mainExecutor);
                    }
                }
                if (bubble != null) {
                    arrayList3.add(bubble);
                }
            }
            ShellExecutor access$getMainExecutor$p = this.this$0.mainExecutor;
            final Function1<List<? extends Bubble>, Unit> function1 = this.$cb;
            access$getMainExecutor$p.execute(new Runnable() {
                public final void run() {
                    function1.invoke(arrayList3);
                }
            });
            return Unit.INSTANCE;
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }
}
