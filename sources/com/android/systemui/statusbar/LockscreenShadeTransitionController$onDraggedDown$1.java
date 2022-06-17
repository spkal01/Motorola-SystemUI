package com.android.systemui.statusbar;

import com.android.systemui.plugins.ActivityStarter;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$onDraggedDown$1 implements ActivityStarter.OnDismissAction {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    LockscreenShadeTransitionController$onDraggedDown$1(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        this.this$0 = lockscreenShadeTransitionController;
    }

    public final boolean onDismiss() {
        this.this$0.nextHideKeyguardNeedsNoAnimation = true;
        return false;
    }
}
