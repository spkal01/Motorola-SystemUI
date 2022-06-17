package com.android.systemui.statusbar;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$goToLockedShade$1 extends Lambda implements Function1<Long, Unit> {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    LockscreenShadeTransitionController$goToLockedShade$1(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        super(1);
        this.this$0 = lockscreenShadeTransitionController;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke(((Number) obj).longValue());
        return Unit.INSTANCE;
    }

    public final void invoke(long j) {
        this.this$0.getNotificationPanelController().animateToFullShade(j);
    }
}
