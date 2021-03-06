package com.android.systemui.controls.management;

import android.util.Log;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
final class ControlsListingControllerImpl$addCallback$1 implements Runnable {
    final /* synthetic */ ControlsListingController.ControlsListingCallback $listener;
    final /* synthetic */ ControlsListingControllerImpl this$0;

    ControlsListingControllerImpl$addCallback$1(ControlsListingControllerImpl controlsListingControllerImpl, ControlsListingController.ControlsListingCallback controlsListingCallback) {
        this.this$0 = controlsListingControllerImpl;
        this.$listener = controlsListingCallback;
    }

    public final void run() {
        if (this.this$0.userChangeInProgress.get() > 0) {
            this.this$0.addCallback(this.$listener);
            return;
        }
        List<ControlsServiceInfo> currentServices = this.this$0.getCurrentServices();
        Log.d("ControlsListingControllerImpl", Intrinsics.stringPlus("Subscribing callback, service count: ", Integer.valueOf(currentServices.size())));
        this.this$0.callbacks.add(this.$listener);
        this.$listener.onServicesUpdated(currentServices);
    }
}
