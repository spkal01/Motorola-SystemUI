package com.android.systemui.controls.controller;

import android.service.controls.Control;
import com.android.systemui.controls.controller.ControlsBindingController;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsBindingControllerImpl.kt */
public final class ControlsBindingControllerImpl$Companion$emptyCallback$1 implements ControlsBindingController.LoadCallback {
    public void accept(@NotNull List<Control> list) {
        Intrinsics.checkNotNullParameter(list, "controls");
    }

    public void error(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "message");
    }

    ControlsBindingControllerImpl$Companion$emptyCallback$1() {
    }
}
