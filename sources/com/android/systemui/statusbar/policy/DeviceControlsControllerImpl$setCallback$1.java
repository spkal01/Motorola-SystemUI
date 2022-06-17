package com.android.systemui.statusbar.policy;

import com.android.systemui.controls.management.ControlsListingController;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DeviceControlsControllerImpl.kt */
final class DeviceControlsControllerImpl$setCallback$1 implements Consumer<ControlsListingController> {
    final /* synthetic */ DeviceControlsControllerImpl this$0;

    DeviceControlsControllerImpl$setCallback$1(DeviceControlsControllerImpl deviceControlsControllerImpl) {
        this.this$0 = deviceControlsControllerImpl;
    }

    public final void accept(@NotNull ControlsListingController controlsListingController) {
        Intrinsics.checkNotNullParameter(controlsListingController, "it");
        controlsListingController.addCallback(this.this$0.listingCallback);
    }
}
