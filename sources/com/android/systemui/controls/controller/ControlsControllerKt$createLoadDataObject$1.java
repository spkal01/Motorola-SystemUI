package com.android.systemui.controls.controller;

import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsController.kt */
public final class ControlsControllerKt$createLoadDataObject$1 implements ControlsController.LoadData {
    final /* synthetic */ List<ControlStatus> $allControls;
    final /* synthetic */ boolean $error;
    final /* synthetic */ List<String> $favorites;
    @NotNull
    private final List<ControlStatus> allControls;
    private final boolean errorOnLoad;
    @NotNull
    private final List<String> favoritesIds;

    ControlsControllerKt$createLoadDataObject$1(List<ControlStatus> list, List<String> list2, boolean z) {
        this.$allControls = list;
        this.$favorites = list2;
        this.$error = z;
        this.allControls = list;
        this.favoritesIds = list2;
        this.errorOnLoad = z;
    }

    @NotNull
    public List<ControlStatus> getAllControls() {
        return this.allControls;
    }

    @NotNull
    public List<String> getFavoritesIds() {
        return this.favoritesIds;
    }

    public boolean getErrorOnLoad() {
        return this.errorOnLoad;
    }
}
