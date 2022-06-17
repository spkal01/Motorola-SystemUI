package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.view.View;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.StructureInfo;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$bindButtons$2$1 implements View.OnClickListener {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$bindButtons$2$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public final void onClick(View view) {
        if (this.this$0.component != null) {
            List<StructureContainer> access$getListOfStructures$p = this.this$0.listOfStructures;
            ControlsFavoritingActivity controlsFavoritingActivity = this.this$0;
            for (StructureContainer structureContainer : access$getListOfStructures$p) {
                List<ControlInfo> favorites = structureContainer.getModel().getFavorites();
                ControlsControllerImpl access$getController$p = controlsFavoritingActivity.controller;
                ComponentName access$getComponent$p = controlsFavoritingActivity.component;
                Intrinsics.checkNotNull(access$getComponent$p);
                access$getController$p.replaceFavoritesForStructure(new StructureInfo(access$getComponent$p, structureContainer.getStructureName(), favorites));
            }
            this.this$0.animateExitAndFinish();
            this.this$0.openControlsOrigin();
        }
    }
}
