package com.android.systemui.controls.controller;

import android.os.IBinder;
import android.service.controls.Control;
import android.service.controls.IControlsSubscription;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsBindingControllerImpl.kt */
final class ControlsBindingControllerImpl$LoadSubscriber$onNext$1 implements Runnable {

    /* renamed from: $c */
    final /* synthetic */ Control f82$c;
    final /* synthetic */ IBinder $token;
    final /* synthetic */ ControlsBindingControllerImpl.LoadSubscriber this$0;
    final /* synthetic */ ControlsBindingControllerImpl this$1;

    ControlsBindingControllerImpl$LoadSubscriber$onNext$1(ControlsBindingControllerImpl.LoadSubscriber loadSubscriber, Control control, ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder) {
        this.this$0 = loadSubscriber;
        this.f82$c = control;
        this.this$1 = controlsBindingControllerImpl;
        this.$token = iBinder;
    }

    public final void run() {
        if (!this.this$0.isTerminated.get()) {
            this.this$0.getLoadedControls().add(this.f82$c);
            if (((long) this.this$0.getLoadedControls().size()) >= this.this$0.getRequestLimit()) {
                ControlsBindingControllerImpl.LoadSubscriber loadSubscriber = this.this$0;
                ControlsBindingControllerImpl controlsBindingControllerImpl = this.this$1;
                IBinder iBinder = this.$token;
                ArrayList<Control> loadedControls = loadSubscriber.getLoadedControls();
                IControlsSubscription access$getSubscription$p = this.this$0.subscription;
                if (access$getSubscription$p != null) {
                    loadSubscriber.maybeTerminateAndRun(new ControlsBindingControllerImpl.OnCancelAndLoadRunnable(controlsBindingControllerImpl, iBinder, loadedControls, access$getSubscription$p, this.this$0.getCallback()));
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("subscription");
                    throw null;
                }
            }
        }
    }
}
