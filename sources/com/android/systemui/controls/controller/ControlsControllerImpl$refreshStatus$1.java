package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$refreshStatus$1 implements Runnable {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ Control $control;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$refreshStatus$1(ComponentName componentName, Control control, ControlsControllerImpl controlsControllerImpl) {
        this.$componentName = componentName;
        this.$control = control;
        this.this$0 = controlsControllerImpl;
    }

    public final void run() {
        Favorites favorites = Favorites.INSTANCE;
        if (favorites.updateControls(this.$componentName, CollectionsKt__CollectionsJVMKt.listOf(this.$control))) {
            this.this$0.persistenceWrapper.storeFavorites(favorites.getAllStructures());
        }
    }
}
