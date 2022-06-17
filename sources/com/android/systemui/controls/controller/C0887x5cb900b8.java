package com.android.systemui.controls.controller;

import com.android.systemui.controls.controller.ControlsBindingControllerImpl;

/* renamed from: com.android.systemui.controls.controller.ControlsBindingControllerImpl$LoadSubscriber$maybeTerminateAndRun$2 */
/* compiled from: ControlsBindingControllerImpl.kt */
final class C0887x5cb900b8 implements Runnable {
    final /* synthetic */ Runnable $postTerminateFn;
    final /* synthetic */ ControlsBindingControllerImpl.LoadSubscriber this$0;

    C0887x5cb900b8(ControlsBindingControllerImpl.LoadSubscriber loadSubscriber, Runnable runnable) {
        this.this$0 = loadSubscriber;
        this.$postTerminateFn = runnable;
    }

    public final void run() {
        this.this$0.isTerminated.compareAndSet(false, true);
        this.$postTerminateFn.run();
    }
}
