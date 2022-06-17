package com.android.p011wm.shell.bubbles.storage;

import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.storage.BubbleVolatileRepository$addBubbles$uniqueBubbles$1$1 */
/* compiled from: BubbleVolatileRepository.kt */
final class BubbleVolatileRepository$addBubbles$uniqueBubbles$1$1 implements Predicate<BubbleEntity> {

    /* renamed from: $b */
    final /* synthetic */ BubbleEntity f181$b;

    BubbleVolatileRepository$addBubbles$uniqueBubbles$1$1(BubbleEntity bubbleEntity) {
        this.f181$b = bubbleEntity;
    }

    public final boolean test(@NotNull BubbleEntity bubbleEntity) {
        Intrinsics.checkNotNullParameter(bubbleEntity, "e");
        return Intrinsics.areEqual((Object) this.f181$b.getKey(), (Object) bubbleEntity.getKey());
    }
}
