package com.android.systemui.statusbar;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$animateAppear$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    LockscreenShadeTransitionController$animateAppear$1(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        super(0);
        this.this$0 = lockscreenShadeTransitionController;
    }

    public final void invoke() {
        this.this$0.mo18462xcfc05636(0.0f);
        this.this$0.forceApplyAmount = false;
    }
}
