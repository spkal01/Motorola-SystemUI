package com.android.systemui.statusbar.policy;

import com.android.systemui.controls.controller.ControlsController;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DeviceControlsControllerImpl.kt */
final class DeviceControlsControllerImpl$checkMigrationToQs$1 implements Consumer<ControlsController> {
    final /* synthetic */ DeviceControlsControllerImpl this$0;

    DeviceControlsControllerImpl$checkMigrationToQs$1(DeviceControlsControllerImpl deviceControlsControllerImpl) {
        this.this$0 = deviceControlsControllerImpl;
    }

    public final void accept(@NotNull ControlsController controlsController) {
        Intrinsics.checkNotNullParameter(controlsController, "it");
        if (!controlsController.getFavorites().isEmpty()) {
            this.this$0.mo23584xa7d03371(3);
            this.this$0.fireControlsUpdate();
        }
    }
}
