package com.android.systemui.controls.controller;

import java.util.function.Consumer;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$addSeedingFavoritesCallback$1 implements Runnable {
    final /* synthetic */ Consumer<Boolean> $callback;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$addSeedingFavoritesCallback$1(ControlsControllerImpl controlsControllerImpl, Consumer<Boolean> consumer) {
        this.this$0 = controlsControllerImpl;
        this.$callback = consumer;
    }

    public final void run() {
        if (this.this$0.seedingInProgress) {
            this.this$0.seedingCallbacks.add(this.$callback);
        } else {
            this.$callback.accept(Boolean.FALSE);
        }
    }
}
