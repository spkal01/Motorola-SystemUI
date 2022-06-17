package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.List;
import java.util.function.Consumer;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$seedFavoritesForComponents$1 implements Runnable {
    final /* synthetic */ Consumer<SeedResponse> $callback;
    final /* synthetic */ List<ComponentName> $componentNames;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$seedFavoritesForComponents$1(ControlsControllerImpl controlsControllerImpl, List<ComponentName> list, Consumer<SeedResponse> consumer) {
        this.this$0 = controlsControllerImpl;
        this.$componentNames = list;
        this.$callback = consumer;
    }

    public final void run() {
        this.this$0.seedFavoritesForComponents(this.$componentNames, this.$callback);
    }
}
