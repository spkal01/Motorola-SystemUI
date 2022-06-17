package com.android.p011wm.shell.bubbles.storage;

import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.storage.BubbleVolatileRepository$removeBubbles$1$1 */
/* compiled from: BubbleVolatileRepository.kt */
final class BubbleVolatileRepository$removeBubbles$1$1 implements Predicate<BubbleEntity> {

    /* renamed from: $b */
    final /* synthetic */ BubbleEntity f182$b;

    BubbleVolatileRepository$removeBubbles$1$1(BubbleEntity bubbleEntity) {
        this.f182$b = bubbleEntity;
    }

    public final boolean test(@NotNull BubbleEntity bubbleEntity) {
        Intrinsics.checkNotNullParameter(bubbleEntity, "e");
        return Intrinsics.areEqual((Object) this.f182$b.getKey(), (Object) bubbleEntity.getKey());
    }
}
