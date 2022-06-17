package com.android.p011wm.shell.common;

import android.graphics.Rect;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.common.FloatingContentCoordinator$Companion$findAreaForContentVertically$positionBelowInBounds$2 */
/* compiled from: FloatingContentCoordinator.kt */
final class C2300xf7317e4 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ Rect $allowedBounds;
    final /* synthetic */ Lazy<Rect> $newContentBoundsBelow$delegate;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2300xf7317e4(Rect rect, Lazy<Rect> lazy) {
        super(0);
        this.$allowedBounds = rect;
        this.$newContentBoundsBelow$delegate = lazy;
    }

    public final boolean invoke() {
        return this.$allowedBounds.contains(FloatingContentCoordinator.Companion.m699findAreaForContentVertically$lambda3(this.$newContentBoundsBelow$delegate));
    }
}
