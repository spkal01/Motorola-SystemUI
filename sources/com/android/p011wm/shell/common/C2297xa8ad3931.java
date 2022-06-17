package com.android.p011wm.shell.common;

import android.graphics.Rect;
import java.util.Collection;
import java.util.List;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref$ObjectRef;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.common.FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsAbove$2 */
/* compiled from: FloatingContentCoordinator.kt */
final class C2297xa8ad3931 extends Lambda implements Function0<Rect> {
    final /* synthetic */ Rect $contentRect;
    final /* synthetic */ Rect $newlyOverlappingRect;
    final /* synthetic */ Ref$ObjectRef<List<Rect>> $rectsToAvoidAbove;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2297xa8ad3931(Rect rect, Ref$ObjectRef<List<Rect>> ref$ObjectRef, Rect rect2) {
        super(0);
        this.$contentRect = rect;
        this.$rectsToAvoidAbove = ref$ObjectRef;
        this.$newlyOverlappingRect = rect2;
    }

    @NotNull
    public final Rect invoke() {
        return FloatingContentCoordinator.Companion.findAreaForContentAboveOrBelow(this.$contentRect, CollectionsKt___CollectionsKt.plus((Collection) this.$rectsToAvoidAbove.element, this.$newlyOverlappingRect), true);
    }
}
