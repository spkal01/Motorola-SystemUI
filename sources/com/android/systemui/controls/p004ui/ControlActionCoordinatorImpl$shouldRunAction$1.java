package com.android.systemui.controls.p004ui;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$shouldRunAction$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$shouldRunAction$1 implements Runnable {
    final /* synthetic */ String $controlId;
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    ControlActionCoordinatorImpl$shouldRunAction$1(ControlActionCoordinatorImpl controlActionCoordinatorImpl, String str) {
        this.this$0 = controlActionCoordinatorImpl;
        this.$controlId = str;
    }

    public final void run() {
        this.this$0.actionsInProgress.remove(this.$controlId);
    }
}
