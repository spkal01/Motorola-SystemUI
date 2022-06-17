package com.android.systemui.controls.controller;

import android.content.ComponentName;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$addFavorite$1 implements Runnable {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ ControlInfo $controlInfo;
    final /* synthetic */ CharSequence $structureName;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$addFavorite$1(ComponentName componentName, CharSequence charSequence, ControlInfo controlInfo, ControlsControllerImpl controlsControllerImpl) {
        this.$componentName = componentName;
        this.$structureName = charSequence;
        this.$controlInfo = controlInfo;
        this.this$0 = controlsControllerImpl;
    }

    public final void run() {
        Favorites favorites = Favorites.INSTANCE;
        if (favorites.addFavorite(this.$componentName, this.$structureName, this.$controlInfo)) {
            this.this$0.persistenceWrapper.storeFavorites(favorites.getAllStructures());
        }
    }
}
