package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import android.util.Log;
import com.android.systemui.controls.controller.ControlsBindingController;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$startSeeding$1 implements ControlsBindingController.LoadCallback {
    final /* synthetic */ Consumer<SeedResponse> $callback;
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ boolean $didAnyFail;
    final /* synthetic */ List<ComponentName> $remaining;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$startSeeding$1(ControlsControllerImpl controlsControllerImpl, Consumer<SeedResponse> consumer, ComponentName componentName, List<ComponentName> list, boolean z) {
        this.this$0 = controlsControllerImpl;
        this.$callback = consumer;
        this.$componentName = componentName;
        this.$remaining = list;
        this.$didAnyFail = z;
    }

    public void accept(@NotNull List<Control> list) {
        Intrinsics.checkNotNullParameter(list, "controls");
        this.this$0.executor.execute(new ControlsControllerImpl$startSeeding$1$accept$1(list, this.this$0, this.$callback, this.$componentName, this.$remaining, this.$didAnyFail));
    }

    public void error(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "message");
        Log.e("ControlsControllerImpl", Intrinsics.stringPlus("Unable to seed favorites: ", str));
        this.this$0.executor.execute(new ControlsControllerImpl$startSeeding$1$error$1(this.$callback, this.$componentName, this.this$0, this.$remaining));
    }
}
