package com.android.systemui.controls.p004ui;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$bouncerOrRun$2 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$bouncerOrRun$2 implements Runnable {
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    ControlActionCoordinatorImpl$bouncerOrRun$2(ControlActionCoordinatorImpl controlActionCoordinatorImpl) {
        this.this$0 = controlActionCoordinatorImpl;
    }

    public final void run() {
        this.this$0.pendingAction = null;
    }
}
