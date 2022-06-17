package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$startSeeding$1$error$1 implements Runnable {
    final /* synthetic */ Consumer<SeedResponse> $callback;
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ List<ComponentName> $remaining;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$startSeeding$1$error$1(Consumer<SeedResponse> consumer, ComponentName componentName, ControlsControllerImpl controlsControllerImpl, List<ComponentName> list) {
        this.$callback = consumer;
        this.$componentName = componentName;
        this.this$0 = controlsControllerImpl;
        this.$remaining = list;
    }

    public final void run() {
        Consumer<SeedResponse> consumer = this.$callback;
        String packageName = this.$componentName.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "componentName.packageName");
        consumer.accept(new SeedResponse(packageName, false));
        this.this$0.startSeeding(this.$remaining, this.$callback, true);
    }
}
