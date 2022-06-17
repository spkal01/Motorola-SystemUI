package com.android.systemui.statusbar;

import com.android.systemui.plugins.ActivityStarter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$goToLockedShadeInternal$1 implements ActivityStarter.OnDismissAction {
    final /* synthetic */ Function1<Long, Unit> $animationHandler;
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    LockscreenShadeTransitionController$goToLockedShadeInternal$1(LockscreenShadeTransitionController lockscreenShadeTransitionController, Function1<? super Long, Unit> function1) {
        this.this$0 = lockscreenShadeTransitionController;
        this.$animationHandler = function1;
    }

    public final boolean onDismiss() {
        this.this$0.animationHandlerOnKeyguardDismiss = this.$animationHandler;
        return false;
    }
}
