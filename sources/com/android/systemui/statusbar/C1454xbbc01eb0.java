package com.android.systemui.statusbar;

import kotlin.jvm.functions.Function0;

/* renamed from: com.android.systemui.statusbar.LockscreenShadeTransitionController$onDraggedDown$cancelRunnable$1 */
/* compiled from: LockscreenShadeTransitionController.kt */
final class C1454xbbc01eb0 implements Runnable {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    C1454xbbc01eb0(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        this.this$0 = lockscreenShadeTransitionController;
    }

    public final void run() {
        LockscreenShadeTransitionController.setDragDownAmountAnimated$default(this.this$0, 0.0f, 0, (Function0) null, 6, (Object) null);
    }
}
